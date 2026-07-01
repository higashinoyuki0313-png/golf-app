package com.golf.app.golfapp.mapper;

import java.util.List;
import com.golf.app.golfapp.model.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper {

    Account findByEmail(String email);

    Account findById(Long id);

    List<Account> findAll();

    void insert(Account account);

    void deleteById(Long id);

    void updateProfile(Account account);

}