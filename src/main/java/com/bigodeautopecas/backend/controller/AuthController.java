package com.bigodeautopecas.backend.controller;

import com.bigodeautopecas.backend.dto.CadastroRequest;
import com.bigodeautopecas.backend.dto.LoginRequest;
import com.bigodeautopecas.backend.dto.LoginResponse;
import com.bigodeautopecas.backend.model.Usuario;
import com.bigodeautopecas.backend.repository.UsuarioRepository;
import com.bigodeautopecas.backend.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          UsuarioRepository usuarioRepo,
                          PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.senha()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        }

        Usuario u = usuarioRepo.findByEmail(req.email()).orElseThrow();
        String token = jwtUtil.gerarToken(u.getEmail(), u.getTipo());
        return ResponseEntity.ok(new LoginResponse(token, u.getTipo(), u.getId(), u.getNome()));
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastro(@Valid @RequestBody CadastroRequest req) {
        if (usuarioRepo.existsByEmail(req.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("E-mail já cadastrado.");
        }
        Usuario u = new Usuario();
        u.setNome(req.nome());
        u.setEmail(req.email());
        u.setSenha(passwordEncoder.encode(req.senha()));
        u.setTipo("CLIENTE");
        usuarioRepo.save(u);
        return ResponseEntity.status(HttpStatus.CREATED).body("Cadastro realizado com sucesso.");
    }
}
