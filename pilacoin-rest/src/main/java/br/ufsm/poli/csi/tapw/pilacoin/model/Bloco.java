package br.ufsm.poli.csi.tapw.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bloco implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long numeroBloco;

    private byte[] chaveUsuarioMinerador;
    private String nonce;
    private String nonceBlocoAnterior;
    /*@SuppressWarnings("JpaAttributeTypeInspection")
    @OneToMany(mappedBy = "transacao")
    private List<Transacao> transacoes;
    */
    public Bloco() {

    }

    public Bloco(Long numeroBloco) {
        this.numeroBloco = numeroBloco;
    }

    public Bloco(Long numeroBloco, byte[] chaveUsuarioMinerador, String nonce, String nonceBlocoAnterior) {
        this.numeroBloco = numeroBloco;
        this.chaveUsuarioMinerador = chaveUsuarioMinerador;
        this.nonce = nonce;
        this.nonceBlocoAnterior = nonceBlocoAnterior;
    }

    public Long getNumeroBloco() {
        return numeroBloco;
    }

    public void setNumeroBloco(Long numeroBloco) {
        this.numeroBloco = numeroBloco;
    }

    public byte[] getChaveUsuarioMinerador() {
        return chaveUsuarioMinerador;
    }

    public void setChaveUsuarioMinerador(byte[] chaveUsuarioMinerador) {
        this.chaveUsuarioMinerador = chaveUsuarioMinerador;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getNonceBlocoAnterior() {
        return nonceBlocoAnterior;
    }

    public void setNonceBlocoAnterior(String nonceBlocoAnterior) {
        this.nonceBlocoAnterior = nonceBlocoAnterior;
    }
    /*
    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes;
    }
    */
}
