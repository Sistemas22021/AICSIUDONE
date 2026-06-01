package com.sso.users.infrastructure.adapter.in.web;

import com.sso.users.application.usecase.CreateUserService;
import com.sso.users.application.usecase.MaintainerUserService;
import com.sso.users.infrastructure.adapter.in.web.dto.CreateUserDto;
import com.sso.users.infrastructure.adapter.in.web.dto.UpdateUserDto;
import com.sso.users.infrastructure.adapter.out.persistence.UserJpaEntity;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final MaintainerUserService maintainerService;
    private final CreateUserService createUserService;

    public UserController(MaintainerUserService maintainerService, CreateUserService createUserService) {
        this.maintainerService = maintainerService;
        this.createUserService = createUserService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<UserJpaEntity> createUser(@Valid @ModelAttribute CreateUserDto dto) throws Exception {
        UserJpaEntity user = createUserService.execute(
                dto.getUsername(), dto.getEmail(), dto.getFullName(),
                dto.getPhoto().getBytes(), dto.getPhoto().getOriginalFilename()
        );
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserJpaEntity> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserDto request) {
        
        UserJpaEntity user = maintainerService.updateUser(id, request.getFullName());
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID id,
            @RequestParam("permanent") boolean permanent) {
        maintainerService.deleteUserLogic(id, permanent);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable UUID id) {
        maintainerService.resetPassword(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<UserJpaEntity>> listUsers(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(maintainerService.listActiveUsersPaginated(search, pageable));
    }
}

