package io;

import model.MalhaBlocos;
import util.Directions;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MalhaReader {

    private int qntLinhas;
    private int qntColunas;

    public MalhaBlocos[][] lerInstancias(String path) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new FileReader(path))) {

            qntLinhas = Integer.parseInt(buffer.readLine().trim());
            qntColunas = Integer.parseInt(buffer.readLine().trim());
            MalhaBlocos[][] montarMatriz = new MalhaBlocos[qntLinhas][qntColunas];

            for (int i = 0; i < qntLinhas; i++) {
                String[] linha = buffer.readLine().trim().split("\t");

                for (int j = 0; j < qntColunas; j++) {
                    int codigoDirecao = Integer.parseInt(linha[j]);
                    // 'direcao' agora é uma variável local.
                    Directions direcao = Directions.existeDirecao(codigoDirecao);

                    // 1. Determina se o bloco é um cruzamento a partir do nome do enum.
                    boolean isCruzamento = direcao.name().startsWith("CRUZAMENTO");

                    // 2. O cálculo de 'entrada' foi removido, pois o novo construtor não o utiliza.
                    boolean saida = ehSaida(i, j, direcao);

                    // 3. CHAMADA AO NOVO CONSTRUTOR com os argumentos na ordem correta.
                    montarMatriz[i][j] = new MalhaBlocos(i, j, direcao, isCruzamento, saida);
                }
            }
            return montarMatriz;
        } catch (IOException | NumberFormatException | NullPointerException e) {
            throw new IOException("Erro na leitura ou processamento do arquivo: " + path, e);
        }
    }

    /**
     * Verifica se um bloco é uma saída.
     * Refatorado para receber a direção como parâmetro, tornando o método mais seguro.
     */
    private boolean ehSaida(int i, int j, Directions direcao) {
        if ((i == 0) || (j == 0)) { // Bordas superior ou esquerda
            return (direcao == Directions.ESTRADA_CIMA || direcao == Directions.ESTRADA_ESQUERDA);
        }
        if ((i == qntLinhas - 1) || (j == qntColunas - 1)) { // Bordas inferior ou direita
            return (direcao == Directions.ESTRADA_BAIXO || direcao == Directions.ESTRADA_DIREITA);
        }
        return false;
    }
}