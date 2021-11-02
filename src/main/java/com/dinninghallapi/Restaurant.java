package com.dinninghallapi;

import com.dinninghallapi.foods.Foods;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;

public class Restaurant {

    private static float totalSum = 0;

    private static int rates = 0;

    @JsonProperty("restaurant_id")
    @Getter
    private final int id;

    @JsonProperty("name")
    @Getter
    private final String name;

    @JsonProperty("address")
    @Getter
    private final String address;

    @JsonProperty("menu_items")
    private final int menuItems;

    @JsonProperty("menu")
    private final ArrayList<Foods> menu;

    @JsonProperty("rating")
    private float generalRating;

    public Restaurant(int id, String name, String address, int menuItems, ArrayList<Foods> menu) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.menuItems = menuItems;
        this.menu = menu;
    }

    public synchronized float addRating(int mark) {
        rates++;
        totalSum += mark;
        generalRating = totalSum / rates;
        return generalRating;
    }

}
