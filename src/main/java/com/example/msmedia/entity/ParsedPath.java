package com.example.msmedia.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedPath {
    private String bucket;
    private String objectKey;
}
