package model.estrategia;

import java.util.concurrent.Semaphore;

public class ControleSemaforo implements EstrategiaDeControle {

    private final Semaphore semaforo = new Semaphore(1);

    @Override
    public void entrarRegiaoCritica() {
        try {
            semaforo.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void sairRegiaoCritica() {
        semaforo.release();
    }
}