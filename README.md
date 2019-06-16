# MikeSimJava
Trading simulator using TWS API for paper trading. Test bed for custom algos.
package com.ib.* is needed to connect to Interactive Brokers API.


###TODO:

1. A copy of the 'Price control' window from C++ MikeSimulator. Pressing buttons prints messages in console.
C++ MikeSimulator here: https://github.com/pytton/MikeSimulator/tree/experiment/Release file: TestingNewMikeSim.exe
2. A copy of the 'Positions' window from C++ MikeSimulator.
3. 'Priceserver' class which has three modules - manual prices, live Interactive Brokers prices, historical prices. Implement only manual prices now.
4. Link 'Priceserver' class with UI. 

5. Bug in PriceControlPanel - setting MinPrice higher than MaxPrice makes the slider disappear - fixed


#current commit:
Added RealTimeData interface which is currently implemented in TWSRealTimeData class. In the future, implement RealTimeData interface
with a different class if you need to connect to trading software other that Interactive Brokers.

added methods connectToTWS and setUpContracts to TWSRealtimeData.
method tickPrice in TWSRealTimeData sets bid and ask for default TickerID = 0 prices in its class.