package com.acme.salarymanagement.common.enums;

public enum Country {

    INDIA("India"),
    UNITED_STATES("United States"),
    UNITED_KINGDOM("United Kingdom"),
    GERMANY("Germany"),
    CANADA("Canada"),
    AUSTRALIA("Australia");

    private final String label;

    Country(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
