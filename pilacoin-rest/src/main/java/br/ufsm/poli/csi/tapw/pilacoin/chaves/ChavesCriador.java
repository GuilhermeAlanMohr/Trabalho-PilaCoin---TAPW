package br.ufsm.poli.csi.tapw.pilacoin.chaves;

public class ChavesCriador {

    private byte[] chavePublica;
    private byte[] chavePrivada;

    public ChavesCriador() {
    }

    public ChavesCriador(byte[] chavePublica, byte[] chavePrivada) {
        this.chavePublica = chavePublica;
        this.chavePrivada = chavePrivada;
    }

    public byte[] getChavePublica() {
        return chavePublica;
    }

    public void setChavePublica(byte[] chavePublica) {
        this.chavePublica = chavePublica;
    }

    public byte[] getChavePrivada() {
        return chavePrivada;
    }

    public void setChavePrivada(byte[] chavePrivada) {
        this.chavePrivada = chavePrivada;
    }

}
