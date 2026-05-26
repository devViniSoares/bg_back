package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.exception.ResourceNotFoundException;
import com.bigodeautopecas.backend.model.Produto;
import com.bigodeautopecas.backend.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repo;

    @InjectMocks
    private ProdutoService service;

    private Produto produto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Amortecedor Dianteiro");
        produto.setPreco(250.0);
        produto.setEstoque(8);
        produto.setCategoria("Suspensão");
        produto.setMarca("Monroe");

        pageable = Pageable.unpaged();
    }

    @Test
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrado() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void buscarPorId_deveRetornarProdutoQuandoEncontrado() {
        when(repo.findById(1L)).thenReturn(Optional.of(produto));

        Produto resultado = service.buscarPorId(1L);

        assertEquals("Amortecedor Dianteiro", resultado.getNome());
    }

    @Test
    @SuppressWarnings("unchecked")
    void listarComFiltros_deveUsarSpecificationComCategoria() {
        Page<Produto> page = new PageImpl<>(List.of(produto));
        when(repo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<Produto> resultado = service.listarComFiltros("Suspensão", null, null, null, pageable);

        assertEquals(1, resultado.getTotalElements());
        verify(repo).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @SuppressWarnings("unchecked")
    void listarComFiltros_deveUsarSpecificationComMarca() {
        Page<Produto> page = new PageImpl<>(List.of(produto));
        when(repo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<Produto> resultado = service.listarComFiltros(null, "Monroe", null, null, pageable);

        assertEquals(1, resultado.getTotalElements());
        verify(repo).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @SuppressWarnings("unchecked")
    void listarComFiltros_deveCombinarFiltros() {
        Page<Produto> page = new PageImpl<>(List.of(produto));
        when(repo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // Categoria + Marca combinados — antes era impossível
        Page<Produto> resultado = service.listarComFiltros("Suspensão", "Monroe", null, null, pageable);

        assertEquals(1, resultado.getTotalElements());
        verify(repo).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @SuppressWarnings("unchecked")
    void listarComFiltros_deveRetornarTodosQuandoSemFiltro() {
        Page<Produto> page = new PageImpl<>(List.of(produto));
        when(repo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<Produto> resultado = service.listarComFiltros(null, null, null, null, pageable);

        assertEquals(1, resultado.getTotalElements());
        verify(repo).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void deletar_deveLancarExcecaoSeNaoExistir() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deletar(99L));
        verify(repo, never()).deleteById(any());
    }

    @Test
    void listarCategorias_deveDelegarAoRepositorio() {
        when(repo.findCategoriasDistintas()).thenReturn(List.of("Freios", "Motor", "Suspensão"));

        List<String> resultado = service.listarCategorias();

        assertEquals(3, resultado.size());
        verify(repo).findCategoriasDistintas();
    }

    @Test
    void listarMarcas_deveDelegarAoRepositorio() {
        when(repo.findMarcasDistintas()).thenReturn(List.of("Bosch", "Monroe"));

        List<String> resultado = service.listarMarcas();

        assertEquals(2, resultado.size());
        verify(repo).findMarcasDistintas();
    }

    @Test
    void listarModelos_deveDelegarAoRepositorio() {
        when(repo.findModelosDistintos()).thenReturn(List.of("Corolla", "Civic", "HB20"));

        List<String> resultado = service.listarModelos();

        assertEquals(3, resultado.size());
        verify(repo).findModelosDistintos();
    }
}