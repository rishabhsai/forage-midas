package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveService {
    private final RestTemplate restTemplate;
    private final String incentiveApiUrl;

    public IncentiveService(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${incentive.api.url:http://localhost:8080/incentive}") String incentiveApiUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.incentiveApiUrl = incentiveApiUrl;
    }

    public float fetchIncentive(Transaction transaction) {
        try {
            Incentive incentive = restTemplate.postForObject(incentiveApiUrl, transaction, Incentive.class);
            return incentive == null ? 0F : incentive.getAmount();
        } catch (RestClientException exception) {
            return 0F;
        }
    }
}
