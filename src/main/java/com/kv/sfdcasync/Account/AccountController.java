package com.kv.sfdcasync.Account;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {
    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Autowired
    private DataSource dataSource;

    @GetMapping("/account")
    String account(Map<String, ArrayList<Account>> model) {
        ArrayList<Account> records = new ArrayList<Account>();
        records = getAccounts();
        model.put("accounts", records);
        return "account/account";
    }

    public AccountController() {
        LOG.debug("Initiating account controller.");
        LOG.debug("Fetching account records");
        try {
            // Class.forName("org.postgresql.Driver");
            // this.conn = dataSource.getConnection();
            // this.records = getAccounts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // this.records = createAccounts();

        // LOG.debug("Total accoun records fetched", this.records.size());
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

    // Get Accounts
    private ArrayList<Account> getAccounts() {
        ArrayList<Account> records = new ArrayList<Account>();
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name, phone FROM salesforce.account");
            while (rs.next()) {
                Account acct1 = AccountConfig.account(rs.getString("Id"), rs.getString("name"), rs.getString("phone"));
                records.add(acct1);
            }
            rs.close();
            stmt.close();

        } catch (Exception ex) {
            String error = ex.getLocalizedMessage();
            LOG.error("Error", error);
            LOG.debug("Debug", ex.getMessage());
        }
        return records;
    }

    @Bean
    public DataSource dataSource() throws SQLException {
        LOG.debug("dataSource 1 : ", "Initializing....." + dbUrl);
        if (dbUrl == null || dbUrl.isEmpty()) {
            LOG.debug("dataSource 2 : ", "dbUrl is empty");
            return new HikariDataSource();
        } else {
            LOG.debug("dataSource 3 : ", "dbUrl is not empty");
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            LOG.debug("dataSource 4 jdbc url : ", config.getJdbcUrl());
            return new HikariDataSource(config);
        }
    }

}
