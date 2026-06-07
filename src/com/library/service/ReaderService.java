package com.library.model;

import com.library.service.policy.FinePolicy;

public abstract class Reader extends User {

    protected String phone;

    public Reader(String id, String name, String phone) {
        super(id, name);
        this.phone = phone;
    }

    public abstract FinePolicy getFinePolicy();
}
