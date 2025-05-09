package com.finbox.subscrititionservice.controller;


import com.finbox.subscrititionservice.exception.ResourceNotFoundException;
import com.finbox.subscrititionservice.exception.SubscriptionServiceException;
import com.finbox.subscrititionservice.models.entities.ClientSubscription;
import com.finbox.subscrititionservice.models.request.ClientRequest;
import com.finbox.subscrititionservice.models.request.ClientResponse;
import com.finbox.subscrititionservice.models.request.ClientSubscriptionRequest;
import com.finbox.subscrititionservice.models.response.ClientFeatures;
import com.finbox.subscrititionservice.models.response.ClientSubscriptionResponse;
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

    /**
     * * Create a new client
     * @param client
     * @return
     * @throws SubscriptionServiceException
     */
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

    /**
     * * Toggle a feature for a client
     * @param clientId
     * @param featureId
     * @param enable
     * @return
     * @throws SubscriptionServiceException
     */
    @PostMapping("/{clientId}/features/{featureId}/toggle")
    public ResponseEntity<?> toggleFeature(
            @PathVariable String clientId,
            @PathVariable Long featureId,
            @RequestParam boolean enable) throws SubscriptionServiceException {
        String response = clientService.toggleFeatureForClient(clientId, featureId, enable);
        CommonResponse commonResponse = CommonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Feature toggled successfully")
                .success(true)
                .data(response)
                .build();
        return ResponseEntity.ok(commonResponse);
    }

    /**
     * * Get all enabled features for a client
     * @param clientId
     * @return
     */
    @GetMapping("/{clientId}/features")
    public ResponseEntity<?> getAllEnabledFeatures(
            @PathVariable String clientId) throws ResourceNotFoundException {
        ClientFeatures response = clientService.getAllEnabledFeatures(clientId);

        CommonResponse commonResponse = CommonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .status("success")
                .success(true)
                .message("Fetched all enabled client feature successfully")
                .data(response)
                .build();
        return ResponseEntity.ok(commonResponse);
    }

    /**
     * * Get the status of a feature flag for a client
     * @param clientId
     * @param featureId
     * @return
     */
    @GetMapping("/{clientId}/features/status")
    public ResponseEntity<?> getFeatureFlagStatus(
            @PathVariable String clientId,
            @RequestParam Long featureId) throws ResourceNotFoundException {
        boolean isEnabled = clientService.getFeatureFlagStatus(clientId, featureId);

        CommonResponse commonResponse = CommonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .status("success")
                .message("Fetched feature status successfully")
                .data(isEnabled)
                .success(true)
                .build();
        return ResponseEntity.ok(commonResponse);
    }

    @PostMapping("/subscription")
    public ResponseEntity<?> createClientSubscription(@RequestBody ClientSubscriptionRequest clientSubscriptionRequest) throws SubscriptionServiceException {

        ClientSubscriptionResponse clientSubscriptionResponse = clientService.createSubscription(clientSubscriptionRequest);

        CommonResponse response = CommonResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Client subscription created successfully")
                .success(true)
                .data(clientSubscriptionResponse)
                .build();
        return ResponseEntity.ok(response);
    }


}
