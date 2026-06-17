package org.expenses.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Expense {

    private Integer id;
    private Integer user_id;
    private LocalDate date;
    private String description;
    private BigDecimal amount;

    public Expense(Integer id, Integer user_id, LocalDate date, String description, BigDecimal amount){
        this.id = id;
        this.user_id = user_id;
        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    public Expense( Integer user_id, LocalDate date, String description, BigDecimal amount){
        this.user_id = user_id;
        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    public void setId(Integer id){
        this.id = id;
    }
    // some reason for User_id and not UserId?
    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public void setDate(LocalDate date){
        this.date = date;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setAmount(BigDecimal amount){
        this.amount = amount;
    }

    public Integer getId(){
        return this.id;
    }

    public Integer getUser_id(){
        return this.user_id;
    }

    public LocalDate getDate(){
        return this.date;
    }

    public String getDescription(){
        return this.description;
    }

    public BigDecimal getAmount(){
        return this.amount;
    }

}
