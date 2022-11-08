package client.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class MarketData {
    private final int sequenceNumber;
    private final String code;
}
