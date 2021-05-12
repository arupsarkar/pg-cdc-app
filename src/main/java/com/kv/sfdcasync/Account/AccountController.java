package com.kv.sfdcasync.Account;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {
    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);
    ArrayList<Account> records = new ArrayList<Account>();

    @GetMapping("/account")
    String account(Map<String, ArrayList<Account>> model) {
        model.put("accounts", records);
        return "account/account";
    }

    public AccountController() {
        LOG.debug("Initiating account controller.");
        LOG.debug("Fetching account records");
        this.records = createAccounts();
        LOG.debug("Total accoun records fetched", this.records.size());
        LOG.debug("Account controller initialized.");
    }

    // Create Accounts
    private ArrayList<Account> createAccounts() {
        ArrayList<Account> records = new ArrayList<Account>();
        Account acct1 = AccountConfig.account("1", "Acme Corp.");
        Account acct2 = AccountConfig.account("2", "Gene Corp.");
        acct2 = AccountService.updateAccountName(acct2);
        Account acct3 = AccountConfig.account("3", "Sally Corp.");
        Account acct4 = AccountConfig.account("4", "Bag Corp.");
        acct4 = AccountService.updateAccountName(acct4);
        records.add(acct1);
        records.add(acct2);
        records.add(acct3);
        records.add(acct4);
        return records;
    }
}
