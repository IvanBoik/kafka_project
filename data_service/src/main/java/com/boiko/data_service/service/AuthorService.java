package com.boiko.data_service.service;

import com.boiko.data_service.model.Author;
import com.boiko.data_service.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;

    public Author findByID(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author with id = %d doesn't exist".formatted(id)));
    }

    public List<Author> findAllByID(Long... ids) {
        return authorRepository.findAllById(Arrays.asList(ids));
    }
}
