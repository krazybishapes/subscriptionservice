package com.finbox.subscrititionservice.models.request;


import com.finbox.subscrititionservice.models.entities.Client;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
public class ClientRequest {
    private String clientId;
    private String name;
    private String email;
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
