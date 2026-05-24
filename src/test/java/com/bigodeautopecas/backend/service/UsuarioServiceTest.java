package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.model.Usuario;
import com.bigodeautopecas.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void salvar_deveCodificarSenhaEmTextoPlano() {
        Usuario usuario = new Usuario();
        usuario.setNome("João");
        usuario.setEmail("joao@teste.com");
        usuario.setSenha("senha123");

        when(passwordEncoder.encode("senha123")).thenReturn("$2a$10$hashBCrypt");
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        usuarioService.salvar(usuario);

        assertEquals("$2a$10$hashBCrypt", usuario.getSenha());
        verify(passwordEncoder).encode("senha123");
    }

    @Test
    void salvar_naoDeveRecodificarSenhaQueJaEhBCrypt() {
        Usuario usuario = new Usuario();
        usuario.setSenha("$2a$10$hashJaExistente");

        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        usuarioService.salvar(usuario);

        verify(passwordEncoder, never()).encode(any());
        assertEquals("$2a$10$hashJaExistente", usuario.getSenha());
    }

    @Test
    void salvar_deveManterSenhaNulaSeNaoFornecida() {
        Usuario usuario = new Usuario();
        usuario.setNome("Maria");
        usuario.setEmail("maria@teste.com");
        usuario.setSenha(null);

        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        usuarioService.salvar(usuario);

        verify(passwordEncoder, never()).encode(any());
        assertNull(usuario.getSenha());
    }

    @Test
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrado() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usuarioService.buscarPorId(99L));
    }
}