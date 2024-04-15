package com.boiko.data_service.repository;

import com.boiko.data_service.model.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query(value = "select * from albums where is_published = true",
    countQuery = "select count(*) from albums where is_published = true",
    nativeQuery = true)
    Page<Album> findAllPublished(Pageable pageable);

    Optional<Album> findPublishedById(Long id);
}
