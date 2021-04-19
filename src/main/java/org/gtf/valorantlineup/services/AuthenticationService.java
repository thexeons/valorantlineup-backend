package org.gtf.valorantlineup.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.gtf.valorantlineup.dto.request.LoginRequest;
import org.gtf.valorantlineup.dto.request.RefreshTokenRequest;
import org.gtf.valorantlineup.dto.request.SignupRequest;
import org.gtf.valorantlineup.dto.response.JwtResponse;
import org.gtf.valorantlineup.dto.response.LoginResponse;
import org.gtf.valorantlineup.enums.ERole;
import org.gtf.valorantlineup.exception.GTFException;
import org.gtf.valorantlineup.models.Role;
import org.gtf.valorantlineup.models.User;
import org.gtf.valorantlineup.models.redis.RedisRefreshToken;
import org.gtf.valorantlineup.repositories.RoleRepository;
import org.gtf.valorantlineup.repositories.UserRepository;
import org.gtf.valorantlineup.security.implementation.UserDetailsImpl;
import org.gtf.valorantlineup.security.jwt.AuthEntryPointJwt;
import org.gtf.valorantlineup.security.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    private int REFRESH_DURATION;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final org.gtf.valorantlineup.repositories.redis.RedisRefreshTokenRepository redisToken;

    @Autowired
    public AuthenticationService(@Value("${refresh.expiration.day}") int REFRESH_DURATION, AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, org.gtf.valorantlineup.repositories.redis.RedisRefreshTokenRepository redisToken) {
        this.REFRESH_DURATION = REFRESH_DURATION;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.redisToken = redisToken;
    }

    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        String username = "";
        User user;
        if(loginRequest.getUserIdentifier().indexOf('@') != -1){
            try {
                user = userRepository.findByEmail(loginRequest.getUserIdentifier()).get();
                username = user.getUsername();
            }catch(NoSuchElementException e)
            {
                user = null;
            }
        }
        else {
            try {
            user = userRepository.findByUsername(loginRequest.getUserIdentifier()).get();
            username = user.getUsername();
            }catch(NoSuchElementException e)
            {
                user = null;
            }
        }

        //Authenticate a user manually. This method will trigger SUCCESS / BAD CREDENTIAL EVENT listened by the AuthenticationListener.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LoginResponse loginResponse = new LoginResponse();

        //generate JWT
        loginResponse.setToken(jwtUtils.generateJwtToken(authentication));
        //generate Refresh Token
        loginResponse.setRefreshToken(createRefreshToken(user));
        //update last login date
        user.setLastLogin(new Date());
        userRepository.saveAndFlush(user);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        //Set roles
        loginResponse.setRoles(userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority().substring(5))
                .collect(Collectors.toList()));
        return loginResponse;
    }

    public JwtResponse refreshAccessToken(String refreshToken) {
        JwtResponse jwtResponse = new JwtResponse();
        RedisRefreshToken token = redisToken.fetchTokenByToken(refreshToken);
        if(token!=null)
        {
            if (token.getExpiryDate().compareTo(new Date()) > 0) {
                jwtResponse.setToken(jwtUtils.generateJwtToken(token.getUsername()));
            } else {
                throw new GTFException(HttpStatus.BAD_REQUEST, "Error : Refresh Token expired");
            }
        }else {
                throw new GTFException(HttpStatus.BAD_REQUEST, "Error : Invalid Refresh Token");
        }
        return jwtResponse;
    }

    private String createRefreshToken(User user) {
        //Generate token from random string
        String token = RandomStringUtils.randomAlphanumeric(128);
        //Test Redis
        RedisRefreshToken refreshToken = redisToken.fetchTokenByUserId(user.getUuid());
        RedisRefreshToken newToken = new RedisRefreshToken();
        newToken.setToken(token);
        newToken.setUserId(user.getUuid());
        newToken.setUsername(user.getUsername());
        newToken.setExpiryDate(DateUtils.addDays(new Date(), REFRESH_DURATION));
        if(refreshToken == null)
        {
            redisToken.saveToken(newToken);
        }
        else {
            redisToken.updateToken(newToken);
        }
        return token;
    }

    public void logoutUser(String username) {
        redisToken.deleteToken(username);
    }

    public void registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new GTFException(HttpStatus.CONFLICT, "Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new GTFException(HttpStatus.CONFLICT, "Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new GTFException(HttpStatus.NOT_FOUND, "Error: Role is not found."));
            roles.add(userRole);

            user.setRoles(roles);
            userRepository.saveAndFlush(user);

    }

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new GTFException(HttpStatus.UNAUTHORIZED, "Error: User is not authenticated");
        }
        return userRepository.findByUsername(authentication.getName());
    }

    public List<ERole> getCurrentUserRoles() {
        List<ERole> roles = new ArrayList<>();
        if (getCurrentUser().isPresent()) {
            for (Role role : getCurrentUser().get().getRoles()) {
                roles.add(role.getName());
            }
        } else {
            throw new GTFException(HttpStatus.NOT_FOUND, "User not found");
        }
        return roles;
    }
}