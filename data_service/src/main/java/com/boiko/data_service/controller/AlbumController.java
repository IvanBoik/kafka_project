package com.boiko.data_service.controller;

import com.boiko.data_service.model.Album;
import com.boiko.data_service.service.AlbumService;
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
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {
    private static final Logger logger = LogManager.getLogger(AlbumController.class);
    private final AlbumService albumService;

    @PostMapping(value = "/upload/at_date", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadAlbumAtDate(
            @RequestParam("authorID") Long[] ids,
            @RequestParam("name") String name,
            @RequestParam("songs") MultipartFile[] audios,
            @RequestParam("picture") MultipartFile picture,
            @RequestParam("dateOfPublication") LocalDateTime dateOfPublication
    ) {
        try {
            Album album = albumService.uploadAlbumAtDate(ids, name, audios, picture, dateOfPublication);
            String message = "Song %s with id = %d will be published at %s".formatted(
                    album.getName(), album.getId(), album.getTimestampAdded()
            );
            logger.info(message);
            return ResponseEntity.ok().body(message);
        }
        catch (IOException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (UnsupportedAudioFileException e) {
            logger.error(e);
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(e.getMessage());
        }
    }

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadAlbum(
            @RequestParam("authorID") Long[] ids,
            @RequestParam("name") String name,
            @RequestParam("songs") MultipartFile[] audios,
            @RequestParam("picture") MultipartFile picture
    ) {
        try {
            Album album = albumService.uploadAlbum(ids, name, audios, picture);
            String message = "Song %s with id = %d published".formatted(
                    album.getName(), album.getId()
            );
            logger.info(message);
            return ResponseEntity.ok().body(message);
        }
        catch (IOException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (UnsupportedAudioFileException e) {
            logger.error(e);
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(e.getMessage());
        }
    }

    @GetMapping(value = "/by_likes")
    public ResponseEntity<?> getTopAlbumsByLikes(
            @RequestParam(value = "page_size", defaultValue = "10") int pageSize,
            @RequestParam("page_number") int pageNumber
    ) {
        try {
            return ResponseEntity.ok(albumService.getTopAlbumsByLikes(pageSize, pageNumber));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/by_auditions")
    public ResponseEntity<?> getTopAlbumsByAuditions(
            @RequestParam(value = "page_size", defaultValue = "10") int pageSize,
            @RequestParam("page_number") int pageNumber
    ) {
        try {
            return ResponseEntity.ok(albumService.getTopAlbumsByAuditions(pageSize, pageNumber));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{albumID}/auditions")
    public ResponseEntity<?> getAlbumAuditions(@PathVariable Long albumID) {
        try {
            return ResponseEntity.ok(albumService.getAlbumAuditions(albumID));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
