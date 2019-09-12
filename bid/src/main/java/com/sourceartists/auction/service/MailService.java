package com.sourceartists.auction.service;

public interface MailService {

    void sendWatchConfirmationToUser(Integer auctionId, Integer personId);
    void sendWatchConfirmationToUserFriends(Integer auctionId, Integer personId);
}
