package com.sourceartists.restaurant.model;

import java.util.Iterator;
import java.util.List;

public class Customer {

    private List<CreditCard> creditCards;

    public void setCreditCards(List<CreditCard> creditCards) {
        this.creditCards = creditCards;
    }

    public Iterator<CreditCard> prepareToPay() {
        return creditCards.iterator();
    }
}
