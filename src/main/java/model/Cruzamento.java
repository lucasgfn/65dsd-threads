package model;

import model.estrategia.ControleMonitor;
import model.estrategia.ControleSemaforo;
import model.estrategia.EstrategiaDeControle;

import java.util.ArrayList;
import java.util.List;

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

    public boolean tryReservarCaminho(List<MalhaBlocos> caminho) {
        mecanismoControle.entrarRegiaoCritica();
        try {
            for (MalhaBlocos bloco : caminho) {
                if (bloco.isReservado()) {
                    return false;
                }
            }
            for (MalhaBlocos bloco : caminho) {
                bloco.setReservado(true);
            }
            return true;
        } finally {
            mecanismoControle.sairRegiaoCritica();
        }
    }

    public void liberarCaminho(List<MalhaBlocos> caminho) {
        mecanismoControle.entrarRegiaoCritica();
        try {
            for (MalhaBlocos bloco : caminho) {
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