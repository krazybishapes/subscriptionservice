package com.finbox.subscrititionservice.models.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResponse {


    private boolean success;
    private Object data;
    private String status;
    private int statusCode;
    private String message;
    private String errorMessage;
    private String errorCode;
}
