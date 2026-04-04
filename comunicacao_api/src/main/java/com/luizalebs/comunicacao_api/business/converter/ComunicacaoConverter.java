package com.luizalebs.comunicacao_api.business.converter;

import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.infraestructure.entities.ComunicacaoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ComunicacaoConverter {

    @Mapping(target = "statusEnvio", ignore = true)
    ComunicacaoEntity paraEntity(ComunicacaoInDTO dto);

    ComunicacaoOutDTO paraDTO(ComunicacaoEntity entity);
}
