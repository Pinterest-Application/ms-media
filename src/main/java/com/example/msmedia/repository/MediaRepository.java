package com.example.msmedia.repository;

import com.example.msmedia.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {

    boolean existsByObjectKey(String objectKey);

    Optional<Media> findByIdAndIsActiveTrue(UUID id);
}