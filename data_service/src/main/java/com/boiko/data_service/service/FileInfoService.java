package com.boiko.data_service.service;

import com.boiko.data_service.model.FileInfo;
import com.boiko.data_service.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileInfoService {
    private final FileInfoRepository fileInfoRepository;
    private final S3Service s3Service;

    public FileInfo getByID(Long id) {
        return fileInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FileInfo with id = %d doesn't exist".formatted(id)));
    }

    public FileInfo upload(String bucket, MultipartFile file) throws IOException {
        FileInfo fileInfo = createFileInfo(file);
        String key = s3Service.generateKey();
        String url = s3Service.uploadAndGetURL(bucket, key, file);
        fileInfo.setS3Key(key);
        fileInfo.setUrl(url);
        return saveFileInfo(fileInfo);
    }

    public FileInfo upload(String bucket, byte[] data, String fileType) {
        FileInfo fileInfo = createFileInfo(fileType);
        String key = s3Service.generateKey();
        String url = s3Service.uploadAndGetURL(bucket, key, data);
        fileInfo.setS3Key(key);
        fileInfo.setUrl(url);
        return saveFileInfo(fileInfo);
    }

    public FileInfo createFileInfo(MultipartFile file) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setType(file.getContentType());
        return fileInfo;
    }

    public FileInfo createFileInfo(String fileType) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setType(fileType);
        return fileInfo;
    }

    public FileInfo getDefaultAvatar() {
        return fileInfoRepository.getDefaultAvatar();
    }

    public FileInfo saveFileInfo(FileInfo fileInfo) {
        return fileInfoRepository.save(fileInfo);
    }
}
