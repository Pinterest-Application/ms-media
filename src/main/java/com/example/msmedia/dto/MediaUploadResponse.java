package com.example.msmedia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadResponse {
    private MediaResponse media;
    private String preSignedUploadUrl;
}