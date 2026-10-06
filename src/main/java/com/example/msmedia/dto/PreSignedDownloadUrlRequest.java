package com.example.msmedia.dto;

import com.example.msmedia.entity.enums.BucketType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreSignedDownloadUrlRequest {
    private BucketType bucketType;
    private String objectKey;
    private String downloadFileName;
}
