package com.portfolio.magnum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/converter")
public class VideoConverterController {

    private final FileService fileService;

    @Autowired
    public VideoConverterController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(path = "/anytoweb")
    public ResponseEntity<byte[]> getVideoFileConverted(@RequestParam("file") MultipartFile file) {
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
