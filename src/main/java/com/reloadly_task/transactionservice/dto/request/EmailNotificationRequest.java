package com.reloadly_task.transactionservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailNotificationRequest {

    private String from;
    private String to;

    private String subject;

    private String narration;
}
