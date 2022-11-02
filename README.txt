The goal of this exercise is to replay one day of TSE market data (trades only).

You are expected to:
    1) Part 1: on the back end side, implement the (i) MarketDataClient class (in Java) and (ii) method(s) to provide 3 key indicators.
    2) Part 2: on the front end side, build a simple (web) UI showing key market indicators in (close to) real time.

Please start with part 1 only (do no spend more than 2~3 hours) and submit your code (even if you could not finish). Along with your code, add a comment on what
additional things you would do / keep working on if you had to release this code to production.
We will review your back-end solution first, and depending on this review, we may (or may not) ask you to continue with part 2 (front-end).

---- 1) Part 1 - Back end: MarketDataClient class --------------------------------------------------------------------------------------------------------------

    The MarketDataClient.onMessage method must be implemented: this is the method receiving (text) messages from the simulator.
        Upon message reception, it is expected to parse the message and process its content. Note that the method is called from multiple threads and that the
        order of the calls is not guaranteed (meaning that the sequence of calls to this method may not correspond to consecutive / increasing message sequence
        numbers)
    Method(s) should be added (on the MarketDataClient class itself or any other class you would have introduced) to provide the following 3 key indicators:
        - Most traded stocks: the N (typically N=5) most traded stocks. Most traded is defined as the stock with the highest value (in currency) traded  from
                              start of day until current time. Required output: tickers and corresponding traded values.
        - Movers up         : the N (typically N=5) stocks with the highest price change. Price change is defined as last trade price vs base price (in %).
                              Required output: tickers and corresponding price change (in %).
        - Movers down       : the N (typically N=5) stocks with the lowest price change. Price change is defined as last trade price vs base price (in %).
                              Required output: tickers and corresponding price change (in %).

    FAQs
        - Q: What configuration should I use for the simulation engine?
                A: The configuration will depend on your hardware and the performance of your back end. You may get started with a speed factor of 900 and 8
                   threads, and adjust these if too aggressive / too conservative. Monitor the message processing delay in the logs to see how your
                   implementation keeps up with the replay pace (a delay of 0ms means that the MarketDataClient is not falling behind at all)
        - Q: May I use Java third parties / frameworks?
                A: A third party framework may be used to host / serve the web application. Spring-boot would be a typical choice for that purpose.
                   However, the MarketDataClient logic (data structures, event handling, message parsing, synchronization, etc.) should be implemented using
                   core Java API only.
        - Q: May I change the Main class
                A: Yes, it may actually be required if you implement a third party framework.
        - Q: May I define / add new classes / source files?
                A: Yes.

---- 2) Part 2 - Front end: (close to) real time market dashboard ----------------------------------------------------------------------------------------------

    A simple UI should be built (ideally a web UI - can be a static HTML page) to show the following 3 market indicators in (close to) real time:
        - Most traded stocks: the 5 most traded stocks (as defined in step 1)
        - Movers up         : the 5 stocks with the highest price change (as defined in step 1)
        - Movers down       : the 5 stocks with the lowest price change (as defined in step 1)

    FAQs
        - Q: What do you mean by "(close to) real time"?
                A: UI does not need to be updated on every trade received. Refreshing indicators every second or half-second is enough (it's real-time from a
                   human perspective).
        - Q: May I use third party libraries?
                A: Yes, please use any third party you like (JQuery, styling, grids, etc. or even frameworks like Angular, React, etc. if that's easier for you)
        - Q: Can both the back end and the front end run on localhost?
                A: Yes

----------------------------------------------------------------------------------------------------------------------------------------------------------------