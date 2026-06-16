package org.expenses.model;

import java.time.LocalDateTime;

public class User {

    private Integer id;
    private String name;
    private String email;
    private LocalDateTime created_at;

    public User(Integer id, String name, String email, LocalDateTime created_at){
        this.id = id;
        this.name = name;
        this.email = email;
        this.created_at = created_at;
    }

    public User(String name, String email){
        this.name = name;
        this.email = email;
    }

    public User(){

    }

    public void setId(Integer id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setCreated_at(LocalDateTime created_at){
        this.created_at = created_at;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }
}
