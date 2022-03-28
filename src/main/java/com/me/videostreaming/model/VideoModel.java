package com.me.videostreaming.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoModel {
    String title;
    String imageRef;
    Instant creationDate;
    String owner;
    Long ownerId;
    Integer videoSize;
    Integer imageSize;

}
