package client.domain;

import client.domain.model.BasePrice;
import client.domain.model.MarketData;
import client.domain.model.MarketDataParser;
import client.domain.model.Trade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MarketDataClient {

    @Autowired
    MarketDataService marketDataService;

    /**
     * Parse a message received from the market data simulator.
     * Note that this method is called from multiple threads, and it is not garanteed to be called in increasing message sequence number.
     * <p>
     * Messages are of String type, and their format is: "seqNb:msgType:msgBody", where:
     * <ul>
     *  <li>seqNb (int): The sequence number of the message. 2 consecutive messages have consecutive (i and i+1) sequence numbers, so a message with a greater sequence number
     *      is more recent than a message with a smaller sequence number</li>
     *  <li>msgType (char): The type of the message. Messages of 2 messages types are sent:
     *      <ul>
     *          <li>msgType='B': base price message</li>
     *          <li>msgType='T': trade message
     *      </ul>
     *      Data in the message body depends on the message type
     *  </li>
     *  <li>msgBody (comma separated values): number of values, order of values and type of values depend on the message type:
     *      <ul>
     *          <li>msgType='B': "ticker,basePrice" where
     *              <ul>
     *                  <li>ticker is the 4 digits TSE stock ticker. For instance: "7203" for Toyota Motors</li>
     *                  <li>basePrice is the reference price to be used for the day. Essentially, the previous close adjusted for corporate actions. For instance: "657.50"</li>
     *              </ul>
     *          </li>
     *          <li>msgType='T': "ticker,timeOfDay,price,cumulVolume,vwap,closeFlag" where
     *              <ul>
     *                  <li>ticker is the 4 digits TSE stock ticker. For instance: "7203" for Toyota Motors</li>
     *                  <li>timeOfDay is the time of the trade, format: "HH:mm:ss.SSSSSSSSS". For instance: "10:14:03.324500316"</li>
     *                  <li>price is the price at which the trade was executed. For instance: "657.50"</li>
     *                  <li>cumulVolume is the cumulated volume (nb of shares) at the time of the trade, including the trade. For instance: "325122"</li>
     *                  <li>vwap is the vwap as of the time of the trade (cumulValue / cumulVolume where cumulValue would be the cumulated value). For instance: "651.1259"</li>
     *                  <li>closeFlag indicates whether this is a closing-auction trade (closeFlag="1") or not (closeFlag="0")</li>
     *              </ul>
     *          </li>
     *      </ul>
     *  </li>
     * </ul>
     * Sample base price message: "342:B:7203,657.50"
     * Sample trade message: "16781:T:7203,10:14:03.324500316,657.50,325122,651.1259,0"
     *
     * @param message The message received from the market data simulator
     */
    public synchronized void onMessage(String message) {
        MarketData marketData = new MarketDataParser().apply(message);
        if (marketData.getClass() == Trade.class) {
            marketDataService.handleNewTradeData((Trade) marketData);
        } else if (marketData.getClass() == BasePrice.class) {
            marketDataService.handleBasePriceData((BasePrice) marketData);
        }
    }

    /**
     * Handle an error reported by the simulation engine.
     *
     * @param e The error (could be an internal error thrown by the simulation engine itself, or an error raised by the client while processing a message)
     */
    public synchronized void onError(Throwable e) {

        e.printStackTrace(System.err);
    }
}
