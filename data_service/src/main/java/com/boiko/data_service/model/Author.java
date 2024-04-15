package com.boiko.data_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "authors")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String biography;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private User userdata;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_author", referencedColumnName = "id")
    private List<AuthorLink> links;

    private long likes;

    public String getNickname() {
        return userdata.getNickname();
    }

    public String getEmail() {
        return userdata.getEmail();
    }

    public String getPassword() {
        return userdata.getPassword();
    }

    public LocalDate getBirthday() {
        return userdata.getBirthday();
    }

    public FileInfo getAvatar() {
        return userdata.getAvatar();
    }
}
