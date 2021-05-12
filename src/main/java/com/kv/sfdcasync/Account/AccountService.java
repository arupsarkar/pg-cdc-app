package com.kv.sfdcasync.Account;

import org.springframework.stereotype.Service;

@Service
public class AccountService {

    static Account updateAccountName(Account acct) {
        Account account = new Account();
        account = acct;
        account.setName(acct.getName() + " - abcd");
        return account;
    }
}
