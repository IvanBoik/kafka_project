package com.boiko.data_service.service;

import com.boiko.data_service.model.Album;
import com.boiko.data_service.model.Author;
import com.boiko.data_service.model.Song;
import com.boiko.data_service.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private static final Logger logger = LogManager.getLogger(AlbumService.class);
    private final AlbumRepository albumRepository;
    private final AuthorService authorService;
    private final SongService songService;
    private final Timer timer = new Timer("albumTimer");

    public Page<Album> getTopAlbums(int pageSize, int pageNumber) {
        Pageable pageParams = PageRequest.of(pageNumber, pageSize, Sort.by("likes").descending());
        return albumRepository.findAllPublished(pageParams);
    }

    public void uploadAlbumAtDate(
            Long[] authorsIDs,
            String name,
            MultipartFile[] audios,
            MultipartFile picture,
            LocalDateTime dateOfPublication
    ) throws UnsupportedAudioFileException, IOException {

        Date convertedDate = Date.from(
                dateOfPublication.atZone(ZoneId.systemDefault()).toInstant()
        );

        List<Author> authors = authorService.findAllByID(authorsIDs);
        List<Song> songs = songService.uploadAlbumSongs(authors, audios, picture);
        Album album = saveAlbum(authors, name, songs, dateOfPublication);

        timer.schedule(new TimerTask() {
            public void run() {
                Album publishedAlbum = publicationAlbum(album);

                logger.info("Album %s (id = \"%d\") published at %s".formatted(
                        publishedAlbum.getName(),
                        publishedAlbum.getId(),
                        dateOfPublication
                ));
            }
        }, convertedDate);
    }

    private Album saveAlbum(
            List<Author> authors,
            String name,
            List<Song> songs,
            LocalDateTime dateOfPublication
    ) {
        Album album = Album.builder()
                .authors(authors)
                .songs(songs)
                .name(name)
                .picture(songs.get(0).getPicture())
                .dateAdded(dateOfPublication.toLocalDate())
                .timeAdded(dateOfPublication.toLocalTime())
                .isPublished(false)
                .build();
        return albumRepository.save(album);
    }

    private Album publicationAlbum(Album album) {
        List<Long> tempsIDs = album
                .getSongs()
                .stream()
                .map(Song::getId)
                .toList();
        List<Song> songs = songService.publicationSongs(tempsIDs);

        album.setSongs(songs);
        album.setPublished(true);
        return albumRepository.save(album);
    }

    public Album findByID(Long albumID) {
        return albumRepository.findPublishedById(albumID)
                .orElseThrow(() -> new RuntimeException("Album with id = %d doesn't exists".formatted(albumID)));
    }
}
