package com.portfolio.magnum.controller;

import com.portfolio.magnum.domain.wrapper.S3ObjectWrapper;
import com.portfolio.magnum.domain.wrapper.VideoWrapper;
import com.portfolio.magnum.exception.ConverterError;
import com.portfolio.magnum.service.ConverterService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/converter")
public class VideoConverterController {

    private final ConverterService converterService;

    @Autowired
    public VideoConverterController(ConverterService converterService) {
        this.converterService = converterService;
    }

    @PostMapping(path = "/anytowebfile")
    public ResponseEntity<?> getVideoFileConvertedFile(@RequestBody VideoWrapper videoWrapper) {
        S3ObjectWrapper response = converterService.getVideoFileConverted(videoWrapper);
        if(response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity
                .badRequest()
                .body(new ConverterError("Erro ao tentar converter o arquivo de vídeo.",
                        HttpStatus.SC_BAD_REQUEST,
                        "/converter/anytoweb"));
    }

    @PostMapping(path = "/anytowebfile")
    public ResponseEntity<?> getVideoFileConvertedURL(@RequestBody VideoWrapper videoWrapper) {
        S3ObjectWrapper response = converterService.getVideoFileConverted(videoWrapper);
        if(response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity
                .badRequest()
                .body(new ConverterError("Erro ao tentar converter o arquivo de vídeo.",
                        HttpStatus.SC_BAD_REQUEST,
                        "/converter/anytoweb"));
    }

}
