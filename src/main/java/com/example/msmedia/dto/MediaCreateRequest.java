package com.example.msmedia.dto;

import com.example.msmedia.entity.enums.BucketType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaCreateRequest {

    private String objectKey;

    private BucketType bucketType;

    private String mediaType;

    private String originalFileName;
}