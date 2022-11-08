package client.domain;

import client.domain.model.BasePrice;
import client.domain.model.Trade;
import client.interfaces.dto.CumulativeVolume;
import client.interfaces.dto.PriceMovement;
import client.interfaces.dto.Turnover;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MarketDataService {
    ConcurrentMap<String, Trade> latestCodeToTrade = new ConcurrentHashMap<>();
    ConcurrentMap<String, BasePrice> codeToBasePrice = new ConcurrentHashMap<>();
    ConcurrentSkipListMap<Double, List<String>> sortedTurnoverToCode = new ConcurrentSkipListMap<>();
    ConcurrentSkipListMap<Double, List<String>> sortedVolumeToCode = new ConcurrentSkipListMap<>();
    ConcurrentSkipListMap<Double, List<String>> sortedPriceMovementToCode = new ConcurrentSkipListMap<>();

    /**
     * update the trade data
     * @param trade new trade
     */
    public synchronized void handleNewTradeData(Trade trade) {
        Trade lastTrade = latestCodeToTrade.get(trade.getCode());
        if (lastTrade != null && lastTrade.getSequenceNumber() > trade.getSequenceNumber()) {
            log.warn("Not processing out of sequence trade, code={}, current sequence number={}, new trade sequence" +
                    " number={}", lastTrade.getCode(), lastTrade.getSequenceNumber(), trade.getSequenceNumber());
        } else {
            updateNewTradeData(trade, lastTrade);
        }
    }

    public synchronized void handleBasePriceData(BasePrice basePrice) {
        codeToBasePrice.put(basePrice.getCode(), basePrice);
    }

    private void updateNewTradeData(Trade newTrade, @Nullable Trade previousTrade) {
        String code = newTrade.getCode();
        if (previousTrade != null) {
            latestCodeToTrade.remove(code);
            sortedTurnoverToCode.get(previousTrade.getTurnover()).remove(code);
            sortedVolumeToCode.get(previousTrade.getCumulativeVolume()).remove(code);
        }
        latestCodeToTrade.put(newTrade.getCode(), newTrade);
        sortedTurnoverToCode.computeIfAbsent(newTrade.getTurnover(), k -> new ArrayList<>()).add(code);
        sortedVolumeToCode.computeIfAbsent(newTrade.getCumulativeVolume(), k -> new ArrayList<>()).add(code);

        updatePriceMovement(newTrade, previousTrade);
    }

    private void updatePriceMovement(Trade newTrade, @Nullable Trade previousTrade) {
        String code = newTrade.getCode();
        BasePrice basePrice = codeToBasePrice.get(code);
        if (basePrice != null) {
            if (previousTrade != null) {
                Double prevPriceMove = previousTrade.calculatePriceMovementInPercent(basePrice);
                sortedPriceMovementToCode.get(prevPriceMove).remove(code);
            }
            Double newPriceMove = newTrade.calculatePriceMovementInPercent(basePrice);
            sortedPriceMovementToCode.computeIfAbsent(newPriceMove, k -> new ArrayList<>()).add(code);

        } else {
            log.warn("Base price of {} cannot be found", code);
        }
    }

    public List<Turnover> getMostTradedByTurnover(int n) {
        return getSorted(n, sortedTurnoverToCode, true).stream()
                .map(p -> new Turnover(p.getValue(), p.getKey()))
                .collect(Collectors.toList());
    }

    public List<Turnover> getLeastTradedByTurnover(int n) {
        return getSorted(n, sortedTurnoverToCode, false).stream()
                .map(p -> new Turnover(p.getValue(), p.getKey()))
                .collect(Collectors.toList());
    }

    public List<CumulativeVolume> getMostTradedByVolume(int n) {
        return getSorted(n, sortedVolumeToCode, true).stream()
                .map(p -> new CumulativeVolume(p.getValue(), p.getKey()))
                .collect(Collectors.toList());
    }

    public List<CumulativeVolume> getLeastTradedByVolume(int n) {
        return getSorted(n, sortedVolumeToCode, false).stream()
                .map(p -> new CumulativeVolume(p.getValue(), p.getKey()))
                .collect(Collectors.toList());
    }

    public List<PriceMovement> getHighestPriceMovement(int n) {
        return getSorted(n, sortedPriceMovementToCode, true).stream()
                .map(p -> new PriceMovement(p.getValue(), p.getKey()))
                .collect(Collectors.toList());
    }

    public List<PriceMovement> getLowestPriceMovement(int n) {
        return getSorted(n, sortedPriceMovementToCode, false).stream()
                .map(p -> new PriceMovement(p.getValue(), p.getKey()))
                .collect(Collectors.toList());
    }

    private List<Pair<Double, String>> getSorted(int n, ConcurrentSkipListMap<Double, List<String>> map, boolean descending) {
        Assert.isTrue(n > 0, String.format("numberOfData=%d must be greater than 0", n));
        Assert.isTrue(n <= latestCodeToTrade.size(), String.format("numberOfData [%d] cannot be greater than" +
                " size of total market data [%d]", n, latestCodeToTrade.size()));

        Iterator<Map.Entry<Double, List<String>>> iterator = descending ? map.descendingMap().entrySet().iterator()
                : map.entrySet().iterator();

        List<Pair<Double, String>> arr = new ArrayList<>();
        while (n > 0 && iterator.hasNext()) {
            Map.Entry<Double, List<String>> entry = iterator.next();
            List<String> codes = entry.getValue();
            for (String code : codes) {
                if (n == 0) {
                    break;
                }
                arr.add(Pair.of(entry.getKey(), code));
                --n;
            }
        }

        return arr;
    }
}
