package com.acme.salarymanagement.common.dto;

import java.util.List;

public record EnumsResponse(
        List<EnumOptionResponse> countries,
        List<CurrencyOptionResponse> currencies,
        List<EnumOptionResponse> roles
) {
}
