/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mystrive.model;

import java.sql.Date;
import java.sql.Timestamp;


public class Goal {
    private int goalId;
    private int userId;
    private Integer categoryId; // Use Integer for nullable foreign key
    private String goalDescription;
    private Date targetDate;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private String categoryName;

    public Goal() {
    }

    public Goal(int goalId, int userId, Integer categoryId, String goalDescription, Date targetDate, String status, Timestamp createdAt, Timestamp updatedAt) {
        this.goalId = goalId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.goalDescription = goalDescription;
        this.targetDate = targetDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Goal(int userId, Integer categoryId, String goalDescription, Date targetDate, String status) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.goalDescription = goalDescription;
        this.targetDate = targetDate;
        this.status = status;
    }

    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getGoalDescription() {
        return goalDescription;
    }

    public void setGoalDescription(String goalDescription) {
        this.goalDescription = goalDescription;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}

