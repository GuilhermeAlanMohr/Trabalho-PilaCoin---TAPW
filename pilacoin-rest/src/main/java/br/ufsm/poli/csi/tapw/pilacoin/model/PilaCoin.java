package br.ufsm.poli.csi.tapw.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Date;

@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class PilaCoin implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //private String idCriador;

    private Date dataCriacao;
    @Column(columnDefinition = "TEXT")
    private byte[] chaveCriador;
    @Column(columnDefinition = "TEXT")
    private byte[] assinaturaMaster;
    private String nonce; //utilizar precisão de 128 bits
    private String status;

    public PilaCoin() {

    }

    public PilaCoin(Long id, Date dataCriacao, byte[] chaveCriador, byte[] assinaturaMaster, String nonce, String status) {
        this.id = id;
        this.dataCriacao = dataCriacao;
        this.chaveCriador = chaveCriador;
        this.assinaturaMaster = assinaturaMaster;
        this.nonce = nonce;
        this.status = status;
    }

    public PilaCoin(Date dataCriacao, byte[] chaveCriador, byte[] assinaturaMaster, String nonce, String status) {
        this.dataCriacao = dataCriacao;
        this.chaveCriador = chaveCriador;
        this.assinaturaMaster = assinaturaMaster;
        this.nonce = nonce;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public byte[] getChaveCriador() {
        return chaveCriador;
    }

    public void setChaveCriador(byte[] chaveCriador) {
        this.chaveCriador = chaveCriador;
    }

    public byte[] getAssinaturaMaster() {
        return assinaturaMaster;
    }

    public void setAssinaturaMaster(byte[] assinaturaMaster) {
        this.assinaturaMaster = assinaturaMaster;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
