package com.example.msmedia.util;

import com.example.libexception.exception.InternalServerErrorException;
import com.example.msmedia.entity.ParsedPath;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

public class DocumentUtil {

    private DocumentUtil(){}


    public static String generateObjectKey(String userId, String extension) {
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

    public static ParsedPath parsePath(String fullPath) {
        String[] parts = fullPath.split("/", 4);
        if (parts.length < 4) {
            return null;
        }
        return new ParsedPath(parts[2], parts[3]);
    }

    private static String hashSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerErrorException("SHA-256 algorithm unavailable");
        }
    }
}
