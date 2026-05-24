package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.model.ItemPedido;
import com.bigodeautopecas.backend.model.Pedido;
import com.bigodeautopecas.backend.model.Produto;
import com.bigodeautopecas.backend.model.Usuario;
import com.bigodeautopecas.backend.repository.PedidoRepository;
import com.bigodeautopecas.backend.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepo;

    @Mock
    private ProdutoRepository produtoRepo;

    @InjectMocks
    private PedidoService pedidoService;

    private Produto produto;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Filtro de Óleo");
        produto.setPreco(25.0);
        produto.setEstoque(10);

        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(3);
        item.setPrecoUnitario(25.0);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("cliente@teste.com");

        pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setTotal(75.0);
        pedido.setStatus("AGUARDANDO");
        pedido.setItens(List.of(item));
    }

    @Test
    void salvar_deveDecrementarEstoqueAoCriarPedido() {
        when(produtoRepo.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepo.save(any())).thenReturn(produto);
        when(pedidoRepo.save(any())).thenReturn(pedido);

        pedidoService.salvar(pedido);

        assertEquals(7, produto.getEstoque()); // 10 - 3 = 7
        verify(produtoRepo).save(produto);
    }

    @Test
    void salvar_deveLancarExcecaoQuandoEstoqueInsuficiente() {
        produto.setEstoque(2); // menos que os 3 solicitados
        when(produtoRepo.findById(1L)).thenReturn(Optional.of(produto));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> pedidoService.salvar(pedido));
        assertTrue(ex.getMessage().contains("Estoque insuficiente"));
        verify(pedidoRepo, never()).save(any());
    }

    @Test
    void salvar_naoDeveAlterarEstoqueAoAtualizarPedidoExistente() {
        pedido.setId(5L); // pedido já existente no banco
        when(pedidoRepo.save(any())).thenReturn(pedido);

        pedidoService.salvar(pedido);

        verify(produtoRepo, never()).findById(any());
        verify(produtoRepo, never()).save(any());
    }

    @Test
    void salvar_deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        when(produtoRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pedidoService.salvar(pedido));
        verify(pedidoRepo, never()).save(any());
    }
}