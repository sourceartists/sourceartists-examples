package com.sourceartists.restaurant.controller;

import com.sourceartists.restaurant.exception.OutOfIngredientsException;
import com.sourceartists.restaurant.exception.WeAreClosedTodayException;
import com.sourceartists.restaurant.exception.WeDontServeThisHereException;
import com.sourceartists.restaurant.model.*;
import com.sourceartists.restaurant.service.RestaurantSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Iterator;

@RestController
public class RestaurantController {

    @Autowired
    private RestaurantSystem restaurantSystem;
    @Autowired
    private Manager manager;
    @Autowired
    private MasterChef masterChef;
    @Autowired
    private Cleaner cleaner;

    public void bookTable(Customer customer){
        manager.answerCall(customer);

        Table table = manager.findFreeTable(restaurantSystem);

        if(table == null){
            restaurantSystem.notifyCustomerWhenTablesAvailable(customer);
            manager.appologiseAndConfirmNotification(customer);

            return;
        }

        restaurantSystem.bookTable(table);
        manager.confirmBooking(customer);
    }

    public void cancelBooking(Customer customer, Table table){
        if(restaurantSystem.minutesBetween(table.bookingTime(), LocalDateTime.now()) > 30) {
            restaurantSystem.freeUpTable(table);

            Customer nextAwaitingCustomer = restaurantSystem.nextAwaitingCustomer();

            restaurantSystem.notifyAwaitingCustomer(table, nextAwaitingCustomer);
        }

        if(restaurantSystem.getNumberOfCancellations(customer) > 3){
            restaurantSystem.blacklistCustomer(customer);
        }
    }

    public Bill processBill(Customer customer, Waiter waiter, Order order){
        Bill bill = waiter.prepareBillForOrder(order);
        Iterator<CreditCard> creditCards = customer.prepareToPay();

        while(!bill.paid()){
            if(creditCards.hasNext()){
                CreditCard creditCard = creditCards.next();

                boolean transactionSuccessful = waiter.performTransaction(bill, creditCard);

                if(transactionSuccessful){
                    bill.setPaid(true);
                    break;
                }else{
                    waiter.informTransactionNotAccepted(customer, bill);
                }
            }else{
                throw new RuntimeException("Customer cannot pay");
            }
        }

        return bill;
    }

    public void closeRestaurant(){
        cleaner.startCleaning();

        manager.calculateTodaysIncome();
        masterChef.makeFoodOrderForTomorrow();

        while(!cleaner.finished()){
            manager.watchLatestGameOfThrones();
        }

        manager.closeRestaurant();
    }

    public void openRestaurant(DayOfWeek dayOfWeek) throws WeAreClosedTodayException {
        if(dayOfWeek.equals(DayOfWeek.SUNDAY)){
            throw new WeAreClosedTodayException();
        }

        int hourOfOpening = restaurantSystem.getHourOfOpening(dayOfWeek);
        int minuteOfOpening = restaurantSystem.getMinuteOfOpening(dayOfWeek);

        boolean shouldOpen = manager.shouldOpenRestaurant(hourOfOpening, minuteOfOpening);

        if(shouldOpen){
            manager.open(restaurantSystem);
        }
    }

    public Meal prepareMeal(Order order, Chef chef) throws WeDontServeThisHereException
            , OutOfIngredientsException {
        Meal meal = null;

        if(order.isSoup()){
            Ingredient[] ingredients = chef.prepareIngredients(order);
            meal = chef.cook(MealType.SOUP, ingredients);
        }else if(order.isStew()){
            Meat meat = chef.prepareMeat(order);
            VegetableMix vegetableMix = chef.prepareVegies(order);
            Sauce sauce = chef.prepareSauce(order);

            meal = chef.cook(MealType.STEW, meat, vegetableMix, sauce);
        }else{
            throw new WeDontServeThisHereException();
        }

        return meal;
    }

    public Order collectOrder(Customer customer, Waiter waiter){
        Order order = new Order();

        waiter.offerFreeBreadAndSauce(customer, order);
        waiter.takeDrinkOrder(customer, order);

        if(waiter.anyStarter(customer)){
            waiter.takeStarterChoice(customer, order);
        }

        waiter.takeMainMealOrder(customer, order);

        return order;
    }

    public void setRestaurantSystem(RestaurantSystem restaurantSystem) {
        this.restaurantSystem = restaurantSystem;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public void setMasterChef(MasterChef masterChef) {
        this.masterChef = masterChef;
    }

    public void setCleaner(Cleaner cleaner) {
        this.cleaner = cleaner;
    }
}
