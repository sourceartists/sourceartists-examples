package com.sourceartists.restaurant.controller.junit4.model;

import com.sourceartists.restaurant.model.Bill;
import com.sourceartists.restaurant.model.CreditCard;
import com.sourceartists.restaurant.model.Customer;
import com.sourceartists.restaurant.model.Order;

public class Waiter {
    public static Bill prepareBillForOrder(Order order) {
        return null;
    }

    public static boolean performTransaction(Bill bill, CreditCard creditCard) {
        return false;
    }

    public static void informTransactionNotAccepted(Customer customer, Bill bill) {

    }

}
