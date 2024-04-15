package com.boiko.api_service.controller;

import com.boiko.api_service.aop.annotations.ValidTypes;
import com.boiko.api_service.aop.annotations.ValidateMultipartFiles;
import com.boiko.api_service.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;

    @GetMapping("/")
    public ResponseEntity<?> getTopSongs(
            @RequestParam(value = "page_size", defaultValue = "10") int pageSize,
            @RequestParam("page_number") int pageNumber
    ) {
        try {
            List<?> songs = songService.getTopSongs(pageSize, pageNumber);
            return ResponseEntity.ok(songs);
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ValidateMultipartFiles
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadSong(
            @RequestParam("authorIDs") Long[] ids,
            @RequestParam("audio") @ValidTypes({"wav", "wave"}) MultipartFile audio,
            @RequestParam("picture") @ValidTypes({"png", "jpg", "jpeg", "webp"}) MultipartFile picture,
            @RequestParam("dateOfPublication") LocalDateTime dateOfPublication
    ) {
        try {
            return songService.uploadSong(ids, audio, picture, dateOfPublication);
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
