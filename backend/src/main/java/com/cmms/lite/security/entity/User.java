package com.cmms.lite.security.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name cannot be blank.")
    @Size(max = 50)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name cannot be blank.")
    @Size(max = 50)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Size(min = 8, max = 255)
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    private Set<Role> roles;

    @Transient
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return "";
    }
}