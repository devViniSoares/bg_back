package com.bigodeautopecas.backend.controller;

import com.bigodeautopecas.backend.dto.AtualizarPedidoRequest;
import com.bigodeautopecas.backend.dto.ItemPedidoRequest;
import com.bigodeautopecas.backend.dto.PagamentoRequest;
import com.bigodeautopecas.backend.dto.PagamentoResponse;
import com.bigodeautopecas.backend.dto.PedidoDTO;
import com.bigodeautopecas.backend.dto.PedidoRequest;
import com.bigodeautopecas.backend.model.ItemPedido;
import com.bigodeautopecas.backend.model.Pedido;
import com.bigodeautopecas.backend.model.Produto;
import com.bigodeautopecas.backend.service.EmailService;
import com.bigodeautopecas.backend.service.PagamentoService;
import com.bigodeautopecas.backend.service.PedidoService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin
@Tag(name = "Pedidos", description = "Criação e gerenciamento de pedidos")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService service;
    private final UsuarioService usuarioService;
    private final PagamentoService pagamentoService;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private EmailService emailService;

    public PedidoController(PedidoService service, UsuarioService usuarioService, PagamentoService pagamentoService) {
        this.service = service;
        this.usuarioService = usuarioService;
        this.pagamentoService = pagamentoService;
    }

    @GetMapping
    @Operation(summary = "Listar pedidos", description = "Admin vê todos; cliente vê apenas os seus")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public Page<PedidoDTO> listar(Authentication auth,
            @PageableDefault(size = 20) Pageable pageable) {
        if (isAdmin(auth)) return service.listar(pageable);
        return service.listarPorEmail(auth.getName(), pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public PedidoDTO buscarPorId(@PathVariable Long id, Authentication auth) {
        PedidoDTO pedido = service.buscarPorIdComoDTO(id);
        if (!isAdmin(auth)) verificarPropriedade(pedido.usuario().email(), auth.getName());
        return pedido;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar pedido", description = "Cria um novo pedido para o usuário autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido criado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Estoque insuficiente")
    })
    public PedidoDTO salvar(@Valid @RequestBody PedidoRequest req, Authentication auth) {
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuarioService.buscarPorEmail(auth.getName()));
        pedido.setStatus("AGUARDANDO");
        pedido.setEnderecoEntrega(req.enderecoEntrega());

        List<ItemPedido> itens = req.itens().stream().map(i -> {
            ItemPedido item = new ItemPedido();
            Produto p = new Produto();
            p.setId(i.produtoId());
            item.setProduto(p);
            item.setQuantidade(i.quantidade());
            return item;
        }).toList();

        pedido.setItens(itens);
        return service.salvarComoDTO(pedido);
    }

    @PostMapping("/{id}/pagamento")
    @Operation(summary = "Processar pagamento", description = "Processa o pagamento de um pedido e o confirma se aprovado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagamento processado"),
        @ApiResponse(responseCode = "400", description = "Pedido não está aguardando pagamento"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public PagamentoResponse processarPagamento(@PathVariable Long id,
            @Valid @RequestBody PagamentoRequest req,
            Authentication auth) {
        PedidoDTO pedidoDTO = service.buscarPorIdComoDTO(id);
        if (!isAdmin(auth)) verificarPropriedade(pedidoDTO.usuario().email(), auth.getName());

        if (!"AGUARDANDO".equals(pedidoDTO.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Pedido não está aguardando pagamento. Status atual: " + pedidoDTO.status());
        }

        PagamentoRequest pagReq = new PagamentoRequest(id, pedidoDTO.total(), req.metodoPagamento());
        PagamentoResponse resp = pagamentoService.processar(pagReq);

        if ("APROVADO".equals(resp.status())) {
            service.atualizarStatus(id, "CONFIRMADO");
            if (emailService != null) {
                emailService.enviarConfirmacaoPagamento(
                        pedidoDTO.usuario().email(), id, resp.codigoTransacao());
            }
        }

        return resp;
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pedido", description = "Admin pode alterar status; cliente só pode cancelar")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido atualizado"),
        @ApiResponse(responseCode = "400", description = "Status inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public PedidoDTO atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarPedidoRequest req,
            Authentication auth) {
        PedidoDTO pedidoDTO = service.buscarPorIdComoDTO(id);

        if (isAdmin(auth)) {
            return service.atualizarStatus(id, req.status());
        }

        verificarPropriedade(pedidoDTO.usuario().email(), auth.getName());
        if (!"CANCELADO".equals(req.status())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cliente só pode cancelar o pedido");
        }
        return service.atualizarStatus(id, "CANCELADO");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletar pedido (ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pedido removido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
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
