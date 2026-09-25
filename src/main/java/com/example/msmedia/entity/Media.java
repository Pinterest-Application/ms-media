package com.example.msmedia.entity;

import com.example.msmedia.entity.enums.BucketType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "media")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "object_key", nullable = false, unique = true)
    private String objectKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "bucket_type", nullable = false, length = 32)
    private BucketType bucketType;

    @Column(name = "media_type", nullable = false, length = 50)
    private String mediaType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "width", nullable = false)
    private Integer width;

    @Column(name = "height", nullable = false)
    private Integer height;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}