package io.bytecode.ims.controller;


import io.bytecode.ims.dto.UserDto;
import io.bytecode.ims.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping ("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping ("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserDto userDto) {
        authService.signUp(userDto);
        return new ResponseEntity<>("User Signed up successfully" , HttpStatus.OK);
    }

    @GetMapping ("accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account activated successfully", HttpStatus.OK);
    }

}

