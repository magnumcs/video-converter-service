package com.portfolio.magnum.domain.wrapper;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public class VideoWrapper implements Serializable {

    private String fileName;
    private String size;
    private MultipartFile file;
    private String url;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
