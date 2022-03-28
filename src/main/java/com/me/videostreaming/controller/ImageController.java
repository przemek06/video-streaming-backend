package com.me.videostreaming.controller;

import com.me.videostreaming.enums.FileType;
import com.me.videostreaming.service.ImageService;
import com.me.videostreaming.service.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class ImageController {

    @Autowired
    FirebaseStorageService firebaseStorageService;

    @Autowired
    ImageService imageService;

    @PostMapping(value = "/images/{name}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Boolean>> uploadImage(@PathVariable(name = "name") String videoTitle,
                                                        @RequestPart(name = "image_file") Mono<FilePart> image,
                                                        ServerWebExchange exchange){
        return imageService.addImage(videoTitle, image, exchange);
    }

    @GetMapping(value = "/images/{name}")
    public Mono<ResponseEntity<String>> getImage(@PathVariable String name) {
        return firebaseStorageService.getSignedUrl(name, FileType.IMAGE);
    }
}
