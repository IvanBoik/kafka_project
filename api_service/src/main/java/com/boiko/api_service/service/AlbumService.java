package com.boiko.api_service.service;

import com.boiko.api_service.dto.AlbumDTO;
import com.boiko.api_service.dto.SongInUploadAlbumDTO;
import com.boiko.api_service.dto.UploadAlbumDTO;
import com.boiko.api_service.producer.AlbumProducer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final RestTemplate restTemplate;
    private final AlbumProducer albumProducer;

    public AlbumDTO[] getTopAlbumsByLikes(int pageSize, int pageNumber) {
        return restTemplate.getForObject(
                "/albums/by_likes?page_size=%s&page_number=%s".formatted(pageSize, pageNumber),
                AlbumDTO[].class
        );
    }

    public AlbumDTO[] getTopAlbumsByAuditions(int pageSize, int pageNumber) {
        return restTemplate.getForObject(
                "/albums/by_auditions?page_size=%s&page_number=%s".formatted(pageSize, pageNumber),
                AlbumDTO[].class
        );
    }

    public void uploadAlbum(Long[] ids, Long[][] songsAuthorsIDs, String name, MultipartFile[] audios, MultipartFile picture) throws IOException {
        uploadAlbumAtDate(ids, songsAuthorsIDs, name, audios, picture, null);
    }

    public void uploadAlbumAtDate(
            Long[] ids,
            Long[][] songsAuthorsIDs,
            String name,
            MultipartFile[] audios,
            MultipartFile picture,
            LocalDateTime dateOfPublication
    ) throws IOException {

        List<SongInUploadAlbumDTO> songs = new ArrayList<>();
        for (int i=0; i<audios.length; i++) {
            String songName = FilenameUtils.removeExtension(audios[i].getOriginalFilename());
            byte[] data = audios[i].getBytes();
            String audioType = audios[i].getContentType();
            SongInUploadAlbumDTO song = new SongInUploadAlbumDTO(songsAuthorsIDs[i], songName, i, data, audioType);
            songs.add(song);
        }
        byte[] pictureData = picture.getBytes();
        String pictureType = picture.getContentType();
        UploadAlbumDTO album = new UploadAlbumDTO(name, songs, ids, pictureData, pictureType, dateOfPublication);
        albumProducer.sendAlbumForUpload(album);
    }

    public Long getAlbumAuditions(Long albumID) {
        return restTemplate.getForObject(
                "/albums/%d/auditions".formatted(albumID),
                Long.class
        );
    }
}
