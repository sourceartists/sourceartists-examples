package com.sourceartists.auction.service.helper;

import com.sourceartists.auction.model.Category;
import com.sourceartists.auction.model.Insurance;

import java.math.BigDecimal;

public class InsuranceCalculator {

    private Category category;
    private Integer creditScore;

    public InsuranceCalculator(Category category, Integer creditScore) {
        this.category = category;
        this.creditScore = creditScore;
    }

    public InsuranceCalculator() {
    }

    public Insurance generateQuote(BigDecimal productWorth, Integer creditScore) {
        return null;
    }

    public Insurance generateQuote(BigDecimal productWorth) {
        return null;
    }
}
