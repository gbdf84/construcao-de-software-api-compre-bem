package com.senai.jonatas.comprebem;

import com.senai.jonatas.comprebem.entity.Produto;
import com.senai.jonatas.comprebem.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:mem:comprebem-pagination-test;DB_CLOSE_DELAY=-1"
)
class ComprebemApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProdutoRepository produtoRepository;

    @BeforeEach
    void clearProducts() {
        produtoRepository.deleteAll();
    }

    @Test
    void shouldPaginateProductListAndFilter() throws Exception {
        for (int i = 1; i <= 12; i++) {
            Produto produto = new Produto();
            produto.setNome("Produto %02d".formatted(13 - i));
            produto.setDescricao("Descrição do produto");
            produto.setPreco(BigDecimal.ONE);
            produto.setQuantidade(i);
            produtoRepository.save(produto);
        }

        mockMvc.perform(get("/api/v1/produtos?page=1&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].nome").value("Produto 07"));

        mockMvc.perform(get("/api/v1/produtos/filtro?filtro=Produto&page=2&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Produto 02"));

        mockMvc.perform(get("/api/v1/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].nome").value("Produto 12"));
    }

    @Test
    void shouldSaveProductAndRejectMissingQuantity() throws Exception {
        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Arroz",
                                  "descricao": "Pacote de 5 kg",
                                  "preco": 25.90,
                                  "categoria": "Alimentos",
                                  "quantidade": 10
                                }
                                """))
                .andExpect(status().isOk());
        org.assertj.core.api.Assertions.assertThat(produtoRepository.count()).isEqualTo(1);

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Feijão",
                                  "preco": 8.50
                                }
                                """))
                .andExpect(status().isBadRequest());
        org.assertj.core.api.Assertions.assertThat(produtoRepository.count()).isEqualTo(1);
    }
}
