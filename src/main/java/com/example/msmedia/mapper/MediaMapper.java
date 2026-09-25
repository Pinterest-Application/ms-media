package com.example.msmedia.mapper;

import com.example.msmedia.dto.MediaCreateRequest;
import com.example.msmedia.dto.MediaResponse;
import com.example.msmedia.entity.Media;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MediaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Media toEntity(MediaCreateRequest request);

    MediaResponse toResponse(Media media);
}