package org.gtf.valorantineup.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gtf.valorantineup.dto.request.*;

import org.gtf.valorantineup.exception.AbstractRequestHandler;
import org.gtf.valorantineup.services.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authorization API")
public class AuthController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final AuthenticationService authenticationService;

    //Spring 5 : Always use constructor based dependency injection in your beans. Always use assertions for mandatory dependencies
    @Autowired
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return authenticationService.authenticateUser(loginRequest);
            }
        };
        return handler.getResult();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                authenticationService.registerUser(signUpRequest);
                return "User registered successfully!";
            }
        };
        return handler.getResult();
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody RefreshTokenRequest refreshToken) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                authenticationService.logoutUser(refreshToken);
                return "Token removed!";
            }
        };
        return handler.getResult(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/refresh/{token}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "Experimental endpoint for sandboxing purposes.")
    public ResponseEntity<?> refreshToken(@PathVariable String token) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return authenticationService.refreshAccessToken(token);
            }
        };
        return handler.getResult();
    }
}
