package com.boiko.data_service.service;

import com.boiko.data_service.model.Author;
import com.boiko.data_service.model.FileInfo;
import com.boiko.data_service.model.Song;
import com.boiko.data_service.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SongService {
    private static final Logger logger = LogManager.getLogger(SongService.class);
    private final SongRepository songRepository;
    private final FileInfoService fileInfoService;
    private final AuthorService authorService;
    private final Timer timer = new Timer("songTimer");

    public Page<Song> getTopSongs(int pageSize, int pageNumber) {
        Pageable pageParams = PageRequest.of(pageNumber, pageSize, Sort.by("likes").descending());
        return songRepository.findAllPublished(pageParams);
    }

    public Song uploadSong(Long[] authorsIDs, MultipartFile audio, MultipartFile picture, LocalDateTime dateOfPublication)
            throws IOException, UnsupportedAudioFileException {
        FileInfo audioInfo = fileInfoService.upload("audio", audio);
        FileInfo pictureInfo = fileInfoService.upload("picture", picture);
        long duration = calculateDuration(audio);
        String name = FilenameUtils.removeExtension(audio.getOriginalFilename());
        List<Author> authors = authorService.findAllByID(authorsIDs);
        Song song = saveSong(audioInfo, pictureInfo, duration, name, authors, dateOfPublication);

        Date convertedDate = Date.from(
                dateOfPublication.atZone(ZoneId.systemDefault()).toInstant()
        );

        timer.schedule(new TimerTask() {
            public void run() {
                Song publishedSong = publicationSong(song);

                logger.info("Song %s (id = \"%d\") published at %s".formatted(
                        publishedSong.getName(),
                        publishedSong.getId(),
                        dateOfPublication
                ));
            }
        }, convertedDate);

        return song;
    }

    private long calculateDuration(MultipartFile file) throws IOException, UnsupportedAudioFileException {
        InputStream stream = file.getInputStream();
        InputStream bufferedIn = new BufferedInputStream(stream);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        return (long) (frames / format.getFrameRate());
    }

    private Song publicationSong(Song song) {
        song.setPublished(true);
        return songRepository.save(song);
    }

    public List<Song> publicationSongs(List<Long> ids) {
        return songRepository.findAllById(ids).stream()
                .map(this::publicationSong)
                .toList();
    }

    private Song saveSong(
            FileInfo audio,
            FileInfo picture,
            long duration,
            String name,
            List<Author> authors,
            LocalDateTime dateTimeAdded
    ) {
        Song song = Song.builder()
                .authors(authors)
                .name(name)
                .duration(duration)
                .audio(audio)
                .picture(picture)
                .dateAdded(dateTimeAdded.toLocalDate())
                .timeAdded(dateTimeAdded.toLocalTime())
                .isPublished(false)
                .build();

        return songRepository.save(song);
    }

    public List<Song> uploadAlbumSongs(List<Author> authors, MultipartFile[] audios, MultipartFile picture)
            throws IOException, UnsupportedAudioFileException {
        List<Song> songs = new ArrayList<>();
        FileInfo pictureInfo = fileInfoService.upload("picture", picture);
        LocalDateTime dateTimeAdded = LocalDateTime.now();

        for (MultipartFile audio : audios) {
            FileInfo audioInfo = fileInfoService.upload("audio", audio);
            long duration = calculateDuration(audio);
            String name = FilenameUtils.removeExtension(audio.getOriginalFilename());
            songs.add(
                    saveSong(audioInfo, pictureInfo, duration, name, authors, dateTimeAdded)
            );
        }
        return songs;
    }
}
