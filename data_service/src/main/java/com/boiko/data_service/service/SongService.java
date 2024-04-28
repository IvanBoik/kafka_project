package com.boiko.data_service.service;

import com.boiko.data_service.dto.SongDTO;
import com.boiko.data_service.dto.SongInAlbumDTO;
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
import java.io.ByteArrayInputStream;
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

    public Song uploadSongAtDate(Long[] authorsIDs, MultipartFile audio, MultipartFile picture, LocalDateTime dateOfPublication)
            throws IOException, UnsupportedAudioFileException {
        FileInfo audioInfo = fileInfoService.upload("audio", audio);
        FileInfo pictureInfo = fileInfoService.upload("picture", picture);
        long duration = calculateDuration(audio);
        String name = FilenameUtils.removeExtension(audio.getOriginalFilename());
        List<Author> authors = authorService.findAllByID(authorsIDs);
        Song song = saveSong(audioInfo, pictureInfo, duration, name, authors, dateOfPublication, false);

        return makeTask(song, dateOfPublication);
    }

    public void uploadSongAtDate(SongDTO songDTO)
            throws IOException, UnsupportedAudioFileException {
        assert songDTO.dateOfPublication() != null;

        FileInfo audioInfo = fileInfoService.upload("audio", songDTO.audio(), songDTO.audioType());
        FileInfo pictureInfo = fileInfoService.upload("picture", songDTO.picture(), songDTO.pictureType());
        long duration = calculateDuration(songDTO.audio());
        List<Author> authors = authorService.findAllByID(songDTO.authorsIDs());
        LocalDateTime dateOfPublication = songDTO.dateOfPublication();
        Song song = saveSong(audioInfo, pictureInfo, duration, songDTO.name(), authors, dateOfPublication, false);

        makeTask(song, dateOfPublication);
    }

    private Song makeTask(Song song, LocalDateTime dateTime) {
        Date convertedDate = Date.from(
                dateTime.atZone(ZoneId.systemDefault()).toInstant()
        );

        timer.schedule(new TimerTask() {
            public void run() {
                Song publishedSong = publicationSong(song);
                logger.info("Song %s (id = \"%d\") published".formatted(
                        publishedSong.getName(), publishedSong.getId()
                ));
            }
        }, convertedDate);

        return song;
    }

    public Song uploadSong(Long[] authorsIDs, MultipartFile audio, MultipartFile picture)
            throws IOException, UnsupportedAudioFileException {
        FileInfo audioInfo = fileInfoService.upload("audio", audio);
        FileInfo pictureInfo = fileInfoService.upload("picture", picture);
        long duration = calculateDuration(audio);
        String name = FilenameUtils.removeExtension(audio.getOriginalFilename());
        List<Author> authors = authorService.findAllByID(authorsIDs);
        LocalDateTime dateTimeAdded = LocalDateTime.now();

        Song song = Song.builder()
                .audio(audioInfo)
                .picture(pictureInfo)
                .duration(duration)
                .name(name)
                .authors(authors)
                .isPublished(true)
                .dateAdded(dateTimeAdded.toLocalDate())
                .timeAdded(dateTimeAdded.toLocalTime())
                .build();
        return songRepository.save(song);
    }

    public void uploadSong(SongDTO songDTO) throws UnsupportedAudioFileException, IOException {
        FileInfo audioInfo = fileInfoService.upload("audio", songDTO.audio(), songDTO.audioType());
        FileInfo pictureInfo = fileInfoService.upload("picture", songDTO.picture(), songDTO.pictureType());
        long duration = calculateDuration(songDTO.audio());
        List<Author> authors = authorService.findAllByID(songDTO.authorsIDs());
        LocalDateTime dateTimeAdded = LocalDateTime.now();

        Song song = Song.builder()
                .audio(audioInfo)
                .picture(pictureInfo)
                .duration(duration)
                .name(songDTO.name())
                .authors(authors)
                .isPublished(true)
                .dateAdded(dateTimeAdded.toLocalDate())
                .timeAdded(dateTimeAdded.toLocalTime())
                .build();
        songRepository.save(song);
    }

    private long calculateDuration(MultipartFile file) throws IOException, UnsupportedAudioFileException {
        InputStream stream = file.getInputStream();
        InputStream bufferedIn = new BufferedInputStream(stream);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        return (long) (frames / format.getFrameRate());
    }

    private long calculateDuration(byte[] data) throws IOException, UnsupportedAudioFileException {
        InputStream stream = new ByteArrayInputStream(data);
        InputStream bufferedIn = new BufferedInputStream(stream);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        return (long) (frames / format.getFrameRate());
    }

    public List<Song> publicationSongs(List<Long> ids) {
        return songRepository.findAllById(ids).stream()
                .map(this::publicationSong)
                .toList();
    }

    private Song publicationSong(Song song) {
        song.setPublished(true);
        return songRepository.save(song);
    }

    private Song saveSong(
            FileInfo audio,
            FileInfo picture,
            long duration,
            String name,
            List<Author> authors,
            LocalDateTime dateTimeAdded,
            boolean isPublished
    ) {
        Song song = Song.builder()
                .authors(authors)
                .name(name)
                .duration(duration)
                .audio(audio)
                .picture(picture)
                .dateAdded(dateTimeAdded.toLocalDate())
                .timeAdded(dateTimeAdded.toLocalTime())
                .isPublished(isPublished)
                .build();

        return songRepository.save(song);
    }

    public List<Song> uploadAlbumSongs(
            List<Author> authors,
            MultipartFile[] audios,
            MultipartFile picture,
            LocalDateTime dateOfPublication,
            boolean isPublished
    ) throws IOException, UnsupportedAudioFileException {
        List<Song> songs = new ArrayList<>();
        FileInfo pictureInfo = fileInfoService.upload("picture", picture);

        for (MultipartFile audio : audios) {
            FileInfo audioInfo = fileInfoService.upload("audio", audio);
            long duration = calculateDuration(audio);
            String name = FilenameUtils.removeExtension(audio.getOriginalFilename());
            songs.add(
                    saveSong(audioInfo, pictureInfo, duration, name, authors, dateOfPublication, isPublished)
            );
        }
        return songs;
    }

    public Song findByID(Long songID) {
        return songRepository.findPublishedById(songID)
                .orElseThrow(() -> new RuntimeException("Song with id = %d doesn't exists".formatted(songID)));
    }

    public List<Song> uploadAlbumSongs(
            List<SongInAlbumDTO> songsDTOs,
            FileInfo pictureInfo,
            List<Author> albumAuthors,
            LocalDateTime dateOfPublication,
            boolean isPublished
    ) throws UnsupportedAudioFileException, IOException {

        List<Song> songs = new ArrayList<>();
        for (SongInAlbumDTO dto : songsDTOs) {
            List<Author> authors = albumAuthors;
            if (albumAuthors.size() != dto.authorsIDs().length) {
                authors = authorService.findAllByID(dto.authorsIDs());
            }
            FileInfo audioInfo = fileInfoService.upload("audio", dto.audio(), dto.audioType());
            long duration = calculateDuration(dto.audio());
            songs.add(
                    saveSong(audioInfo, pictureInfo, duration, dto.name(), authors, dateOfPublication, isPublished)
            );
        }
        return songs;
    }
}
