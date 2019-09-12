package com.sourceartists.restaurant.controller.junit4;

import com.sourceartists.restaurant.controller.junit4.RestaurantController;
import com.sourceartists.restaurant.controller.junit4.model.Waiter;
import com.sourceartists.restaurant.model.*;
import com.sourceartists.restaurant.service.RestaurantSystem;
import com.sourceartists.restaurant.util.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Manager.class, Waiter.class})
public class RestaurantControllerTest {

    @InjectMocks
    private RestaurantController restaurantControllerSUT;

    @Mock
    private RestaurantSystem restaurantSystemMock;

    @Mock
    private Manager managerStub;

    @Mock
    private Configuration configuration;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldBookATableForCustomer() throws Exception{
        // Arrange
        Table table = new Table();
        Customer customer = new Customer();
        PowerMockito.mockStatic(Manager.class);
        PowerMockito.when(Manager.findFreeTable()).thenReturn(table);

        // Act
        restaurantControllerSUT.bookTable(customer);

        // Assert
        Mockito.verify(restaurantSystemMock).bookTable(table);
    }

    @Test
    public void shouldBookATableForCustomer2() throws Exception{
        // Arrange
        Table table = new Table();
        Customer customer = new Customer();
        PowerMockito.mockStatic(Manager.class);
        PowerMockito.when(Manager.findFreeTable(10,20)).thenReturn(table);

        // Act
        restaurantControllerSUT.bookTable2(customer);

        // Assert
        Mockito.verify(restaurantSystemMock).bookTable(table);
    }

    @Test
    public void shouldBookATableForCustomer3() throws Exception{
        // Arrange
        Table table = new Table();
        Customer customer = new Customer();
        PowerMockito.mockStatic(Manager.class);
        PowerMockito.when(Manager.findFreeTable(ArgumentMatchers.anyInt(),ArgumentMatchers.anyInt()))
                .thenReturn(table);

        // Act
        restaurantControllerSUT.bookTable3(customer);

        // Assert
        Mockito.verify(restaurantSystemMock).bookTable(table);
    }

    @Test
    public void shouldProcessBill_afterUnsuccessfullAttempt() throws Exception{
        // Arrange
        Customer customer = new Customer();
        customer.setCreditCards(Arrays.asList(new CreditCard[]{new CreditCard(1), new CreditCard(2)}));

        PowerMockito.spy(Waiter.class);
        PowerMockito.when(Waiter.performTransaction(
                ArgumentMatchers.any(Bill.class), ArgumentMatchers.any(CreditCard.class)))
            .thenReturn(false)
            .thenReturn(true);

        // Act
        Bill processedBill = restaurantControllerSUT.processBill(customer, new Bill());

        // Assert
        assertThat(processedBill.paid()).isTrue();
    }
}
