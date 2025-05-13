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
import com.sila.login.model.UserActivity;
import com.sila.login.model.ActivityType;
import com.sila.login.repository.LastPasswordsRepository;
import com.sila.login.repository.SingleValRepo;
import com.sila.login.repository.UserRepository;
import com.sila.login.repository.UserActivityRepository;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private static final int MAX_PASSWORD_HISTORY = 5;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LastPasswordsRepository passwordRepository;
    
    @Autowired
    private SingleValRepo singleValRepo;
    
    @Autowired
    private UserActivityRepository userActivityRepository;
    
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
        
        // Log user creation activity
        logUserActivity(user.getUsername(), ActivityType.USER_CREATED, "User account created");
        
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
        // Log before deletion
        logUserActivity(username, ActivityType.USER_DELETED, "User account deleted");
        userRepository.deleteById(username);
    }
    
    @Override
    public void deleteUser(User user) {
        if (user != null) {
            // Log before deletion
            logUserActivity(user.getUsername(), ActivityType.USER_DELETED, "User account deleted");
        }
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
        if (password != null && password.getUser() != null) {
            logUserActivity(password.getUser().getUsername(), ActivityType.PASSWORD_CHANGED, 
                    "Password updated (history entry " + password.getPw_no() + ")");
        }
        return passwordRepository.save(password);
    }
    
    @Override
    public void updatePw(LastPasswords lp) {
        if (lp != null && lp.getUser() != null) {
            logUserActivity(lp.getUser().getUsername(), ActivityType.PASSWORD_CHANGED, 
                    "Password history entry updated (entry " + lp.getPw_no() + ")");
        }
        passwordRepository.updatePw(lp.getUser().getUsername(), lp.getPw_no(), lp.getPw());
    }
    
    @Override
    public User updateUser(User user) {
        if (user != null) {
            logUserActivity(user.getUsername(), ActivityType.USER_UPDATED, "User account details updated");
        }
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
        User user = userRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        // Log successful user lookup
        logUserActivity(username, ActivityType.LOGIN_ATTEMPT, "User authentication lookup");
        
        return user;
    }
    
    /**
     * Logs a user activity event
     * 
     * @param username The username of the user
     * @param activityType The type of activity
     * @param description Description of the activity
     * @return The saved UserActivity record
     */
    public UserActivity logUserActivity(String username, ActivityType activityType, String description) {
        UserActivity activity = new UserActivity();
        activity.setUsername(username);
        activity.setActivityType(activityType);
        activity.setDescription(description);
        activity.setTimestamp(LocalDateTime.now());
        activity.setIpAddress(getCurrentIpAddress());
        
        return userActivityRepository.save(activity);
    }
    
    /**
     * Records a failed login attempt for a user
     * 
     * @param username The username that failed to login
     * @param reason The reason for the failure
     */
    public void logFailedLogin(String username, String reason) {
        logUserActivity(username, ActivityType.LOGIN_FAILED, "Failed login attempt: " + reason);
    }
    
    /**
     * Records a successful login for a user
     * 
     * @param username The username that logged in
     */
    public void logSuccessfulLogin(String username) {
        logUserActivity(username, ActivityType.LOGIN_SUCCESS, "User logged in successfully");
    }
    
    /**
     * Records a user logout event
     * 
     * @param username The username that logged out
     */
    public void logLogout(String username) {
        logUserActivity(username, ActivityType.LOGOUT, "User logged out");
    }
    
    /**
     * Get activity history for a specific user
     * 
     * @param username The username to get history for
     * @param limit Maximum number of records to return (0 for all)
     * @return List of user activities
     */
    public List<UserActivity> getUserActivityHistory(String username, int limit) {
        if (limit > 0) {
            return userActivityRepository.findLatestActivitiesByUsername(username, limit);
        } else {
            return userActivityRepository.findByUsernameOrderByTimestampDesc(username);
        }
    }
    
    /**
     * Get recent activity across all users
     * 
     * @param limit Maximum number of records to return
     * @return List of recent user activities
     */
    public List<UserActivity> getRecentActivity(int limit) {
        return userActivityRepository.findRecentActivity(limit);
    }
    
    /**
     * Helper method to get the current IP address
     * This would typically be injected from a web context
     * 
     * @return The current IP address or "unknown" if not available
     */
    private String getCurrentIpAddress() {
        // In a real implementation, this would get the IP from the HTTP request context
        // This is a placeholder implementation
        return "unknown";
    }
}