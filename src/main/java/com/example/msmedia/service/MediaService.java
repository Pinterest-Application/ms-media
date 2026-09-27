package com.example.msmedia.service;

import com.example.libexception.exception.ConflictException;
import com.example.libexception.exception.NotFoundException;
import com.example.msmedia.dto.*;
import com.example.msmedia.entity.Media;
import com.example.msmedia.mapper.MediaMapper;
import com.example.msmedia.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;
    private final DocumentService documentService;

    @Transactional(readOnly = true)
    public MediaResponse getById(UUID id) {
        return mediaRepository.findByIdAndIsActiveTrue(id)
                .map(mediaMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Media not found with id: " + id));
    }

    @Transactional
    public MediaUploadResponse generateUploadUrlAndInitiateMedia(String userId, PreSignedUrlRequest request) {
        PreSignedUrlResponse urlResponse = documentService.generateUploadUrl(userId, request);

        if (mediaRepository.existsByObjectKey(urlResponse.getObjectKey())) {
            throw new ConflictException("Media already exists with objectKey: " + urlResponse.getObjectKey());
        }

        Media media = Media.builder()
                .objectKey(urlResponse.getObjectKey())
                .bucketType(request.getBucketType())
                .mediaType(request.getContentType())
                .originalFileName(request.getFileName())
                .build();

        Media savedMedia = mediaRepository.save(media);

        return MediaUploadResponse.builder()
                .media(mediaMapper.toResponse(savedMedia))
                .preSignedUploadUrl(urlResponse.getPreSignedUrl())
                .build();
    }

    public PreSignedDownloadUrlResponse generateDownloadUrl(PreSignedDownloadUrlRequest request) {
        return documentService.generateDownloadUrl(request);
    }
}