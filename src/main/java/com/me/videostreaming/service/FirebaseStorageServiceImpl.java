package com.me.videostreaming.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;
import com.me.videostreaming.enums.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseStorageServiceImpl implements FirebaseStorageService {

    Logger logger;

    private StorageOptions options;
    @Value("${firebase.storage.bucket}")
    private String bucketName;
    @Value("${firebase.storage.project.id}")
    private String projectId;
    @Value("${firebase.storage.key}")
    private String serviceAccountKey;

    @PostConstruct
    private void initializeFirebase() throws Exception {
        logger = LoggerFactory.getLogger(FirebaseStorageServiceImpl.class);

        FileInputStream serviceAccount =
                new FileInputStream(serviceAccountKey);

        options = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
    }

    @Override
    public Mono<ResponseEntity<String>> getSignedUrl(String id, FileType type) {
        URL url = options.getService()
                .signUrl(BlobInfo.newBuilder(
                        bucketName + "/" + type.getDirectory(), id).build(),
                        1,
                        TimeUnit.HOURS);
        return Mono.just(ResponseEntity.ok(url.toString()));
    }

    @Override
    public Flux<Blob> listFiles(FileType type) {
        Storage storage = options.getService();
        Iterable<Blob> files = storage.list(bucketName).iterateAll();
        return Flux.fromIterable(files);
    }

    @Override
    public void updateMetadata(String name, FileType type, Map<String, String> metadata){
        Storage storage = options.getService();
        BlobId blobId = BlobId.of(bucketName, type.getDirectory() + "/" + name);
        Blob blob = storage.get(blobId);
        Map<String, String> previousMetadata = blob.getMetadata();
        if(previousMetadata==null) previousMetadata=new HashMap<>();
        HashMap<String, String> newMetadata = new HashMap<>();
        metadata.forEach(newMetadata::put);
        previousMetadata.forEach(newMetadata::putIfAbsent);
        storage.update(BlobInfo.newBuilder(blobId).setMetadata(newMetadata).build());
    }

    public Map<String, String> getMetadata(String name, FileType type){
        Storage storage = options.getService();
        BlobId blobId = BlobId.of(bucketName, type.getDirectory() + "/" + name);
        Blob file = storage.get(blobId);
        return file.getMetadata();
    }

    public Boolean doesFileExist(String name,FileType type){
        Storage storage = options.getService();
        BlobId blobId = BlobId.of(bucketName, type.getDirectory() + "/" + name);
        Blob blob = storage.get(blobId);
        return blob != null;
    }

    private WriteChannel getChannel(String name, FileType type, Map<String, String> metadata) {
        Storage storage = options.getService();

        BlobId blobId = BlobId.of(bucketName, type.getDirectory() + "/" + name);
        storage.create(BlobInfo.newBuilder(blobId).setMetadata(metadata).setContentType(type.getContentType()).build());
        return storage.get(blobId).writer();
    }

    private void closeChannel(WriteChannel channel){
        try {
            channel.close();
        } catch (IOException e) {
            logger.error("Firebase Storage channel couldn't close.");
        }
    }

    private Integer writeToChannel(WriteChannel channel, List<DataBuffer> buffers){
        int size = 0;
        try {
            for (DataBuffer content : buffers) {
                int written = channel.write(content.asByteBuffer());
                size = size + written;
            }
            return size;
        } catch (IOException e) {
            return 0;
        }
    }

    @Override
    public void deleteFile(String name, FileType type) {
        Storage storage = options.getService();
        BlobId blobId = BlobId.of(bucketName, type.getDirectory() + "/" + name);
        storage.delete(blobId);
    }

    @Override
    public Mono<Integer> uploadFile(String name, Mono<FilePart> file, FileType type,
                                    Map<String, String> metadata) {
        WriteChannel channel = getChannel(name, type, metadata);
        Mono<List<DataBuffer>> dataBuffers = file.flatMap(filePart -> filePart.content().collectList());
        return dataBuffers
                .map(buffers -> writeToChannel(channel, buffers))
                .onErrorReturn(0)
                .doOnCancel(()->deleteFile(name, type))
                .doAfterTerminate(() -> closeChannel(channel));
    }
}
