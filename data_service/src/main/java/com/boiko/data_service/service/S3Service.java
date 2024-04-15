package com.boiko.data_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    public String uploadAndGetURL(String bucket, String key, MultipartFile file) throws IOException {
        upload(bucket, key, file);
        return getURL(bucket, key);
    }

    public void upload(String bucket, String key, MultipartFile file) throws IOException {
        String path = makePath(bucket);
        amazonS3.putObject(
                new PutObjectRequest(path, key, file.getInputStream(), new ObjectMetadata())
        );
    }

    public void uploadIfAbsent(String bucket, String key, MultipartFile file) throws IOException {
        String path = makePath(bucket);
        boolean isExist = amazonS3.doesObjectExist(bucket, key);
        if (!isExist) {
            amazonS3.putObject(
                    new PutObjectRequest(path, key, file.getInputStream(), new ObjectMetadata())
            );
        }
    }

    private String makePath(String bucket) {
        return "musium/" + bucket;
    }

    public byte[] download(String bucket, String key) throws IOException {
        String path = makePath(bucket);
        S3Object s3object = amazonS3.getObject(path, key);
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        return inputStream.readAllBytes();
    }

    public String getURL(String bucket, String key) {
        String path = makePath(bucket);
        URL url = amazonS3.getUrl(path, key);
        return url.toString();
    }

    public String generateKey() {
        return UUID.randomUUID().toString();
    }
}
