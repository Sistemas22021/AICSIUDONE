package com.sso.users.application.usecase;

import com.sso.users.domain.model.UserStatus;
import com.sso.users.domain.port.out.EventPublisherPort;
import com.sso.users.domain.port.out.FileStoragePort;
import com.sso.users.infrastructure.adapter.out.persistence.UserJpaEntity;
import com.sso.users.infrastructure.adapter.out.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStoragePort fileStoragePort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @InjectMocks
    private CreateUserService createUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldThrowExceptionWhenPhotoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            createUserService.execute("user1", "u1@t.com", "User 1", null, "null.jpg");
        });
    }

    @Test
    void shouldCreateUserSuccessfully() {
        when(fileStoragePort.storeFile(any(), any(), any())).thenReturn("/api/users/photos/1.jpg");
        
        UserJpaEntity mockSavedUser = UserJpaEntity.builder()
                .username("user1")
                .email("u1@t.com")
                .status(UserStatus.ACTIVE)
                .build();
        
        when(userRepository.save(any(UserJpaEntity.class))).thenReturn(mockSavedUser);

        UserJpaEntity result = createUserService.execute("user1", "u1@t.com", "User 1", new byte[]{1,2,3}, "photo.jpg");

        assertEquals("user1", result.getUsername());
        verify(userRepository, times(1)).save(any(UserJpaEntity.class));
        verify(eventPublisherPort, times(1)).publish(eq("users-topic"), eq("UserCreatedEvent"), any());
    }
}
