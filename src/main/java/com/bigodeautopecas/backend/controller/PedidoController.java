package com.bigodeautopecas.backend.controller;

import com.bigodeautopecas.backend.model.Pedido;
import com.bigodeautopecas.backend.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin
public class PedidoController {

    @Autowired
    private PedidoService service;

    @GetMapping
    public List<Pedido> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Pedido buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Pedido salvar(@RequestBody Pedido pedido) {
        return service.salvar(pedido);
    }

    @PutMapping("/{id}")
    public Pedido atualizar(@PathVariable Long id, @RequestBody Pedido novoPedido) {

        Pedido pedido = service.buscarPorId(id);

        pedido.setUsuario(novoPedido.getUsuario());
        pedido.setTotal(novoPedido.getTotal());
        pedido.setStatus(novoPedido.getStatus());
        pedido.setItens(novoPedido.getItens());

        return service.salvar(pedido);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
