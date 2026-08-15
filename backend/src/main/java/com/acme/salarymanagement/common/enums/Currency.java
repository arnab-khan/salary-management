package com.acme.salarymanagement.common.enums;

public enum Currency {

    INR("INR", "Indian Rupee", "₹"),
    USD("USD", "US Dollar", "$"),
    GBP("GBP", "British Pound", "£"),
    EUR("EUR", "Euro", "€"),
    CAD("CAD", "Canadian Dollar", "C$"),
    AUD("AUD", "Australian Dollar", "A$");

    private final String code;
    private final String label;
    private final String symbol;

    Currency(String code, String label, String symbol) {
        this.code = code;
        this.label = label;
        this.symbol = symbol;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getSymbol() {
        return symbol;
    }
}
