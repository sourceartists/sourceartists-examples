package com.sourceartists.auction.service.helper;

import com.sourceartists.auction.exception.AuctionLockedException;
import com.sourceartists.auction.model.Auction;
import com.sourceartists.auction.model.Bid;
import com.sourceartists.auction.repository.AuctionRepository;
import com.sourceartists.auction.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.PessimisticLockException;
import java.util.Optional;

@Component
public class BidSaver {

    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private BidRepository bidRepository;

    public boolean saveBidOnAuction(Integer auctionId, Bid bid) throws AuctionLockedException{
        Auction auction = auctionRepository.findById(auctionId).get();

        try{
            auction.getBids().add(bid);
            bid.setAuction(auction);
            bidRepository.save(bid);
        } catch (PessimisticLockException e){
          throw new AuctionLockedException();
        }

        return false;
    }
}
