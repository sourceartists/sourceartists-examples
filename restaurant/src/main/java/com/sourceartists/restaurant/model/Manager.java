package com.sourceartists.restaurant.model;

import com.sourceartists.restaurant.service.RestaurantSystem;
import org.springframework.stereotype.Component;

@Component
public class Manager {
    public void answerCall(Customer customer) {

    }

    public static Table findFreeTable(Integer leaveAtLeast, Integer latestHour) {
        return null;
    }

    public static Table findFreeTable() {
        return null;
    }

    public Table findFreeTable(RestaurantSystem restaurantSystem) {
        return null;
    }

    public void appologiseAndConfirmNotification(Customer customer) {

    }

    public void confirmBooking(Customer customer) {

    }

    public void calculateTodaysIncome() {

    }

    public void watchLatestGameOfThrones() {

    }

    public void closeRestaurant() {

    }

    public boolean shouldOpenRestaurant(int hourOfOpening, int minuteOfOpening) {

        return false;
    }

    public void open(RestaurantSystem restaurantSystem) {

    }
}
