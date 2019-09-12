package com.sourceartists.restaurant.model;

public class Waiter {
    public Bill prepareBillForOrder(Order order) {
        return null;
    }

    public boolean performTransaction(Bill bill, CreditCard creditCard) {
        return false;
    }

    public void informTransactionNotAccepted(Customer customer, Bill bill) {

    }

    public void offerFreeBreadAndSauce(Customer customer, Order order) {

    }

    public void takeDrinkOrder(Customer customer, Order order) {

    }

    public void takeStarterChoice(Customer customer, Order order) {

    }

    public void takeMainMealOrder(Customer customer, Order order) {

    }

    public boolean anyStarter(Customer customer) {
        return false;
    }
}
