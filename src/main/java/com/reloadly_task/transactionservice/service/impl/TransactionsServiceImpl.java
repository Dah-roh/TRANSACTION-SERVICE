package com.reloadly_task.transactionservice.service.impl;

import com.reloadly_task.transactionservice.dto.request.EmailNotificationRequest;
import com.reloadly_task.transactionservice.dto.request.TransactionRequest;
import com.reloadly_task.transactionservice.model.Transactions ;
import com.reloadly_task.transactionservice.repository.TransactionRepository;
import com.reloadly_task.transactionservice.service.TransactionsService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Optional;


//@Async
@Service
@Transactional
public class TransactionsServiceImpl implements TransactionsService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    @Value("${account.service}")private  String url;

    public TransactionsServiceImpl(TransactionRepository transactionRepository, NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
    }


    @Override
    public Transactions createNewDepositTransaction(TransactionRequest transactionRequest) {
        Transactions transaction = new Transactions();
        ModelMapper modelMapper = new ModelMapper();
        try {
            Mono<BigDecimal> accountBalance = getBalance("/account/details/"+transactionRequest.getUserId());
            BigDecimal newBalance = new BigDecimal(String.valueOf(accountBalance.block())).add(transactionRequest.getAmount());
            transaction = modelMapper.map(transactionRequest, Transactions.class);
            transaction.setStatus("PENDING");
            transaction.setTransactionType("DEPOSIT");
            transaction.setAccountBalance(newBalance);
            Transactions transactions = transactionRepository.save(transaction);
            updateTransactionsAccountBalance("/account/update/"+transactionRequest.getUserId()+"/"+newBalance, newBalance, transactions.getUserId());
            notificationService.sendMail(new EmailNotificationRequest("darogadibia@gmail.com",
                    "darogadibia@gmail.com",
                    "DEPOSIT SUCCESSFUL",
                    "A successful transaction of N"+transactions.getAmount()
                            +", has just occurred!"), "http://localhost:9092", "/notification/email");
            return transactionRepository.findById(transactions.getId()).get();
        } catch (Exception e) {
            log.info("Deposit failure :" + e.getCause());
            transaction.setStatus("FAILED");
            transactionRepository.save(transaction);
            throw e;
        }
    }

    private Mono<BigDecimal> getBalance(String uri) {
        WebClient webClient = WebClient.create(url);
        return webClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BigDecimal.class);
    }

    private String updateTransactionsAccountBalance(String uri, BigDecimal newBalance, Long id) {
        WebClient webClient = WebClient.create(url);
        return webClient.put()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(String.class).block();
    }

    @Override
    public Transactions createNewWithdrawalTransaction(TransactionRequest transactionRequest) {
        Transactions transaction = new Transactions();
        ModelMapper modelMapper = new ModelMapper();
        try{
            Mono<BigDecimal> accountBalance = getBalance("/account/details/"+transactionRequest.getUserId());
            BigDecimal newBalance = new BigDecimal(String.valueOf(accountBalance.block())).subtract(transactionRequest.getAmount());
            transaction = modelMapper.map(transactionRequest, Transactions.class);
            transaction.setStatus("PENDING");
            transaction.setTransactionType("WITHDRAWAL");
            transaction.setAccountBalance(newBalance);
            Transactions transactions = transactionRepository.save(transaction);
            updateTransactionsAccountBalance("/account/update/"+transactionRequest.getUserId()+"/"+newBalance, newBalance, transactions.getUserId());
            notificationService.sendMail(new EmailNotificationRequest("darogadibia@gmail.com",
                    "darogadibia@gmail.com",
                    "WITHDRAWAL SUCCESSFUL",
                    "A successful transaction of N"+transactions.getAmount()
                            +", has just occurred!"), "http://localhost:9092", "/notification/email");
            return transactionRepository.findById(transactions.getId()).get();
    } catch(Exception e)

    {
        log.info("Deposit failure :" + e.getMessage());
        transaction.setStatus("FAILED");
        throw e;
    }

}


    @Override
    public String setSuccessfulTransactions(Long id) {
        Optional<Transactions> transaction;
        try {
        transaction = transactionRepository.findById(id);
            if (transaction.isPresent()) {
                Transactions transactions = transaction.get();
                transactions.setStatus("SUCCESSFUL");
                Transactions transactions1 = transactionRepository.save(transactions);
                System.out.println(transactions1.getStatus());
                return "Transactions Successful";
            }
        }
        catch (Exception e){
            log.info("status update failed :", e.getMessage());
        }
        return "Transactions failed";
    }
}