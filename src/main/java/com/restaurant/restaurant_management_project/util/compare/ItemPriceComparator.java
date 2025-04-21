package com.restaurant.restaurant_management_project.util.compare;

import com.restaurant.restaurant_management_project.model.MenuItem;

import java.util.Comparator;

public class ItemPriceComparator implements Comparator<MenuItem> {

    @Override
    public int compare(MenuItem o1, MenuItem o2) {
        return o1.getPrice().compareTo(o2.getPrice());
    }
}
