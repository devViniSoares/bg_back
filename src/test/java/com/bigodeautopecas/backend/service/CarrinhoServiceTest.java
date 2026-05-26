package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.exception.ResourceNotFoundException;
import com.bigodeautopecas.backend.model.Carrinho;
import com.bigodeautopecas.backend.model.ItemCarrinho;
import com.bigodeautopecas.backend.model.Produto;
import com.bigodeautopecas.backend.model.Usuario;
import com.bigodeautopecas.backend.repository.CarrinhoRepository;
import com.bigodeautopecas.backend.repository.ProdutoRepository;
import com.bigodeautopecas.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarrinhoServiceTest {

    @Mock private CarrinhoRepository carrinhoRepo;
    @Mock private ProdutoRepository produtoRepo;
    @Mock private UsuarioRepository usuarioRepo;

    @InjectMocks
    private CarrinhoService service;

    private Produto produto;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Filtro de Ar");
        produto.setPreco(30.0);
        produto.setEstoque(5);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("cliente@teste.com");
    }

    @Test
    void adicionarItem_deveCriarCarrinhoSeNaoExistir() {
        when(produtoRepo.findById(1L)).thenReturn(Optional.of(produto));
        when(carrinhoRepo.findByUsuarioEmail("cliente@teste.com")).thenReturn(Optional.empty());
        when(usuarioRepo.findByEmail("cliente@teste.com")).thenReturn(Optional.of(usuario));

        Carrinho carrinhoVazio = new Carrinho();
        carrinhoVazio.setUsuario(usuario);
        when(carrinhoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Carrinho resultado = service.adicionarItem("cliente@teste.com", 1L, 2);

        assertNotNull(resultado);
        assertEquals(1, resultado.getItens().size());
        assertEquals(2, resultado.getItens().get(0).getQuantidade());
        verify(usuarioRepo).findByEmail("cliente@teste.com");
    }

    @Test
    void adicionarItem_deveIncrementarQuantidadeSeItemJaExiste() {
        ItemCarrinho itemExistente = new ItemCarrinho();
        itemExistente.setId(10L);
        itemExistente.setProduto(produto);
        itemExistente.setQuantidade(3);

        Carrinho carrinho = new Carrinho();
        carrinho.setUsuario(usuario);
        carrinho.setItens(new ArrayList<>(List.of(itemExistente)));

        when(produtoRepo.findById(1L)).thenReturn(Optional.of(produto));
        when(carrinhoRepo.findByUsuarioEmail("cliente@teste.com")).thenReturn(Optional.of(carrinho));
        when(carrinhoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Carrinho resultado = service.adicionarItem("cliente@teste.com", 1L, 2);

        assertEquals(1, resultado.getItens().size());
        assertEquals(5, resultado.getItens().get(0).getQuantidade()); // 3 + 2
    }

    @Test
    void adicionarItem_deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        when(produtoRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.adicionarItem("cliente@teste.com", 99L, 1));
    }

    @Test
    void removerItem_deveLancarExcecaoQuandoCarrinhoNaoEncontrado() {
        when(carrinhoRepo.findByUsuarioEmail("cliente@teste.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.removerItem("cliente@teste.com", 1L));
    }

    @Test
    void removerItem_deveLancarExcecaoQuandoItemNaoEncontradoNoCarrinho() {
        Carrinho carrinho = new Carrinho();
        carrinho.setUsuario(usuario);
        carrinho.setItens(new ArrayList<>());

        when(carrinhoRepo.findByUsuarioEmail("cliente@teste.com")).thenReturn(Optional.of(carrinho));

        assertThrows(ResponseStatusException.class,
                () -> service.removerItem("cliente@teste.com", 999L));
    }

    @Test
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrado() {
        when(carrinhoRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(99L));
    }
}