package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.model.Usuario;
import com.bigodeautopecas.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
    }

    public Usuario buscarPorEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));
    }

    public Usuario salvar(Usuario usuario) {
        // Garante que a senha sempre seja armazenada com hash BCrypt
        if (usuario.getSenha() != null && !usuario.getSenha().startsWith("$2")) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        return repo.save(usuario);
    }

    public void deletar(Long id) {
        repo.deleteById(id);
    }
}
