package com.sila.login.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sila.login.model.LastPasswords;
import com.sila.login.model.SingleValues;
import com.sila.login.model.User;
import com.sila.login.repository.LastPasswordsRepository;
import com.sila.login.repository.SingleValRepo;
import com.sila.login.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private static final int MAX_PASSWORD_HISTORY = 5;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LastPasswordsRepository passwordRepository;

    @Autowired
    private SingleValRepo singleValRepo;

    @Override
    public User saveUser(User user) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");

        Optional<User> existingUser = userRepository.findById(user.getUsername());
        if (existingUser.isPresent()) {
            return existingUser.get(); // Don't save duplicate
        }

        user.setChange_date(LocalDateTime.now());
        user.setNo_of_pw(1);
        User savedUser = userRepository.save(user);

        LastPasswords initialPassword = new LastPasswords(user, 1, user.getPassword());
        passwordRepository.save(initialPassword);

        return savedUser;
    }

    @Override
    public User getUserByName(String username) {
        return userRepository.findById(username).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public void deleteUserByName(String username) {
        userRepository.deleteById(username);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public List<LastPasswords> findAllPwsByUsername(String username) {
        return passwordRepository.findPwsByUsername(username);
    }

    @Override
    public LastPasswords findPwByUsernameAndNo(String username, int pwNo) {
        return passwordRepository.findPwByNameAndNo(username, pwNo);
    }

    @Override
    public LastPasswords insertPw(LastPasswords password) {
        return passwordRepository.save(password);
    }

    @Override
    public void updatePw(LastPasswords lp) {
        passwordRepository.updatePw(lp.getUser().getUsername(), lp.getPw_no(), lp.getPw());
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public SingleValues updateTimeSlot(SingleValues timeSlot) {
        return singleValRepo.save(timeSlot);
    }

    @Override
    public int getTimeSlot(String valueName) {
        return singleValRepo.findById(valueName)
            .map(SingleValues::getValue)
            .orElseThrow(() -> new IllegalStateException("Time slot value not found: " + valueName));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
