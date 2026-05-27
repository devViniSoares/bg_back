package com.bigodeautopecas.backend.controller;

import com.bigodeautopecas.backend.dto.AdicionarItemCarrinhoRequest;
import com.bigodeautopecas.backend.dto.CarrinhoDTO;
import com.bigodeautopecas.backend.model.Carrinho;
import com.bigodeautopecas.backend.service.CarrinhoService;
import com.bigodeautopecas.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/carrinho")
@CrossOrigin
@Tag(name = "Carrinho", description = "Gerenciamento do carrinho de compras")
@SecurityRequirement(name = "bearerAuth")
public class CarrinhoController {

    private final CarrinhoService service;
    private final UsuarioService usuarioService;

    public CarrinhoController(CarrinhoService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar carrinhos", description = "Admin vê todos; cliente vê apenas o seu")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public List<CarrinhoDTO> listar(Authentication auth) {
        if (isAdmin(auth)) return service.listar();
        return service.listarPorEmail(auth.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar carrinho por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrinho encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Carrinho não encontrado")
    })
    public CarrinhoDTO buscarPorId(@PathVariable Long id, Authentication auth) {
        CarrinhoDTO carrinho = service.buscarPorIdComoDTO(id);
        if (!isAdmin(auth)) verificarPropriedade(carrinho.usuarioEmail(), auth.getName());
        return carrinho;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar carrinho")
    @ApiResponse(responseCode = "201", description = "Carrinho criado")
    public CarrinhoDTO salvar(@RequestBody Carrinho carrinho, Authentication auth) {
        if (!isAdmin(auth)) {
            carrinho.setUsuario(usuarioService.buscarPorEmail(auth.getName()));
        }
        return service.salvarComoDTO(carrinho);
    }

    @PostMapping("/item")
    @Operation(summary = "Adicionar item", description = "Adiciona ou incrementa um item no carrinho do usuário autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item adicionado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public CarrinhoDTO adicionarItem(@Valid @RequestBody AdicionarItemCarrinhoRequest req, Authentication auth) {
        return service.adicionarItem(auth.getName(), req.produtoId(), req.quantidade());
    }

    @DeleteMapping("/item/{itemId}")
    @Operation(summary = "Remover item", description = "Remove um item do carrinho do usuário autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item removido"),
        @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public CarrinhoDTO removerItem(@PathVariable Long itemId, Authentication auth) {
        return service.removerItem(auth.getName(), itemId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar carrinho")
    @ApiResponse(responseCode = "200", description = "Carrinho atualizado")
    public CarrinhoDTO atualizar(@PathVariable Long id, @RequestBody Carrinho novoCarrinho, Authentication auth) {
        CarrinhoDTO existente = service.buscarPorIdComoDTO(id);
        if (!isAdmin(auth)) verificarPropriedade(existente.usuarioEmail(), auth.getName());

        var novoUsuario = isAdmin(auth) ? novoCarrinho.getUsuario() : null;
        return service.atualizarItens(id, novoCarrinho.getItens(), novoUsuario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletar carrinho")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Carrinho deletado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public void deletar(@PathVariable Long id, Authentication auth) {
        if (!isAdmin(auth)) {
            CarrinhoDTO carrinho = service.buscarPorIdComoDTO(id);
            verificarPropriedade(carrinho.usuarioEmail(), auth.getName());
        }
        service.deletar(id);
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private void verificarPropriedade(String emailDono, String emailAutenticado) {
        if (!emailDono.equals(emailAutenticado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
    }
}
