package client.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class MarketDataParserTest {
    String message = "44570:T:9104,09:01:11.254769000,10930.00,354600,10890.6318,0";
    Trade expectTrade = Trade.builder()
            .sequenceNumber(44570)
            .code("9104")
            .time(LocalTime.parse("09:01:11.254769000"))
            .price(10930.0)
            .cumulativeVolume(354600d)
            .vwap(10890.6318)
            .isClose(false)
            .build();

    @Test
    void apply() {
        MarketData marketData = new MarketDataParser().apply(message);
        assertEquals(marketData.getClass(), Trade.class);
        Trade trade = (Trade) marketData;
        assertEquals(expectTrade.getSequenceNumber(), trade.getSequenceNumber());
        assertEquals(expectTrade.getCode(), trade.getCode());
        assertEquals(expectTrade.getTime(), trade.getTime());
        assertEquals(expectTrade.getPrice(), trade.getPrice());
        assertEquals(expectTrade.getCumulativeVolume(), trade.getCumulativeVolume());
        assertEquals(expectTrade.getVwap(), trade.getVwap());
        assertEquals(expectTrade.isClose(), trade.isClose());
        assertEquals(expectTrade.getTurnover(), trade.getTurnover());
    }
}