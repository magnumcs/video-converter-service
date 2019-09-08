package com.portfolio.magnum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping(path = "/")
    ResponseEntity<?> getStatusApplication() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
