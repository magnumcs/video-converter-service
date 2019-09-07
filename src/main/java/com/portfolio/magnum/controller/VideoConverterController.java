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

@CrossOrigin
@RestController
@RequestMapping(path = "/converter")
public class VideoConverterController {

    private final ConverterService converterService;

    @Autowired
    public VideoConverterController(ConverterService converterService) {
        this.converterService = converterService;
    }

    @PostMapping(path = "/anytowebfile")
    public ResponseEntity<?> getVideoFileConvertedFile(@RequestParam("file") MultipartFile file) throws Exception{
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
    public ResponseEntity<?> getVideoFileConvertedURL(@RequestBody VideoWrapper videoWrapper) throws InterruptedException {
        S3ObjectWrapper response = converterService.getVideoFileConvertedFileURL(videoWrapper);
        if(response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity
                .badRequest()
                .body(new ConverterError("Erro ao tentar converter o arquivo de vídeo.",
                        HttpStatus.SC_BAD_REQUEST,
                        "/converter/anytoweburl"));
    }

}
