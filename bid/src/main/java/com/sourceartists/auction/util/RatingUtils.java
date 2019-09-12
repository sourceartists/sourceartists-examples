package com.sourceartists.auction.util;

import com.sourceartists.auction.model.Rating;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.List;

public final class RatingUtils {

    public static BigDecimal calculateRating(List<Rating> ratings){
        final Integer roundingStrategy = Integer.valueOf(
                getApplicationContext().getEnvironment().getProperty("ROUNDING_STRATEGY"));

        return ratings.stream()
                .map(Rating::getRating)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(ratings.size()), roundingStrategy);
    }

    private static ApplicationContext getApplicationContext(){
        return null;
    }
}
