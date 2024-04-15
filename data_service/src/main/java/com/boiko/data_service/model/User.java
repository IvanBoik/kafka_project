package com.boiko.data_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


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

    private String email;
    private String password;
    private String nickname;
    private LocalDate birthday;
    private LocalDate dateRegistered;
    private LocalTime timeRegistered;
}
