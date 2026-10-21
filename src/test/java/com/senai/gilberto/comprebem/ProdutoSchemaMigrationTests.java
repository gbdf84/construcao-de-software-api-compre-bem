package com.senai.gilberto.comprebem;

import com.senai.gilberto.comprebem.entity.Produto;
import com.senai.gilberto.comprebem.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.DriverManager;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:comprebem-schema-migration;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=update"
})
class ProdutoSchemaMigrationTests {

    private static final String DATABASE_URL =
            "jdbc:h2:mem:comprebem-schema-migration;DB_CLOSE_DELAY=-1";
    private static final UUID LEGACY_PRODUCT_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    static {
        try (var connection = DriverManager.getConnection(DATABASE_URL, "sa", "");
             var statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE TB_PRODUTO (
                        ID UUID PRIMARY KEY,
                        NOME VARCHAR(100) NOT NULL,
                        PRECO NUMERIC(10, 2) NOT NULL,
                        CATEGORIA VARCHAR(255),
                        DESCRICAO VARCHAR(500),
                        IMAGEM VARCHAR(255),
                        QUANTIDADE INTEGER NOT NULL
                    )
                    """);
            statement.executeUpdate("""
                    INSERT INTO TB_PRODUTO (ID, NOME, PRECO, QUANTIDADE)
                    VALUES ('00000000-0000-0000-0000-000000000001', 'Produto antigo', 10.00, 3)
                    """);
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldMigrateLegacyRowsAndSupportGetAndPost() throws Exception {
        Produto legacyProduct = produtoRepository.findById(LEGACY_PRODUCT_ID).orElseThrow();
        assertThat(legacyProduct.getCriadoEm()).isNull();

        mockMvc.perform(get("/api/v1/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Produto antigo"));

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Produto novo",
                                  "preco": 10.00,
                                  "quantidade": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Produto novo"));

        Produto savedProduct = produtoRepository.findAll().stream()
                .filter(produto -> "Produto novo".equals(produto.getNome()))
                .findFirst()
                .orElseThrow();
        assertThat(savedProduct.getCriadoEm()).isNotNull();
    }
}
