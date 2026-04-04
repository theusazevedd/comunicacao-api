package com.luizalebs.comunicacao_api.infraestructure.client.config;

import com.luizalebs.comunicacao_api.infraestructure.exceptions.BadRequestException;
import com.luizalebs.comunicacao_api.infraestructure.exceptions.BusinessException;
import com.luizalebs.comunicacao_api.infraestructure.exceptions.ConflictException;
import com.luizalebs.comunicacao_api.infraestructure.exceptions.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignError implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {

        switch (response.status()) {
            case 400:
                return new BadRequestException("Dados de entrada inválidos");
            case 404:
                return new ResourceNotFoundException("Erro atributo não encontrado");
            case 409:
                return new ConflictException("Erro de atributo");
            default:
                return new BusinessException("Erro de servidor");


        }
    }
}