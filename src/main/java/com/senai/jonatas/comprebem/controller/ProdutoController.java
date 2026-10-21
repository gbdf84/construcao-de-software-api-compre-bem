package com.senai.jonatas.comprebem.controller;

import com.senai.jonatas.comprebem.dto.ProdutoRequestDTO;
import com.senai.jonatas.comprebem.dto.ProdutoResponseDTO;
import com.senai.jonatas.comprebem.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public List<ProdutoResponseDTO> findAll(
            @PageableDefault(size = 10) Pageable pageable) {
        return produtoService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ProdutoResponseDTO findById(UUID id) {
        return produtoService.findById(id);
    }

    @GetMapping("/filtro")
    public List<ProdutoResponseDTO> findByFiltro(
            @RequestParam String filtro,
            @PageableDefault(size = 10) Pageable pageable) {
        return produtoService.findByFiltro(filtro, pageable);
    }

    @PostMapping
    public ProdutoResponseDTO save(@Valid @RequestBody ProdutoRequestDTO produto) {
        return produtoService.save(produto);
    }

    @PutMapping("/{produtoId}")
    public ProdutoResponseDTO update(@PathVariable UUID produtoId, @Valid @RequestBody ProdutoRequestDTO produto) {
        return produtoService.update(produtoId, produto);
    }

    @DeleteMapping
    public void delete(UUID produtoId) {
        produtoService.delete(produtoId);
    }
}
