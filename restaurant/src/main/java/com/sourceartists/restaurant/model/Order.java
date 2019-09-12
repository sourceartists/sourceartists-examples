package com.sourceartists.restaurant.model;

public class Order {

    public Order() {
    }

    public Order(MealType mealType) {
        this.mealType = mealType;
    }

    private MealType mealType;
    private boolean vegetarian;

    public boolean isVegetarian() {
        return false;
    }

    public boolean isSoup() {
        return mealType.equals(MealType.SOUP);
    }

    public boolean isStew() {
        return mealType.equals(MealType.STEW);
    }
}
