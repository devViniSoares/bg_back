package com.bigodeautopecas.backend.controller;

import com.bigodeautopecas.backend.model.Carrinho;
import com.bigodeautopecas.backend.service.CarrinhoService;
import com.bigodeautopecas.backend.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/carrinho")
@CrossOrigin
public class CarrinhoController {

    private final CarrinhoService service;
    private final UsuarioService usuarioService;

    public CarrinhoController(CarrinhoService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Carrinho> listar(Authentication auth) {
        if (isAdmin(auth)) return service.listar();
        return service.listarPorEmail(auth.getName());
    }

    @GetMapping("/{id}")
    public Carrinho buscarPorId(@PathVariable Long id, Authentication auth) {
        Carrinho carrinho = service.buscarPorId(id);
        if (!isAdmin(auth)) verificarPropriedade(carrinho.getUsuario().getEmail(), auth.getName());
        return carrinho;
    }

    @PostMapping
    public Carrinho salvar(@RequestBody Carrinho carrinho, Authentication auth) {
        if (!isAdmin(auth)) {
            carrinho.setUsuario(usuarioService.buscarPorEmail(auth.getName()));
        }
        return service.salvar(carrinho);
    }

    @PutMapping("/{id}")
    public Carrinho atualizar(@PathVariable Long id, @RequestBody Carrinho novoCarrinho, Authentication auth) {
        Carrinho carrinho = service.buscarPorId(id);
        if (!isAdmin(auth)) verificarPropriedade(carrinho.getUsuario().getEmail(), auth.getName());

        carrinho.setItens(novoCarrinho.getItens());
        if (isAdmin(auth)) carrinho.setUsuario(novoCarrinho.getUsuario());

        return service.salvar(carrinho);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id, Authentication auth) {
        if (!isAdmin(auth)) {
            Carrinho carrinho = service.buscarPorId(id);
            verificarPropriedade(carrinho.getUsuario().getEmail(), auth.getName());
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
