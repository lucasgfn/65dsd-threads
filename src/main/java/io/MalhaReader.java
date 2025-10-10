package io;

import model.MalhaBlocos;
import util.Directions;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MalhaReader {

    private int qntLinhas;
    private int qntColunas;
    private Directions direcao;

    public MalhaBlocos[][] lerInstancias(String path, boolean usarSemaforo) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new FileReader(path))) {

            qntLinhas = Integer.parseInt(buffer.readLine().trim());
            qntColunas = Integer.parseInt(buffer.readLine().trim());
            MalhaBlocos[][] montarMatriz = new MalhaBlocos[qntLinhas][qntColunas];

            for (int i = 0; i < qntLinhas; i++) {
                String[] linha = buffer.readLine().trim().split("\t");

                for (int j = 0; j < qntColunas; j++) {
                    int codigoDirecao = Integer.parseInt(linha[j]);
                    direcao = Directions.existeDirecao(codigoDirecao);

                    boolean entrada = ehEntrada(i, j);
                    boolean saida = ehSaida(i, j);

                    montarMatriz[i][j] = new MalhaBlocos(entrada, saida, direcao, i, j, usarSemaforo);
                }
            }
            return montarMatriz;
        } catch (IOException | NumberFormatException | NullPointerException e) {
            throw new IOException("Erro na leitura ou processamento do arquivo: " + path, e);
        }
    }

    private boolean ehEntrada(int i, int j) {
        if ((i == 0) || (j == 0)) {
            return (direcao == Directions.ESTRADA_BAIXO || direcao == Directions.ESTRADA_DIREITA);
        }
        if ((i == qntLinhas - 1) || (j == qntColunas - 1)) {
            return (direcao == Directions.ESTRADA_CIMA || direcao == Directions.ESTRADA_ESQUERDA);
        }
        return false;
    }

    private boolean ehSaida(int i, int j) {
        if ((i == 0) || (j == 0)) {
            return (direcao == Directions.ESTRADA_CIMA || direcao == Directions.ESTRADA_ESQUERDA);
        }
        if ((i == qntLinhas - 1) || (j == qntColunas - 1)) {
            return (direcao == Directions.ESTRADA_BAIXO || direcao == Directions.ESTRADA_DIREITA);
        }
        return false;
    }
}
