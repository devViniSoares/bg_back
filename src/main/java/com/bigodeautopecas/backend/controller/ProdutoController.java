package com.bigodeautopecas.backend.controller;

import com.bigodeautopecas.backend.dto.ProdutoRequest;
import com.bigodeautopecas.backend.model.Produto;
import com.bigodeautopecas.backend.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@CrossOrigin
@Tag(name = "Produtos", description = "Catálogo de produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar produtos", description = "Catálogo paginado com filtros combinados opcionais")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public Page<Produto> listar(
            @Parameter(description = "Filtrar por categoria")       @RequestParam(required = false) String categoria,
            @Parameter(description = "Filtrar por marca")           @RequestParam(required = false) String marca,
            @Parameter(description = "Filtrar por modelo de veículo") @RequestParam(required = false) String modelo,
            @Parameter(description = "Buscar por nome (parcial)")   @RequestParam(required = false) String nome,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return service.listarComFiltros(categoria, marca, modelo, nome, pageable);
    }

    @GetMapping("/categorias")
    @Operation(summary = "Listar categorias", description = "Retorna todas as categorias distintas cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de categorias")
    public List<String> listarCategorias() {
        return service.listarCategorias();
    }

    @GetMapping("/marcas")
    @Operation(summary = "Listar marcas", description = "Retorna todas as marcas distintas cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de marcas")
    public List<String> listarMarcas() {
        return service.listarMarcas();
    }

    @GetMapping("/modelos")
    @Operation(summary = "Listar modelos de veículo", description = "Retorna todos os modelos distintos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de modelos")
    public List<String> listarModelos() {
        return service.listarModelos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public Produto buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar produto (ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Produto criado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public Produto salvar(@Valid @RequestBody ProdutoRequest req) {
        Produto produto = fromRequest(req, new Produto());
        return service.salvar(produto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar produto (ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto atualizado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public Produto atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequest req) {
        Produto produto = service.buscarPorId(id);
        fromRequest(req, produto);
        return service.salvar(produto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletar produto (ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Produto removido"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    private Produto fromRequest(ProdutoRequest req, Produto produto) {
        produto.setNome(req.nome());
        produto.setDescricao(req.descricao());
        produto.setPreco(req.preco());
        produto.setEstoque(req.estoque());
        produto.setCategoria(req.categoria());
        produto.setMarca(req.marca());
        produto.setModelo(req.modelo());
        produto.setImagemUrl(req.imagemUrl());
        return produto;
    }
}