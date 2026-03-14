package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IncentiveService incentiveService;

    public TransactionService(
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            IncentiveService incentiveService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveService = incentiveService;
    }

    @Transactional
    public boolean process(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        if (sender == null || recipient == null) {
            return false;
        }
        if (sender.getBalance() < transaction.getAmount()) {
            return false;
        }

        float incentive = incentiveService.fetchIncentive(transaction);
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentive);

        userRepository.save(sender);
        userRepository.save(recipient);
        transactionRepository.save(new TransactionRecord(sender, recipient, transaction.getAmount(), incentive));
        return true;
    }
}
