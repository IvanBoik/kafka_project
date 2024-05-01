package com.boiko.data_service.repository;

import com.boiko.data_service.model.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query(value = "select * from albums where is_published = true",
    countQuery = "select count(*) from albums where is_published = true",
    nativeQuery = true)
    Page<Album> findAllPublished(Pageable pageable);

    Optional<Album> findPublishedById(Long id);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
    update albums a
    set auditions=a.auditions+1
    from songs_in_albums sia
    join songs s on s.id = sia.id_song
    where s.id=?1 and a.id=sia.id_album
    """)
    void incrementAuditions(Long songID);
}
