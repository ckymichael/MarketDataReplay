package client.domain;

import client.domain.model.Trade;
import client.interfaces.dto.CumulativeVolume;
import client.interfaces.dto.Turnover;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MarketDataServiceTest {

    private static final Trade trade1 = Trade.builder()
            .sequenceNumber(1)
            .code("123")
            .time(LocalTime.parse("09:01:11.254769000"))
            .price(10000d)
            .cumulativeVolume(200000d)
            .vwap(10000d)
            .isClose(false)
            .build();

    private static final Trade trade2 = Trade.builder()
            .sequenceNumber(2)
            .code("124")
            .time(LocalTime.parse("09:01:11.254769000"))
            .price(100d)
            .cumulativeVolume(500000d)
            .vwap(100d)
            .isClose(false)
            .build();

    private static final Trade trade3 = Trade.builder()
            .sequenceNumber(3)
            .code("125")
            .time(LocalTime.parse("09:01:11.254769000"))
            .price(10000d)
            .cumulativeVolume(50d)
            .vwap(10000d)
            .isClose(false)
            .build();

    private static final Trade trade4 = Trade.builder()
            .sequenceNumber(4)
            .code("126")
            .time(LocalTime.parse("09:01:11.254769000"))
            .price(20d)
            .cumulativeVolume(10000d)
            .vwap(20d)
            .isClose(false)
            .build();

    private static final Trade trade5 = Trade.builder()
            .sequenceNumber(5)
            .code("123")
            .time(LocalTime.parse("09:01:11.254769000"))
            .price(10000d)
            .cumulativeVolume(300000d)
            .vwap(10000d)
            .isClose(false)
            .build();

    @Autowired
    MarketDataService marketDataService;

    @BeforeEach
    void setUp() {


        Stream.of(trade1, trade2, trade3, trade4, trade5)
                .forEach( trade -> marketDataService.handleNewTradeData(trade));
    }

    @Test
    void getMostTradedByTurnover() {
        Turnover turnover = marketDataService.getMostTradedByTurnover(1).get(0);
        assertEquals(turnover.getCode(), trade5.getCode());
        assertEquals(turnover.getTurnover(), trade5.getTurnover());
    }

    @Test
    void getMostTradedByVolume() {
        CumulativeVolume volume = marketDataService.getMostTradedByVolume(1).get(0);
        assertEquals(volume.getCode(), trade2.getCode());
        assertEquals(volume.getCumulativeVolume(), trade2.getCumulativeVolume());
    }

    @Test
    void getLeastTradedByTurnover() {
        Turnover turnover = marketDataService.getLeastTradedByTurnover(1).get(0);
        assertEquals(turnover.getCode(), trade4.getCode());
        assertEquals(turnover.getTurnover(), trade4.getTurnover());
    }

    @Test
    void getLeastTradedByVolume() {
        CumulativeVolume volume = marketDataService.getLeastTradedByVolume(1).get(0);
        assertEquals(volume.getCode(), trade3.getCode());
        assertEquals(volume.getCumulativeVolume(), trade3.getCumulativeVolume());
    }
}