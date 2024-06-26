package com.boiko.data_service.controller;

import com.boiko.data_service.dto.UserDTO;
import com.boiko.data_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findByID(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.findUserDTOByID(id));
        }
        catch (RuntimeException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserDTO userDTO) {
        try {
            Long id = userService.signUp(userDTO);
            logger.info("User with id = %d was registered".formatted(id));
            return ResponseEntity.ok(id);
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/login")
    public ResponseEntity<?> logIn(
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
        try {
            return ResponseEntity.ok(userService.logIn(email, password));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/like/song")
    public ResponseEntity<?> toggleLikeSong(
            @RequestParam("userID") Long userID,
            @RequestParam("songID") Long songID
    ) {
        try {
            return ResponseEntity.ok(userService.toggleLikeSong(userID, songID));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/like/album")
    public ResponseEntity<?> toggleLikeAlbum(
            @RequestParam("userID") Long userID,
            @RequestParam("albumID") Long albumID
    ) {
        try {
            return ResponseEntity.ok(userService.toggleLikeAlbum(userID, albumID));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/like/author")
    public ResponseEntity<?> toggleLikeAuthor(
            @RequestParam("userID") Long userID,
            @RequestParam("authorID") Long authorID
    ) {
        try {
            return ResponseEntity.ok(userService.toggleLikeAuthor(userID, authorID));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
