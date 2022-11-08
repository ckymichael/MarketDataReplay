package client.interfaces.dto;

import lombok.Value;

@Value
public class CumulativeVolume {
    String code;
    Double cumulativeVolume;
}
