package http.execution;

public final class Main {

    public static void main(String... args) {
        PeriodicConsumerRunner consumerRunner = new PeriodicConsumerRunner();
        new Thread(consumerRunner).start();
    }
}
