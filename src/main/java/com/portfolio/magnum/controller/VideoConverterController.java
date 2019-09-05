package com.portfolio.magnum.controller;

import com.portfolio.magnum.domain.wrapper.S3ObjectWrapper;
import com.portfolio.magnum.domain.wrapper.VideoWrapper;
import com.portfolio.magnum.exception.ConverterError;
import com.portfolio.magnum.service.ConverterService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping(path = "/converter")
public class VideoConverterController {

    private final ConverterService converterService;

    @Autowired
    public VideoConverterController(ConverterService converterService) {
        this.converterService = converterService;
    }

    @PostMapping(path = "/anytowebfile")
    public ResponseEntity<?> getVideoFileConvertedFile(@RequestParam("file") MultipartFile file) {
        S3ObjectWrapper response = converterService.getVideoFileConvertedFile(file);
        if(response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity
                .badRequest()
                .body(new ConverterError("Erro ao tentar converter o arquivo de vídeo.",
                        HttpStatus.SC_BAD_REQUEST,
                        "/converter/anytowebfile"));
    }

    @PostMapping(path = "/anytoweburl")
    public ResponseEntity<?> getVideoFileConvertedURL(@RequestParam("URL") VideoWrapper url) {
        S3ObjectWrapper response = converterService.getVideoFileConvertedFileURL(url);
        if(response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity
                .badRequest()
                .body(new ConverterError("Erro ao tentar converter o arquivo de vídeo.",
                        HttpStatus.SC_BAD_REQUEST,
                        "/converter/anytoweburl"));
    }

    @PostMapping(path = "/uploadfile")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        S3ObjectWrapper response = converterService.getVideoFileConvertedFile(file);
        if(response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity
                .badRequest()
                .body(new ConverterError("Erro ao tentar converter o arquivo de vídeo.",
                        HttpStatus.SC_BAD_REQUEST,
                        "/converter/uploadfile"));
    }

    @PostMapping(path = "/uploadurl")
    public ResponseEntity<?> uploadFileURL(@RequestBody VideoWrapper videoWrapper) {
        S3ObjectWrapper response = converterService.getVideoFileConvertedFileURL(videoWrapper);
        if(response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity
                .badRequest()
                .body(new ConverterError("Erro ao tentar converter o arquivo de vídeo.",
                        HttpStatus.SC_BAD_REQUEST,
                        "/converter/uploadurl"));
    }

}
