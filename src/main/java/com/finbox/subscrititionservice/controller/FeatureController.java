package com.finbox.subscrititionservice.controller;



import com.finbox.subscrititionservice.exception.SubscriptionServiceException;
import com.finbox.subscrititionservice.models.entities.Feature;
import com.finbox.subscrititionservice.models.request.FeatureRequest;
import com.finbox.subscrititionservice.models.response.CommonResponse;
import com.finbox.subscrititionservice.service.FeatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/features")
public class FeatureController {
    private static final Logger log = LoggerFactory.getLogger(FeatureController.class);

    private final FeatureService featureService;

    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }

    /**
     * Create a new feature
     *
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<?> createFeature(@RequestBody FeatureRequest request) throws SubscriptionServiceException {
        log.info("Creating feature with request: {}", request);
        Feature feature = featureService.createFeature(request);
        CommonResponse created = CommonResponse.builder()
                .status("success")
                .statusCode(HttpStatus.CREATED.value())
                .success(true)
                .message("Feature created successfully")
                .data(feature)
                .build();
        return ResponseEntity.ok(created);
    }

    /**
     * * Get all features
     * @param code
     * @return
     */

    @GetMapping("/{code}/enabled")
    public ResponseEntity<?> isFeatureEnabled(@PathVariable String code) throws SubscriptionServiceException {
        boolean flag =  featureService.isFeatureEnabled(code);
        CommonResponse created = CommonResponse.builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Feature flag fetched successfully")
                .data(flag)
                .build();
        return ResponseEntity.ok(created);
    }

    /**
     * * Toggle a feature
     * @param code
     * @param enable
     * @return
     */

    @PostMapping("/{code}/toggle")
    public ResponseEntity<?> toggleFeature(@PathVariable String code, @RequestParam boolean enable) throws SubscriptionServiceException {
        Feature feature = featureService.toggleFeature(code, enable);
        CommonResponse response = CommonResponse.builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .message("Feature toggled successfully")
                .success(true)
                .data(feature)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * * Get all feature flags
     * @return
     * @throws SubscriptionServiceException
     */

    @GetMapping("/toggles")
    public ResponseEntity<?> getAllFeatureFlag() throws SubscriptionServiceException {
        List<Feature> feature = featureService.getAllFeatureFlag();
        CommonResponse response = CommonResponse.builder()
                .status("success")
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("All feature toggles fetched successfully")
                .data(feature)
                .build();
        return ResponseEntity.ok(response);
    }
}
