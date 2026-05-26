package com.bigodeautopecas.backend.controller;

import com.bigodeautopecas.backend.dto.CadastroRequest;
import com.bigodeautopecas.backend.dto.LoginRequest;
import com.bigodeautopecas.backend.dto.LoginResponse;
import com.bigodeautopecas.backend.dto.RefreshTokenRequest;
import com.bigodeautopecas.backend.model.Usuario;
import com.bigodeautopecas.backend.repository.UsuarioRepository;
import com.bigodeautopecas.backend.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
@Tag(name = "Autenticação", description = "Login, cadastro e renovação de token")
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
    @Operation(summary = "Login", description = "Autentica e retorna access token (24h) + refresh token (7 dias)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.senha()));

        Usuario u = usuarioRepo.findByEmail(req.email()).orElseThrow();
        String token = jwtUtil.gerarToken(u.getEmail(), u.getTipo());
        String refreshToken = jwtUtil.gerarRefreshToken(u.getEmail());
        return ResponseEntity.ok(new LoginResponse(token, refreshToken, u.getTipo(), u.getId(), u.getNome()));
    }

    @PostMapping("/cadastro")
    @Operation(summary = "Cadastro", description = "Registra um novo usuário como CLIENTE")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
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

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Recebe o refresh token e retorna novo par de tokens sem precisar da senha")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tokens renovados com sucesso"),
        @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado")
    })
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest req) {
        String refreshToken = req.refreshToken();

        if (!jwtUtil.validar(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtUtil.extrairEmail(refreshToken);
        Usuario u = usuarioRepo.findByEmail(email).orElse(null);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String newToken = jwtUtil.gerarToken(email, u.getTipo());
        String newRefresh = jwtUtil.gerarRefreshToken(email);
        return ResponseEntity.ok(new LoginResponse(newToken, newRefresh, u.getTipo(), u.getId(), u.getNome()));
    }
}