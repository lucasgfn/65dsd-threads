package model;

import io.MalhaReader;
import java.io.IOException;

public class MalhaViaria {

    private final MalhaReader construirMatriz = new MalhaReader();
    private MalhaBlocos[][] malha;
    private final int qntLimiteVeiculos;
    private final boolean usarSemaforo;

    public MalhaViaria(String pathMatriz, int qntLimiteVeiculos, boolean usarSemaforo) throws IOException {
        this.usarSemaforo = usarSemaforo;
        this.qntLimiteVeiculos = qntLimiteVeiculos;
        this.malha = construirMatriz.lerInstancias(pathMatriz, usarSemaforo);
    }

    public MalhaBlocos[][] getMalha() { return malha; }
    public int getQntLimiteVeiculos() { return qntLimiteVeiculos; }
    public boolean isUsarSemaforo() { return usarSemaforo; }
}
