package com.me.videostreaming.service;

import com.me.videostreaming.enums.FileType;
import com.me.videostreaming.model.UserDetailsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class ImageService {

    @Autowired
    FirebaseStorageService storageService;

    Mono<Boolean> verify(String title, ServerWebExchange exchange){
        return  exchange.getPrincipal().map(principal->{
            String id = Long.toString(((UserDetailsModel) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getId());
            if(storageService.getMetadata(title, FileType.VIDEO).getOrDefault("id", "anonymous").equals(id)){
                storageService.deleteFile(title, FileType.IMAGE);
                return true;
            }
            return false;
        });
    }

    Mono<Integer> transferImageToStorage(String title, Mono<FilePart> image, Boolean verified){
        if(verified){
            storageService.updateMetadata(title, FileType.VIDEO, Map.of("image_ref", title));
            return storageService.uploadFile(title, image, FileType.IMAGE, new HashMap<>());
        } else return Mono.just(0);
    }

    ResponseEntity<Boolean> buildResponse(Integer transferredBytes){
        if(transferredBytes>0) return ResponseEntity.ok(true);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }

    public Mono<ResponseEntity<Boolean>> addImage(String title, Mono<FilePart> image, ServerWebExchange exchange){
        if(storageService.doesFileExist(title, FileType.VIDEO)){
            return verify(title, exchange)
                    .flatMap(bool->transferImageToStorage(title, image, bool))
                    .map(this::buildResponse);
        }
        return Mono.just(ResponseEntity.notFound().build());
    }

}
