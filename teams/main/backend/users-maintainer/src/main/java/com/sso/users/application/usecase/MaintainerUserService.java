package com.sso.users.application.usecase;

import com.sso.users.domain.model.UserStatus;
import com.sso.users.domain.port.out.EventPublisherPort;
import com.sso.users.infrastructure.adapter.out.persistence.UserJpaEntity;
import com.sso.users.infrastructure.adapter.out.persistence.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MaintainerUserService {

    private final UserRepository userRepository;
    private final EventPublisherPort eventPublisherPort;

    public MaintainerUserService(UserRepository userRepository, EventPublisherPort eventPublisherPort) {
        this.userRepository = userRepository;
        this.eventPublisherPort = eventPublisherPort;
    }

    public UserJpaEntity updateUser(UUID userId, String fullName) {
        UserJpaEntity user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setFullName(fullName);
        UserJpaEntity savedUser = userRepository.save(user);

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", savedUser.getId());
        payload.put("fullName", savedUser.getFullName());
        eventPublisherPort.publish("users-topic", "UserUpdatedEvent", payload);

        return savedUser;
    }

    public void deleteUserLogic(UUID userId, boolean permanent) {
        UserJpaEntity user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setStatus(permanent ? UserStatus.BANNED : UserStatus.INACTIVE);
        userRepository.save(user);

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", user.getId());
        payload.put("status", user.getStatus().name());
        eventPublisherPort.publish("users-topic", "UserStatusChangedEvent", payload);
    }

    public Page<UserJpaEntity> listActiveUsersPaginated(String search, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            return userRepository.findByStatusNotAndFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(
                    UserStatus.BANNED, search, search, pageable
            );
        }
        return userRepository.findByStatusNot(UserStatus.BANNED, pageable);
    }
    
    public void resetPassword(UUID userId) {
        UserJpaEntity user = userRepository.findById(userId).orElseThrow();
        String tempPass = "{bcrypt}" + UUID.randomUUID().toString();
        user.setPassword(tempPass);
        user.setRequirePasswordChange(true);
        userRepository.save(user);

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", user.getId());
        payload.put("email", user.getEmail());
        payload.put("tempPassword", tempPass);
        eventPublisherPort.publish("users-topic", "UserPasswordResetEvent", payload);
    }
}
