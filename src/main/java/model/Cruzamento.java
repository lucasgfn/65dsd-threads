package model;

import model.estrategia.ControleMonitor;
import model.estrategia.ControleSemaforo;
import model.estrategia.EstrategiaDeControle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Cruzamento {

    private final int id;
    private final List<MalhaBlocos> blocosDoCruzamento;

    private final EstrategiaDeControle mecanismoControle;


    public Cruzamento(int id, boolean usarSemaforo) {
        this.id = id;
        this.blocosDoCruzamento = new ArrayList<>();

        if (usarSemaforo) {
            this.mecanismoControle = new ControleSemaforo();
        } else {
            this.mecanismoControle = new ControleMonitor();
        }
    }

    public void adicionarBloco(MalhaBlocos bloco) {
        this.blocosDoCruzamento.add(bloco);
        bloco.setCruzamentoPai(this);
    }

    public boolean tryReservarCaminho() {
        boolean reservado = false;
        mecanismoControle.entrarRegiaoCritica();

        try {
            // Verifica se algum bloco está reservado
            for (MalhaBlocos bloco : blocosDoCruzamento) {
                if (bloco.isReservado()) {
                    return false; // está ocupado
                }
            }

            // Reserva todos os blocos
            for (MalhaBlocos bloco : blocosDoCruzamento) {
                bloco.setReservado(true);
            }
            reservado = true;
        } finally {
            mecanismoControle.sairRegiaoCritica();
        }

        return reservado;
    }

    public void liberarCaminho() {
        mecanismoControle.entrarRegiaoCritica();
        try {
            for (MalhaBlocos bloco : blocosDoCruzamento) {
                bloco.setReservado(false);
            }
        } finally {
            mecanismoControle.sairRegiaoCritica();
        }
    }

    public int getId() {
        return id;
    }

    public List<MalhaBlocos> getBlocosDoCruzamento() {
        return blocosDoCruzamento;
    }
}