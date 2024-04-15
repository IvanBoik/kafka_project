package com.boiko.data_service.repository;

import com.boiko.data_service.model.AuthorLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorLinkRepository extends JpaRepository<AuthorLink, Long> {
}
