package br.ufsm.poli.csi.tapw.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@Builder
@ToString
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Transacao {

    /*
      "assinatura": "string",
      "chaveUsuarioDestino": "string",
      "chaveUsuarioOrigem": "string",
      "dataTransacao": "2023-02-01T16:38:37.893Z",
      "id": 0,
      "idBloco": 0,
      "noncePila": "string",
      "status": "string"
    */

    @Id
    @GeneratedValue
    private Long id;
    private byte[] assinatura;
    private byte[] chaveUsuarioDestino;
    private byte[] chaveUsuarioOrigem;
    private Date dataTransacao;
    private Long idBloco;
    private String noncePila;
    private String status;

}
