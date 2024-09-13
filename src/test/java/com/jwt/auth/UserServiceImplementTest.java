package com.jwt.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.jwt.auth.model.User;
import com.jwt.auth.model.UserRedis;
import com.jwt.auth.model.Role;
import com.jwt.auth.service.implement.UserServiceImplement;

class UserServiceImplementTest {

    @Mock
    private RedisTemplate<String, UserRedis> redisTemplate;

    @Mock
    private ValueOperations<String, UserRedis> valueOperations;

    @InjectMocks
    private UserServiceImplement userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testSaveUserSimple() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setRole(Role.USER);  // Set role to Role.USER

        // Act
        userService.saveUser(user);

        // Assert
        verify(valueOperations, times(1)).set(eq(user.getUsername()), any(UserRedis.class));
    }

    @Test
    void testFindUserByUsernameSimple() {
        // Arrange
        String username = "testUser";
        UserRedis userRedis = new UserRedis();
        userRedis.setUsername(username);
        userRedis.setPassword("testPassword");
        userRedis.setRole(Role.USER);

        when(redisTemplate.opsForValue().get(username)).thenReturn(userRedis);

        // Act
        User user = userService.findUserByUsername(username);

        // Assert
        assertEquals(username, user.getUsername());
        assertEquals("testPassword", user.getPassword());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void testFindUserByUsernameNotFound() {
        // Arrange
        String username = "nonExistingUser";
        when(redisTemplate.opsForValue().get(username)).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            userService.findUserByUsername(username);
        });

        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testSaveUserDataCorrectness() {
        // Arrange
        User user = new User();
        user.setUsername("dataUser");
        user.setPassword("dataPassword");
        user.setRole(Role.ADMIN);  // Assign Role.ADMIN for this test case

        // Act
        userService.saveUser(user);

        // Capture the UserRedis that is passed to ValueOperations.set()
        verify(valueOperations, times(1)).set(eq(user.getUsername()), argThat(userRedis ->
                userRedis.getUsername().equals("dataUser") &&
                        userRedis.getPassword().equals("dataPassword") &&
                        userRedis.getRole().equals(Role.ADMIN)
        ));
    }
}
