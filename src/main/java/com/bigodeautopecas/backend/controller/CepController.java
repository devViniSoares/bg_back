package com.bigodeautopecas.backend.controller;

import com.bigodeautopecas.backend.dto.EnderecoViaCEPResponse;
import com.bigodeautopecas.backend.service.CepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cep")
@CrossOrigin
@Tag(name = "CEP", description = "Consulta de endereço pelo CEP (ViaCEP)")
public class CepController {

    private final CepService service;

    public CepController(CepService service) {
        this.service = service;
    }

    @GetMapping("/{cep}")
    @Operation(
        summary = "Buscar endereço pelo CEP",
        description = "Consulta a API ViaCEP e retorna os dados de endereço. Aceita o CEP com ou sem hífen (ex.: 01310-100 ou 01310100)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
        @ApiResponse(responseCode = "400", description = "CEP com formato inválido"),
        @ApiResponse(responseCode = "404", description = "CEP não encontrado"),
        @ApiResponse(responseCode = "503", description = "Serviço de CEP indisponível")
    })
    public EnderecoViaCEPResponse buscar(
            @Parameter(description = "CEP com ou sem hífen (ex.: 01310-100)", example = "01310100")
            @PathVariable String cep) {
        return service.buscar(cep);
    }
}