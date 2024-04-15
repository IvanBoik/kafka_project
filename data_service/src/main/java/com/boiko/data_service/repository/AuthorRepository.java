package com.boiko.data_service.repository;

import com.boiko.data_service.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
