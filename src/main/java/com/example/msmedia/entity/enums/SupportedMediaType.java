package com.example.msmedia.entity.enums;

import com.example.msmedia.exception.UnsupportedFileFormatException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SupportedMediaType {
    JPEG("image/jpeg", ".jpg"),
    PNG("image/png", ".png"),
    WEBP("image/webp", ".webp"),
    GIF("image/gif", ".gif");

    private final String mimeType;
    private final String extension;

    public static SupportedMediaType fromMimeType(String mimeType) {
        return Arrays.stream(values())
                .filter(type -> type.mimeType.equalsIgnoreCase(mimeType))
                .findFirst()
                .orElseThrow(UnsupportedFileFormatException::new);
    }
}