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
Working on GUI. Create all windows in MainGUIController.
Create methods for making PositionsWindow and PriceController Window.
