package com.sourceartists.auction.service.impl;

import com.sourceartists.auction.exception.AuctionLockedException;
import com.sourceartists.auction.exception.WatchingException;
import com.sourceartists.auction.model.*;
import com.sourceartists.auction.repository.AuctionRepository;
import com.sourceartists.auction.repository.BuyerRepository;
import com.sourceartists.auction.repository.SellerRepository;
import com.sourceartists.auction.service.AuctionService;
import com.sourceartists.auction.service.LoggingService;
import com.sourceartists.auction.service.MailService;
import com.sourceartists.auction.service.helper.BidSaver;
import com.sourceartists.auction.service.helper.InsuranceCalculator;
import com.sourceartists.auction.service.helper.PopularityResolver;
import com.sourceartists.auction.util.RatingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class BasicAuctionService implements AuctionService {

    public static final int MAX_NUMBER_OF_WATCHED_AUCTIONS = 4;
    public static final Integer WAIT_MILIS = Integer.valueOf(3000);
    private static final Integer MOST_POPULAR_CAP = 10;
    private static final Integer MAX_BID_ATTEMPTS = Integer.valueOf(3);

    @Autowired
    private BidSaver bidSaver;
    @Autowired
    private LoggingService loggingService;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private MailService mailService;

    @Autowired
    private PopularityResolver popularityResolver;

    public boolean bidOnAuction(Integer auctionId, Bid bid){
        Integer attemptCount = Integer.valueOf(0);
        boolean bidSuccessfull = false;

        while(attemptCount < MAX_BID_ATTEMPTS){
            try{
                bidSuccessfull = bidSaver.saveBidOnAuction(auctionId, bid);
                break;
            }catch(AuctionLockedException e){
                loggingService.logBidUnsuccessfull(auctionId, bid);
                attemptCount++;
            }

            waitABit(WAIT_MILIS);
        }

        return bidSuccessfull;
    }

    @Override
    @Transactional
    public void rateSeller(Integer sellerId, Rating newRating) {
        Seller seller = sellerRepository.getOne(sellerId);
        List<Rating> sellerRatings = seller.getRatings();
        sellerRatings.add(newRating);

        BigDecimal newAverageRating = calculateRating(sellerRatings);

        seller.setAverageRating(newAverageRating);
    }

    @Override
    @Transactional
    public boolean watchAnOffer(Integer auctionId, Integer personId) throws WatchingException {
        Auction auction = auctionRepository.findById(auctionId).get();
        Buyer buyer = buyerRepository.findById(personId).get();

        if(buyer.getWatchedAuctions().size() > MAX_NUMBER_OF_WATCHED_AUCTIONS){
            throw new WatchingException("Max number of offers are already watched.");
        }

        for(Auction watchedAuction: buyer.getWatchedAuctions()) {
            if(watchedAuction.getId().equals(auctionId)){
                throw new WatchingException("Auction is already watched.");
            }
        }

        mailService.sendWatchConfirmationToUser(auctionId, personId);

        if(buyer.isSendNotificationToFriends()){
            mailService.sendWatchConfirmationToUserFriends(auctionId, personId);
        }

        return true;
    }

    @Override
    public Insurance offerInsurance(Integer auctionId, Integer buyerId) {
        Buyer buyer = buyerRepository.findById(buyerId).get();
        Auction auction = auctionRepository.findById(auctionId).get();

        InsuranceCalculator insuranceCalculator = new InsuranceCalculator();

        if(!buyer.hasInsuranceForAuction(auction)){
            return insuranceCalculator.generateQuote(
                    auction.getProductWorth(), buyer.getCreditScore());
        }

        return null;
    }

    @Override
    public Insurance offerInsurancePerCategory(Integer auctionId, Integer buyerId) {
        Buyer buyer = buyerRepository.findById(buyerId).get();
        Auction auction = auctionRepository.findById(auctionId).get();

        InsuranceCalculator insuranceCalculator
                = new InsuranceCalculator(auction.getCategory(), buyer.getCreditScore());

        if(!buyer.hasInsuranceForAuction(auction)){
            return insuranceCalculator.generateQuote(auction.getProductWorth());
        }

        return null;
    }

    @Override
    public List<Auction> getMostPopularAuctionsFromCategory(Integer categoryId) {
        List<Auction> initialPopularAuctionCandidates = auctionRepository
                .findByCategoryOrderByViewsDesc(categoryId, Integer.valueOf(20));

        List<Auction> topAuctions = popularityResolver
                .resolveWithCap(initialPopularAuctionCandidates, MOST_POPULAR_CAP);

        return topAuctions;
    }

    BigDecimal calculateRating(List<Rating> ratings){
        return RatingUtils.calculateRating(ratings);
    }

    void waitABit(Integer milis){
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setPopularityResolver(PopularityResolver popularityResolver) {
        this.popularityResolver = popularityResolver;
    }
}
