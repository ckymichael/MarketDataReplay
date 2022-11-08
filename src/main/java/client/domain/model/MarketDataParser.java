package client.domain.model;

import server.MessageType;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class MarketDataParser implements Function<String, MarketData> {
    private static final String MESSAGE_SPLITTER = ":";
    private static final String DATA_SPLITTER = ",";

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS");

    private static final String TRUE = "1";
    /**
     * Applies this function to the given argument.
     *
     * @param s the function argument
     * @return the function result
     */
    @Override
    public MarketData apply(String s) {
        try {
            String[] msgPart = s.split(MESSAGE_SPLITTER, 3);
            int sequenceNumber = Integer.parseInt(msgPart[0]);
            char messageType = msgPart[1].charAt(0);
            String data = msgPart[2];
            String[] dataPart = data.split(DATA_SPLITTER);
            String code = dataPart[0];
            if (messageType == MessageType.BASE_PRICE.getKey()) {
                double basePrice = Double.parseDouble(dataPart[1]);
                return BasePrice.builder()
                        .sequenceNumber(sequenceNumber)
                        .code(code)
                        .basePrice(basePrice)
                        .build();
            } else if (messageType == MessageType.TRADE.getKey()) {
                LocalTime time = LocalTime.parse(dataPart[1], TIME_FORMAT);
                double price = Double.parseDouble(dataPart[2]);
                double cumulativeVolume = Double.parseDouble(dataPart[3]);
                double vwap = Double.parseDouble(dataPart[4]);
                boolean isClose = TRUE.equals(dataPart[5]);
                return Trade.builder()
                        .sequenceNumber(sequenceNumber)
                        .code(code)
                        .time(time)
                        .price(price)
                        .cumulativeVolume(cumulativeVolume)
                        .vwap(vwap)
                        .isClose(isClose)
                        .build();
            } else {
                throw new RuntimeException("");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return null;
    }
}
