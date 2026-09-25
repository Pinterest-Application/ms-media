package com.example.msmedia.controller;

import com.example.msmedia.dto.MediaCreateRequest;
import com.example.msmedia.dto.MediaResponse;
import com.example.msmedia.service.MediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping
    public ResponseEntity<MediaResponse> save(@Valid @RequestBody MediaCreateRequest request) {
        MediaResponse response = mediaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaResponse> getById(@PathVariable UUID id) {
        MediaResponse response = mediaService.getById(id);
        return ResponseEntity.ok(response);
    }
}