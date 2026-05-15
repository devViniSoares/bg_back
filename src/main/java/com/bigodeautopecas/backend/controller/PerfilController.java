package com.bigodeautopecas.backend.controller;

import com.bigodeautopecas.backend.dto.AlterarSenhaRequest;
import com.bigodeautopecas.backend.dto.AtualizarPerfilRequest;
import com.bigodeautopecas.backend.model.Usuario;
import com.bigodeautopecas.backend.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/me")
@CrossOrigin
public class PerfilController {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    public PerfilController(UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public Usuario meuPerfil(Authentication auth) {
        return usuarioRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping
    public Usuario atualizarPerfil(@Valid @RequestBody AtualizarPerfilRequest req, Authentication auth) {
        Usuario u = usuarioRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!req.email().equals(u.getEmail()) && usuarioRepo.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        }
        u.setNome(req.nome());
        u.setEmail(req.email());
        return usuarioRepo.save(u);
    }

    @PutMapping("/senha")
    public ResponseEntity<String> alterarSenha(@Valid @RequestBody AlterarSenhaRequest req, Authentication auth) {
        Usuario u = usuarioRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(req.senhaAtual(), u.getSenha())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta.");
        }
        u.setSenha(passwordEncoder.encode(req.novaSenha()));
        usuarioRepo.save(u);
        return ResponseEntity.ok("Senha alterada com sucesso.");
    }
}
