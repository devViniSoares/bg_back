package com.bigodeautopecas.backend.controller;

import com.bigodeautopecas.backend.model.Usuario;
import com.bigodeautopecas.backend.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Usuario> listar(@PageableDefault(size = 20) Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Usuario salvar(@RequestBody Usuario usuario) {
        return service.salvar(usuario);
    }

    @PutMapping("/{id}")
    public Usuario atualizar(@PathVariable Long id, @RequestBody Usuario novoUsuario) {
        Usuario usuario = service.buscarPorId(id);

        usuario.setNome(novoUsuario.getNome());
        usuario.setEmail(novoUsuario.getEmail());
        if (novoUsuario.getSenha() != null && !novoUsuario.getSenha().isBlank()) {
            usuario.setSenha(novoUsuario.getSenha());
        }
        usuario.setTipo(novoUsuario.getTipo());

        return service.salvar(usuario);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}