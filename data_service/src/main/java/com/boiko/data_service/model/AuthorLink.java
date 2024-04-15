package com.boiko.data_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "author_profile_links")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_author")
    private Long authorID;
    private String url;
    private String serviceName;
}
