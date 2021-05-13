package com.kv.sfdcasync.Account;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AccountConfig {

    public AccountConfig() {
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    static Account account(String id, String name, String phone) {
        Account acct = new Account();
        acct.setName(name);
        acct.setId(id);
        acct.setPhone(phone);
        return acct;
    }
}
