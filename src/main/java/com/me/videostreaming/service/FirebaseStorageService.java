package com.me.videostreaming.service;

import com.google.cloud.storage.Blob;
import com.me.videostreaming.enums.FileType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface FirebaseStorageService {
    Mono<Integer> uploadFile(String name, Mono<FilePart> file, FileType type, Map<String, String> metadata);
    Flux<Blob> listFiles(FileType type);
    void deleteFile(String name, FileType type);
    Boolean doesFileExist(String name, FileType type);
    Map<String, String> getMetadata(String name, FileType type);
    void updateMetadata(String name, FileType type, Map<String, String> metadata);
    public Mono<ResponseEntity<String>> getSignedUrl(String id, FileType type);
}
