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

    @NotBlank(message = "Object key cannot be blank")
    private String objectKey;

    @NotNull(message = "Bucket type cannot be null")
    private BucketType bucketType;

    @NotBlank(message = "Media type cannot be blank")
    private String mediaType;

    @NotNull(message = "File size cannot be null")
    @Positive(message = "File size must be positive")
    private Long fileSize;

    @NotNull(message = "Width cannot be null")
    @Positive(message = "Width must be positive")
    private Integer width;

    @NotNull(message = "Height cannot be null")
    @Positive(message = "Height must be positive")
    private Integer height;

    private String originalFileName;
}