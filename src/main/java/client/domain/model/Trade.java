package client.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class Trade extends MarketData {
    private final LocalTime time;
    private final double price;
    private final double cumulativeVolume;
    private final double vwap;
    private final boolean isClose;

    @Builder
    public Trade(int sequenceNumber, String code, LocalTime time, double price, double cumulativeVolume, double vwap, boolean isClose) {
        super(sequenceNumber, code);
        this.time = time;
        this.price = price;
        this.cumulativeVolume = cumulativeVolume;
        this.vwap = vwap;
        this.isClose = isClose;
    }

    /**
     * get current turnover, calculated by vwap * cumulativeVolume
     * @return current turnover
     */
    public double getTurnover() {
        return cumulativeVolume * vwap;
    }
}