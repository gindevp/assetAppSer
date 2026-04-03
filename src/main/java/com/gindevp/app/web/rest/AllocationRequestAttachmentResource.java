package com.gindevp.app.web.rest;

import com.gindevp.app.config.ApplicationProperties;
import com.gindevp.app.web.rest.errors.BadRequestAlertException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Upload / tải file đính kèm yêu cầu cấp phát (ảnh, PDF, video ngắn theo Phase 1).
 */
@RestController
@RequestMapping("/api/allocation-request-attachments")
public class AllocationRequestAttachmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(AllocationRequestAttachmentResource.class);

    private static final String ENTITY_NAME = "allocationAttachment";

    private static final Pattern STORED_NAME = Pattern.compile(
        "^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}\\.[a-z0-9]{1,8}$"
    );

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp", "pdf", "mp4", "webm", "mov");

    private final ApplicationProperties applicationProperties;

    private Path uploadDirAbsolute;

    public AllocationRequestAttachmentResource(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @PostConstruct
    public void initUploadDir() throws IOException {
        String dir = applicationProperties.getUpload().getAllocationDirectory();
        uploadDirAbsolute = Path.of(dir).toAbsolutePath().normalize();
        Files.createDirectories(uploadDirAbsolute);
        LOG.info("Allocation attachments directory: {}", uploadDirAbsolute);
    }

    /**
     * {@code POST} multipart field {@code file}.
     *
     * @return JSON {@code { "url": "/api/allocation-request-attachments/<uuid>.<ext>" }}
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestAlertException("Chọn file", ENTITY_NAME, "emptyfile");
        }
        long max = applicationProperties.getUpload().getMaxAllocationAttachmentBytes();
        if (file.getSize() > max) {
            throw new BadRequestAlertException(
                "File quá lớn (tối đa " + (max / 1024 / 1024) + " MB)",
                ENTITY_NAME,
                "filetoobig"
            );
        }
        String ext = extensionOf(file.getOriginalFilename()).toLowerCase(Locale.ROOT);
        if (ext.isEmpty() || !ALLOWED_EXT.contains(ext)) {
            throw new BadRequestAlertException(
                "Chỉ chấp nhận ảnh (jpg, png, gif, webp), pdf hoặc video (mp4, webm, mov)",
                ENTITY_NAME,
                "badext"
            );
        }
        String ct = file.getContentType();
        if (
            ct != null &&
            !ct.isBlank() &&
            !ct.equalsIgnoreCase(MediaType.APPLICATION_OCTET_STREAM_VALUE) &&
            !isAllowedContentType(ct, ext)
        ) {
            throw new BadRequestAlertException("Loại MIME không khớp phần mở rộng file", ENTITY_NAME, "badmimetype");
        }
        String name = UUID.randomUUID().toString() + "." + ext;
        Path target = uploadDirAbsolute.resolve(name).normalize();
        if (!target.startsWith(uploadDirAbsolute)) {
            throw new BadRequestAlertException("Đường dẫn không hợp lệ", ENTITY_NAME, "invalidpath");
        }
        Files.copy(file.getInputStream(), target);
        return ResponseEntity.ok(Map.of("url", "/api/allocation-request-attachments/" + name));
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> download(@PathVariable String fileName) throws IOException {
        if (!STORED_NAME.matcher(fileName).matches()) {
            return ResponseEntity.notFound().build();
        }
        Path target = uploadDirAbsolute.resolve(fileName).normalize();
        if (!target.startsWith(uploadDirAbsolute) || !Files.isRegularFile(target)) {
            return ResponseEntity.notFound().build();
        }
        Resource body = new FileSystemResource(target);
        String ext = extensionOf(fileName).toLowerCase(Locale.ROOT);
        MediaType media = mediaTypeForExt(ext);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
            .contentType(media)
            .contentLength(Files.size(target))
            .body(body);
    }

    private static String extensionOf(String name) {
        if (name == null || !name.contains(".")) {
            return "";
        }
        return name.substring(name.lastIndexOf('.') + 1);
    }

    private static boolean isAllowedContentType(String ct, String extLower) {
        String c = ct.toLowerCase(Locale.ROOT);
        return switch (extLower) {
            case "jpg", "jpeg", "png", "gif", "webp" -> c.startsWith("image/");
            case "pdf" -> c.contains("pdf");
            case "mp4" -> c.contains("video/mp4");
            case "webm" -> c.contains("video/webm");
            case "mov" -> c.contains("video/quicktime") || c.contains("video/");
            default -> false;
        };
    }

    private static MediaType mediaTypeForExt(String extLower) {
        return switch (extLower) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "webp" -> MediaType.parseMediaType("image/webp");
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "mp4" -> MediaType.parseMediaType("video/mp4");
            case "webm" -> MediaType.parseMediaType("video/webm");
            case "mov" -> MediaType.parseMediaType("video/quicktime");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}
