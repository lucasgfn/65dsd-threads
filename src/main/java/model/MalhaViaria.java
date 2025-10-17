package model;

import io.MalhaReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class MalhaViaria {

    private final MalhaReader construirMatriz = new MalhaReader();
    private MalhaBlocos[][] malha;
    private final int qntLimiteVeiculos;
    private final boolean usarSemaforo;

    public MalhaViaria(String pathMatriz, int qntLimiteVeiculos, boolean usarSemaforo) throws IOException {
        this.qntLimiteVeiculos = qntLimiteVeiculos;
        this.usarSemaforo = usarSemaforo;
        this.malha = construirMatriz.lerInstancias(pathMatriz);
        inicializarCruzamentos();
    }

    private void inicializarCruzamentos() {
        int idCruzamentoAtual = 0;
        for (int i = 0; i < malha.length; i++) {
            for (int j = 0; j < malha[i].length; j++) {
                MalhaBlocos blocoAtual = malha[i][j];
                if (blocoAtual.isCruzamento() && blocoAtual.getCruzamentoPai() == null) {
                    // ---- MUDANÇA AQUI: Passa a escolha do usuário para o construtor do Cruzamento ----
                    Cruzamento novoCruzamento = new Cruzamento(idCruzamentoAtual++, this.usarSemaforo);

                    Queue<MalhaBlocos> fila = new LinkedList<>();
                    fila.add(blocoAtual);

                    while (!fila.isEmpty()) {
                        MalhaBlocos blocoDaBusca = fila.poll();
                        if (blocoDaBusca.getCruzamentoPai() != null) continue;
                        novoCruzamento.adicionarBloco(blocoDaBusca);

                        adicionarVizinhoSeCruzamento(fila, blocoDaBusca.getIdxLinha() - 1, blocoDaBusca.getIdxColuna());
                        adicionarVizinhoSeCruzamento(fila, blocoDaBusca.getIdxLinha() + 1, blocoDaBusca.getIdxColuna());
                        adicionarVizinhoSeCruzamento(fila, blocoDaBusca.getIdxLinha(), blocoDaBusca.getIdxColuna() - 1);
                        adicionarVizinhoSeCruzamento(fila, blocoDaBusca.getIdxLinha(), blocoDaBusca.getIdxColuna() + 1);
                    }
                }
            }
        }
    }

    private void adicionarVizinhoSeCruzamento(Queue<MalhaBlocos> fila, int i, int j) {
        if (i >= 0 && i < malha.length && j >= 0 && j < malha[0].length) {
            MalhaBlocos vizinho = malha[i][j];
            if (vizinho.isCruzamento() && vizinho.getCruzamentoPai() == null) {
                fila.add(vizinho);
            }
        }
    }

    public MalhaBlocos[][] getMalha() { return malha; }
    public int getQntLimiteVeiculos() { return qntLimiteVeiculos; }
}