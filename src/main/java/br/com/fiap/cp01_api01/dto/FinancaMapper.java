package br.com.fiap.cp01_api01.dto;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import br.com.fiap.cp01_api01.model.Financa;

@Component
public class FinancaMapper {
    private final ModelMapper modelMapper = new ModelMapper();
    
    public Financa toModel(FinancaCreateRequest dto){
        return modelMapper.map(dto, Financa.class);
    }

public FinancaResponse toDTO(Financa entity){
    return modelMapper.map(entity, FinancaResponse.class);
}

public Financa toModel(Long id, FinancaUpdateRequest dto){
    Financa financa = modelMapper.map(dto, Financa.class);
    financa.setId(id);
    return financa;
}
}
