package com.senai.jonatas.comprebem;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import com.senai.jonatas.comprebem.entity.Produto;
import com.senai.jonatas.comprebem.repository.ProdutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@OpenAPIDefinition
public class ComprebemApplication {

    private static final int QUANTIDADE_MINIMA_PRODUTOS = 60;

    public static void main(String[] args) {
        SpringApplication.run(ComprebemApplication.class, args);
    }

    @Bean
    CommandLineRunner carregarProdutosIniciais(ProdutoRepository produtoRepository) {
        return args -> {
            long cadastrados = produtoRepository.count();
            int faltantes = (int) Math.max(0, QUANTIDADE_MINIMA_PRODUTOS - cadastrados);
            if (faltantes == 0) {
                return;
            }

            List<Produto> produtos = new ArrayList<>(faltantes);
            for (int i = 1; i <= faltantes; i++) {
                Produto produto = new Produto();
                produto.setNome(i == 1 ? "Cadeira de escritório" : "Produto demonstração %02d".formatted(i));
                produto.setDescricao("Produto de demonstração para consulta e paginação.");
                produto.setCategoria(i == 1 ? "Móveis" : "Demonstração");
                produto.setPreco(BigDecimal.valueOf(10L + i));
                produto.setQuantidade(10);
                produtos.add(produto);
            }
            produtoRepository.saveAll(produtos);
        };
    }

}
