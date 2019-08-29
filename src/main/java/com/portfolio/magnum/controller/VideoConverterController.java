package com.portfolio.magnum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/converter")
public class VideoConverterController {

    @PostMapping(path = "/anytoweb")
    public ResponseEntity<byte[]> getVideoFileConverted(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(null);
    }

}
