package com.boiko.api_service.controller;

import com.boiko.api_service.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam("authorIDs") Long[] ids,
            @RequestParam("name") String name,
            @RequestParam("audios") MultipartFile[] audios,
            @RequestParam("songsAuthorsIDs") Long[][] songsAuthorsIDs,
            @RequestParam("picture") MultipartFile picture,
            @RequestParam("dateOfPublication") LocalDateTime dateOfPublication
    ) {
        try {
            albumService.uploadAlbumAtDate(ids, songsAuthorsIDs, name, audios, picture, dateOfPublication);
            return ResponseEntity.ok().body("OK");
        }
        catch (IOException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadAlbum(
            @RequestParam("authorIDs") Long[] ids,
            @RequestParam("name") String name,
            @RequestParam("audios") MultipartFile[] audios,
            @RequestParam("songsAuthorsIDs") Long[][] songsAuthorsIDs,
            @RequestParam("picture") MultipartFile picture
    ) {
        try {
            albumService.uploadAlbum(ids, songsAuthorsIDs, name, audios, picture);
            return ResponseEntity.ok().body("OK");
        }
        catch (IOException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
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
