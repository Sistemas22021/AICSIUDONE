package com.sso.users.infrastructure.adapter.out.persistence;

import com.sso.users.domain.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserJpaEntity, UUID>, JpaSpecificationExecutor<UserJpaEntity> {

    @Query("SELECT u FROM UserJpaEntity u WHERE u.status != :status AND (LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%')) OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')))")
    Page<UserJpaEntity> findByStatusNotAndFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(UserStatus status, String fullName, String username, Pageable pageable);

    Page<UserJpaEntity> findByStatusNot(UserStatus status, Pageable pageable);
}
