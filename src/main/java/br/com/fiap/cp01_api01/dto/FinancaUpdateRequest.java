package br.com.fiap.cp01_api01.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class FinancaUpdateRequest {
    
    private double taxa;
    
    private String emissor;
  
    private String risco;

    private String vencimento;

    private int quantidade;
}
