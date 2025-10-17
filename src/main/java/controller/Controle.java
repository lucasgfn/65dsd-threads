package controller;

import model.Carro;
import model.MalhaBlocos;
import model.MalhaViaria;
import util.Directions;
import view.SimuladorTrafegoView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Controle {

    private SimuladorTrafegoView view;
    private MalhaViaria malhaViaria;
    private boolean usarSemaforo = true;
    private int maxVeiculos = 10;
    private final List<Integer> idDisponiveis = Collections.synchronizedList(new ArrayList<>());

    private int intervaloInsercao = 500; // ms

    private final List<Carro> carros = Collections.synchronizedList(new ArrayList<>());
    private boolean insercaoAtiva = false;
    private Thread threadInsercao;

    public void setView(SimuladorTrafegoView view) { this.view = view; }
    public void setModoControle(boolean semaforo) { this.usarSemaforo = semaforo; }
    public void setIntervaloInsercao(int intervalo) { this.intervaloInsercao = intervalo; }

    public boolean criarMalha(String path) {
        try {
            this.malhaViaria = new MalhaViaria(path, maxVeiculos, usarSemaforo);
            atualizarView();
            return true;
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            return false;
        }
    }

    public void atualizarView() { exibirMalha(); }

    public void exibirMalha() {
        if (malhaViaria == null || view == null) return;

        MalhaBlocos[][] malha = malhaViaria.getMalha();
        StringBuilder sb = new StringBuilder();

        for (MalhaBlocos[] linha : malha) {
            for (MalhaBlocos bloco : linha) {
                String symbol = " ";
                if (bloco == null || bloco.getDirecao() == null) symbol = " ";
                else if (bloco.getCarro() != null) {
                    int numero = bloco.getCarro().getIdCarro();
                    symbol = String.format("🚘%02d", numero);

                }
                else switch (bloco.getDirecao()) {
                        case ESTRADA_CIMA, CRUZAMENTO_CIMA -> symbol = " ↑";
                        case ESTRADA_BAIXO, CRUZAMENTO_BAIXO -> symbol = " ↓";
                        case ESTRADA_ESQUERDA, CRUZAMENTO_ESQUERDA -> symbol = " ←";
                        case ESTRADA_DIREITA, CRUZAMENTO_DIREITA -> symbol = " →";
                        case CRUZAMENTO_CIMA_DIREITA -> symbol = " ╔";
                        case CRUZAMENTO_CIMA_ESQUERDA -> symbol = " ╗";
                        case CRUZAMENTO_BAIXO_DIREITA -> symbol = " ╚";
                        case CRUZAMENTO_BAIXO_ESQUERDA -> symbol = " ╝";
                        default -> symbol = "#####";
                    }
                sb.append(String.format("%-5s", symbol));
            }
            sb.append("\n");
        }
        view.atualizarMalha(sb.toString());
    }

    public void iniciarSimulacao() {
        if (malhaViaria == null) return;
        insercaoAtiva = true;

        threadInsercao = new Thread(() -> {
            while (insercaoAtiva) {
                // A lista 'carros' agora é sincronizada, então o bloco synchronized é opcional mas bom para atomicidade.
                synchronized (carros) {
                    while (carros.size() < maxVeiculos) {
                        if (!inserirVeiculo()) break;
                    }
                }

                try { Thread.sleep(intervaloInsercao); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
            }
        });
        threadInsercao.start();
    }

    private boolean inserirVeiculo() {
        MalhaBlocos[][] malha = malhaViaria.getMalha();
        // Para evitar ConcurrentModificationException ao iterar, podemos usar uma cópia ou um for indexado
        for (int i = 0; i < malha.length; i++) {
            for (int j = 0; j < malha[i].length; j++) {
                MalhaBlocos bloco = malha[i][j];

                if (bloco != null && ehEntrada(bloco) && bloco.getCarro() == null) {
                    int idDisponivel = pegarNumeroDisponivel();
                    if (idDisponivel == -1) return false;

                    Carro carro = new Carro(idDisponivel, "Carro" + idDisponivel, malhaViaria, bloco, this);
                    carros.add(carro);
                    carro.start();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean ehEntrada(MalhaBlocos bloco) {
        int i = bloco.getIdxLinha();
        int j = bloco.getIdxColuna();
        Directions direcao = bloco.getDirecao();
        int qntLinhas = malhaViaria.getMalha().length;
        int qntColunas = malhaViaria.getMalha()[0].length;

        if ((i == 0) || (j == 0)) {
            return (direcao == Directions.ESTRADA_BAIXO || direcao == Directions.ESTRADA_DIREITA);
        }
        if ((i == qntLinhas - 1) || (j == qntColunas - 1)) {
            return (direcao == Directions.ESTRADA_CIMA || direcao == Directions.ESTRADA_ESQUERDA);
        }
        return false;
    }

    public void encerrarInsercao() {
        insercaoAtiva = false;
        if (threadInsercao != null) threadInsercao.interrupt();
    }

    public void encerrarSimulacao() {
        // 1. Para a inserção de novos carros.
        encerrarInsercao();

        // 2. Cria uma cópia da lista de carros para evitar problemas de concorrência
        //    enquanto sinaliza e aguarda o término.
        List<Carro> carrosASeremParados;
        synchronized (carros) {
            carrosASeremParados = new ArrayList<>(carros);
        }

        // 3. Sinaliza para todas as threads de carros pararem.
        //    O método parar() agora interrompe a thread se ela estiver dormindo.
        for (Carro c : carrosASeremParados) {
            c.parar();
        }

        // 4. Aguarda que TODAS as threads de carros de fato terminem sua execução.
        //    Este é o passo mais importante para um encerramento seguro.
        for (Carro c : carrosASeremParados) {
            try {
                c.join(); // Bloqueia esta thread (de controle) até que a thread 'c' morra.
            } catch (InterruptedException e) {
                System.err.println("A thread de controle foi interrompida enquanto aguardava os carros.");
                Thread.currentThread().interrupt(); // Preserva o status de interrupção
            }
        }

        // 5. Agora que temos 100% de certeza que nenhuma thread de carro está rodando,
        //    podemos limpar os recursos com total segurança.
        carros.clear();
        idDisponiveis.clear();
        setMaxVeiculos(this.maxVeiculos); // Reinicia a lista de IDs para uma nova simulação

        // 6. Atualiza a interface gráfica para refletir o estado final (malha vazia).
        atualizarView();
    }


    public void removerVeiculo(Carro carro) {
        // A lista 'carros' já é thread-safe (Collections.synchronizedList),
        // então a sincronização manual aqui é para garantir a atomicidade das duas operações.
        synchronized (carros) {
            carros.remove(carro);
            liberarNumero(carro.getIdCarro());
        }
    }

    public void setMaxVeiculos(int max) {
        this.maxVeiculos = max;
        idDisponiveis.clear();
        for (int i = 1; i <= maxVeiculos; i++) {
            idDisponiveis.add(i);
        }
    }

    private int pegarNumeroDisponivel() {
        if (idDisponiveis.isEmpty()) return -1;
        return idDisponiveis.remove(0);
    }

    private void liberarNumero(int numero) {
        idDisponiveis.add(numero);
        Collections.sort(idDisponiveis); // Mantém os IDs ordenados
    }
}