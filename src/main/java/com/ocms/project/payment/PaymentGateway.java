package com.ocms.project.payment;

public class PaymentGateway {

    private static PaymentGateway instance;

    private PaymentGateway() {}

    public static PaymentGateway getInstance() {
        if (instance == null) {
            instance = new PaymentGateway();
        }
        return instance;
    }

    public boolean validatePayment(String method, double amount) {
        if (amount <= 0) {
            return false;
        }
        return method != null && (method.equalsIgnoreCase("UPI") || method.equalsIgnoreCase("CREDITCARD") || method.equalsIgnoreCase("NETBANKING"));
    }
}
