package com.example.msmedia.service;

import com.example.libexception.exception.InternalServerErrorException;
import com.example.msmedia.dto.PreSignedDownloadUrlRequest;
import com.example.msmedia.dto.PreSignedDownloadUrlResponse;
import com.example.msmedia.dto.PreSignedUrlRequest;
import com.example.msmedia.dto.PreSignedUrlResponse;
import com.example.msmedia.entity.enums.SupportedMediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private static final Duration EXPIRATION_DURATION = Duration.ofMinutes(5);
    private final S3Presigner s3Presigner;

    public PreSignedUrlResponse generateUploadUrl(String userId, PreSignedUrlRequest request) {
        SupportedMediaType mediaType = SupportedMediaType.fromMimeType(request.getContentType());
        String objectKey = generateObjectKey(userId, mediaType.getExtension());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(request.getBucketType().getBucketName())
                .key(objectKey)
                .contentType(request.getContentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(EXPIRATION_DURATION)
                .putObjectRequest(putObjectRequest)
                .build();

        String url = s3Presigner.presignPutObject(presignRequest).url().toString();

        return PreSignedUrlResponse.builder()
                .preSignedUrl(url)
                .objectKey(objectKey)
                .build();
    }

    public PreSignedDownloadUrlResponse generateDownloadUrl(PreSignedDownloadUrlRequest request) {
        GetObjectRequest.Builder getObjectRequestBuilder = GetObjectRequest.builder()
                .bucket(request.getBucketType().getBucketName())
                .key(request.getObjectKey());

        if (request.getDownloadFileName() != null && !request.getDownloadFileName().isBlank()) {
            getObjectRequestBuilder.responseContentDisposition(
                    "attachment; filename=\"" + request.getDownloadFileName() + "\"");
        }

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(EXPIRATION_DURATION)
                .getObjectRequest(getObjectRequestBuilder.build())
                .build();

        String url = s3Presigner.presignGetObject(presignRequest).url().toString();

        return PreSignedDownloadUrlResponse.builder()
                .preSignedDownloadUrl(url)
                .build();
    }

    private String generateObjectKey(String userId, String extension) {
        String userFolder = hashSha256(userId).substring(0, 16);
        String fileHash = UUID.randomUUID().toString().replace("-", "");

        return String.format("%s/%s/%s/%s/%s%s",
                userFolder,
                fileHash.substring(0, 2),
                fileHash.substring(2, 4),
                fileHash.substring(4, 6),
                fileHash,
                extension
        );
    }

    private String hashSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerErrorException("SHA-256 algorithm unavailable");
        }
    }
}