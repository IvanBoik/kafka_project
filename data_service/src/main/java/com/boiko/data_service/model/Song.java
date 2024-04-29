package com.boiko.data_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "songs")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "song_authors",
            joinColumns = { @JoinColumn(name = "id_song") },
            inverseJoinColumns = { @JoinColumn(name = "id_author") })
    private List<Author> authors;

    @OneToOne
    @JoinColumn(name = "id_audio")
    private FileInfo audio;

    @OneToOne
    @JoinColumn(name = "id_picture")
    private FileInfo picture;

    private String name;
    private long duration;
    private Timestamp timestampAdded;
    private long likes;
    private boolean isPublished;
    private long auditions;

    public String toString() {
        String res = "{\n";
        res += "\tid = %d,\n".formatted(id);
        res += "\taudioID = %d,\n".formatted(audio.getId());
        res += "\tpictureID = %d,\n".formatted(picture.getId());
        res += "\tname = %s,\n".formatted(name);
        res += "\tduration = %d,\n".formatted(duration);
        res += "\tauditions = %d,\n".formatted(likes);
        res += "\ttimestampAdded = %s,\n".formatted(timestampAdded);
        return res;
    }
}
