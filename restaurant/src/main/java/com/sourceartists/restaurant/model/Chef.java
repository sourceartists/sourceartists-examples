package com.sourceartists.restaurant.model;

import com.sourceartists.restaurant.exception.OutOfIngredientsException;

public class Chef {
    public Ingredient[] prepareIngredients(Order order) throws OutOfIngredientsException {
        return null;
    }

    public Meat prepareMeat(Order order) {
        return null;
    }

    public Meal cook(MealType soup, Ingredient... ingredients) {
        return null;
    }

    public VegetableMix prepareVegies(Order order) {
        return null;
    }

    public Sauce prepareSauce(Order order) {
        return null;
    }
}
