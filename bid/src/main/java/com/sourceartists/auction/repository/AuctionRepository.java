package com.sourceartists.auction.repository;

import com.sourceartists.auction.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Integer> {


    List<Auction> findByCategoryOrderByViewsDesc(Integer id, Integer categoryId);
}
