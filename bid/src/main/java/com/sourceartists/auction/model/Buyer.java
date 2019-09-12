package com.sourceartists.auction.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Buyer implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;
    private boolean sendNotificationToFriends;

    @ManyToMany
    private List<Auction> watchedAuctions;
    private Integer creditScore;

    public Buyer(){

    }

    public Buyer(Integer id){
        this.id = id;
    }

    public boolean isSendNotificationToFriends() {
        return sendNotificationToFriends;
    }

    public void setSendNotificationToFriends(boolean sendNotificationToFriends) {
        this.sendNotificationToFriends = sendNotificationToFriends;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Auction> getWatchedAuctions() {
        return watchedAuctions;
    }

    public void setWatchedAuctions(List<Auction> watchedAuctions) {
        this.watchedAuctions = watchedAuctions;
    }

    public boolean hasInsuranceForAuction(Auction auction) {
        return false;
    }

    public Integer getCreditScore() {
        return creditScore;
    }
}
