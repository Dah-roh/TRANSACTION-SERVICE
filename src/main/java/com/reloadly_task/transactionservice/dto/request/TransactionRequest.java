package com.reloadly_task.transactionservice.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TransactionRequest {

    @NotBlank(message = "please enter the narration")
    private String narration;

    @NotNull(message = "please enter the amount")
    private BigDecimal amount;

    @NotBlank(message = "please enter your email as username")
    @Email(message = "please enter a valid email")
    private String username;

    @NotNull(message = "please enter the user id")
    private Long userId;
}
