package com.boiko.data_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "avatar", referencedColumnName = "id")
    private FileInfo avatar;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "liked_songs",
            joinColumns = { @JoinColumn(name = "id_user") },
            inverseJoinColumns = { @JoinColumn(name = "id_song") })
    private List<Song> likedSongs;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "liked_albums",
            joinColumns = { @JoinColumn(name = "id_user") },
            inverseJoinColumns = { @JoinColumn(name = "id_album") })
    private List<Album> likedAlbums;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "liked_authors",
            joinColumns = { @JoinColumn(name = "id_user") },
            inverseJoinColumns = { @JoinColumn(name = "id_author") })
    private List<Author> likedAuthors;

    private String email;
    private String password;
    private String nickname;
    private LocalDate birthday;
    private Timestamp timestampRegistered;
}
