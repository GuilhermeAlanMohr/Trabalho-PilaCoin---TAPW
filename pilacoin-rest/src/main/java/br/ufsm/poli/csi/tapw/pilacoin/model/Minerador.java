package br.ufsm.poli.csi.tapw.pilacoin.model;


public class Minerador {

    private String id;
    private String chavePublica;
    private String nome;

    public Minerador(String id, String chavePublica, String nome) {
        this.id = id;
        this.chavePublica = chavePublica;
        this.nome = nome;
    }

    public Minerador() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChavePublica() {
        return chavePublica;
    }

    public void setChavePublica(String chavePublica) {
        this.chavePublica = chavePublica;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
