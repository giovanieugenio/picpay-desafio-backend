package com.ms.picpaydesafiobackend.wallet;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends CrudRepository<Wallet, Long> {
}
