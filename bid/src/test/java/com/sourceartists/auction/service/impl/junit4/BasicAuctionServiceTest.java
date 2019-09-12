package com.sourceartists.auction.service.impl.junit4;

import com.sourceartists.auction.exception.AuctionLockedException;
import com.sourceartists.auction.model.*;
import com.sourceartists.auction.repository.AuctionRepository;
import com.sourceartists.auction.repository.BuyerRepository;
import com.sourceartists.auction.service.LoggingService;
import com.sourceartists.auction.service.helper.BidSaver;
import com.sourceartists.auction.service.helper.InsuranceCalculator;
import com.sourceartists.auction.service.helper.PopularityResolver;
import com.sourceartists.auction.service.impl.BasicAuctionService;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javafx.beans.binding.When;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { PopularityResolver.class, BasicAuctionService.class })
public class BasicAuctionServiceTest {

    @Mock
    private BuyerRepository buyerRepositoryStub;
    @Mock
    private AuctionRepository auctionRepositoryStub;
    @Mock
    private InsuranceCalculator insuranceCalculatorStub;
    @Mock private BidSaver bidSaverStub;
    @Mock private LoggingService loggingServiceStub;
    @Mock private Bid dummyBid;

    @InjectMocks
    @Spy
    private BasicAuctionService basicAuctionServiceSUT = new BasicAuctionService();

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCalculateInsuranceForBuyer() throws Exception{
        // Arrange
        PowerMockito.whenNew(InsuranceCalculator.class)
                .withNoArguments()
                .thenReturn(insuranceCalculatorStub);

        Integer auctionId = Integer.valueOf(10);
        Integer buyerId = Integer.valueOf(4);
        Integer buyerCreditScore = Integer.valueOf(900);
        BigDecimal productWorth = BigDecimal.TEN;
        Auction auction = Mockito.mock(Auction.class);
        Buyer buyer = Mockito.mock(Buyer.class);
        Insurance expectedInsurance = new Insurance();

        when(auctionRepositoryStub.findById(auctionId))
                .thenReturn(Optional.of(auction));
        when(buyerRepositoryStub.findById(buyerId))
                .thenReturn(Optional.of(buyer));
        when(insuranceCalculatorStub.generateQuote(productWorth, buyerCreditScore))
                .thenReturn(expectedInsurance);
        when(auction.getProductWorth()).thenReturn(productWorth);
        when(buyer.getCreditScore()).thenReturn(buyerCreditScore);

        // Act
        Insurance insurance = basicAuctionServiceSUT.offerInsurance(auctionId, buyerId);

        // Assert
        assertThat(insurance).isSameAs(expectedInsurance);
    }

    @Test
    public void shouldCalculateInsurancePerCategory() throws Exception{
        // Arrange
        Integer auctionId = Integer.valueOf(10);
        Integer buyerId = Integer.valueOf(4);
        Integer buyerCreditScore = Integer.valueOf(900);
        BigDecimal productWorth = BigDecimal.TEN;
        Category category = new Category();
        Auction auction = Mockito.mock(Auction.class);
        Buyer buyer = Mockito.mock(Buyer.class);
        Insurance expectedInsurance = new Insurance();

        PowerMockito.whenNew(InsuranceCalculator.class)
                .withArguments(category, buyerCreditScore)
                .thenReturn(insuranceCalculatorStub);

        when(auctionRepositoryStub.findById(auctionId))
                .thenReturn(Optional.of(auction));
        when(buyerRepositoryStub.findById(buyerId))
                .thenReturn(Optional.of(buyer));
        when(insuranceCalculatorStub.generateQuote(productWorth))
                .thenReturn(expectedInsurance);
        when(auction.getProductWorth()).thenReturn(productWorth);
        when(buyer.getCreditScore()).thenReturn(buyerCreditScore);
        when(auction.getCategory()).thenReturn(category);

        // Act
        Insurance insurance = basicAuctionServiceSUT.offerInsurancePerCategory(auctionId, buyerId);

        // Assert
        assertThat(insurance).isSameAs(expectedInsurance);
    }

    @Test
    public void shouldGetMostPopularAuctionsFromCategory() throws Exception{
        // Arrange
        Integer categoryId = Integer.valueOf(10);
        List<Auction> initialAuctions = new ArrayList<>();
        List<Auction> expectedAuctions = new ArrayList<>();

        AuctionRepository auctionRepositoryStub = Mockito.mock(AuctionRepository.class);
        when(auctionRepositoryStub.findByCategoryOrderByViewsDesc(categoryId, Integer.valueOf(20)))
                .thenReturn(initialAuctions);

        PopularityResolver popularityResolverStub = PowerMockito.mock(PopularityResolver.class);
        basicAuctionServiceSUT.setPopularityResolver(popularityResolverStub);
        PowerMockito.when(popularityResolverStub.resolveWithCap(initialAuctions, Integer.valueOf(10)))
                .thenReturn(expectedAuctions);

        // Act
        List<Auction> mostPopularAuctionsFromCategory = basicAuctionServiceSUT
                .getMostPopularAuctionsFromCategory(categoryId);

        // Assert
        assertThat(mostPopularAuctionsFromCategory).isSameAs(expectedAuctions);
    }

    @Test
    public void shouldRetryBidOnAuction() throws Exception{
        // Arrange
        Integer auctionId = Integer.valueOf(10);

        when(bidSaverStub.saveBidOnAuction(auctionId,dummyBid))
                .thenThrow(new AuctionLockedException())
                .thenReturn(true);

        PowerMockito.doNothing().when(basicAuctionServiceSUT, "waitABit");

        // Act
        boolean success = basicAuctionServiceSUT.bidOnAuction(auctionId, dummyBid);

        // Assert
        assertThat(success).isTrue();
        verify(bidSaverStub, times(2)).saveBidOnAuction(auctionId, dummyBid);
    }

    @Test
    public void shouldFailBidOnAuction_whenToManyAttempts() throws Exception{
        // Arrange
        Integer auctionId = Integer.valueOf(10);

        when(bidSaverStub.saveBidOnAuction(auctionId,dummyBid))
                .thenThrow(new AuctionLockedException());

        PowerMockito.doNothing().when(basicAuctionServiceSUT, "waitABit", anyInt());

        // Act
        boolean success = basicAuctionServiceSUT.bidOnAuction(auctionId, dummyBid);

        // Assert
        assertThat(success).isFalse();
        verify(bidSaverStub, times(3)).saveBidOnAuction(auctionId, dummyBid);
    }
}
