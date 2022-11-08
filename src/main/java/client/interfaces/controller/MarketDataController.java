package client.interfaces.controller;

import client.domain.MarketDataService;
import client.interfaces.dto.CumulativeVolume;
import client.interfaces.dto.PriceMovement;
import client.interfaces.dto.Turnover;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MarketDataController {
    @Autowired
    MarketDataService marketDataService;

    @GetMapping("/mostTradedByTurnover")
    public ResponseEntity<List<Turnover>> getMostTradedByTurnover(@RequestParam int numberOfData) {
        List<Turnover> turnovers = marketDataService.getMostTradedByTurnover(numberOfData);
        return new ResponseEntity<>(turnovers, HttpStatus.OK);
    }

    @GetMapping("/mostTradedByVolume")
    public ResponseEntity<List<CumulativeVolume>> getMostTradedByVolume(@RequestParam int numberOfData) {
        List<CumulativeVolume> cumulativeVolumes = marketDataService.getMostTradedByVolume(numberOfData);
        return new ResponseEntity<>(cumulativeVolumes, HttpStatus.OK);
    }

    @GetMapping("/highestPriceMovement")
    public ResponseEntity<List<PriceMovement>> getHighestPriceMovement(@RequestParam int numberOfData) {
        List<PriceMovement> priceMovements = marketDataService.getHighestPriceMovement(numberOfData);
        return new ResponseEntity<>(priceMovements, HttpStatus.OK);
    }

    @GetMapping("/lowestPriceMovement")
    public ResponseEntity<List<PriceMovement>> getLowestPriceMovement(@RequestParam int numberOfData) {
        List<PriceMovement> priceMovements = marketDataService.getLowestPriceMovement(numberOfData);
        return new ResponseEntity<>(priceMovements, HttpStatus.OK);
    }
}
