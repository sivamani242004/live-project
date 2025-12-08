package com.mrtech.adminportal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "finance_report")
public class FinanceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int year;

    @Column(name = "month", nullable = false)
    private String month;

    private double income;

    @Column(name = "running_expenses")
    private double runningExpenses;

    private double salaries;

    @Column(name = "trainer_share")
    private double trainerShare;

    private double expenditure;

    @Column(name = "profit_or_loss")
    private double profitOrLoss;

    public FinanceReport() {}

    // getters / setters (use standard camelCase names)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public double getIncome() { return income; }
    public void setIncome(double income) { this.income = income; }

    public double getRunningExpenses() { return runningExpenses; }
    public void setRunningExpenses(double runningExpenses) { this.runningExpenses = runningExpenses; }

    public double getSalaries() { return salaries; }
    public void setSalaries(double salaries) { this.salaries = salaries; }

    public double getTrainerShare() { return trainerShare; }
    public void setTrainerShare(double trainerShare) { this.trainerShare = trainerShare; }

    public double getExpenditure() { return expenditure; }
    public void setExpenditure(double expenditure) { this.expenditure = expenditure; }

    public double getProfitOrLoss() { return profitOrLoss; }
    public void setProfitOrLoss(double profitOrLoss) { this.profitOrLoss = profitOrLoss; }
}
