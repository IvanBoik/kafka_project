package com.boiko.data_service.controller;

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
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {
    private static final Logger logger = LogManager.getLogger(AlbumController.class);
    private final AlbumService albumService;

    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadAlbum(
            @RequestParam("authorID") Long[] ids,
            @RequestParam("name") String name,
            @RequestParam("songs") MultipartFile[] audios,
            @RequestParam("picture") MultipartFile picture,
            @RequestParam("dateOfPublication") LocalDateTime dateOfPublication
    ) {
        try {
            albumService.uploadAlbumAtDate(ids, name, audios, picture, dateOfPublication);
            logger.info("Album with name = \"%s\" and authors ids = %s will be published at %s".formatted(
                    name, Arrays.toString(ids), dateOfPublication
            ));
            return ResponseEntity.ok().body("Album uploaded");
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

    @GetMapping("/")
    public ResponseEntity<?> topAlbums(
            @RequestParam(value = "page_size", defaultValue = "10") int pageSize,
            @RequestParam("page_number") int pageNumber
    ) {
        try {
            return ResponseEntity.ok(albumService.getTopAlbums(pageSize, pageNumber));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
