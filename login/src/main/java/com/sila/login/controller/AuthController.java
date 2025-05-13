package com.sila.login.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.sila.login.model.*;
import com.sila.login.services.UserService;
import com.sila.login.utility.JwtUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtility jwtUtility;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, JwtUtility jwtUtility, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtility = jwtUtility;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("!isAuthenticated()")
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user, HttpServletRequest request) {
        if (userService.getUserByName(user.getUsername()) != null) {
            logger.warn("Attempt to register with existing username: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        User savedUser = userService.saveUser(user);

        try {
            request.login(user.getUsername(), rawPassword);
        } catch (ServletException e) {
            logger.error("Login failed after registration", e);
        }

        return ResponseEntity.ok(savedUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/set-timeslot")
    public ResponseEntity<SingleValues> setTimeSlot(@RequestBody SingleValues timeSlot) {
        timeSlot.setValuename("timeslot");
        userService.updateTimeSlot(timeSlot);
        return ResponseEntity.ok(timeSlot);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public ResponseEntity<UserResponse> changePassword(Authentication auth, @RequestBody PwData pwData) {
        User user = (User) auth.getPrincipal();

        List<LastPasswords> lastPasswords = userService.findAllPwsByUsername(user.getUsername());
        for (LastPasswords lp : lastPasswords) {
            if (passwordEncoder.matches(pwData.getPw(), lp.getPw())) {
                logger.info("Password reuse attempt by {}", user.getUsername());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }

        String newEncodedPassword = passwordEncoder.encode(pwData.getPw());
        user.setPassword(newEncodedPassword);
        user.setChange_date(LocalDateTime.now());

        LastPasswords newLastPw = new LastPasswords(user, (user.getNo_of_pw() % 5) + 1, newEncodedPassword);
        user.setNo_of_pw(user.getNo_of_pw() + 1);
        userService.updateUser(user);

        if (user.getNo_of_pw() <= 5) {
            userService.insertPw(newLastPw);
        } else {
            userService.updatePw(newLastPw);
        }

        UserResponse response = new UserResponse(user.getUsername(), user.getRole(), 0, false);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/post-time", consumes = "application/json", produces = "application/json")
    public ResponseEntity<SingleValues> postTime(@RequestBody SingleValues sv) {
        return ResponseEntity.ok(userService.updateTimeSlot(sv));
    }

    @PostMapping("/user-login")
    public ResponseEntity<UserResponse> login(@RequestBody User loginRequest, HttpServletRequest request) {
        User user = userService.getUserByName(loginRequest.getUsername());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (request.getSession(false) != null) {
            try {
                request.logout();
            } catch (ServletException e) {
                logger.error("Error during logout before login", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        try {
            request.login(loginRequest.getUsername(), loginRequest.getPassword());
        } catch (ServletException e) {
            logger.error("Login failed for user {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        long minutesSinceChange = ChronoUnit.MINUTES.between(user.getChange_date(), LocalDateTime.now());
        boolean expired = minutesSinceChange >= userService.getTimeSlot("timeslot");

        UserResponse response = new UserResponse(user.getUsername(), user.getRole(), minutesSinceChange, expired);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/post-auth", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> postAuth(@RequestBody ApiAuth auth) {
        User user = userService.getUserByName(auth.getUsername());

        if (user != null && passwordEncoder.matches(auth.getPassword(), user.getPassword())) {
            String token = jwtUtility.generateToken(auth.getUsername());
            return ResponseEntity.ok(token);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized.");
    }

    @PutMapping("/user-logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        try {
            request.logout();
            return ResponseEntity.ok().build();
        } catch (ServletException e) {
            logger.error("Logout failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
