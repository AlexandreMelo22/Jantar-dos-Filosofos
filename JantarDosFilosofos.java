// Importando as classes necessárias
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

// Classe principal
class JantarDosFilosofos {
    // Número de filósofos
    private static final int NUM_FILOSOFOS = 5;

    public static void main(String[] args) {
        // Cria um array para os filósofos
        Filosofo[] filosofos = new Filosofo[NUM_FILOSOFOS];
        // Cria um array para os garfos
        ReentrantLock[] garfos = new ReentrantLock[NUM_FILOSOFOS];
        // Cria um semáforo para controlar o acesso aos garfos
        Semaphore mutex = new Semaphore(1);

        // Inicializa os garfos
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            garfos[i] = new ReentrantLock();
        }

        // Inicializa os filósofos e inicia suas threads
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            filosofos[i] = new Filosofo(i, garfos[i], garfos[(i + 1) % NUM_FILOSOFOS], mutex);
            new Thread(filosofos[i]).start();
        }
    }
}

// Classe que representa um filósofo
class Filosofo implements Runnable {
    // Identificador do filósofo
    private final int id;
    // Garfos à esquerda e à direita do filósofo
    private final ReentrantLock garfoEsquerdo;
    private final ReentrantLock garfoDireito;
    // Semáforo para controlar o acesso aos garfos
    private final Semaphore mutex;
    // Contador de refeições
    private int refeicoes = 0;
    // Número máximo de refeições que um filósofo pode fazer
    private static final int MAX_REFEICOES = 5;

    // Construtor do filósofo
    public Filosofo(int id, ReentrantLock garfoEsquerdo, ReentrantLock garfoDireito, Semaphore mutex) {
        this.id = id;
        this.garfoEsquerdo = garfoEsquerdo;
        this.garfoDireito = garfoDireito;
        this.mutex = mutex;
    }

    // Método que será executado quando a thread do filósofo for iniciada
    @Override
    public void run() {
        try {
            // Enquanto o filósofo não tiver comido o máximo de refeições
            while (refeicoes < MAX_REFEICOES) {
                // O filósofo pensa
                pensar();
                // O filósofo tenta pegar os garfos
                pegarGarfos();
                // O filósofo come
                comer();
                // O filósofo solta os garfos
                soltarGarfos();
                // Incrementa o contador de refeições
                refeicoes++;
            }
        } catch (InterruptedException e) {
            // Se a thread for interrompida, imprime a exceção e termina a thread
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    // Método que simula o filósofo pensando
    private void pensar() throws InterruptedException {
        System.out.println("Filósofo " + id + " está pensando");
        Thread.sleep((long) (Math.random() * 10000));
    }

    // Método que simula o filósofo pegando os garfos
    private void pegarGarfos() throws InterruptedException {
        // Adquire o semáforo para garantir acesso exclusivo aos garfos
        mutex.acquire();
        // Tenta pegar os garfos
        garfoEsquerdo.lock();
        garfoDireito.lock();
        // Libera o semáforo
        mutex.release();
    }

    // Método que simula o filósofo comendo
    private void comer() throws InterruptedException {
        System.out.println("Filósofo " + id + " está comendo");
        Thread.sleep((long) (Math.random() * 10000));
    }

    // Método que simula o filósofo soltando os garfos
    private void soltarGarfos() {
        // Solta os garfos
        garfoEsquerdo.unlock();
        garfoDireito.unlock();
    }
}
