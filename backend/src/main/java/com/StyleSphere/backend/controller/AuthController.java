package com.StyleSphere.backend.controller;

import com.StyleSphere.backend.dto.JwtAuthenticationResponse;
import com.StyleSphere.backend.dto.LoginRequest;
import com.StyleSphere.backend.dto.OtpRequest;
import com.StyleSphere.backend.dto.OtpVerificationRequest;
import com.StyleSphere.backend.security.JwtTokenProvider;
import com.StyleSphere.backend.service.EmailService;
import com.StyleSphere.backend.service.OtpService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;


    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody OtpRequest request) {
        try {
            String code = String.format("%06d", new SecureRandom().nextInt(1000000));
            otpService.saveOtp(request.getEmail(), code);
            emailService.sendHtmlEmail(request.getEmail(), "Your StyleSphere Verification Code", code);
            return ResponseEntity.ok("Verification code sent to your email.");
        } catch (MessagingException e) {
            // You MUST catch the exception here because sendHtmlEmail throws it
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending email: " + e.getMessage());
        }

    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request)
    {
        String storedCode = otpService.getOtp(request.getEmail());

        if(storedCode != null  && (storedCode.equals(request.getCode())))
        {
            otpService.deleteOtp(request.getEmail());
            String jwt = tokenProvider.generateToken(request.getEmail());
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired code.");

    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), // Use email here
                        loginRequest.getPassword()
                )
        );

        String jwt = tokenProvider.generateToken(authentication.getName());

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }


}
