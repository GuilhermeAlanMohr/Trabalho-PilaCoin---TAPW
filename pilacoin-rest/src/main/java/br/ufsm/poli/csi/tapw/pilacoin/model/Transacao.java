package br.ufsm.poli.csi.tapw.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class Transacao implements Serializable {

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private byte[] assinatura;
    private byte[] chaveUsuarioDestino;
    private byte[] chaveUsuarioOrigem;
    private Date dataTransacao;
    private Long idBloco;
    private String noncePila;
    private String status;

    public Transacao() {
    }

    public Transacao(Long id, byte[] assinatura, byte[] chaveUsuarioDestino, byte[] chaveUsuarioOrigem, Date dataTransacao, Long idBloco, String noncePila, String status) {
        this.id = id;
        this.assinatura = assinatura;
        this.chaveUsuarioDestino = chaveUsuarioDestino;
        this.chaveUsuarioOrigem = chaveUsuarioOrigem;
        this.dataTransacao = dataTransacao;
        this.idBloco = idBloco;
        this.noncePila = noncePila;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(byte[] assinatura) {
        this.assinatura = assinatura;
    }

    public byte[] getChaveUsuarioDestino() {
        return chaveUsuarioDestino;
    }

    public void setChaveUsuarioDestino(byte[] chaveUsuarioDestino) {
        this.chaveUsuarioDestino = chaveUsuarioDestino;
    }

    public byte[] getChaveUsuarioOrigem() {
        return chaveUsuarioOrigem;
    }

    public void setChaveUsuarioOrigem(byte[] chaveUsuarioOrigem) {
        this.chaveUsuarioOrigem = chaveUsuarioOrigem;
    }

    public Date getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(Date dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public Long getIdBloco() {
        return idBloco;
    }

    public void setIdBloco(Long idBloco) {
        this.idBloco = idBloco;
    }

    public String getNoncePila() {
        return noncePila;
    }

    public void setNoncePila(String noncePila) {
        this.noncePila = noncePila;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
