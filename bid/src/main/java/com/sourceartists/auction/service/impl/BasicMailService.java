package com.sourceartists.auction.service.impl;

import com.sourceartists.auction.service.MailService;
import org.springframework.stereotype.Service;

@Service
public class BasicMailService implements MailService {


    @Override
    public void sendWatchConfirmationToUser(Integer auctionId, Integer personId) {

    }

    @Override
    public void sendWatchConfirmationToUserFriends(Integer auctionId, Integer personId) {

    }
}
