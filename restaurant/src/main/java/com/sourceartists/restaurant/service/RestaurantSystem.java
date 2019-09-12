package com.sourceartists.restaurant.service;

import com.sourceartists.restaurant.model.Cancellation;
import com.sourceartists.restaurant.model.Customer;
import com.sourceartists.restaurant.model.Table;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class RestaurantSystem {
    public void notifyCustomerWhenTablesAvailable(Customer customer) {

    }

    public void bookTable(Table table) {

    }

    public Integer getNumberOfCancellations(Customer customer) {
        return null;
    }

    public void blacklistCustomer(Customer customer) {
    }

    public void freeUpTable(Table table) {

    }

    public void notifyNextAwaitingCustomer(Table table) {

    }

    public Integer minutesBetween(LocalDateTime bookingTime, LocalDateTime now) {
        return null;
    }

    public Customer nextAwaitingCustomer() {
        return null;
    }

    public void notifyAwaitingCustomer(Table table, Customer nextAwaitingCustomer) {

    }

    public void getCancellations(ArrayList<Cancellation> cancellations) {

    }

    public int getHourOfOpening(DayOfWeek dayOfWeek) {
        return 0;
    }

    public int getMinuteOfOpening(DayOfWeek dayOfWeek) {
        return 0;
    }
}
