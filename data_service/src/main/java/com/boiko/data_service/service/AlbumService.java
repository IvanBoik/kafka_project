package com.boiko.data_service.service;

import com.boiko.data_service.dto.AlbumDTO;
import com.boiko.data_service.dto.UploadAlbumDTO;
import com.boiko.data_service.mapper.AlbumMapper;
import com.boiko.data_service.model.Album;
import com.boiko.data_service.model.Author;
import com.boiko.data_service.model.FileInfo;
import com.boiko.data_service.model.Song;
import com.boiko.data_service.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.sql.Timestamp;
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
    private final FileInfoService fileInfoService;
    private final AlbumMapper albumMapper;
    private final Timer timer = new Timer("albumTimer");

    public List<AlbumDTO> getTopAlbumsByLikes(int pageSize, int pageNumber) {
        Pageable pageParams = PageRequest.of(pageNumber, pageSize, Sort.by("likes").descending());
        return albumRepository
                .findAllPublished(pageParams)
                .map(albumMapper::modelToDTO)
                .toList();
    }

    public List<AlbumDTO> getTopAlbumsByAuditions(int pageSize, int pageNumber) {
        Pageable pageParams = PageRequest.of(pageNumber, pageSize, Sort.by("auditions").descending());
        return albumRepository
                .findAllPublished(pageParams)
                .map(albumMapper::modelToDTO)
                .toList();
    }

    public Album uploadAlbumAtDate(
            Long[] authorsIDs,
            String name,
            MultipartFile[] audios,
            MultipartFile picture,
            LocalDateTime dateOfPublication
    ) throws UnsupportedAudioFileException, IOException {

        List<Author> authors = authorService.findAllByID(authorsIDs);
        List<Song> songs = songService.uploadAlbumSongs(authors, audios, picture, dateOfPublication, false);
        Album album = saveAlbum(authors, name, songs, dateOfPublication);

        return makeTask(album, dateOfPublication);
    }

    private Album makeTask(Album album, LocalDateTime dateTime) {
        Date convertedDate = Date.from(
                dateTime.atZone(ZoneId.systemDefault()).toInstant()
        );

        timer.schedule(new TimerTask() {
            public void run() {
                Album publishedAlbum = publicationAlbum(album);

                logger.info("Album %s (id = \"%d\") published".formatted(
                        publishedAlbum.getName(),
                        publishedAlbum.getId()
                ));
            }
        }, convertedDate);

        return album;
    }

    public Album uploadAlbum(Long[] authorsIDs, String name, MultipartFile[] audios, MultipartFile picture)
            throws UnsupportedAudioFileException, IOException {
        List<Author> authors = authorService.findAllByID(authorsIDs);
        LocalDateTime dateOfPublication = LocalDateTime.now();
        List<Song> songs = songService.uploadAlbumSongs(authors, audios, picture, dateOfPublication, true);

        Album album = Album.builder()
                .name(name)
                .songs(songs)
                .picture(songs.get(0).getPicture())
                .authors(authors)
                .isPublished(true)
                .timestampAdded(Timestamp.valueOf(dateOfPublication))
                .build();
        return albumRepository.save(album);
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
                .timestampAdded(Timestamp.valueOf(dateOfPublication))
                .isPublished(false)
                .build();
        return albumRepository.save(album);
    }

    private Album publicationAlbum(Album album) {
        List<Long> songsIDs = album
                .getSongs()
                .stream()
                .map(Song::getId)
                .toList();
        List<Song> songs = songService.publicationSongs(songsIDs);

        album.setSongs(songs);
        album.setPublished(true);
        return albumRepository.save(album);
    }

    public Album findByID(Long albumID) {
        return albumRepository.findPublishedById(albumID)
                .orElseThrow(() -> new RuntimeException("Album with id = %d doesn't exists".formatted(albumID)));
    }

    public void uploadAlbum(UploadAlbumDTO albumDTO) throws UnsupportedAudioFileException, IOException {
        List<Author> authors = authorService.findAllByID(albumDTO.authorsIDs());
        LocalDateTime dateOfPublication = LocalDateTime.now();
        FileInfo pictureInfo = fileInfoService.upload("picture", albumDTO.picture(), albumDTO.pictureType());
        List<Song> songs = songService.uploadAlbumSongs(
                albumDTO.songs(), pictureInfo, authors, dateOfPublication, true
        );

        Album album = Album.builder()
                .name(albumDTO.name())
                .songs(songs)
                .picture(pictureInfo)
                .authors(authors)
                .isPublished(true)
                .timestampAdded(Timestamp.valueOf(dateOfPublication))
                .build();
        albumRepository.save(album);
    }

    public void uploadAlbumAtDate(UploadAlbumDTO albumDTO) throws UnsupportedAudioFileException, IOException {
        assert albumDTO.dateOfPublication() != null;

        List<Author> authors = authorService.findAllByID(albumDTO.authorsIDs());
        FileInfo pictureInfo = fileInfoService.upload("picture", albumDTO.picture(), albumDTO.pictureType());
        List<Song> songs = songService.uploadAlbumSongs(
                albumDTO.songs(), pictureInfo, authors, albumDTO.dateOfPublication(), false
        );
        Album album = saveAlbum(authors, albumDTO.name(), songs, albumDTO.dateOfPublication());

        makeTask(album, albumDTO.dateOfPublication());
    }

    public Long getAlbumAuditions(Long albumID) {
        return findByID(albumID).getAuditions();
    }

    public void incrementAuditions(Long songID) {
        albumRepository.incrementAuditions(songID);
    }
}
