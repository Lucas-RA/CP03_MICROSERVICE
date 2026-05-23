package br.com.fiap.cp01_api01.dto;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import br.com.fiap.cp01_api01.model.Futebol;

@Component
public class FutebolMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public Futebol toModel(FutebolCreateRequest dto){
        return modelMapper.map(dto, Futebol.class);
    }

    public FutebolResponse toDTO(Futebol entity){
        return modelMapper.map(entity, FutebolResponse.class);
    }

    public Futebol toModel(Long id, FutebolUpdateRequest dto){
        Futebol futebol = modelMapper.map(dto, Futebol.class);
        futebol.setId(id);
        return futebol;
    }
}
