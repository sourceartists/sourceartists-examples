package com.sourceartists.auction.service.helper;

import com.sourceartists.auction.model.Auction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PopularityResolver {
    public final List<Auction> resolveWithCap(List<Auction> topTwentyAuctions, Integer mostPopularCount) {
        return null;
    }
}
