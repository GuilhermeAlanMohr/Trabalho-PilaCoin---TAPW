package br.ufsm.poli.csi.tapw.pilacoin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Minerador {

    private String id;
    private String chavePublica;
    private String nome;

}
