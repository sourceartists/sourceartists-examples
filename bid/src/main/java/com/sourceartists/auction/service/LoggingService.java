package com.sourceartists.auction.service;

import com.sourceartists.auction.model.Bid;

public interface LoggingService {

    void logBidUnsuccessfull(Integer auctionId, Bid bid);
}
