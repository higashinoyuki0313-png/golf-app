package com.golf.app.golfapp.repository;

import com.golf.app.golfapp.model.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryAccountRepository implements AccountRepository {

    private final List<Account> accounts = new ArrayList<>();

    public InMemoryAccountRepository() {
        Account account1 = new Account();
        account1.setId(1L);
        account1.setEmail("user@gmail.com");
        account1.setPassword("123");
        account1.setRole(1);
        account1.setName("一般ユーザー");

        accounts.add(account1);

        Account account2 = new Account();
        account2.setId(2L);
        account2.setEmail("pro@gmail.com");
        account2.setPassword("123");
        account2.setRole(2);
        account2.setName("東野プロ");
        account2.setImage("https://placehold.co/300x300");

        accounts.add(account2);

    }

    @Override
    public List<Account> findAll() {
        return accounts;
    }

    @Override
    public Optional<Account> findById(Long id) {
        return accounts.stream()
                .filter(account -> account.getId().equals(id))
                .findFirst();
    }

    @Override
    public void save(Account account) {
        accounts.add(account);
    }

    @Override
    public void update(Account account){
        findById(account.getId()).ifPresent(existingAccount
                -> {
            existingAccount.setEmail(account.getEmail());
            existingAccount.setPassword(account.getPassword());
            existingAccount.setRole(account.getRole());
        });

    }

    @Override
    public void deleteById(Long id){
        accounts.removeIf(account -> account.getId().equals(id));
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return accounts.stream()
                .filter(account -> account.getEmail().equals(email))
                .findFirst();
    }
}