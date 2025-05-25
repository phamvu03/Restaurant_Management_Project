package com.restaurant.restaurant_management_project.util;


import com.restaurant.restaurant_management_project.model.RMenuItem;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class MenuItemValidatorUtils {

    // Hằng số giới hạn
    private static final int NAME_MAX_LENGTH = 100;
    private static final BigDecimal PRICE_MIN = BigDecimal.ZERO;
    private static final BigDecimal PRICE_MAX = new BigDecimal("9999999"); // Tương đương 9,999,999 VND
    private static final int QUANTITY_MAX = 1000000; // 1 triệu

    // Regex patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\d\\s.,_\\-()/:+'\"&@#]+$");


    public static boolean validateName(String name, Text errorText) {
        if (name == null || name.trim().isEmpty()) {
            showError(errorText, "Tên món ăn không được để trống");
            return false;
        } else if (name.length() > NAME_MAX_LENGTH) {
            showError(errorText, "Tên món ăn không được vượt quá " + NAME_MAX_LENGTH + " ký tự");
            return false;
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            showError(errorText, "Tên món ăn chứa ký tự không hợp lệ");
            return false;
        }

        hideError(errorText);
        return true;
    }

    public static boolean validatePrice(String priceText, Text errorText) {
        try {
            if (priceText == null || priceText.trim().isEmpty()) {
                showError(errorText, "Giá tiền không được để trống");
                return false;
            }

            BigDecimal price = parsePrice(priceText);
            if (price.compareTo(PRICE_MIN) < 0) {
                showError(errorText, "Giá tiền không được âm");
                return false;
            } else if (price.compareTo(PRICE_MAX) > 0) {
                showError(errorText, "Giá tiền không được vượt quá 9,999,999 VND");
                return false;
            }

            hideError(errorText);
            return true;
        } catch (NumberFormatException e) {
            showError(errorText, "Giá tiền không hợp lệ");
            return false;
        }
    }

    public static boolean validateForm(String name, String price,
                                       Text errorNameText, Text errorPriceText
                                      ) {
        // Ẩn tất cả lỗi trước khi kiểm tra
        hideError(errorNameText);
        hideError(errorPriceText);

        // Kiểm tra từng trường
        boolean nameValid = validateName(name, errorNameText);
        boolean priceValid = validatePrice(price, errorPriceText);
        return nameValid && priceValid  ;
    }

    public static BigDecimal parsePrice(String priceText) throws NumberFormatException {
        if (priceText == null || priceText.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Loại bỏ tất cả ký tự không phải số và dấu thập phân
        String cleaned = priceText.replaceAll("[^0-9.,]", "");

        // Xử lý dấu phẩy thập phân
        cleaned = cleaned.replace(",", ".");

        // Nếu có nhiều dấu chấm, chỉ giữ lại dấu chấm cuối cùng
        int lastDot = cleaned.lastIndexOf(".");
        if (lastDot != cleaned.indexOf(".") && lastDot != -1) {
            cleaned = cleaned.substring(0, lastDot).replace(".", "") + cleaned.substring(lastDot);
        }

        return new BigDecimal(cleaned);
    }

    private static void showError(Text errorText, String message) {
        errorText.setText(message);
        errorText.setVisible(true);
    }


    private static void hideError(Text errorText) {
        errorText.setVisible(false);
    }

    public static Map<String, String> validateRMenuItem(RMenuItem item) {
        Map<String, String> errors = new HashMap<>();

        // Kiểm tra tên
        if (item.getItemName() == null || item.getItemName().trim().isEmpty()) {
            errors.put("itemName", "Tên món ăn không được để trống");
        } else if (item.getItemName().length() > NAME_MAX_LENGTH) {
            errors.put("itemName", "Tên món ăn không được vượt quá " + NAME_MAX_LENGTH + " ký tự");
        } else if (!NAME_PATTERN.matcher(item.getItemName()).matches()) {
            errors.put("itemName", "Tên món ăn chứa ký tự không hợp lệ");
        }

        // Kiểm tra giá
        if (item.getItemPrice() == null) {
            errors.put("itemPrice", "Giá tiền không được để trống");
        } else if (item.getItemPrice().compareTo(PRICE_MIN) < 0) {
            errors.put("itemPrice", "Giá tiền không được âm");
        } else if (item.getItemPrice().compareTo(PRICE_MAX) > 0) {
            errors.put("itemPrice", "Giá tiền không được vượt quá 9,999,999 VND");
        }

        return errors;
    }
}
