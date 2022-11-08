package client;

import client.domain.MarketDataClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import server.MarketDataSimulator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class MarketDataReplayApplication {

    /**
     * Instantiate and run the market data simulator
     * <p>
     * 1) Instantiate the market data simulator with config:
     * <ul>
     *  <li>deplayDate: the trading date the simulator will replay (please use 24 Mar 2022, as it's the only date for which historical data files are provided)</li>
     *  <li>speedFactor: the simulator speed factor. For instance, a speed factor of 900 means that the simulator will replay 900 seconds (15 min) of market data in (roughly) 1 second</li>
     *  <li>nbThreads: the number of threads simulator will use to send messages to the client callback (marketDataClient::onMessage)</li>
     * </ul>
     * Messages will be sent to this::onMessage. See javadoc of this method for details about the message format.
     * Any exception thrown will be reported to this::onSimulatorError
     * <p>
     * If the client falls behind (if message processing fails to keep up with the pace of the simulator), the speedFactor should be reduced (to 900, 300, 60, etc.)
     * Consequently, simulation will take longer (estimated duration = 5h/speedFactor).
     * <p>
     * 2) Start and wait for the simulator to finish.
     * The simulator will take a few seconds to load, then start sending messages to marketDataClient::onMessages:
     * - First, base price messages (one message per ticker)
     * - Then, trade (execution) messages (one message per trade)
     *
     * @param args Arguments:
     *             <ul>
     *              <li>args[0]: the simulation date (ISO date format: YYYY-MM-DD)</li>
     *              <li>args[1]: the spead factor</li>
     *              <li>args[2]: the number of threads</li>
     *             </ul>
     */
    public static void main(String... args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(MarketDataReplayApplication.class, args);

        setup();

        // Parse arguments
        LocalDate replayDate = LocalDate.parse(args[0], DateTimeFormatter.ISO_DATE);
        int speedFactor = Integer.parseInt(args[1]);
        int nbThreads = Integer.parseInt(args[2]);

        // Instantiate client
        MarketDataClient marketDataClient = context.getBean(MarketDataClient.class);

        // Run simulator / replay
        MarketDataSimulator marketDataSimulator = new MarketDataSimulator(replayDate, speedFactor, nbThreads, marketDataClient::onMessage, marketDataClient::onError);
        marketDataSimulator.start();
    }

    private static void setup() {

        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT.%1$tL [%4$-7s] %5$s %n");
    }
}
