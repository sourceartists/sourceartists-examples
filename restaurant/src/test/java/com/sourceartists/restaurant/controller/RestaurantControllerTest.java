package com.sourceartists.restaurant.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.mockito.Mockito.*;

import com.sourceartists.restaurant.model.*;
import com.sourceartists.restaurant.service.RestaurantSystem;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

class RestaurantControllerTest {

    @InjectMocks private RestaurantController restaurantControllerSUT;
    @Mock private RestaurantSystem restaurantSystemMock;
    @Mock private Manager managerMock;
    @Mock private MasterChef masterChefMock;
    @Mock private Cleaner cleanerMock;

    @BeforeEach
    private void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldBookATable() throws Exception{
        // Given
        Table tableToBook = new Table();
        Customer customer = new Customer();
        RestaurantSystem restaurantSystemMock = mock(RestaurantSystem.class
                , withSettings().verboseLogging());
        Manager managerMock = mock(Manager.class
                , withSettings().verboseLogging());

        restaurantControllerSUT.setRestaurantSystem(restaurantSystemMock);
        restaurantControllerSUT.setManager(managerMock);

        doReturn(tableToBook).when(managerMock).findFreeTable(restaurantSystemMock);

        // When
        restaurantControllerSUT.bookTable(customer);

        // Then
        verify(restaurantSystemMock).bookTable(tableToBook);
        verify(managerMock, times(1)).confirmBooking(customer);
    }

    @Test
    public void shouldApologiseAndNotifyWhenNoFreeTables() throws Exception{
        // Given
        Customer customer = new Customer();
        when(managerMock.findFreeTable(restaurantSystemMock))
                .thenReturn(null);

        // When
        restaurantControllerSUT.bookTable(customer);

        // Then
        verify(restaurantSystemMock).notifyCustomerWhenTablesAvailable(customer);
        verify(managerMock).appologiseAndConfirmNotification(customer);

        verify(restaurantSystemMock, never()).bookTable(any(Table.class));
        verify(managerMock, times(0)).confirmBooking(customer);
    }

    @Test
    public void shouldProcessBillWhenManyCreditCardsAttempted() throws Exception{
        // Given
        Bill bill = new Bill();
        Order orderDummy = new Order();
        Waiter waiterMock = mock(Waiter.class);
        Customer customer = new Customer();
        customer.setCreditCards(Arrays.asList(new CreditCard[]{
                new CreditCard(1),new CreditCard(2),new CreditCard(3)}));

        when(waiterMock.prepareBillForOrder(orderDummy)).thenReturn(bill);

        when(waiterMock.performTransaction(eq(bill), any(CreditCard.class)))
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(true);

        // When
        Bill resultBill = restaurantControllerSUT.processBill(customer, waiterMock, orderDummy);

        // Then
        assertThat(resultBill.paid()).isTrue();

        verify(waiterMock, times(2)).informTransactionNotAccepted(customer, bill);
    }

    @Test
    public void shouldProcessDifferentCardAfterEachTransactionFail() throws Exception{
        // Given
        Bill bill = new Bill();
        Order orderDummy = new Order();
        Waiter waiterMock = mock(Waiter.class);
        Customer customer = new Customer();
        customer.setCreditCards(Arrays.asList(new CreditCard[]{
                new CreditCard(1),new CreditCard(2),new CreditCard(3)}));

        when(waiterMock.prepareBillForOrder(orderDummy)).thenReturn(bill);

        when(waiterMock.performTransaction(eq(bill), any(CreditCard.class)))
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(true);

        // When
        Bill resultBill = restaurantControllerSUT.processBill(customer, waiterMock, orderDummy);

        // Then
        ArgumentCaptor<CreditCard> creditCardCaptor = ArgumentCaptor.forClass(CreditCard.class);

        verify(waiterMock, times(3)).performTransaction(eq(bill), creditCardCaptor.capture());

        List<CreditCard> capturedCards = creditCardCaptor.getAllValues();
        Set<CreditCard> creditCardSet = new HashSet<>(capturedCards);

        assertThat(creditCardSet.size()).isEqualTo(3);
    }

