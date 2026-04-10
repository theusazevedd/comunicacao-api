package com.luizalebs.comunicacao_api.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.business.service.ComunicacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ComunicacaoController.class)
public class ComunicacaoControllerTest {


    @MockBean
    ComunicacaoService comunicacaoService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void deveAgendarComunicaoComSucesso() throws Exception {
        ComunicacaoInDTO dto = new ComunicacaoInDTO();
        dto.setNomeDestinatario("João");
        dto.setEmailDestinatario("teste@email.com");
        dto.setMensagem("Teste");
        dto.setDataHoraEnvio(LocalDateTime.now());
        ComunicacaoOutDTO outDTO = new ComunicacaoOutDTO();

        when(comunicacaoService.agendarComunicacao(dto)).thenReturn(outDTO);
        // quando o controller chamar o service → retorna outDTO

        mockMvc.perform(post("/comunicacao/agendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                // faz POST com JSON

                .andExpect(status().isOk());
        // espera HTTP 200
    }

    @Test
    void deveBuscarStatusComSucesso() throws Exception {

        String email = "teste@email.com";
        ComunicacaoOutDTO outDTO = new ComunicacaoOutDTO();

        when(comunicacaoService.buscarStatusComunicacao(email)).thenReturn(outDTO);

        mockMvc.perform(get("/comunicacao")
                        .param("emailDestinatario", email))
                // passa query param

                .andExpect(status().isOk());
    }


    @Test
    void deveCancelarComunicacaoComSucesso() throws Exception {

        String email = "teste@email.com";
        ComunicacaoOutDTO outDTO = new ComunicacaoOutDTO();

        when(comunicacaoService.alterarStatusComunicacao(email)).thenReturn(outDTO);

        mockMvc.perform(patch("/comunicacao/cancelar")
                        .param("emailDestinatario", email))
                .andExpect(status().isOk());
    }
}




