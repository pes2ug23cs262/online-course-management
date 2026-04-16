package com.ocms.project.payment.strategy;

public class UPIPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {
        System.out.println("Paid using UPI: " + amount);
    }
}