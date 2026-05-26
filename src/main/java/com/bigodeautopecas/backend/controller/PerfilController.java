package com.bigodeautopecas.backend.controller;

import com.bigodeautopecas.backend.dto.AlterarSenhaRequest;
import com.bigodeautopecas.backend.dto.AtualizarPerfilRequest;
import com.bigodeautopecas.backend.model.Usuario;
import com.bigodeautopecas.backend.repository.UsuarioRepository;
import com.bigodeautopecas.backend.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/me")
@CrossOrigin
@Tag(name = "Perfil", description = "Gerenciamento do perfil do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class PerfilController {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private EmailService emailService;

    public PerfilController(UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @Operation(summary = "Meu perfil", description = "Retorna os dados do usuário autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil retornado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public Usuario meuPerfil(Authentication auth) {
        return usuarioRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping
    @Operation(summary = "Atualizar perfil", description = "Atualiza nome e e-mail do usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil atualizado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "E-mail já utilizado")
    })
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
    @Operation(summary = "Alterar senha", description = "Altera a senha do usuário autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Senha alterada"),
        @ApiResponse(responseCode = "400", description = "Senha atual incorreta")
    })
    public ResponseEntity<String> alterarSenha(@Valid @RequestBody AlterarSenhaRequest req, Authentication auth) {
        Usuario u = usuarioRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(req.senhaAtual(), u.getSenha())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta.");
        }
        u.setSenha(passwordEncoder.encode(req.novaSenha()));
        usuarioRepo.save(u);

        if (emailService != null) {
            emailService.enviarRedefinicaoSenha(u.getEmail(), null);
        }

        return ResponseEntity.ok("Senha alterada com sucesso.");
    }
}