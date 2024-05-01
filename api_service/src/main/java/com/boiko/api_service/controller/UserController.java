package com.boiko.api_service.controller;


import com.boiko.api_service.dto.BecomeAuthorRequest;
import com.boiko.api_service.dto.UserDTO;
import com.boiko.api_service.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
            return ResponseEntity.ok(userService.findUserByID(id));
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

    @PostMapping("/login")
    public ResponseEntity<?> logIn(@RequestParam String email, @RequestParam String password) {
        try {
            return ResponseEntity.ok(userService.logIn(email, password));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/become_author")
    public ResponseEntity<?> becomeAuthor(@RequestBody BecomeAuthorRequest request) {
        try {
            userService.becomeAuthor(request);
            return ResponseEntity.ok("OK");
        }
        catch (JsonProcessingException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/like/song")
    public ResponseEntity<?> toggleLikeSong(@RequestParam Long userID, @RequestParam Long songID) {
        try {
            return ResponseEntity.ok(userService.toggleLikeSong(userID, songID));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/like/album")
    public ResponseEntity<?> toggleLikeAlbum(@RequestParam Long userID, @RequestParam Long albumID) {
        try {
            return ResponseEntity.ok(userService.toggleLikeAlbum(userID, albumID));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/like/author")
    public ResponseEntity<?> toggleLikeAuthor(@RequestParam Long userID, @RequestParam Long authorID) {
        try {
            return ResponseEntity.ok(userService.toggleLikeAuthor(userID, authorID));
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
