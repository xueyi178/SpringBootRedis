package com.jbg.redis.model.entity;

public class Product {
    private Integer id;

    private String name;

    private Integer userId;

    private Integer scanTotal;

    private Byte isActive;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getScanTotal() {
        return scanTotal;
    }

    public void setScanTotal(Integer scanTotal) {
        this.scanTotal = scanTotal;
    }

    public Byte getIsActive() {
        return isActive;
    }

    public void setIsActive(Byte isActive) {
        this.isActive = isActive;
    }
}