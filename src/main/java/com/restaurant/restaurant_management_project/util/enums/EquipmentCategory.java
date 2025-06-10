package com.restaurant.restaurant_management_project.util.enums;

public enum EquipmentCategory {
    IN_STOCK("Còn hàng"),
    NORMAL("Tốt"),
    NEED_REPLENISHMENT("Bổ sung");

    private final String displayName;

    EquipmentCategory(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Lấy tên hiển thị (tiếng Việt) của trạng thái tồn kho.
     * @return Tên hiển thị.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Lấy danh sách tất cả các tên hiển thị.
     * @return Danh sách tên hiển thị.
     */
    public static java.util.List<String> getAllDisplayNames() {
        java.util.List<String> names = new java.util.ArrayList<>();
        for (EquipmentCategory status : values()) {
            names.add(status.getDisplayName());
        }
        return names;
    }

    /**
     * Lấy danh sách tất cả các trạng thái tồn kho.
     * @return Danh sách InventoryStatus.
     */
    public static java.util.List<EquipmentCategory> getAllStatuses() {
        return java.util.Arrays.asList(values());
    }

}
