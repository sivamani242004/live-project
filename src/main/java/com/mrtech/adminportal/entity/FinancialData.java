package com.mrtech.adminportal.entity;

public class FinancialData {
    private String month;
    private int income;
    private int expense;

    public FinancialData(String month, int income, int expense) {
        this.month = month;
        this.income = income;
        this.expense = expense;
    }

    // Getters & Setters
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public int getIncome() { return income; }
    public void setIncome(int income) { this.income = income; }

    public int getExpense() { return expense; }
    public void setExpense(int expense) { this.expense = expense; }
}
