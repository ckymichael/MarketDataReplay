package client.domain;

import client.domain.model.Trade;
import client.interfaces.dto.CumulativeVolume;
import client.interfaces.dto.Turnover;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
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
    ConcurrentSkipListMap<Double, List<String>> sortedTurnoverToCode = new ConcurrentSkipListMap<>();
    ConcurrentSkipListMap<Double, List<String>> sortedVolumeToCode = new ConcurrentSkipListMap<>();

    /**
     * update the trade data
     * @param trade
     */
    public synchronized void handleNewTradeData(Trade trade) {
        Trade lastTrade = latestCodeToTrade.get(trade.getCode());
        if (lastTrade != null && lastTrade.getSequenceNumber() > trade.getSequenceNumber()) {
            log.warn("Not processing out of sequence trade, code={}, current sequence number={}, new trade sequence" +
                    " number={}", lastTrade.getCode(), lastTrade.getSequenceNumber(), trade.getSequenceNumber());
        } else {
            if (lastTrade != null) {
                removePreviousTradeData(lastTrade);
            }
            updateNewTradeData(trade);
        }
    }

    private void removePreviousTradeData(Trade previousTrade) {
        latestCodeToTrade.remove(previousTrade.getCode());
        sortedTurnoverToCode.get(previousTrade.getTurnover()).remove(previousTrade.getCode());
        sortedVolumeToCode.get(previousTrade.getCumulativeVolume()).remove(previousTrade.getCode());
    }

    private void updateNewTradeData(Trade newTrade) {
        latestCodeToTrade.put(newTrade.getCode(), newTrade);
        sortedTurnoverToCode.computeIfAbsent(newTrade.getTurnover(), k -> new ArrayList<>()).add(newTrade.getCode());
        sortedVolumeToCode.computeIfAbsent(newTrade.getCumulativeVolume(), k -> new ArrayList<>()).add(newTrade.getCode());
    }

    public List<Turnover> getMostTradedByTurnover(int n) {
        return getSorted(n, sortedTurnoverToCode, true).stream()
                .map(p -> new Turnover(p.getValue(), p.getKey()))
                .collect(Collectors.toList());
    }

    public List<CumulativeVolume> getMostTradedByVolume(int n) {
        return getSorted(n, sortedVolumeToCode, true).stream()
                .map(p -> new CumulativeVolume(p.getValue(), p.getKey()))
                .collect(Collectors.toList());
    }

    public List<Turnover> getLeastTradedByTurnover(int n) {
        return getSorted(n, sortedTurnoverToCode, false).stream()
                .map(p -> new Turnover(p.getValue(), p.getKey()))
                .collect(Collectors.toList());
    }

    public List<CumulativeVolume> getLeastTradedByVolume(int n) {
        return getSorted(n, sortedVolumeToCode, false).stream()
                .map(p -> new CumulativeVolume(p.getValue(), p.getKey()))
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
