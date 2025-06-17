/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mystrive.model;

import java.sql.Timestamp;

public class Category {
    private int categoryId;
    private int userId;
    private String categoryName;
    private Timestamp createdAt;


    public Category() {
    }


    public Category(int categoryId, int userId, String categoryName, Timestamp createdAt) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.categoryName = categoryName;
        this.createdAt = createdAt;
    }

    public Category(int userId, String categoryName) {
        this.userId = userId;
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

