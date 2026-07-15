package com.ccc.sistema_balistico.core.infrastructure.in.rest.controller;

import com.ccc.sistema_balistico.core.application.dto.BulletDTO;
import com.ccc.sistema_balistico.core.application.dto.ImageDTO;
import com.ccc.sistema_balistico.core.application.services.BulletService;
import com.ccc.sistema_balistico.core.application.services.BulletImagesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/bullet")
@Tag(name = "Registro Balístico", description = "Endpoints para la gestión, seguimiento y análisis de evidencias balísticas")
@Validated
public class BulletController {

    @Autowired
    private BulletService bulletService;
    @Autowired
    BulletImagesService bulletImagesService;

    @Operation(
            summary = "Obtener evidencias paginadas",
            description = "Retorna una página de evidencias balísticas. Permite gestionar grandes volúmenes de datos mediante parámetros de paginación."
    )
    @GetMapping
    public ResponseEntity<Page<BulletDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(bulletService.getAll(pageable));
    }

    @Operation(
            summary = "Buscar evidencia por ID",
            description = "Recupera los detalles técnicos de las evidencias registradas en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evidencia encontrada con éxito"),
            @ApiResponse(responseCode = "404", description = "No se encontró ninguna evidencia con el ID proporcionado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BulletDTO> getBulletById(
            @Parameter(description = "ID único de la evidencia", example = "1")
            @Min(value = 1, message = "only id > 0")
            @PathVariable Long id) {
        BulletDTO bulletDTO = bulletService.getBullet(id);
        return ResponseEntity.ok(bulletDTO);
    }

    @Operation(
            summary = "Registrar nueva evidencia",
            description = "Crea un nuevo registro de evidencia balistica en la base de datos, incluyendo calibre, fabricante y tipo de percusión."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evidencia creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "413", description = "El archivo es demasiado pesado (Max 5MB)"),
            @ApiResponse(responseCode = "415", description = "Tipo de archivo no soportado (Solo JPG/PNG)")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BulletDTO> createdBullet(
            @Valid @ModelAttribute BulletDTO bulletDTO,
            @Parameter(description = "Archivo de imagen de la evidencia (JPG,PNG,JPEG)")
            @RequestParam("file") List<MultipartFile> fileList) {
        BulletDTO bullet = bulletService.createBullet(bulletDTO,fileList);
        return ResponseEntity.created(URI.create("api/v1/bullet" + bullet.getIdBullet())).body(bullet);
    }

    @Operation(
            summary = "Actualizar evidencia existente",
            description = "Modifica los datos de un proyectil ya registrado. Ideal para corregir información o actualizar el estado de la investigación."
    )
    @PutMapping("/{id}")
    public ResponseEntity<BulletDTO> updateBullet(@PathVariable @Min(value = 1, message = "only id > 0") Long id, @Valid @RequestBody BulletDTO bulletDTO) {
        BulletDTO bullet = bulletService.updateBullet(id, bulletDTO);
        return ResponseEntity.ok(bullet);
    }

    @Operation(
            summary = "Eliminar (Borrado lógico) de evidencia",
            description = "Marca un proyectil como eliminado en el sistema sin borrarlo físicamente de la base de datos (is_delete = true)."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBullet(@PathVariable Long id) {
        bulletService.deleteBullet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/images/{filePath}")
    @Operation(summary = "Visualizar una imagen de evidencia en el navegador")
    public ResponseEntity<Resource> getBulletImage(@PathVariable String filePath) {
        ImageDTO file = bulletImagesService.loadImage(filePath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.img().getFilename() + "\"")
                .body(file.img());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/{id}/images")
    @Operation(summary = "Añadir imagen a evidencia ya creada")
    public ResponseEntity<BulletDTO> addBulletImages(@PathVariable Long id,
                                                    @RequestParam("file") List<MultipartFile> file) {
        BulletDTO bulletDTO = bulletImagesService.saveImageList(file,id);
        return ResponseEntity.ok(bulletDTO);
    }

    @Operation(
            summary = "Buscar evidencias por expediente o fabricante",
            description = "Retorna una página de evidencias que coinciden con el texto de búsqueda en el número de expediente o nombre de fabricante."
    )
    @GetMapping("/search")
    public ResponseEntity<Page<BulletDTO>> searchBullets(
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            Pageable pageable) {
        return ResponseEntity.ok(bulletService.searchBullets(query, pageable));
    }

}
