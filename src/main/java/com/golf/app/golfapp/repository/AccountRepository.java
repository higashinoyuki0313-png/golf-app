package com.golf.app.golfapp.repository;

import com.golf.app.golfapp.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    List<Account> findAll();

    Optional<Account> findById(Long id);
    Optional<Account> findByEmail(String email);

    void save(Account account);

    void update(Account account);

    void deleteById(Long id);
}
