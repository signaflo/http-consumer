package execution;
/**
 * The entry point for the application.
 */
public final class Main {

    private Main() {}

    /**
     * The method that starts application execution.
     *
     * @param args arguments provided at startup.
     */
    public static void main(String... args) {
        new ConsumerExecutor();
    }
}
