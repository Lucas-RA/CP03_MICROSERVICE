package br.com.fiap.cp01_api01.dto;

import lombok.Data;

@Data
public class FinancaResponse {
    private Long id;
    private String emissor;
    private double taxa;
    private String risco;
    private String vencimento;
    private int quantidade;
}
