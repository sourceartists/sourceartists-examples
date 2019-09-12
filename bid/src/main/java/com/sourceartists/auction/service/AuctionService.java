package com.sourceartists.auction.service;

import com.sourceartists.auction.exception.WatchingException;
import com.sourceartists.auction.model.Auction;
import com.sourceartists.auction.model.Bid;
import com.sourceartists.auction.model.Insurance;
import com.sourceartists.auction.model.Rating;

import java.util.List;

public interface AuctionService {

    boolean bidOnAuction(Integer auctionId, Bid bid);

    void rateSeller(Integer sellerId, Rating newRating);

    boolean watchAnOffer(Integer auctionId, Integer personId) throws WatchingException;

    Insurance offerInsurance(Integer auctionId, Integer buyerId);

    Insurance offerInsurancePerCategory(Integer auctionId, Integer buyerId);

    List<Auction> getMostPopularAuctionsFromCategory(Integer categoryId);
}
