package com.reloadly_task.transactionservice.controller;

import com.reloadly_task.transactionservice.dto.request.TransactionRequest;
import com.reloadly_task.transactionservice.model.Transactions;
import com.reloadly_task.transactionservice.service.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionsService transactionService;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/deposit")
    ResponseEntity<?> createNewDepositTransaction(
            @Valid @RequestBody TransactionRequest transactionRequest){
        Transactions transactions = transactionService.createNewDepositTransaction(transactionRequest);
        if (transactions!=null){
            transactionService.setSuccessfulTransactions(transactions.getId());
        }
        return new ResponseEntity<>(transactions, HttpStatus.CREATED);
    };

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/withdrawal")
    ResponseEntity<?> createNewWithdrawalTransaction(
            @Valid @RequestBody TransactionRequest transactionRequest){
        Transactions transactions = transactionService.createNewWithdrawalTransaction(transactionRequest);
        if (transactions!=null){
            transactionService.setSuccessfulTransactions(transactions.getId());
        }
        return new ResponseEntity<>(transactions, HttpStatus.CREATED);
    };

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("status/{transactionId}")
    ResponseEntity<?> setSuccessfulTransactions(
            @PathVariable Long transactionId){
        return new ResponseEntity<>(transactionService.setSuccessfulTransactions(transactionId), HttpStatus.OK);
    };

}
