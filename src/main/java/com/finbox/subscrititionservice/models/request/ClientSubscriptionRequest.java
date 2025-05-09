package com.finbox.subscrititionservice.models.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter
@Setter
public class ClientSubscriptionRequest {

    private String clientId;
    private String subscriptionId;
    private String subscriptionCategoryId;
    private Date startDate;
}


