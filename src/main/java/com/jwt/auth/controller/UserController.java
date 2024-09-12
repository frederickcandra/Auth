package com.jwt.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/user")
@Secured("ROLE_USER")
public class UserController {
    @Operation(summary = "Display Hello World")
    @GetMapping("/hello")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hello World displayed successfully")
    })
    public ResponseEntity<String> sayHello() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }
}
