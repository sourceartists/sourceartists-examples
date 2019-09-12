package com.sourceartists.auction.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class Bid implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    private BigDecimal amount;

    @ManyToOne
    @JoinColumn( name = "auction_id")
    private Auction auction;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }
}
