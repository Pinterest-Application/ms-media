package com.example.msmedia.dto;

import com.example.msmedia.entity.enums.BucketType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponse {

    private UUID id;
    private String objectKey;
    private BucketType bucketType;
    private String mediaType;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private String originalFileName;
    private Boolean isActive;
    private Instant createdAt;
}
