package com.sourceartists.auction.service.impl;

import com.sourceartists.auction.model.Auction;
import com.sourceartists.auction.model.Bid;
import com.sourceartists.auction.service.LoggingService;
import org.springframework.stereotype.Service;

@Service
public class BasicLoggingService implements LoggingService {

    @Override
    public void logBidUnsuccessfull(Integer auctionId, Bid bid) {

    }
}
