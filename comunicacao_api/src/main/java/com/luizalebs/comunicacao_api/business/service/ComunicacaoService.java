package com.luizalebs.comunicacao_api.business.service;

import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.api.dto.NotificacaoRequestDTO;
import com.luizalebs.comunicacao_api.business.converter.ComunicacaoConverter;
import com.luizalebs.comunicacao_api.infraestructure.client.NotificacaoClient;
import com.luizalebs.comunicacao_api.infraestructure.entities.ComunicacaoEntity;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.exceptions.BadRequestException;
import com.luizalebs.comunicacao_api.infraestructure.exceptions.ResourceNotFoundException;
import com.luizalebs.comunicacao_api.infraestructure.repositories.ComunicacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComunicacaoService {

    private final ComunicacaoRepository repository;
    private final ComunicacaoConverter converter;
    private final NotificacaoClient notificacaoClient;


    public ComunicacaoOutDTO agendarComunicacao(ComunicacaoInDTO dto) {
        if (Objects.isNull(dto)) {
            throw new BadRequestException("Erro ao gravar dados");
        }
        ComunicacaoEntity entity = converter.paraEntity(dto);
        entity.setStatusEnvio(StatusEnvioEnum.PENDENTE);
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
            log.error("Erro ao enviar notificação", e);
            entity.setStatusEnvio(StatusEnvioEnum.ERRO);
        }
        repository.save(entity);
        return converter.paraDTO(entity);
    }

    public ComunicacaoOutDTO buscarStatusComunicacao(String emailDestinatario) {
        ComunicacaoEntity entity = repository.findByEmailDestinatario(emailDestinatario);
        if (Objects.isNull(entity)) {
            throw new ResourceNotFoundException("Comunicação não encontrada");
        }
        return converter.paraDTO(entity);
    }

    public ComunicacaoOutDTO alterarStatusComunicacao(String emailDestinatario) {
        ComunicacaoEntity entity = repository.findByEmailDestinatario(emailDestinatario);
        if (Objects.isNull(entity)) {
            throw new ResourceNotFoundException("Comunicação não encontrada");
        }
        entity.setStatusEnvio(StatusEnvioEnum.CANCELADO);
        repository.save(entity);
        return (converter.paraDTO(entity));
    }

}
