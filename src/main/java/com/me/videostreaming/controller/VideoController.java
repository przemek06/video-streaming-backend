package com.me.videostreaming.controller;

import com.me.videostreaming.enums.FileType;
import com.me.videostreaming.model.VideoModel;
import com.me.videostreaming.service.FirebaseStorageService;
import com.me.videostreaming.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@CrossOrigin(origins = "http://localhost:63342", allowCredentials = "true")
public class VideoController {

    @Autowired
    FirebaseStorageService firebaseStorageService;

    @Autowired
    VideoService videoService;

    @GetMapping(value = "/videos")
    public Flux<VideoModel> listVideos(@RequestParam("date") Long sinceWhen) {
        return videoService.getVideos(sinceWhen);
    }

    @GetMapping(value = "/videos/{name}")
    public Mono<ResponseEntity<String>> getVideo(@PathVariable String name) {
        return firebaseStorageService.getSignedUrl(name, FileType.VIDEO);
    }

    @PostMapping(value = "/videos/{name}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<VideoModel>> uploadVideo(@PathVariable String name,
                                                        @RequestPart(name = "video_file") Mono<FilePart> videoFile,
                                                        ServerWebExchange exchange) {
        return videoService.saveVideo(name, videoFile,exchange);
    }
}
