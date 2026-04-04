package com.luizalebs.comunicacao_api.api.controller;

import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.business.service.ComunicacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comunicacao")
@Tag(name = "Comunicacao", description = "Controller para gerenciamento de comunicações")
public class    ComunicacaoController {

    private final ComunicacaoService service;

    public ComunicacaoController(ComunicacaoService service) {
        this.service = service;
    }

    @PostMapping("/agendar")
    @Operation(summary = "Agendar uma nova comunicação", description = "Permite agendar uma nova comunicação para um destinatário específico.")
    @ApiResponse(responseCode = "200", description = "Comunicação agendada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "Conflito ao agendar comunicação")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<ComunicacaoOutDTO> agendar(@RequestBody ComunicacaoInDTO dto)  {
        return ResponseEntity.ok(service.agendarComunicacao(dto));
    }

    @GetMapping()
    @Operation(summary = "Buscar status de comunicação", description = "Permite buscar o status de uma comunicação agendada para um destinatário específico.")
    @ApiResponse(responseCode = "200", description = "Status da comunicação retornado com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetro de consulta inválido")
    @ApiResponse(responseCode = "404", description = "Comunicação não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<ComunicacaoOutDTO> buscarStatus(@RequestParam String emailDestinatario) {
        return ResponseEntity.ok(service.buscarStatusComunicacao(emailDestinatario));
    }

    @PatchMapping("/cancelar")
    @Operation(summary = "Cancelar status de comunicação", description = "Permite cancelar uma comunicação agendada para um destinatário específico.")
    @ApiResponse(responseCode = "200", description = "Status da comunicação cancelado com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetro de consulta inválido")
    @ApiResponse(responseCode = "404", description = "Comunicação não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<ComunicacaoOutDTO> cancelarStatus(@RequestParam String emailDestinatario) {
        return ResponseEntity.ok(service.alterarStatusComunicacao(emailDestinatario));
    }
}
