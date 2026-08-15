package com.acme.salarymanagement.common.enums;

public enum Role {

    SOFTWARE_ENGINEER("Software Engineer"),
    SENIOR_SOFTWARE_ENGINEER("Senior Software Engineer"),
    TECH_LEAD("Tech Lead"),
    QA_ENGINEER("QA Engineer"),
    PRODUCT_MANAGER("Product Manager"),
    BUSINESS_ANALYST("Business Analyst"),
    HR_MANAGER("HR Manager");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
