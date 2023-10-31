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
public class UserServiceImpl implements UserService, UserDetailsService{

    @Autowired
    UserRepository ur;

    @Autowired
    LastPasswordsRepository lpr;

    @Autowired
    SingleValRepo svr;

    @Override
    public User saveUser(User user) {
        assert user!=null;
        List<User> users = getAllUsers();
        //if a user with same primary key already exists this method should not save
        for(User u: users) {
            if(u.equals(user))
                return u;
        }
        user.setChange_date(LocalDateTime.now());
        user.setNo_of_pw(1);
        User u = ur.save(user);
        LastPasswords l = new LastPasswords(user, user.getNo_of_pw() % 4, user.getPassword());
        lpr.save(l);
        return u;
    }

    @Override
    public User getUserByName(String username) {
        if(ur.findById(username).isPresent()) {
            return ur.findById(username).get();
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = (List<User>) ur.findAll();
        return users;
    }

    @Override
    public void deleteUserByName(String username) {
        ur.deleteById(username);
    }

    @Override
    public void deleteUser(User user) {
        ur.delete(user);
    }

    @Override
    public List<LastPasswords> findAllPwsByUsername(String username) {
        return lpr.findPwsByUsername(username);
    }

    @Override
    public LastPasswords findPwByUsernameAndNo(String username, int pw_no) {
        return lpr.findPwByNameAndNo(username, pw_no);
    }

    @Override
    public LastPasswords insertPw(LastPasswords password) {
        return lpr.save(password);
    }

    @Override
    public void updatePw(LastPasswords lp) {
        lpr.updatePw(lp.getUser().getUsername(), lp.getPw_no(), lp.getPw());
    }

    @Override
    public User updateUser(User user) {
        return ur.save(user);
    }

    @Override
    public SingleValues updateTimeSlot(SingleValues timeSlot) {
        return svr.save(timeSlot);
    }

    @Override
    public int getTimeSlot(String valueName) {
        return svr.findById(valueName).get().getValue();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> u = ur.findById(username);
        if(u.isPresent())
            return u.get();
        return null;
    }
}
