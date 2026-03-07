package com.abraham_bankole.runestone_bank.user.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.abraham_bankole.runestone_bank.security.entity.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String otherName;
    private String gender;
    private String address;
    private String stateOfOrigin;
    private String accountNumber; // generated on account creation

    private BigDecimal accountBalance;
    private String email;
    private String password; // hashed to avoid clownery
    private String phoneNumber;
    private String alternativePhoneNumber;
    private String status;
    @Enumerated(EnumType.STRING)
    private Role role;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // neither is fully fleshed out yet, so for now its just true
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {
            return List.of(new SimpleGrantedAuthority(Role.ROLE_USER.name()));
        }
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }


    public String getFullName() {
        StringBuilder fullName = new StringBuilder(firstName).append(" ").append(lastName);
        if (otherName != null && !otherName.trim().isEmpty() && !otherName.trim().equalsIgnoreCase("null")) {
            fullName.append(" ").append(otherName.trim());
        }
        return fullName.toString();
    }
}
