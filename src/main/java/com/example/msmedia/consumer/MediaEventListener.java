package com.example.msmedia.consumer;

import com.example.msmedia.dto.ImageDimensions;
import com.example.msmedia.entity.Media;
import com.example.msmedia.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import seaweedfs.client.FilerProto.EventNotification;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
@Component
@RequiredArgsConstructor
public class MediaEventListener {

    private final MediaRepository mediaRepository;
    private final S3Client s3Client;

    @Transactional
    @KafkaListener(topics = "seaweedfs_events", groupId = "ms-media-group")
    public void consumeMediaEvent(ConsumerRecord<String, byte[]> record) {
        String fullPath = record.key(); // Məs: /buckets/media/userFolder/12/34/56/uuid.jpg

        if (fullPath == null || !fullPath.startsWith("/buckets/")) {
            return;
        }

        try {
            EventNotification notification = EventNotification.parseFrom(record.value());

            if (!notification.hasNewEntry() || notification.getNewEntry().getIsDirectory()) {
                return;
            }

            var newEntry = notification.getNewEntry();
            long fileSize = newEntry.getAttributes().getFileSize();

            ParsedPath parsedPath = parsePath(fullPath);
            if (parsedPath == null) {
                return;
            }

            Media media = mediaRepository.findByObjectKey(parsedPath.objectKey())
                    .orElse(null);

            if (media == null) {
                log.warn("Media bazada tapılmadı: {}", parsedPath.objectKey());
                return;
            }

            ImageDimensions dimensions = extractDimensions(parsedPath.bucket(), parsedPath.objectKey());

            media.setFileSize(fileSize);
            media.setWidth(dimensions.getWidth());
            media.setHeight(dimensions.getHeight());
            media.setIsActive(true);

            mediaRepository.save(media);
            log.info("Media uğurla aktivləşdirildi: id={}, key={}", media.getId(), media.getObjectKey());

        } catch (Exception e) {
            log.error("SeaweedFS hadisəsi emal edilərkən xəta baş verdi: path={}, xəta={}", fullPath, e.getMessage(), e);
        }
    }

    private ImageDimensions extractDimensions(String bucket, String objectKey) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        try (ResponseInputStream<GetObjectResponse> s3Stream = s3Client.getObject(request);
             ImageInputStream imageStream = ImageIO.createImageInputStream(s3Stream)) {

            if (imageStream == null) {
                return new ImageDimensions(null, null);
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageStream);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(imageStream, true, true);
                    int width = reader.getWidth(0);
                    int height = reader.getHeight(0);
                    return new ImageDimensions(width, height);
                } finally {
                    reader.dispose();
                }
            }
        } catch (IOException e) {
            log.warn("Şəkil ölçüləri çıxarıla bilmədi (fayl formatı və ya şəbəkə xətası): {}", objectKey);
        }
        return new ImageDimensions(null, null);
    }

    private ParsedPath parsePath(String fullPath) {
        String[] parts = fullPath.split("/", 4);
        if (parts.length < 4) {
            return null;
        }
        return new ParsedPath(parts[2], parts[3]);
    }

    private record ParsedPath(String bucket, String objectKey) {}
}