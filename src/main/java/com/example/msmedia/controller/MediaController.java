package com.example.msmedia.controller;

import com.example.msmedia.dto.*;
import com.example.msmedia.service.MediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @GetMapping("/{id}")
    public ResponseEntity<MediaResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(mediaService.getById(id));
    }

    @PostMapping("/upload-url")
    public ResponseEntity<MediaUploadResponse> generateUploadUrl(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody PreSignedUrlRequest request) {

        return ResponseEntity.ok(mediaService.generateUploadUrlAndInitiateMedia(jwt.getSubject(), request));
    }

    @PostMapping("/download-url")
    public ResponseEntity<PreSignedDownloadUrlResponse> generateDownloadUrl(
            @Valid @RequestBody PreSignedDownloadUrlRequest request) {

        return ResponseEntity.ok(mediaService.generateDownloadUrl(request));
    }
}