package br.ufsm.poli.csi.tapw.pilacoin.model;

import org.springframework.stereotype.Service;

@Service
public class TratarThread {

    private Boolean parar = false;
    private Thread mineracao;

    public void iniciarMin() {
        parar = false;
        if (mineracao == null) {
            mineracao = new Thread(new ThreadMineracao());
            mineracao.start();
        } else {
            TratarThread.this.notify();
        }
    }

    public void paraMin() {
        synchronized (TratarThread.this) {
            parar = true;
        }
    }

    class ThreadMineracao implements Runnable{

        @Override
        public void run() {
            //FAZ MINERAÇÃO
            synchronized (TratarThread.this) {
                if (parar) {
                    try {
                        TratarThread.this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
