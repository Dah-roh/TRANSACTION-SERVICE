package com.reloadly_task.transactionservice.service.impl;

import com.reloadly_task.transactionservice.dto.request.EmailNotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
public class NotificationService {

    public Mono<String> sendMail(EmailNotificationRequest emailNotificationRequest, String url, String uri) {
        WebClient webClient = WebClient.create(url);
        return webClient.post()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(emailNotificationRequest), EmailNotificationRequest.class)
                .retrieve()
                .bodyToMono(String.class);
    }
}
