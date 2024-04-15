package com.boiko.data_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "files")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    @Column(name = "s3_key")
    private String S3Key;
    private String url;
}
