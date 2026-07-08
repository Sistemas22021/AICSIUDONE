package com.ccc.sistema_balistico.core.application.usecase;

import com.ccc.sistema_balistico.core.application.dto.CorrelationBreakdown;
import com.ccc.sistema_balistico.core.application.dto.CorrelationResultDTO;
import com.ccc.sistema_balistico.core.application.dto.MatchResult;
import com.ccc.sistema_balistico.core.application.services.CorrelationService;
import com.ccc.sistema_balistico.core.application.services.FileStorageService;
import com.ccc.sistema_balistico.core.application.services.ImageProcessingService;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.BulletIsDeleted;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.BulletNotFound;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.BulletImagesEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.BulletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CorrelationServiceImpl implements CorrelationService {

    @Autowired
    private BulletRepository bulletRepository;

    @Autowired
    private ImageProcessingService imageProcessingService;

    @Autowired
    private FileStorageService fileStorageService;

    private static final int MIN_INLIERS_FOR_STRIAE_MATCH = 8;

    @Transactional(readOnly = true)
    @Override
    public Page<CorrelationResultDTO> correlateBullet(Long evidenceId, Pageable pageable) {
        BulletEntity source = bulletRepository.findWithImagesByIdBulletAndIsDeleteFalse(evidenceId)
                .orElseThrow(() -> new BulletNotFound("Source bullet not found"));

        if (source.getIsDelete()) {
            throw new BulletIsDeleted();
        }

        List<BulletEntity> candidates = bulletRepository.findCompatibleCandidates(
                source.getCaliberEntity().getIdCaliber(),
                evidenceId
        );

        List<CandidateEvaluation> evaluations = new ArrayList<>();

        for (BulletEntity candidate : candidates) {
            evaluations.add(evaluateCandidateFast(source, candidate));
        }

        evaluations.sort(Comparator.comparingDouble(CandidateEvaluation::score).reversed());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), evaluations.size());
        
        List<CandidateEvaluation> pagedEvaluations = (start >= evaluations.size()) 
                ? List.of() 
                : evaluations.subList(start, end);

        List<CorrelationResultDTO> content = new ArrayList<>();
        for (CandidateEvaluation eval : pagedEvaluations) {
            content.add(buildFinalCorrelationResult(source, eval));
        }

        return new PageImpl<>(content, pageable, evaluations.size());
    }

    private CandidateEvaluation evaluateCandidateFast(BulletEntity source, BulletEntity candidate) {
        // Evaluate mechanical metadata
        boolean twistMatched = source.getTwistDirection() != null 
                && candidate.getTwistDirection() != null 
                && source.getTwistDirection() == candidate.getTwistDirection();

        boolean percussionMatched = source.getPercussionType() != null 
                && candidate.getPercussionType() != null 
                && source.getPercussionType() == candidate.getPercussionType();

        boolean brandMatched = source.getManufacturer() != null 
                && candidate.getManufacturer() != null 
                && source.getManufacturer().equalsIgnoreCase(candidate.getManufacturer());

        int maxInliers = 0;
        BulletImagesEntity bestSrcImg = null;
        BulletImagesEntity bestCandImg = null;

        for (BulletImagesEntity srcImg : source.getImagePaths()) {
            if (srcImg.getDescriptor() == null || srcImg.getDescriptor().length == 0) continue;

            for (BulletImagesEntity candImg : candidate.getImagePaths()) {
                if (candImg.getDescriptor() == null || candImg.getDescriptor().length == 0) continue;

                MatchResult result = imageProcessingService.matchFeatures(
                        srcImg.getKeypoints(), srcImg.getDescriptor(), null,
                        candImg.getKeypoints(), candImg.getDescriptor(), null
                );

                if (result.inliersCount() > maxInliers) {
                    maxInliers = result.inliersCount();
                    bestSrcImg = srcImg;
                    bestCandImg = candImg;
                }
            }
        }

        boolean striaeMatched = maxInliers >= MIN_INLIERS_FOR_STRIAE_MATCH;

        double score = 0.0;
        if (striaeMatched) score += 40.0;
        if (twistMatched) score += 30.0;
        if (percussionMatched) score += 20.0;
        if (brandMatched) score += 10.0;

        return new CandidateEvaluation(
                candidate, score, striaeMatched, twistMatched, percussionMatched, brandMatched,
                maxInliers, bestSrcImg, bestCandImg
        );
    }

    private CorrelationResultDTO buildFinalCorrelationResult(BulletEntity source, CandidateEvaluation eval) {
        String base64Image = null;

        // If we found a matching image pair, we generate the drawMatches comparison image
        if (eval.bestSrcImg() != null && eval.bestCandImg() != null) {
            byte[] srcImgBytes = loadImageBytes(eval.bestSrcImg().getPathImage());
            byte[] candImgBytes = loadImageBytes(eval.bestCandImg().getPathImage());

            if (srcImgBytes != null && candImgBytes != null) {
                MatchResult result = imageProcessingService.matchFeatures(
                        eval.bestSrcImg().getKeypoints(), eval.bestSrcImg().getDescriptor(), srcImgBytes,
                        eval.bestCandImg().getKeypoints(), eval.bestCandImg().getDescriptor(), candImgBytes
                );
                base64Image = result.comparisonImageBase64();
            }
        }

        CorrelationBreakdown breakdown = new CorrelationBreakdown(
                eval.striaeMatched(),
                eval.twistMatched(),
                eval.percussionMatched(),
                eval.brandMatched(),
                eval.inliersCount(),
                base64Image
        );

        return new CorrelationResultDTO(
                eval.candidate().getIdBullet(),
                eval.candidate().getCaseFile(),
                eval.candidate().getManufacturer(),
                eval.score(),
                breakdown
        );
    }

    private byte[] loadImageBytes(String path) {
        if (path == null) return null;
        try {
            Resource resource = fileStorageService.loadImageFile(path);
            try (InputStream is = resource.getInputStream()) {
                return is.readAllBytes();
            }
        } catch (Exception e) {
            return null;
        }
    }

    private record CandidateEvaluation(
            BulletEntity candidate,
            double score,
            boolean striaeMatched,
            boolean twistMatched,
            boolean percussionMatched,
            boolean brandMatched,
            int inliersCount,
            BulletImagesEntity bestSrcImg,
            BulletImagesEntity bestCandImg
    ) {}
}
