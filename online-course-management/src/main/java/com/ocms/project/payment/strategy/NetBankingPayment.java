package com.ocms.project.payment.strategy;

public class NetBankingPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {
        System.out.println("Paid using Net Banking: " + amount);
    }
}