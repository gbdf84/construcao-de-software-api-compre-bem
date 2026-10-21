package com.senai.gilberto.comprebem.service;

import com.senai.gilberto.comprebem.dto.ProdutoRequestDTO;
import com.senai.gilberto.comprebem.dto.ProdutoResponseDTO;
import com.senai.gilberto.comprebem.entity.Produto;
import com.senai.gilberto.comprebem.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
        return ProdutoResponseDTO.toDto(produto);
    }

    public List<ProdutoResponseDTO> findByFiltro(String filtro, Pageable pageable) {
        return produtoRepository.findByFiltro(filtro, inInsertionOrder(pageable)).stream()
                .map(ProdutoResponseDTO::toDto)
                .toList();
    }

    public List<ProdutoResponseDTO> findByNome(String busca, Pageable pageable) {
        return produtoRepository.findByNomeContainingIgnoreCase(busca, inInsertionOrder(pageable)).stream()
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        produtoExistente.setNome(produto.nome());
        produtoExistente.setDescricao(produto.descricao());
        produtoExistente.setPreco(produto.preco());
        produtoExistente.setCategoria(produto.categoria());
        produtoExistente.setImagem(produto.imagem());
        produtoExistente.setQuantidade(produto.quantidade());

        return ProdutoResponseDTO.toDto(produtoRepository.save(produtoExistente));
    }
}
