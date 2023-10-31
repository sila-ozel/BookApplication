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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sila.login.model.ApiAuth;
import com.sila.login.model.LastPasswords;
import com.sila.login.model.PwData;
import com.sila.login.model.SingleValues;
import com.sila.login.model.User;
import com.sila.login.model.UserResponse;
import com.sila.login.services.UserService;
import com.sila.login.utility.JwtUtility;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class AuthController {
    @Autowired
    UserService us;

    @Autowired
    JwtUtility jwtUtility;

    BCryptPasswordEncoder e = new BCryptPasswordEncoder();

    @PreAuthorize("!isAuthenticated()")
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user, HttpServletRequest req) {
        User u = us.getUserByName(user.getUsername());
        if (u == null) {
            String pw = user.getPassword();
            user.setPassword(e.encode(user.getPassword()));
            u = us.saveUser(user);
            try {
                req.login(user.getUsername(), pw);
            } catch (ServletException se) {
                se.printStackTrace();
            }
            return ResponseEntity.ok(u);
        }
        System.out.println("user already exists");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // user already exists
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/SetTimeSlot")
    public SingleValues setTimeSlot(@RequestBody SingleValues timeSlot) {
        timeSlot.setValuename("timeslot");
        us.updateTimeSlot(timeSlot);
        return timeSlot;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("changepw")
    public ResponseEntity<UserResponse> changePassword(Authentication a, @RequestBody PwData password) {
        User u = (User) a.getPrincipal();
        List<LastPasswords> pws = us.findAllPwsByUsername(u.getUsername());
        for(int i = 0; i<pws.size(); i++) {
            if(e.matches(password.getPw(), pws.get(i).getPw())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
        }
        String pw = e.encode(password.getPw());
        u.setPassword(pw); // new password
        u.setChange_date(LocalDateTime.now());
        LastPasswords lp = new LastPasswords(u, (u.getNo_of_pw() % 3) + 1, u.getPassword());
        u.setNo_of_pw(u.getNo_of_pw() + 1);
        us.updateUser(u);
        if (u.getNo_of_pw() <= 3) {
            us.insertPw(lp);
        } else {
            us.updatePw(lp);
        }
        UserResponse ur = new UserResponse(u.getUsername(), u.getRole(), 0, false);
        return ResponseEntity.ok(ur);
    }

    @PostMapping(value = "/posttime", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public SingleValues postTime(@RequestBody SingleValues sv) {
        return us.updateTimeSlot(sv);
    }

    // @PreAuthorize("!isAuthenticated()")
    @PostMapping(value = "/userlogin")
    public ResponseEntity<UserResponse> login(@RequestBody User user, HttpServletRequest request) {
        User u = us.getUserByName(user.getUsername());
        if(request.getSession(false) != null) {
            try{request.logout();}
            catch (ServletException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }
        try {
            request.login(user.getUsername(), user.getPassword());
        } catch (ServletException e) {
            e.printStackTrace();
            // System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        long diff = ChronoUnit.MINUTES.between(u.getChange_date(), LocalDateTime.now());
        UserResponse resp = new UserResponse(u.getUsername(), u.getRole(), diff,(diff >= us.getTimeSlot("timeslot")));
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/postauth", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> postAuth(@RequestBody ApiAuth auth) {
        User u = us.getUserByName(auth.getUsername());
        String token;
        if (u != null && e.matches(auth.getPassword(), u.getPassword())) {
            token = jwtUtility.generateToken(auth.getUsername());
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized.");
    }

    @PutMapping(value = "/userlogout")
    public ResponseEntity<Void> logout(HttpServletRequest req, HttpSession session) {
        try {
            req.logout();
            return ResponseEntity.ok(null);
        } catch (ServletException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
}