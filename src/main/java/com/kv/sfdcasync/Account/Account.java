package com.kv.sfdcasync.Account;

public class Account {

    private String name;
    private String id;
    private String phone;

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
