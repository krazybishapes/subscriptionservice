package com.finbox.subscrititionservice.models.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ClientSubscriptionResponse {

    private String clientId;
    private String subscriptionId;

}
