package com.coopera2075.cooperanki.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity 
@Table (name = "auth_identities")
@Getter 
@Setter 
@NoArgsConstructor 
@ToString 
@EqualsAndHashCode (onlyExplicitlyIncluded = true)
public class AuthIdentity {
    @Id 
    @EqualsAndHashCode.Include
    private Long id; //Crear constructor con id para poder crear un AuthIdentity con un User ya existente

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUser user;

    @NotBlank (message = "Email is required")
    @Email (message = "Email must be valid")
    @Column (name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank (message = "Password is required")
    @Size (min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Column (name = "password_hash", nullable = false)
    private String password;
}
