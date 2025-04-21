package com.restaurant.restaurant_management_project.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record ItemFillterData( Boolean status,
                               BigDecimal minPrice,
                               BigDecimal maxPrice,
                               List<String> selectedCategories) {
        public ItemFillterData {
            Objects.requireNonNullElse(selectedCategories, List.of());
        }
    }

