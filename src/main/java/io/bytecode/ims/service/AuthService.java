package io.bytecode.ims.service;


import io.bytecode.ims.dto.AuthenticationResponse;
import io.bytecode.ims.dto.LoginRequest;
import io.bytecode.ims.dto.RefreshTokenRequest;
import io.bytecode.ims.dto.UserDto;
import io.bytecode.ims.exception.SpringImsException;
import io.bytecode.ims.model.NotificationEmail;
import io.bytecode.ims.model.User;
import io.bytecode.ims.model.VerifyToken;
import io.bytecode.ims.respository.UserRepository;
import io.bytecode.ims.respository.VerifyTokenRepository;
import io.bytecode.ims.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final VerifyTokenRepository verifyTokenRepository;
    private final MailService mailService;
    private final UserRepository userRepository;


    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;


    public void signUp(UserDto userDto) {

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);
        if (userDto.getRole().equals("admin"))
            user.setRole("ROLE_ADMIN");
        else
            user.setRole("ROLE_USER");

        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendEmail(new NotificationEmail("Please Activate your Account",
                user.getEmail(), "Thank you for signing up to Shopify, " +
                "please click on the below url to activate your account : " +
                "http://localhost:7070/api/auth/accountVerification/" + token));
    }

    private String generateVerificationToken(User user) {

        String token = UUID.randomUUID().toString();
        VerifyToken verificationToken = new VerifyToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verifyTokenRepository.save(verificationToken);
        return token;
    }

    private void fetchUserAndEnable(VerifyToken verifyToken) {
        String username = verifyToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringImsException("User not found with name " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void verifyAccount(String token) {
        Optional<VerifyToken> verificationToken = verifyTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new SpringImsException("Invalid Token")));
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Jwt principal = (Jwt) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        System.out.println(userRepository.findByUsername(principal.getSubject().toString())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getSubject())));
        return userRepository.findByUsername(principal.getSubject())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getSubject()));
    }


    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }
}

