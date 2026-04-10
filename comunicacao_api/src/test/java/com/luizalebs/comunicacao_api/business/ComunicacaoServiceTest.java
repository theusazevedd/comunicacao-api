package com.luizalebs.comunicacao_api.business;

import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.business.converter.ComunicacaoConverter;
import com.luizalebs.comunicacao_api.business.service.ComunicacaoService;
import com.luizalebs.comunicacao_api.infraestructure.client.NotificacaoClient;
import com.luizalebs.comunicacao_api.infraestructure.entities.ComunicacaoEntity;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.exceptions.BadRequestException;
import com.luizalebs.comunicacao_api.infraestructure.exceptions.ResourceNotFoundException;
import com.luizalebs.comunicacao_api.infraestructure.repositories.ComunicacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComunicacaoServiceTest {

    @InjectMocks
    private ComunicacaoService comunicacaoService;

    @Mock
    private ComunicacaoRepository comunicacaoRepository;

    @Mock
    private ComunicacaoConverter comunicacaoConverter;

    @Mock
    private NotificacaoClient notificacaoClient;



    @Test
    void deveAgendarComunicacaoComSucesso() {
        ComunicacaoInDTO inDTO = new ComunicacaoInDTO();
        ComunicacaoEntity entity = new ComunicacaoEntity();
        entity.setNomeDestinatario("João");
        entity.setEmailDestinatario("mazevedo814@gmail.com");
        entity.setMensagem("Teste de comunicação");
        entity.setDataHoraEnvio(LocalDateTime.now());
        ComunicacaoOutDTO outDTO = new ComunicacaoOutDTO();

        when(comunicacaoConverter.paraEntity(inDTO)).thenReturn(entity);
        // quando converter o DTO → retorna a entity mockada

        when(comunicacaoRepository.save(any(ComunicacaoEntity.class))).thenReturn(entity);
        // quando salvar no banco → retorna a própria entity

        when(comunicacaoConverter.paraDTO(entity)).thenReturn(outDTO);
        // quando converter para DTO de saída → retorna outDTO

        doNothing().when(notificacaoClient).enviarNotificacao(any());
        // simula sucesso na chamada da API externa (não faz nada)

        ComunicacaoOutDTO response = comunicacaoService.agendarComunicacao(inDTO);
        // executa o método real do service

        assertNotNull(response);
        // garante que retornou algo

        assertEquals(StatusEnvioEnum.ENVIADO, entity.getStatusEnvio());
        // garante que o status foi atualizado para ENVIADO

        verify(comunicacaoRepository, times(2)).save(any());
        // verifica que salvou no banco 2 vezes (antes e depois da notificação)
        verify(notificacaoClient, times(1)).enviarNotificacao(any());
        // verifica que chamou a API externa 1 vez

    }

    @Test
    void deveLancarExcecaoQuandoDtoForNulo() {

        assertThrows(BadRequestException.class, () -> {
            comunicacaoService.agendarComunicacao(null);
        });

        verifyNoInteractions(comunicacaoRepository);
    }

    @Test
    void deveRetornarErroQuandoFalharEnvioNotificacao() {
        ComunicacaoInDTO inDTO = new ComunicacaoInDTO();
        ComunicacaoEntity entity = new ComunicacaoEntity();
        entity.setNomeDestinatario("João");
        entity.setEmailDestinatario("teste@email.com");
        entity.setMensagem("Teste de comunicação");
        entity.setDataHoraEnvio(LocalDateTime.now());
        ComunicacaoOutDTO outDTO = new ComunicacaoOutDTO();

        when(comunicacaoConverter.paraEntity(inDTO)).thenReturn(entity);
        // mock da conversão DTO → Entity

        when(comunicacaoRepository.save(any(ComunicacaoEntity.class))).thenReturn(entity);
        // mock do save no banco

        when(comunicacaoConverter.paraDTO(entity)).thenReturn(outDTO);
        // mock da conversão Entity → DTO

        doThrow(new RuntimeException())
                .when(notificacaoClient)
                .enviarNotificacao(any());
        // simula erro ao chamar a API externa

        ComunicacaoOutDTO response = comunicacaoService.agendarComunicacao(inDTO);
        // executa o método

        assertNotNull(response);
        // garante que retornou algo

        assertEquals(StatusEnvioEnum.ERRO, entity.getStatusEnvio());
        // verifica que caiu no catch e definiu status ERRO

        verify(comunicacaoRepository, times(2)).save(any());
        // verifica que salvou antes e depois

        verify(notificacaoClient, times(1)).enviarNotificacao(any());
        // verifica tentativa de envio

    }

    @Test
    void deveBuscarStatusComSucesso() {
        String email = "teste@mail.com";

        ComunicacaoEntity entity = new ComunicacaoEntity();
        entity.setNomeDestinatario("João");
        entity.setEmailDestinatario("teste@email.com");
        entity.setMensagem("Teste de comunicação");
        entity.setDataHoraEnvio(LocalDateTime.now());
        ComunicacaoOutDTO outDTO = new ComunicacaoOutDTO();

        when(comunicacaoRepository.findByEmailDestinatario(email)).thenReturn(entity);
        // simula retorno do banco

        when(comunicacaoConverter.paraDTO(entity)).thenReturn(outDTO);
        // simula conversão

        ComunicacaoOutDTO response = comunicacaoService.buscarStatusComunicacao(email);
        // executa o método

        assertNotNull(response);
        // garante retorno

        verify(comunicacaoRepository).findByEmailDestinatario(email);
        // verifica chamada ao repository

        verify(comunicacaoConverter).paraDTO(entity);
        // verifica chamada ao converter

    }

    @Test
    void deveLancarExcecaoQuandoNaoEncontrarComunicacao() {
        String email = "teste@email.com";

        when(comunicacaoRepository.findByEmailDestinatario(email)).thenReturn(null);
        // simula que não encontrou no banco

        assertThrows(ResourceNotFoundException.class, () -> {
            comunicacaoService.buscarStatusComunicacao(email);
        });
        // espera exceção
    }

    @Test
    void deveAlterarStatusParaCancelado() {

        String email = "teste@email.com";

        ComunicacaoEntity entity = new ComunicacaoEntity();
        entity.setNomeDestinatario("João");
        entity.setEmailDestinatario("teste@email.com");
        entity.setMensagem("Teste de comunicação");
        entity.setDataHoraEnvio(LocalDateTime.now());
        ComunicacaoOutDTO outDTO = new ComunicacaoOutDTO();

        when(comunicacaoRepository.findByEmailDestinatario(email)).thenReturn(entity);
        when(comunicacaoRepository.save(entity)).thenReturn(entity);
        when(comunicacaoConverter.paraDTO(entity)).thenReturn(outDTO);

        ComunicacaoOutDTO response = comunicacaoService.alterarStatusComunicacao(email);
        assertNotNull(response);
        assertEquals(StatusEnvioEnum.CANCELADO, entity.getStatusEnvio());
        verify(comunicacaoRepository).save(entity);

    }


}
