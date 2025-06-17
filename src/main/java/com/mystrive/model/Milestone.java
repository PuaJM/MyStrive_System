/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mystrive.model;

import java.sql.Date;
import java.sql.Timestamp;


public class Milestone {
    private int milestoneId;
    private int goalId;
    private String milestoneDescription;
    private Date dueDate;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Milestone() {
    }

    public Milestone(int milestoneId, int goalId, String milestoneDescription, Date dueDate, String status, Timestamp createdAt, Timestamp updatedAt) {
        this.milestoneId = milestoneId;
        this.goalId = goalId;
        this.milestoneDescription = milestoneDescription;
        this.dueDate = dueDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Milestone(int goalId, String milestoneDescription, Date dueDate, String status) {
        this.goalId = goalId;
        this.milestoneDescription = milestoneDescription;
        this.dueDate = dueDate;
        this.status = status;
    }

    public int getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(int milestoneId) {
        this.milestoneId = milestoneId;
    }

    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public String getMilestoneDescription() {
        return milestoneDescription;
    }

    public void setMilestoneDescription(String milestoneDescription) {
        this.milestoneDescription = milestoneDescription;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
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
}

