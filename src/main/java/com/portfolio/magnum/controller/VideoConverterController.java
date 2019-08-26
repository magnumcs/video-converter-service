package com.portfolio.magnum.controller;

import com.portfolio.magnum.service.Imp.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping(path = "/converter")
public class VideoConverterController {

    private final FileService fileService;

    @Autowired
    public VideoConverterController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(path = "/anytoweb")
    public ResponseEntity<byte[]> getVideoFileConverted(byte[] source) {
        return ResponseEntity.ok(null);
    }

    @GetMapping(path = "/bytefromfile")
    public ResponseEntity<byte[]> getByteFromFile() {
        try {
            byte[] response = fileService.getByteFromFile();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

}
