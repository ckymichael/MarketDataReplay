package client.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BasePrice extends MarketData {
    private final double basePrice;

    @Builder
    public BasePrice(int sequenceNumber, String code, double basePrice) {
        super(sequenceNumber, code);
        this.basePrice = basePrice;
    }
}
