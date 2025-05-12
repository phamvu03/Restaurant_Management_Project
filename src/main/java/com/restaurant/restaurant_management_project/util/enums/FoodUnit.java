package com.restaurant.restaurant_management_project.util.enums;

public enum FoodUnit {
    // Đơn vị cho thức ăn
    PLATE("Đĩa"),
    BOWL("Bát"),
    PIECE("Miếng"),
    PORTION("Phần"),
    SERVING("Khẩu phần"),


    // Đơn vị thể tích cho đồ uống
    CUP("Cốc"),
    GLASS("Ly"),
    BOTTLE("Chai"),
    CAN("Lon"),

    // Đơn vị cho đồ uống đặc biệt
    SHOT("Shot"),
    TEAPOT("Ấm");
    private final String displayName;


    FoodUnit(String displayName) {
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }


    @Override
    public String toString() {
        return displayName;
    }


    public static FoodUnit fromDisplayName(String displayName) {
        for (FoodUnit unit : FoodUnit.values()) {
            if (unit.getDisplayName().equalsIgnoreCase(displayName)) {
                return unit;
            }
        }
        return null;
    }
    public static java.util.List<String> getAllDisplayNames() {
        java.util.List<String> names = new java.util.ArrayList<>();
        for (FoodUnit unit : values()) {
            names.add(unit.getDisplayName());
        }
        return names;
    }
    public static java.util.List<FoodUnit> getAllUnit() {
        return java.util.Arrays.asList(values());
    }
}