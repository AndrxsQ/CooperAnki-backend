package com.coopera2075.cooperanki.dto;

import com.coopera2075.cooperanki.entity.EconomicActivity;

public record EconomicActivityResponse(String ciiuId, String name) {
    public static EconomicActivityResponse from(EconomicActivity entity) {
        return new EconomicActivityResponse(entity.getCiiuId(), entity.getName());
    }
}
