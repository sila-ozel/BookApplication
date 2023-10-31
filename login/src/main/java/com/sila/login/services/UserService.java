package com.sila.login.services;

import com.sila.login.model.LastPasswords;
import com.sila.login.model.SingleValues;
import com.sila.login.model.User;
import java.util.List;

public interface UserService {
    User saveUser(User user);
    List<User> getAllUsers();
    void deleteUserByName(String username);
    void deleteUser(User user);
    User getUserByName(String username);
    List<LastPasswords> findAllPwsByUsername(String username);
    LastPasswords findPwByUsernameAndNo(String username, int pw_no);
    LastPasswords insertPw(LastPasswords password);
    void updatePw(LastPasswords lp);
    User updateUser(User user);
    SingleValues updateTimeSlot(SingleValues timeSlot);
    int getTimeSlot(String valueName);
}
