package com.senai.jonatas.comprebem.service;

import com.senai.jonatas.comprebem.dto.ProdutoRequestDTO;
import com.senai.jonatas.comprebem.dto.ProdutoResponseDTO;
import com.senai.jonatas.comprebem.entity.Produto;
import com.senai.jonatas.comprebem.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public List<ProdutoResponseDTO> findAll(Pageable pageable) {
        return produtoRepository.findAll(inInsertionOrder(pageable)).stream()
                .map(ProdutoResponseDTO::toDto)
                .toList();
    }

    public ProdutoResponseDTO findById(UUID id) {
        var produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        return ProdutoResponseDTO.toDto(produto);
    }

    public List<ProdutoResponseDTO> findByFiltro(String filtro, Pageable pageable) {
        return produtoRepository.findByFiltro(filtro, inInsertionOrder(pageable)).stream()
                .map(ProdutoResponseDTO::toDto)
                .toList();
    }

    private Pageable inInsertionOrder(Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "criadoEm"));
    }

    public ProdutoResponseDTO save(ProdutoRequestDTO produto) {
        Produto produtoSalvo = produtoRepository.save(Produto.toEntity(produto));
        return ProdutoResponseDTO.toDto(produtoSalvo);
    }

    public void delete(UUID produtoId) {
        produtoRepository.deleteById(produtoId);
    }

    public ProdutoResponseDTO update(UUID produtoId, ProdutoRequestDTO produto) {
        Produto produtoExistente = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produtoExistente.setNome(produto.nome());
        produtoExistente.setDescricao(produto.descricao());
        produtoExistente.setPreco(produto.preco());
        produtoExistente.setCategoria(produto.categoria());
        produtoExistente.setImagem(produto.imagem());
        produtoExistente.setQuantidade(produto.quantidade());

        Produto produtoAtualizado = produtoRepository.save(produtoExistente);
        return ProdutoResponseDTO.toDto(produtoAtualizado);

    }
}
