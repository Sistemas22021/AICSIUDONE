package naranja.custodia_360.dtos;

public record TestimonyContentDTO(
        String sessionId,
        String originalText,
        String modifiedText,
        String audioUrl) {
}
