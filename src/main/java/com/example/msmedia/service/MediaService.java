package com.example.msmedia.service;

import com.example.libexception.exception.ConflictException;
import com.example.libexception.exception.NotFoundException;
import com.example.msmedia.dto.MediaCreateRequest;
import com.example.msmedia.dto.MediaResponse;
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

    @Transactional
    public MediaResponse create(MediaCreateRequest request) {
        if (mediaRepository.existsByObjectKey(request.getObjectKey())) {
            throw new ConflictException("Media already exists with objectKey: " + request.getObjectKey());
        }

        Media media = mediaMapper.toEntity(request);
        Media savedMedia = mediaRepository.save(media);
        return mediaMapper.toResponse(savedMedia);
    }

    @Transactional(readOnly = true)
    public MediaResponse getById(UUID id) {
        Media media = mediaRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new NotFoundException("Media not found with id: " + id));

        return mediaMapper.toResponse(media);
    }
}