    @Test
    public void shouldInformCustomerWhenTransactionNoAccepted_whenProcessingBill() throws Exception{
        // Given
        Bill bill = new Bill();
        Bill billPassedToCustomerAfterDecline = new Bill();

        Order orderDummy = new Order();
        Waiter waiterMock = mock(Waiter.class);
        Customer customer = new Customer();
        customer.setCreditCards(Arrays.asList(new CreditCard[]{
                new CreditCard(1),new CreditCard(2)}));

        when(waiterMock.prepareBillForOrder(orderDummy)).thenReturn(bill);
        when(waiterMock.performTransaction(eq(bill), any(CreditCard.class)))
                .thenReturn(false)
                .thenReturn(true);

        doNothing().when(waiterMock).informTransactionNotAccepted(
                eq(customer), argThat((billPassed) -> {
            billPassedToCustomerAfterDecline.setPaid(billPassed.paid());

            return true;
        }));

        // When
        Bill resultBill = restaurantControllerSUT.processBill(customer, waiterMock, orderDummy);

        // Then
        assertThat(billPassedToCustomerAfterDecline.paid()).isFalse();
    }

    @Test
    public void shouldCollectOrderWithStarter() throws Exception{
        // Given
        Waiter waiterMock = mock(Waiter.class);
        Customer customer = new Customer();

        when(waiterMock.anyStarter(customer)).thenReturn(true);

        // When
        restaurantControllerSUT.collectOrder(customer, waiterMock);

        // Then
        InOrder inOrder = inOrder(waiterMock);

        inOrder.verify(waiterMock).offerFreeBreadAndSauce(eq(customer), any(Order.class));
        inOrder.verify(waiterMock).takeDrinkOrder(eq(customer), any(Order.class));
        inOrder.verify(waiterMock).takeStarterChoice(eq(customer), any(Order.class));
        inOrder.verify(waiterMock).takeMainMealOrder(eq(customer), any(Order.class));
    }

    @Test
    public void shouldCloseTheRestaurant() throws Exception{
        // Given
        when(cleanerMock.finished()).thenReturn(true);

        // When
        restaurantControllerSUT.closeRestaurant();

        // Then
        InOrder inOrder = inOrder(cleanerMock, managerMock, masterChefMock);

        inOrder.verify(cleanerMock).startCleaning();
        inOrder.verify(managerMock).calculateTodaysIncome();
        inOrder.verify(masterChefMock).makeFoodOrderForTomorrow();
        inOrder.verify(cleanerMock).finished();
        inOrder.verify(managerMock).closeRestaurant();
    }

    @Test
    public void shouldCloseTheRestaurantLoose() throws Exception{
        // Given
        when(cleanerMock.finished()).thenReturn(true);

        // When
        restaurantControllerSUT.closeRestaurant();

        // Then
        InOrder inOrderChef = inOrder(cleanerMock, managerMock, masterChefMock);

        inOrderChef.verify(cleanerMock).startCleaning();
        inOrderChef.verify(masterChefMock).makeFoodOrderForTomorrow();
        inOrderChef.verify(cleanerMock).finished();
        inOrderChef.verify(managerMock).closeRestaurant();

        InOrder inOrderManager = inOrder(cleanerMock, managerMock, masterChefMock);

        inOrderManager.verify(cleanerMock).startCleaning();
        inOrderManager.verify(managerMock).calculateTodaysIncome();
        inOrderManager.verify(cleanerMock).finished();
        inOrderManager.verify(managerMock).closeRestaurant();
    }

    @Test
    public void shouldPrepareStew() throws Exception {
        // Given
        Meal meal = new Meal();
        Order order = new Order(MealType.STEW);

        when(masterChefMock.cook(eq(MealType.STEW), any())).thenReturn(meal);

        // When
        Meal preparedMeal = restaurantControllerSUT.prepareMeal(order, masterChefMock);
        Percentage.withPercentage(10.0);
        // Then
        assertThat(preparedMeal).isSameAs(meal);

        assertThat(true).isTrue().describedAs("Is he really the greatest");
    }


    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, mode = EXCLUDE, names = {"SUNDAY"})
    public void shouldOpenRestaurant(DayOfWeek dayOfWeek) throws Exception{
        // Given

        when(managerMock.shouldOpenRestaurant(anyInt(), anyInt())).thenReturn(true);

        // When
        restaurantControllerSUT.openRestaurant(dayOfWeek);

        // Then
        verify(managerMock).open(restaurantSystemMock);
    }
}