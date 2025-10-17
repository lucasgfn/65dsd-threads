package model.estrategia;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ControleMonitor implements EstrategiaDeControle {

    // Um ReentrantLock é a implementação explícita do padrão Monitor.
    private final Lock monitorLock = new ReentrantLock();

    @Override
    public void entrarRegiaoCritica() {
        monitorLock.lock();
    }

    @Override
    public void sairRegiaoCritica() {
        monitorLock.unlock();
    }
}