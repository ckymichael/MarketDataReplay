MarketDataReplay
A web api which provides real time indicator from market data.

Technical stack
- Java 8
- Springboot
- Junit 5
- http + restful api
- Spring fox Swagger

To run the project
    mvn clean install
    mvn exec:java -Dexec.args="arg[0], arg[1], arg[2]" 
    args[0]: the simulation date (ISO date format: YYYY-MM-DD)
    args[1]: the spead factor
    args[2]: the number of threads
    for example "2022-03-24 1 1"
    
4 restful api have been expose to client application

/highestPriceMovement
the N stocks with the highest price change
request parameter
- numberOfData int32 #number of stocks
response
[PriceMovement{
    code	string
    priceMovementInPercent	number($double)
}]
example http://localhost:8080/highestPriceMovement?numberOfData=5

/lowestPriceMovement
the N stocks with the lowest price change
request parameter
- numberOfData int32 #number of stocks
response
[PriceMovement{
    code	string
    priceMovementInPercent	number($double)
}]
example http://localhost:8080/lowestPriceMovement?numberOfData=5

/mostTradedByTurnover
the N stocks with the highest turnover
request parameter
- numberOfData int32 #number of stocks
response
[Turnover{
    code	string
    turnover	number($double)
}]
example http://localhost:8080/mostTradedByTurnover?numberOfData=5

/mostTradedByVolume
the N stocks with the highest volume
request parameter
- numberOfData int32 #number of stocks
[CumulativeVolume{
    code	string
    cumulativeVolume	number($double)
}]
example http://localhost:8080/mostTradedByVolume?numberOfData=5


A swagger api doc have been set up, launching locally http://localhost:8080/swagger-ui/index.html


----------------------------------------------------------------------------------------------------------------------------------------------------------------
