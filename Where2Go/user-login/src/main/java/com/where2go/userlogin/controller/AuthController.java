package com.where2go.userlogin.controller;

import com.where2go.userlogin.config.JwtUtil;
import com.where2go.userlogin.dao.JWTResponse;
import com.where2go.userlogin.dto.Authenticate;
import com.where2go.userlogin.dto.AuthenticationRequest;
import com.where2go.userlogin.dto.User;
import com.where2go.userlogin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<JWTResponse> authenticate(@RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        final UserDetails user = userService.findUserByEmail(request.getEmail());

        JWTResponse jwtResponse = new JWTResponse();

        if (user != null) {
            String token = jwtUtil.generateToken(user);
            jwtResponse.setAuth(true);
            jwtResponse.setToken(token);

            return ResponseEntity.ok(jwtResponse);
        }

        jwtResponse.setAuth(false);
        jwtResponse.setToken("Some error has occured");

        return ResponseEntity.status(400).body(jwtResponse);
    }

    @PostMapping("/decrypte")
    public ResponseEntity<User> authenticate(@RequestBody Authenticate request) {
        String[] chunks = request.getToken().split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();

        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        Pattern pattern = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+", Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(payload);

        if (!matcher.find())
            return ResponseEntity.status(400).body(null);

        String userEmail = matcher.group();

        User user = this.userService.findUserByEmailRepository(userEmail);

        if(user == null)
            ResponseEntity.status(400).body(null);

        return ResponseEntity.status(200).body(user);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> sayGoodBye() {
        return ResponseEntity.ok("Logout");
    }
}
