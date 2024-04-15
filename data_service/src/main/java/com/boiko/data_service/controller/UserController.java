package com.boiko.data_service.controller;

import com.boiko.data_service.dto.BecomeAuthorRequest;
import com.boiko.data_service.dto.UserDTO;
import com.boiko.data_service.model.Author;
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
            return ResponseEntity.ok(userService.findByID(id));
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

    @PostMapping("/{id}/become_author")
    public ResponseEntity<?> becomeAuthor(@PathVariable Long id, @RequestBody BecomeAuthorRequest request) {
        try {
            Author author = userService.becomeAuthor(id, request);
            logger.info("User %s become an author".formatted(author.getNickname()));
            return ResponseEntity.ok(author.getId());
        }
        catch (RuntimeException e) {
            logger.error(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
