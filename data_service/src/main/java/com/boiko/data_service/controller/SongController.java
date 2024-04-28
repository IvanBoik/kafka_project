package com.boiko.data_service.controller;

import com.boiko.data_service.model.Song;
import com.boiko.data_service.service.SongService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/songs")
public class SongController {
    private static final Logger logger = LogManager.getLogger(SongController.class);
    private final SongService songService;

    @PostMapping(value = "/upload/at_date", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadSongAtDate(
            @RequestParam("authorIDs") Long[] ids,
            @RequestParam("audio") MultipartFile audio,
            @RequestParam("picture") MultipartFile picture,
            @RequestParam("dateOfPublication")LocalDateTime dateOfPublication
            ) {
        try {
            Song song = songService.uploadSongAtDate(ids, audio, picture, dateOfPublication);
            String message = "Song %s will be published at %s".formatted(
                    song.getName(), dateOfPublication
            );
            logger.info(message);
            return ResponseEntity.ok(message);
        }
        catch (UnsupportedAudioFileException e) {
            logger.error(e);
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(e.getMessage());
        }
        catch (IOException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadSong(
            @RequestParam("authorIDs") Long[] ids,
            @RequestParam("audio") MultipartFile audio,
            @RequestParam("picture") MultipartFile picture
    ) {
        try {
            Song song = songService.uploadSong(ids, audio, picture);
            String message = "Song %s with id = %d published".formatted(
                    song.getName(), song.getId()
            );
            logger.info(message);
            return ResponseEntity.ok(message);
        }
        catch (UnsupportedAudioFileException e) {
            logger.error(e);
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(e.getMessage());
        }
        catch (IOException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> topSongs(
            @RequestParam(value = "page_size", defaultValue = "10") int pageSize,
            @RequestParam("page_number") int pageNumber
    ) {
        try {
            return ResponseEntity.ok(songService.getTopSongs(pageSize, pageNumber));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
