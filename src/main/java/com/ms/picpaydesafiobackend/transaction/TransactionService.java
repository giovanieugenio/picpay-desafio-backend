package com.ms.picpaydesafiobackend.transaction;


import com.ms.picpaydesafiobackend.authorization.AuthorizerService;
import com.ms.picpaydesafiobackend.exception.InvalidTransactionException;
import com.ms.picpaydesafiobackend.wallet.Wallet;
import com.ms.picpaydesafiobackend.wallet.WalletRepository;
import com.ms.picpaydesafiobackend.wallet.WalletType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final WalletRepository walletRepository;

    private final AuthorizerService authorizerService;

    public TransactionService(TransactionRepository transactionRepository,
                              WalletRepository walletRepository,
                              AuthorizerService authorizerService){
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.authorizerService = authorizerService;
    }

    @Transactional
    public Transaction create(Transaction transaction){
        Transaction create = transactionRepository.save(transaction);
        Wallet wallet = walletRepository.findById(transaction.payer()).get();
        walletRepository.save(wallet.debit(transaction.value()));
        authorizerService.authorize(transaction);
        return create;
    }

    private void validate(Transaction transaction){
        walletRepository.findById(transaction.payee())
                .map(payee -> walletRepository.findById(transaction.payer())
                .map(payer -> isTransactionValid(transaction, payer) ? transaction : null)
                        .orElseThrow(
                                ()-> new InvalidTransactionException("Invalid transaction: %s".formatted(transaction))))
                .orElseThrow(
                        ()-> new InvalidTransactionException("Invalid transaction: %s".formatted(transaction)));

    }

    private static boolean isTransactionValid(Transaction transaction, Wallet payer) {
        return payer.type() == WalletType.COMMON.getValue() &&
                payer.balance().compareTo(transaction.value()) >= 0 &&
                !payer.id().equals(transaction.payee());
    }
}
