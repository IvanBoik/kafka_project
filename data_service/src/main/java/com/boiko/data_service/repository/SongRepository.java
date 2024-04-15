package com.boiko.data_service.repository;

import com.boiko.data_service.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SongRepository extends JpaRepository<Song, Long> {
    @Query(value = "select * from songs where is_published = true",
    countQuery = "select count(*) from songs where is_published = true",
    nativeQuery = true)
    Page<Song> findAllPublished(Pageable pageable);
}
