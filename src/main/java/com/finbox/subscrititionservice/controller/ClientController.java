package com.finbox.subscrititionservice.controller;


import com.finbox.subscrititionservice.exception.SubscriptionServiceException;
import com.finbox.subscrititionservice.models.request.ClientRequest;
import com.finbox.subscrititionservice.models.request.ClientResponse;
import com.finbox.subscrititionservice.models.response.CommonResponse;
import com.finbox.subscrititionservice.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<CommonResponse> createClient(@RequestBody ClientRequest client) throws SubscriptionServiceException {

        ClientResponse clientResponse = clientService.createClient(client);
        CommonResponse response = CommonResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Client created successfully")
                .success(true)
                .data(clientResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}
