package com.restaurant.restaurant_management_project.util.enums;

public enum ItemCategory {

        APPETIZER("Món khai vị"),
        MAIN_COURSE("Món chính"),
        DESSERT("Món tráng miệng"),
        DRINK("Đồ uống"),
        SIDE_DISH("Món ăn kèm"),
        VEGETARIAN("Món chay");

        private final String displayName;

        ItemCategory(String displayName) {
            this.displayName = displayName;
        }

        /**
         * Lấy tên hiển thị (tiếng Việt) của phân loại.
         * @return Tên hiển thị.
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Lấy danh sách tất cả các tên hiển thị.
         * @return List các tên hiển thị.
         */
        public static java.util.List<String> getAllDisplayNames() {
            java.util.List<String> names = new java.util.ArrayList<>();
            for (ItemCategory classification : values()) {
                names.add(classification.getDisplayName());
            }
            return names;
        }

        /**
         * Lấy danh sách tất cả các enum constants.
         * @return List các FoodClassification.
         */
        public static java.util.List<ItemCategory> getAllClassifications() {
            return java.util.Arrays.asList(values());
        }
}
