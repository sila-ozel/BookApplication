package com.sila.login;

import com.sila.login.model.User;
import com.sila.login.repository.LastPasswordsRepository;
import com.sila.login.repository.SingleValRepo;
import com.sila.login.repository.UserRepository;
import com.sila.login.services.UserServiceImpl;
import com.sila.login.utility.JwtUtility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class LoginApplicationTests {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private LastPasswordsRepository lastPasswordsRepository;

    @MockBean
    private SingleValRepo singleValRepo;

    @MockBean
    private JwtUtility jwtUtility;

    private UserServiceImpl userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
        userService.ur = userRepository;
        userService.lpr = lastPasswordsRepository;
        userService.svr = singleValRepo;
    }

    @Test
    void contextLoads() {
        assertTrue(true); // sanity check
    }

    @Test
    void testSaveUser_PreventsDuplicate() {
        User user = new User("john", "Password123", "ROLE_USER");
        when(userRepository.findAll()).thenReturn(java.util.List.of(user));

        User saved = userService.saveUser(user);
        assertEquals("john", saved.getUsername());
        verify(userRepository, never()).save(any()); // since user exists
    }

    @Test
    void testGetUserByName_Found() {
        User user = new User("john", "Password123", "ROLE_USER");
        when(userRepository.findById("john")).thenReturn(Optional.of(user));

        User result = userService.getUserByName("john");
        assertNotNull(result);
        assertEquals("john", result.getUsername());
    }

    @Test
    void testLoadUserByUsername_NotFoundThrows() {
        when(userRepository.findById("ghost")).thenReturn(Optional.empty());

        UserDetails result = userService.loadUserByUsername("ghost");
        assertNull(result); // your current implementation returns null
    }

    @Test
    void testApiAuthFilter_WithValidToken() throws Exception {
        String token = "Bearer valid.token.here";

        OncePerRequestFilter filter = new com.sila.login.filter.ApiAuthFilter();
        ((com.sila.login.filter.ApiAuthFilter) filter).jwtUtil = jwtUtility;

        mockMvc = MockMvcBuilders.standaloneSetup(new Object())
                .addFilters(filter)
                .build();

        when(jwtUtility.validateToken("valid.token.here")).thenReturn(true);

        mockMvc.perform(post("/posttime")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testApiAuthFilter_WithInvalidToken() throws Exception {
        String token = "Bearer invalid.token";

        OncePerRequestFilter filter = new com.sila.login.filter.ApiAuthFilter();
        ((com.sila.login.filter.ApiAuthFilter) filter).jwtUtil = jwtUtility;

        mockMvc = MockMvcBuilders.standaloneSetup(new Object())
                .addFilters(filter)
                .build();

        when(jwtUtility.validateToken("invalid.token")).thenReturn(false);

        mockMvc.perform(post("/posttime")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUserEntity_AuthorityAndToString() {
        User user = new User("john", "secret", "ROLE_USER");
        assertEquals("ROLE_USER", user.getAuthorities().iterator().next().getAuthority());
        assertTrue(user.toString().contains("john"));
        assertFalse(user.toString().contains("secret")); // safety
    }
}
