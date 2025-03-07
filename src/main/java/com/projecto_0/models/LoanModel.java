package com.projecto_0.models;
import java.sql.Timestamp;

public class LoanModel {
    private int id;
    private Integer userId;
    private Integer loanTypeId;
    private double amount;
    private Integer statusId;

    public LoanModel() {}

    public LoanModel(int id, int userId, int loanTypeId, double amount, int statusId, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.loanTypeId = loanTypeId;
        this.amount = amount;
        this.statusId = statusId;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getLoanTypeId() {
        return loanTypeId;
    }

    public void setLoanTypeId(int loanTypeId) {
        this.loanTypeId = loanTypeId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
}