package com.finbox.subscrititionservice.models.request;

import com.finbox.subscrititionservice.models.entities.Client;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
public class ClientRequest {

    @NotBlank(message = "Client ID cannot be blank")
    private String clientId;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phone;

    public Client toClient() {
        return Client.builder()
                .clientId(clientId)
                .name(name)
                .email(email)
                .phone(phone)
                .isActive(true)
                .build();
    }
}