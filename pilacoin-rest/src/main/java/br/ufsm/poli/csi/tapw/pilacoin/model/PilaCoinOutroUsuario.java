package br.ufsm.poli.csi.tapw.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PilaCoinOutroUsuario implements Serializable {

    private String nonce; //utilizar precisão de 128 bits
    private String tipo;
    private byte[] chavePublica;
    private byte[] hashPilaBloco;
    private byte[] assinatura;

    public PilaCoinOutroUsuario(){}

    public PilaCoinOutroUsuario(String nonce, String tipo, byte[] chavePublica, byte[] hashPilaBloco, byte[] assinatura) {
        this.nonce = nonce;
        this.tipo = tipo;
        this.chavePublica = chavePublica;
        this.hashPilaBloco = hashPilaBloco;
        this.assinatura = assinatura;
    }

    public byte[] getAssinaturaMaster() {
        return assinatura;
    }

    public void setAssinaturaMaster(byte[] assinaturaMaster) {
        this.assinatura = assinaturaMaster;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public byte[] getChavePublica() {
        return chavePublica;
    }

    public void setChavePublica(byte[] chavePublica) {
        this.chavePublica = chavePublica;
    }

    public byte[] getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(byte[] assinatura) {
        this.assinatura = assinatura;
    }

    public byte[] getHashPilaBloco() {
        return hashPilaBloco;
    }

    public void setHashPilaBloco(byte[] hashPilaBloco) {
        this.hashPilaBloco = hashPilaBloco;
    }
}
