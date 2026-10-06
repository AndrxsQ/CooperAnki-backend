package com.coopera2075.cooperanki.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentRequest {
    @NotNull (message = "El monto es obligatorio")
    @Positive (message = "El monto debe ser mayor que 0")
    private Double amount;
}
