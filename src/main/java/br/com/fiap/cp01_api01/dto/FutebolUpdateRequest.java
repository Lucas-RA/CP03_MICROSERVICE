package br.com.fiap.cp01_api01.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class FutebolUpdateRequest {
    
    private int ano;
    
    private String capeao;

    private String sede;

    private String vice;

    private String melhorJogador;
}
