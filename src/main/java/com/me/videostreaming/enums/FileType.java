package com.me.videostreaming.enums;

public enum FileType {
    VIDEO("videos", "video/mp4"),
    IMAGE("images", "image/jpeg");

    private final String directory;
    private final String contentType;

    FileType(String directory, String contentType) {
        this.directory = directory;
        this.contentType = contentType;
    }

    public String getContentType() { return contentType; }
    public String getDirectory() {
        return directory;
    }
}
