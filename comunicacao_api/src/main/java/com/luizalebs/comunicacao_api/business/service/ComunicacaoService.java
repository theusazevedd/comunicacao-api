package com.luizalebs.comunicacao_api.business.service;

import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.api.dto.NotificacaoRequestDTO;
import com.luizalebs.comunicacao_api.business.converter.ComunicacaoConverter;
import com.luizalebs.comunicacao_api.infraestructure.client.NotificacaoClient;
import com.luizalebs.comunicacao_api.infraestructure.entities.ComunicacaoEntity;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.repositories.ComunicacaoRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Objects;

@Service
public class ComunicacaoService {

    private final ComunicacaoRepository repository;
    private final ComunicacaoConverter converter;
    private final NotificacaoClient notificacaoClient;

    public ComunicacaoService(ComunicacaoRepository repository, ComunicacaoConverter converter, NotificacaoClient notificacaoClient) {
        this.repository = repository;
        this.converter = converter;
        this.notificacaoClient = notificacaoClient;
    }

    public ComunicacaoOutDTO agendarComunicacao(ComunicacaoInDTO dto) {
        if (Objects.isNull(dto)) {
            throw new IllegalArgumentException("DTO não pode ser nulo");
        }
        dto.setStatusEnvio(StatusEnvioEnum.PENDENTE);
        ComunicacaoEntity entity = converter.paraEntity(dto);
        repository.save(entity);
        try {
            NotificacaoRequestDTO notificacaoDTO = NotificacaoRequestDTO.builder()
                    .nomeTarefa("Mensagem para " + entity.getNomeDestinatario())
                    .descricao(entity.getMensagem())
                    .emailUsuario(entity.getEmailDestinatario())
                    .dataEvento(entity.getDataHoraEnvio()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime())
                    .build();
            notificacaoClient.enviarNotificacao(notificacaoDTO);
            entity.setStatusEnvio(StatusEnvioEnum.ENVIADO);
        } catch (Exception e) {
            System.out.println("Erro ao enviar notificação: " + e.getMessage());
            entity.setStatusEnvio(StatusEnvioEnum.ERRO);
        }
        repository.save(entity);
        return converter.paraDTO(entity);
    }

    public ComunicacaoOutDTO buscarStatusComunicacao(String emailDestinatario) {
        ComunicacaoEntity entity = repository.findByEmailDestinatario(emailDestinatario);
        if (Objects.isNull(entity)) {
            throw new RuntimeException();
        }
        return converter.paraDTO(entity);
    }

    public ComunicacaoOutDTO alterarStatusComunicacao(String emailDestinatario) {
        ComunicacaoEntity entity = repository.findByEmailDestinatario(emailDestinatario);
        if (Objects.isNull(entity)) {
            throw new RuntimeException();
        }
        entity.setStatusEnvio(StatusEnvioEnum.CANCELADO);
        repository.save(entity);
        return (converter.paraDTO(entity));
    }

}
