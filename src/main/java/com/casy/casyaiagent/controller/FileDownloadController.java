package com.casy.casyaiagent.controller;

import com.casy.casyaiagent.constant.FileConstant;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * 提供生成文件（PDF / 下载资源 / 文本文件）的下载。
 */
@RestController
@RequestMapping("/files")
public class FileDownloadController {

    private static final Set<String> ALLOWED_CATEGORIES = Set.of("pdf", "file", "download");

    @GetMapping("/{category}/{fileName:.+}")
    public ResponseEntity<Resource> download(@PathVariable String category, @PathVariable String fileName) {
        if (!ALLOWED_CATEGORIES.contains(category)) {
            return ResponseEntity.badRequest().build();
        }

        Path root = Path.of(FileConstant.getFileSaveDir()).toAbsolutePath().normalize();
        Path target = root.resolve(category).resolve(fileName).normalize();
        if (!target.startsWith(root) || !Files.isRegularFile(target)) {
            return ResponseEntity.notFound().build();
        }

        String contentType;
        try {
            contentType = Files.probeContentType(target);
        } catch (Exception e) {
            contentType = null;
        }
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(target.getFileName().toString(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.parseMediaType(contentType))
                .body(new FileSystemResource(target));
    }
}
