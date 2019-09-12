package com.sourceartists.auction.service.impl;

import com.sourceartists.auction.exception.WatchingException;
import com.sourceartists.auction.model.Auction;
import com.sourceartists.auction.model.Buyer;
import com.sourceartists.auction.model.Rating;
import com.sourceartists.auction.model.Seller;
import com.sourceartists.auction.repository.AuctionRepository;
import com.sourceartists.auction.repository.BuyerRepository;
import com.sourceartists.auction.repository.SellerRepository;
import com.sourceartists.auction.service.MailService;
import com.sourceartists.auction.service.helper.PopularityResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class BasicAuctionServiceTest {

    @InjectMocks
    @Spy
    private BasicAuctionService basicAuctionServiceSpy;

    @InjectMocks
    private BasicAuctionService basicAuctionServiceSUT;

    @Mock
    private SellerRepository sellerRepository;
    @Mock
    private MailService mailService;
    @Mock
    private AuctionRepository auctionRepository;
    @Mock
    private BuyerRepository buyerRepository;

    @BeforeEach
    private void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldRecalculateAverageRating() throws Exception{
        // Given
        Integer sellerId = Integer.valueOf(1);
        List<Rating> ratings = new ArrayList<>();
        Seller seller = new Seller();
        seller.setRatings(ratings);
        Rating newRating = new Rating();
        BigDecimal newRatingAverage = BigDecimal.valueOf(9.5);

        when(sellerRepository.getOne(sellerId)).thenReturn(seller);

        doReturn(newRatingAverage).when(basicAuctionServiceSpy).calculateRating(ratings);

        // When
        basicAuctionServiceSpy.rateSeller(sellerId, newRating);

        // Then
        assertThat(seller.getAverageRating()).isEqualTo(newRatingAverage);
    }

    @Test
    public void shouldNotSendAnyMailsOnException() throws Exception{
        // Given
        Integer auctionToWatchId = Integer.valueOf(10);
        Auction auctionToWatch = new Auction(auctionToWatchId);
        Integer buyerId = Integer.valueOf(1);
        Buyer buyer = new Buyer(buyerId);
        List<Auction> watchedAuctions = Arrays.asList(new Auction[]{
                new Auction(1),new Auction(2),new Auction(3),new Auction(4),new Auction(5)});
        buyer.setWatchedAuctions(watchedAuctions);

        when(buyerRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
        when(auctionRepository.findById(auctionToWatchId)).thenReturn(Optional.of(auctionToWatch));

        // When
        try {
            basicAuctionServiceSUT.watchAnOffer(auctionToWatchId, buyerId);
        }catch (WatchingException e){}

        // Then
        watchedAuctions = Arrays.asList(new Auction[]{new Auction(auctionToWatchId)});
        buyer.setWatchedAuctions(watchedAuctions);

        // When
        try {
            basicAuctionServiceSUT.watchAnOffer(auctionToWatchId, buyerId);
        }catch (WatchingException e){}

        // Then
        verifyZeroInteractions(mailService);
    }


}