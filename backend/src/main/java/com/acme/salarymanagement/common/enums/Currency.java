package com.acme.salarymanagement.common.enums;

public enum Currency {

    INR("INR", "Indian Rupee", Country.INDIA, "INR"),
    USD("USD", "US Dollar", Country.UNITED_STATES, "$"),
    GBP("GBP", "British Pound", Country.UNITED_KINGDOM, "GBP"),
    EUR("EUR", "Euro", Country.GERMANY, "EUR"),
    CAD("CAD", "Canadian Dollar", Country.CANADA, "C$"),
    AUD("AUD", "Australian Dollar", Country.AUSTRALIA, "A$");

    private final String code;
    private final String label;
    private final Country country;
    private final String symbol;

    Currency(String code, String label, Country country, String symbol) {
        this.code = code;
        this.label = label;
        this.country = country;
        this.symbol = symbol;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public Country getCountry() {
        return country;
    }

    public String getSymbol() {
        return symbol;
    }

    public static Currency fromCountry(Country country) {
        for (Currency currency : values()) {
            if (currency.country == country) {
                return currency;
            }
        }

        throw new IllegalArgumentException("No currency configured for country: " + country);
    }
}
