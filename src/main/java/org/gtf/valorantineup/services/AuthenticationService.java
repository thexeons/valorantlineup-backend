package org.gtf.valorantineup.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.gtf.valorantineup.dto.request.LoginRequest;
import org.gtf.valorantineup.dto.request.RefreshTokenRequest;
import org.gtf.valorantineup.dto.request.SignupRequest;
import org.gtf.valorantineup.dto.response.JwtResponse;
import org.gtf.valorantineup.dto.response.LoginResponse;
import org.gtf.valorantineup.enums.ERole;
import org.gtf.valorantineup.exception.GTFException;
import org.gtf.valorantineup.models.Role;
import org.gtf.valorantineup.models.User;
import org.gtf.valorantineup.models.UserRefreshToken;
import org.gtf.valorantineup.repositories.RoleRepository;
import org.gtf.valorantineup.repositories.UserRefreshTokenRepository;
import org.gtf.valorantineup.repositories.UserRepository;
import org.gtf.valorantineup.security.implementation.UserDetailsImpl;
import org.gtf.valorantineup.security.jwt.AuthEntryPointJwt;
import org.gtf.valorantineup.security.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.util.Optionals.ifPresentOrElse;

@Service
public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    private int REFRESH_DURATION;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;


    @Autowired
    public AuthenticationService(@Value("${refresh.expiration.day}") int REFRESH_DURATION, AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, RoleRepository roleRepository, UserRefreshTokenRepository userRefreshTokenRepository, PasswordEncoder encoder) {
        this.REFRESH_DURATION = REFRESH_DURATION;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRefreshTokenRepository = userRefreshTokenRepository;
        this.encoder = encoder;
    }

    public LoginResponse authenticateUser(LoginRequest loginRequest) {

        //Authenticate a user manually. This method will trigger SUCCESS / BAD CREDENTIAL EVENT listened by the AuthenticationListener.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LoginResponse loginResponse = new LoginResponse();
        User user = userRepository.findByEmail(loginRequest.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + loginRequest.getUsername()));

//         if (user.getERoles().contains(ERole.ROLE_PENDAMPING)) {
//                PendampingLoginDataResponse pendampingLoginDataResponse = new PendampingLoginDataResponse();
//                PendampingAkad pendampingAkad = pendampingAkadRepository.findByUser(user);
//                if (null == pendampingAkad)
//                    throw new XcidicException(HttpStatus.NOT_FOUND, "Error: Data pendamping akad tidak ditemukan.");
//                pendampingLoginDataResponse.setEmail(user.getEmail());
//                pendampingLoginDataResponse.setFullName(pendampingAkad.getFullName());
//                pendampingLoginDataResponse.setUuidPendamping(pendampingAkad.getUuid()); //Set pendamping akad's UUID, not his/her notary UUID.
//                pendampingLoginDataResponse.setUuidUser(user.getUuid());
//                pendampingLoginDataResponse.setChangePassword(user.getImmediateChangePassword());
//                loginResponse.setData(pendampingLoginDataResponse);
//            }
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
        ifPresentOrElse(userRefreshTokenRepository.findByToken(refreshToken), token -> {
            if (token.getExpiryDate().compareTo(new Date()) > 0) {
                //Authentication authentication = new UsernamePasswordAuthenticationToken(token.getUser().getUsername(), null);
                //SecurityContextHolder.getContext().setAuthentication(authentication);
                jwtResponse.setToken(jwtUtils.generateJwtToken(token.getUser()));
            } else {
                throw new GTFException(HttpStatus.BAD_REQUEST, "Error : Refresh Token expired");
            }
        }, () -> {
            throw new GTFException(HttpStatus.BAD_REQUEST, "Error : Invalid Refresh Token");
        });
        return jwtResponse;
    }

    private String createRefreshToken(User user) {
        //Generate token from random string
        String token = RandomStringUtils.randomAlphanumeric(128);
        ifPresentOrElse(userRefreshTokenRepository.findByUser(user), refreshToken -> {
            refreshToken.setToken(token);
            refreshToken.setExpiryDate(DateUtils.addDays(new Date(), REFRESH_DURATION));
            userRefreshTokenRepository.save(refreshToken);
        }, () -> {
            UserRefreshToken newRefreshToken = new UserRefreshToken();
            newRefreshToken.setExpiryDate(DateUtils.addDays(new Date(), REFRESH_DURATION));
            newRefreshToken.setToken(token);
            newRefreshToken.setUser(user);
            userRefreshTokenRepository.save(newRefreshToken);
        });
        return token;
    }

    public void logoutUser(RefreshTokenRequest refreshToken) {
        userRefreshTokenRepository.findByToken(refreshToken.getRefreshToken())
                .ifPresent(userRefreshTokenRepository::delete);
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

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        System.out.println(strRoles);

        if (strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new GTFException(HttpStatus.NOT_FOUND, "Error: Role is not found."));
            roles.add(userRole);

            user.setRoles(roles);
            userRepository.saveAndFlush(user);
        }
        else {
            System.out.println("strroles tidak kosong");
        }
        System.out.print(user);
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