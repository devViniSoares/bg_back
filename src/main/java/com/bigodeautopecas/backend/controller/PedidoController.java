package com.bigodeautopecas.backend.controller;

import com.bigodeautopecas.backend.model.Pedido;
import com.bigodeautopecas.backend.service.PedidoService;
import com.bigodeautopecas.backend.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin
public class PedidoController {

    private final PedidoService service;
    private final UsuarioService usuarioService;

    public PedidoController(PedidoService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public Page<Pedido> listar(Authentication auth,
            @PageableDefault(size = 20) Pageable pageable) {
        if (isAdmin(auth)) return service.listar(pageable);
        return service.listarPorEmail(auth.getName(), pageable);
    }

    @GetMapping("/{id}")
    public Pedido buscarPorId(@PathVariable Long id, Authentication auth) {
        Pedido pedido = service.buscarPorId(id);
        if (!isAdmin(auth)) verificarPropriedade(pedido.getUsuario().getEmail(), auth.getName());
        return pedido;
    }

    @PostMapping
    public Pedido salvar(@RequestBody Pedido pedido, Authentication auth) {
        if (!isAdmin(auth)) {
            pedido.setUsuario(usuarioService.buscarPorEmail(auth.getName()));
            pedido.setStatus("AGUARDANDO");
        }
        return service.salvar(pedido);
    }

    @PutMapping("/{id}")
    public Pedido atualizar(@PathVariable Long id, @RequestBody Pedido novoPedido, Authentication auth) {
        Pedido pedido = service.buscarPorId(id);

        if (isAdmin(auth)) {
            pedido.setUsuario(novoPedido.getUsuario());
            pedido.setTotal(novoPedido.getTotal());
            pedido.setStatus(novoPedido.getStatus());
            pedido.setItens(novoPedido.getItens());
        } else {
            verificarPropriedade(pedido.getUsuario().getEmail(), auth.getName());
            pedido.setStatus("CANCELADO");
        }

        return service.salvar(pedido);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id, Authentication auth) {
        if (!isAdmin(auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
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