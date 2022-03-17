package com.reloadly_task.transactionservice.service;

import com.reloadly_task.transactionservice.dto.request.TransactionRequest;
import com.reloadly_task.transactionservice.model.Transactions;

public interface TransactionsService {

    Transactions createNewDepositTransaction(TransactionRequest transactionRequest);
    Transactions createNewWithdrawalTransaction(TransactionRequest transactionRequest);

    String setSuccessfulTransactions(Long id);
}
