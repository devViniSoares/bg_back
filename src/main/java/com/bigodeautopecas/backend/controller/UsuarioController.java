package com.bigodeautopecas.backend.controller;

import com.bigodeautopecas.backend.dto.UsuarioRequest;
import com.bigodeautopecas.backend.model.Usuario;
import com.bigodeautopecas.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin
@Tag(name = "Usuários", description = "Gestão de usuários (somente ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Lista paginada de usuários (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public Page<Usuario> listar(@PageableDefault(size = 20) Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public Usuario buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar usuário (ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuário criado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public Usuario salvar(@Valid @RequestBody UsuarioRequest req) {
        Usuario u = new Usuario();
        u.setNome(req.nome());
        u.setEmail(req.email());
        u.setSenha(req.senha());
        u.setTipo(req.tipo());
        return service.salvar(u);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário (ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public Usuario atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest req) {
        Usuario usuario = service.buscarPorId(id);
        usuario.setNome(req.nome());
        usuario.setEmail(req.email());
        if (req.senha() != null && !req.senha().isBlank()) {
            usuario.setSenha(req.senha());
        }
        usuario.setTipo(req.tipo());
        return service.salvar(usuario);
    }

    @PatchMapping("/{id}/tipo")
    @Operation(summary = "Alterar tipo do usuário (ADMIN)", description = "Altera o tipo de um usuário para ADMIN ou CLIENTE")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tipo alterado"),
        @ApiResponse(responseCode = "400", description = "Tipo inválido"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public Usuario alterarTipo(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String tipo = body.get("tipo");
        if (tipo == null || !tipo.matches("ADMIN|CLIENTE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tipo inválido. Use ADMIN ou CLIENTE");
        }
        Usuario usuario = service.buscarPorId(id);
        usuario.setTipo(tipo);
        return service.salvar(usuario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletar usuário (ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário removido"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}