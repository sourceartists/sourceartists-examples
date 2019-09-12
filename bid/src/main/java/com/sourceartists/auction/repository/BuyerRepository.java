package com.sourceartists.auction.repository;

import com.sourceartists.auction.model.Buyer;
import com.sourceartists.auction.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Integer> {


}
