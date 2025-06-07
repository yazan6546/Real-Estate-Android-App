package com.example.realestate.domain.service;

import java.util.HashMap;
import java.util.Map;

public class CountryService {

    public static final Map<String, String[]> countriesWithCities = new HashMap<>() {{
        put("Palestine", new String[]{"Nablus", "Tulkarem", "Ramallah"});
        put("Jordan", new String[]{"Amman", "Al Karak", "Irbid"});
        put("UAE", new String[]{"Dubai", "Abu Dhabi", "Sharjah"});
    }};

    public static final Map<String, String> countryCodeMap = new HashMap<>() {{
        put("Palestine", "970");
        put("UAE", "962");
        put("Jordan", "971");
    }};
}
