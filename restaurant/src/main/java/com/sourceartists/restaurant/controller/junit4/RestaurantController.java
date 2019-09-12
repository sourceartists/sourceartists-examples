package com.sourceartists.restaurant.controller.junit4;

import com.sourceartists.restaurant.controller.junit4.model.Waiter;
import com.sourceartists.restaurant.model.*;
import com.sourceartists.restaurant.service.RestaurantSystem;
import com.sourceartists.restaurant.util.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;

@RestController
public class RestaurantController {

    public static final String LEAVE_AT_LEAST_NUMBER_OF_SEATS_PARAM = "least.seats";
    public static final String LATEST_HOUR_PARAM = "latest.hour";

    public static final Integer LEAVE_AT_LEAST_NUMBER_OF_SEATS = Integer.valueOf(10);
    public static final Integer LATEST_HOUR = Integer.valueOf(20);

    @Autowired
    private RestaurantSystem restaurantSystem;
    @Autowired
    private Manager manager;
    @Autowired
    private Configuration configuration;

    public void bookTable(Customer customer){
        manager.answerCall(customer);

        Table table = manager.findFreeTable();

        if(table == null){
            restaurantSystem.notifyCustomerWhenTablesAvailable(customer);
            manager.appologiseAndConfirmNotification(customer);

            return;
        }

        restaurantSystem.bookTable(table);
        manager.confirmBooking(customer);
    }

    public void bookTable2(Customer customer){
        manager.answerCall(customer);

        Table table = manager.findFreeTable(LEAVE_AT_LEAST_NUMBER_OF_SEATS, LATEST_HOUR);

        if(table == null){
            restaurantSystem.notifyCustomerWhenTablesAvailable(customer);
            manager.appologiseAndConfirmNotification(customer);

            return;
        }

        restaurantSystem.bookTable(table);
        manager.confirmBooking(customer);
    }

    public void bookTable3(Customer customer){
        manager.answerCall(customer);

        Table table = manager.findFreeTable(configuration.getInt(LEAVE_AT_LEAST_NUMBER_OF_SEATS_PARAM),
                configuration.getInt(LATEST_HOUR_PARAM));

        if(table == null){
            restaurantSystem.notifyCustomerWhenTablesAvailable(customer);
            manager.appologiseAndConfirmNotification(customer);

            return;
        }

        restaurantSystem.bookTable(table);
        manager.confirmBooking(customer);
    }

    public Bill processBill(Customer customer, Bill bill){
        Iterator<CreditCard> creditCards = customer.prepareToPay();

        while(!bill.paid()){
            if(creditCards.hasNext()){
                CreditCard creditCard = creditCards.next();

                boolean transactionSuccessful = Waiter.performTransaction(bill, creditCard);

                if(transactionSuccessful){
                    bill.setPaid(true);
                    break;
                }else{
                    Waiter.informTransactionNotAccepted(customer, bill);
                }
            }else{
                throw new RuntimeException("Customer cannot pay");
            }
        }

        return bill;
    }

}
