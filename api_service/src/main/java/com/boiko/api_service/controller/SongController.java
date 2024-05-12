package com.boiko.api_service.controller;

import com.boiko.api_service.aop.annotations.ValidTypes;
import com.boiko.api_service.aop.annotations.ValidateMultipartFiles;
import com.boiko.api_service.service.SongService;
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
            songService.uploadSongAtDate(ids, audio, picture, dateOfPublication);
            return ResponseEntity.ok("OK");
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
            songService.uploadSong(ids, audio, picture);
            return ResponseEntity.ok("OK");
        }
        catch (IOException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/by_likes")
    public ResponseEntity<?> getTopSongsByLikes(
            @RequestParam(value = "page_size", defaultValue = "10") int pageSize,
            @RequestParam("page_number") int pageNumber
    ) {
        try {
            return ResponseEntity.ok(songService.getTopSongsByLikes(pageSize, pageNumber));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/by_auditions")
    public ResponseEntity<?> getTopSongsByAuditions(
            @RequestParam(value = "page_size", defaultValue = "10") int pageSize,
            @RequestParam("page_number") int pageNumber
    ) {
        try {
            return ResponseEntity.ok(songService.getTopSongsByAuditions(pageSize, pageNumber));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{songID}/auditions/increment")
    public ResponseEntity<?> incrementSongAuditions(@PathVariable Long songID) {
        try {
            songService.incrementSongAuditions(songID);
            return ResponseEntity.ok("OK");
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{songID}/auditions")
    public ResponseEntity<?> getSongAuditions(@PathVariable Long songID) {
        try {
            return ResponseEntity.ok(songService.getSongAuditions(songID));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
