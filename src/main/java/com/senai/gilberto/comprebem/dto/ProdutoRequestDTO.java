package com.senai.gilberto.comprebem.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProdutoRequestDTO(
        @NotBlank String nome,
        String descricao,
        @NotNull BigDecimal preco,
        String categoria,
        String imagem,
        @NotNull @PositiveOrZero Integer quantidade
) {
}
