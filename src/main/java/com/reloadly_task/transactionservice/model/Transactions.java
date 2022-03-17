package com.reloadly_task.transactionservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


@EqualsAndHashCode(callSuper = false)
@Entity
@Data
public class Transactions extends GeneralBaseModel{
    private String narration;
    @NotNull(message = "Please enter an amount")
    private BigDecimal amount;
    private String transactionType;
    @Column(nullable = false)
    private String username;
    private BigDecimal accountBalance;
    private String status;
    private Long userId;

}
