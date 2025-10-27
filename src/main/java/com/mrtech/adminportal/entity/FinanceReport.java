package com.mrtech.adminportal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "finance_report")
public class FinanceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year")
    private int year;

    @Column(name = "month")
    private String month;

    @Column(name = "income")
    private double income;
    
    @Column(name = "Salaries")
    private double Salaries;

    @Column(name = "trainer_share")
    private double trainerShare;

    @Column(name = "expenditure")
    private double expenditure;

    @Column(name = "profit_or_loss")
    private double profitOrLoss;

    // Default constructor (required by JPA)
    public FinanceReport() {}

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }
    public double getSalaries() {
        return income;
    }

    public void setSalaries(double Salaries) {
        this.Salaries= Salaries;
    }

    public double getTrainerShare() {
        return trainerShare;
    }

    public void setTrainerShare(double trainerShare) {
        this.trainerShare = trainerShare;
    }

    public double getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(double expenditure) {
        this.expenditure = expenditure;
    }

    public double getProfitOrLoss() {
        return profitOrLoss;
    }

    public void setProfitOrLoss(double profitOrLoss) {
        this.profitOrLoss = profitOrLoss;
    }
}
