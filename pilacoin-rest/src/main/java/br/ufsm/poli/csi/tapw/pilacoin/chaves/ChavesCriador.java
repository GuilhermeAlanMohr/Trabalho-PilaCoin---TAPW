package br.ufsm.poli.csi.tapw.pilacoin.chaves;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChavesCriador {

    private byte[] chavePublica;
    private byte[] chavePrivada;

}
