package client.domain;

import client.domain.model.BasePrice;
import client.domain.model.Trade;
import client.interfaces.dto.CumulativeVolume;
import client.interfaces.dto.PriceMovement;
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

    private static final BasePrice price1 = BasePrice.builder()
            .sequenceNumber(1)
            .code("123")
            .basePrice(8000d)
            .build();

    private static final BasePrice price2 = BasePrice.builder()
            .sequenceNumber(2)
            .code("124")
            .basePrice(90d)
            .build();

    private static final BasePrice price3 = BasePrice.builder()
            .sequenceNumber(3)
            .code("125")
            .basePrice(12000d)
            .build();

    private static final BasePrice price4 = BasePrice.builder()
            .sequenceNumber(4)
            .code("126")
            .basePrice(40d)
            .build();

    private static final Trade trade1 = Trade.builder()
            .sequenceNumber(101)
            .code("123")
            .time(LocalTime.parse("09:01:11.254769000"))
            .price(10000d)
            .cumulativeVolume(200000d)
            .vwap(10000d)
            .isClose(false)
            .build();

    private static final Trade trade2 = Trade.builder()
            .sequenceNumber(102)
            .code("124")
            .time(LocalTime.parse("09:01:11.254769000"))
            .price(100d)
            .cumulativeVolume(500000d)
            .vwap(100d)
            .isClose(false)
            .build();

    private static final Trade trade3 = Trade.builder()
            .sequenceNumber(103)
            .code("125")
            .time(LocalTime.parse("09:01:11.254769000"))
            .price(10000d)
            .cumulativeVolume(50d)
            .vwap(10000d)
            .isClose(false)
            .build();

    private static final Trade trade4 = Trade.builder()
            .sequenceNumber(104)
            .code("126")
            .time(LocalTime.parse("09:01:11.254769000"))
            .price(20d)
            .cumulativeVolume(10000d)
            .vwap(20d)
            .isClose(false)
            .build();

    private static final Trade trade5 = Trade.builder()
            .sequenceNumber(105)
            .code("123")
            .time(LocalTime.parse("09:01:11.254769000"))
            .price(15000d)
            .cumulativeVolume(300000d)
            .vwap(10000d)
            .isClose(false)
            .build();

    @Autowired
    MarketDataService marketDataService;

    @BeforeEach
    void setUp() {
        Stream.of(price1, price2, price3, price4)
                .forEach(price -> marketDataService.handleBasePriceData(price));
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

    @Test
    void getHighestPriceMovement() {
        PriceMovement priceMove = marketDataService.getHighestPriceMovement(1).get(0);
        assertEquals(priceMove.getCode(), trade5.getCode());
        assertEquals(priceMove.getPriceMovementInPercent(), trade5.calculatePriceMovement(price1));
    }

    @Test
    void getLowestPriceMovement() {
        PriceMovement priceMove = marketDataService.getLowestPriceMovement(1).get(0);
        assertEquals(priceMove.getCode(), trade4.getCode());
        assertEquals(priceMove.getPriceMovementInPercent(), trade4.calculatePriceMovement(price4));
    }

    @Test
    void testInvalidInput() {
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> marketDataService.getMostTradedByTurnover(0).get(0));
        assertEquals("numberOfData=0 must be greater than 0", exception.getMessage());
    }
}