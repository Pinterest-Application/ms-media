package com.example.msmedia.consumer;

import com.example.msmedia.entity.ImageDimensions;
import com.example.msmedia.entity.Media;
import com.example.msmedia.entity.ParsedPath;
import com.example.msmedia.exception.MediaStorageException;
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
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Iterator;

import static com.example.msmedia.util.DocumentUtil.parsePath;

@Slf4j
@Component
@RequiredArgsConstructor
public class MediaConsumer {

    private final MediaRepository mediaRepository;
    private final S3Client s3Client;

    @Transactional
    @KafkaListener(topics = "seaweedfs_events", groupId = "ms-media-group")
    public void consumeMediaEvent(ConsumerRecord<String, byte[]> record) {
        String fullPath = record.key(); // get bucket full path

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

            Media media = mediaRepository.findByObjectKey(parsedPath.getObjectKey())
                    .orElse(null);

            if (media == null) {
                log.warn("Media not found in the database: {}", parsedPath.getObjectKey());
                return;
            }

            ImageDimensions dimensions = extractDimensions(parsedPath.getBucket(), parsedPath.getObjectKey());

            media.setFileSize(fileSize);
            media.setWidth(dimensions.getWidth());
            media.setHeight(dimensions.getHeight());
            media.setIsActive(true); // status is active

            mediaRepository.save(media);
            log.info("Media activated successfully: id={}, key={}", media.getId(), media.getObjectKey());
        } catch (Exception e) {
            log.error("An error occurred while processing the SeaweedFS event: path={}, error={}", fullPath, e.getMessage(), e);
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

        } catch (S3Exception e) {
            throw new MediaStorageException(
                    "Failed to retrieve media from storage: " + objectKey);
        } catch (IOException e) {
            log.warn(
                    "Failed to extract image dimensions (invalid file format or I/O error): {}",
                    objectKey,
                    e
            );
        }

        return new ImageDimensions(null, null);
    }
}