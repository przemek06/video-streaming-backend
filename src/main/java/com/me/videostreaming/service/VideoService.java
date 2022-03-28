package com.me.videostreaming.service;

import com.me.videostreaming.enums.FileType;
import com.me.videostreaming.model.UserDetailsModel;
import com.me.videostreaming.model.VideoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Service
public class VideoService {

    @Autowired
    FirebaseStorageService firebaseStorageService;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    public Mono<Map<String, String>> extractUserInfo(ServerWebExchange exchange){
        return exchange.getPrincipal()
                .map(principal -> {
                    String username = ((UserDetailsModel) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUsername();
                    Long id = ((UserDetailsModel) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getId();

                    return Map.of("username", username, "id", String.valueOf(id));
                });
    }

    private Mono<Integer> transferVideoToStorage(Mono<FilePart> file, String videoTitle, ServerWebExchange exchange) {
        if (!firebaseStorageService.doesFileExist(videoTitle, FileType.VIDEO)) {
            return extractUserInfo(exchange)
                    .flatMap(map -> firebaseStorageService.uploadFile(videoTitle, file, FileType.VIDEO, map));
        }
        return Mono.just(0);
    }

    Mono<VideoModel> buildVideoModel(Mono<FilePart> file, String videoTitle, ServerWebExchange exchange) {

        Mono<Integer> transferredVideo = transferVideoToStorage(file, videoTitle, exchange);

        return Mono.zip(transferredVideo, exchange.getPrincipal(), (size, principal) -> {
            String username = ((UserDetailsModel) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUsername();

            return VideoModel.builder()
                    .title(videoTitle)
                    .owner(username)
                    .videoSize(size)
                    .creationDate(Instant.now())
                    .build();
        });
    }

    ResponseEntity<VideoModel> buildResponse(VideoModel videoModel) {
        if (videoModel.getVideoSize() > 0) {
            return ResponseEntity.ok(videoModel);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(videoModel);
    }

    public Mono<ResponseEntity<VideoModel>> saveVideo(String videoTitle,
                                                      Mono<FilePart> videoFile,
                                                      ServerWebExchange exchange) {
        Mono<VideoModel> builtVideo = buildVideoModel(videoFile, videoTitle, exchange);
        return builtVideo.map(this::buildResponse);
    }

    public Flux<VideoModel> getVideos(Long sinceWhen) {
        return firebaseStorageService.listFiles(FileType.VIDEO)
                .filter(blob -> blob.getCreateTime() > sinceWhen)
                .filter(blob -> FileType.VIDEO.getContentType().equals(blob.getContentType()))
                .map(blob ->
                    VideoModel.builder()
                            .title(blob.getName())
                            .owner(blob.getMetadata().get("username"))
                            .imageRef(blob.getMetadata().get("image_ref"))
                            .videoSize(Math.toIntExact(blob.getSize()))
                            .creationDate(Instant.ofEpochMilli(blob.getCreateTime()))
                            .build());
    }

}
