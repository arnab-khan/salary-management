package com.acme.salarymanagement.common;

import com.acme.salarymanagement.common.dto.CurrencyOptionResponse;
import com.acme.salarymanagement.common.dto.EnumOptionResponse;
import com.acme.salarymanagement.common.dto.EnumsResponse;
import com.acme.salarymanagement.common.enums.Country;
import com.acme.salarymanagement.common.enums.Currency;
import com.acme.salarymanagement.common.enums.Role;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping
    public EnumsResponse getEnums() {
        return new EnumsResponse(
                countryOptions(),
                currencyOptions(),
                roleOptions());
    }

    private List<EnumOptionResponse> countryOptions() {
        return Arrays.stream(Country.values())
                .map(country -> new EnumOptionResponse(country.name(), country.getLabel()))
                .toList();
    }

    private List<CurrencyOptionResponse> currencyOptions() {
        return Arrays.stream(Currency.values())
                .map(currency -> new CurrencyOptionResponse(currency.name(), currency.getLabel(), currency.getSymbol()))
                .toList();
    }

    private List<EnumOptionResponse> roleOptions() {
        return Arrays.stream(Role.values())
                .map(role -> new EnumOptionResponse(role.name(), role.getLabel()))
                .toList();
    }
}
