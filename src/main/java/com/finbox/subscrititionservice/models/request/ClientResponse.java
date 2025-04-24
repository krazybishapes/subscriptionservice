package com.finbox.subscrititionservice.models.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ClientResponse {
    private String clientId;
    private String name;
    private String email;
    private String phone;
}
