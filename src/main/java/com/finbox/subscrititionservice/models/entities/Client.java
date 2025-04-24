package com.finbox.subscrititionservice.models.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, name = "client_id")
    private String clientId;
    @Column(unique = true)
    //@Email(message = "Email should be valid")
    private String email;
    @Column(unique = true)
    //@Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    //@Phone(message = "Phone number should be valid")
    private String phone;
    private String name;
    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}

