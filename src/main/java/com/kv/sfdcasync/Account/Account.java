package com.kv.sfdcasync.Account;

public class Account {

    private String name;
    private String id;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String newName) {
        this.name = newName;
    }
}
