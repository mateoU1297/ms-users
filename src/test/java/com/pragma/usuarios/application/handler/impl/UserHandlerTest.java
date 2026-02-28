package com.pragma.usuarios.application.handler.impl;

import com.pragma.usuarios.application.dto.JwtResponse;
import com.pragma.usuarios.application.dto.LoginRequest;
import com.pragma.usuarios.application.mapper.IJwtResponseMapper;
import com.pragma.usuarios.domain.api.IAuthenticationServicePort;
import com.pragma.usuarios.domain.api.IJwtServicePort;
import com.pragma.usuarios.domain.api.IUserServicePort;
import com.pragma.usuarios.domain.model.User;
import com.pragma.usuarios.domain.model.enums.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserHandlerTest {

    @Mock
    private IJwtResponseMapper jwtResponseMapper;

    @Mock
    private IJwtServicePort jwtServicePort;

    @Mock
    private IUserServicePort userServicePort;

    @Mock
    private IAuthenticationServicePort authenticationServicePort;

    @InjectMocks
    private UserHandler userHandler;

    private LoginRequest loginRequest;
    private User user;
    private JwtResponse jwtResponse;
    private Set<RoleName> roles;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        roles = new HashSet<>();
        roles.add(RoleName.ADMIN);
        roles.add(RoleName.CLIENT);

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("test@example.com");
        user.setDocumentId("123456789");
        user.setPhoneNumber("+1234567890");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setActive(true);

        jwtResponse = new JwtResponse();
        jwtResponse.setId(1L);
        jwtResponse.setFirstName("John");
        jwtResponse.setLastName("Doe");
        jwtResponse.setEmail("test@example.com");
        jwtResponse.setRoles(List.of("ADMIN", "CLIENT"));
    }

    @Test
    void testAuthenticate_Success() {
        String expectedToken = "jwt.token.123";

        doNothing().when(authenticationServicePort).authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        when(userServicePort.findByEmail(loginRequest.getEmail())).thenReturn(user);
        when(jwtResponseMapper.toJwtResponse(user)).thenReturn(jwtResponse);
        when(jwtServicePort.generateToken(user)).thenReturn(expectedToken);

        JwtResponse result = userHandler.authenticate(loginRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(expectedToken, result.getToken());
        assertTrue(result.getRoles().contains("ADMIN"));
        assertTrue(result.getRoles().contains("CLIENT"));
        assertEquals(2, result.getRoles().size());

        verify(authenticationServicePort, times(1)).authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
        verify(userServicePort, times(1)).findByEmail(loginRequest.getEmail());
        verify(jwtResponseMapper, times(1)).toJwtResponse(user);
        verify(jwtServicePort, times(1)).generateToken(user);
    }

    @Test
    void testAuthenticate_WhenAuthenticationFails_ShouldThrowException() {
        doThrow(new RuntimeException("Invalid credentials"))
                .when(authenticationServicePort)
                .authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userHandler.authenticate(loginRequest));

        assertEquals("Invalid credentials", exception.getMessage());

        verify(authenticationServicePort, times(1)).authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
        verify(userServicePort, never()).findByEmail(anyString());
        verify(jwtResponseMapper, never()).toJwtResponse(any());
        verify(jwtServicePort, never()).generateToken(any());
    }

    @Test
    void testAuthenticate_WhenUserNotFound_ShouldThrowException() {
        doNothing().when(authenticationServicePort).authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        when(userServicePort.findByEmail(loginRequest.getEmail()))
                .thenThrow(new RuntimeException("User not found"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userHandler.authenticate(loginRequest));

        assertEquals("User not found", exception.getMessage());

        verify(authenticationServicePort, times(1)).authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
        verify(userServicePort, times(1)).findByEmail(loginRequest.getEmail());
        verify(jwtResponseMapper, never()).toJwtResponse(any());
        verify(jwtServicePort, never()).generateToken(any());
    }

    @Test
    void testAuthenticate_WithNullLoginRequest_ShouldThrowException() {
        assertThrows(NullPointerException.class,
                () -> userHandler.authenticate(null));
    }

    @Test
    void testAuthenticate_WithNullEmail_ShouldThrowException() {
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setPassword("password123");
        invalidRequest.setEmail(null);

        doThrow(new IllegalArgumentException("Email cannot be null"))
                .when(authenticationServicePort)
                .authenticate(null, "password123");

        assertThrows(IllegalArgumentException.class,
                () -> userHandler.authenticate(invalidRequest));
    }

    @Test
    void testAuthenticate_WithNullPassword_ShouldThrowException() {
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("test@example.com");
        invalidRequest.setPassword(null);

        doThrow(new IllegalArgumentException("Password cannot be null"))
                .when(authenticationServicePort)
                .authenticate("test@example.com", null);

        assertThrows(IllegalArgumentException.class,
                () -> userHandler.authenticate(invalidRequest));
    }

    @Test
    void testAuthenticate_WithEmptyEmail_ShouldThrowException() {
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("");
        invalidRequest.setPassword("password123");

        doThrow(new IllegalArgumentException("Email cannot be empty"))
                .when(authenticationServicePort)
                .authenticate("", "password123");

        assertThrows(IllegalArgumentException.class,
                () -> userHandler.authenticate(invalidRequest));
    }

    @Test
    void testAuthenticate_WithEmptyPassword_ShouldThrowException() {
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("test@example.com");
        invalidRequest.setPassword("");

        doThrow(new IllegalArgumentException("Password cannot be empty"))
                .when(authenticationServicePort)
                .authenticate("test@example.com", "");

        assertThrows(IllegalArgumentException.class,
                () -> userHandler.authenticate(invalidRequest));
    }

    @Test
    void testAuthenticate_WhenMapperReturnsNull_ShouldThrowException() {
        doNothing().when(authenticationServicePort).authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        when(userServicePort.findByEmail(loginRequest.getEmail())).thenReturn(user);
        when(jwtResponseMapper.toJwtResponse(user)).thenReturn(null);

        assertThrows(NullPointerException.class,
                () -> userHandler.authenticate(loginRequest));
    }

    @Test
    void testAuthenticate_WhenTokenGenerationFails_ShouldThrowException() {
        doNothing().when(authenticationServicePort).authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        when(userServicePort.findByEmail(loginRequest.getEmail())).thenReturn(user);
        when(jwtResponseMapper.toJwtResponse(user)).thenReturn(jwtResponse);
        when(jwtServicePort.generateToken(user)).thenThrow(new RuntimeException("Token generation failed"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userHandler.authenticate(loginRequest));

        assertEquals("Token generation failed", exception.getMessage());
    }

    @Test
    void testAuthenticate_WithDifferentEmailCase_ShouldWorkCorrectly() {
        String emailWithDifferentCase = "Test@Example.com";
        loginRequest.setEmail(emailWithDifferentCase);
        loginRequest.setPassword("password123");

        String expectedToken = "jwt.token.123";

        doNothing().when(authenticationServicePort).authenticate(
                emailWithDifferentCase,
                "password123"
        );

        when(userServicePort.findByEmail(emailWithDifferentCase)).thenReturn(user);
        when(jwtResponseMapper.toJwtResponse(user)).thenReturn(jwtResponse);
        when(jwtServicePort.generateToken(user)).thenReturn(expectedToken);

        JwtResponse result = userHandler.authenticate(loginRequest);

        assertNotNull(result);
        assertEquals(expectedToken, result.getToken());
        verify(authenticationServicePort, times(1)).authenticate(
                emailWithDifferentCase,
                "password123"
        );
        verify(userServicePort, times(1)).findByEmail(emailWithDifferentCase);
    }

    @Test
    void testAuthenticate_WhenUserHasNoRoles_ShouldReturnEmptyRoles() {
        User userWithoutRoles = new User();
        userWithoutRoles.setId(2L);
        userWithoutRoles.setFirstName("Jane");
        userWithoutRoles.setLastName("Smith");
        userWithoutRoles.setEmail("jane@example.com");
        userWithoutRoles.setRoles(null);

        JwtResponse responseWithoutRoles = new JwtResponse();
        responseWithoutRoles.setId(2L);
        responseWithoutRoles.setFirstName("Jane");
        responseWithoutRoles.setLastName("Smith");
        responseWithoutRoles.setEmail("jane@example.com");
        responseWithoutRoles.setRoles(new ArrayList<>());

        String expectedToken = "jwt.token.456";

        doNothing().when(authenticationServicePort).authenticate(
                "jane@example.com",
                "password123"
        );

        LoginRequest request = new LoginRequest();
        request.setEmail("jane@example.com");
        request.setPassword("password123");

        when(userServicePort.findByEmail("jane@example.com")).thenReturn(userWithoutRoles);
        when(jwtResponseMapper.toJwtResponse(userWithoutRoles)).thenReturn(responseWithoutRoles);
        when(jwtServicePort.generateToken(userWithoutRoles)).thenReturn(expectedToken);

        JwtResponse result = userHandler.authenticate(request);

        assertNotNull(result);
        assertNotNull(result.getRoles());
        assertTrue(result.getRoles().isEmpty());
        assertEquals(expectedToken, result.getToken());
    }
}