# MikeSimJava
Trading simulator using TWS API for paper trading. Test bed for custom algos.

###TODO:

1. A copy of the 'Price control' window from C++ MikeSimulator. Pressing buttons prints messages in console.
C++ MikeSimulator here: https://github.com/pytton/MikeSimulator/tree/experiment/Release file: TestingNewMikeSim.exe
2. A copy of the 'Positions' window from C++ MikeSimulator.
3. 'Priceserver' class which has three modules - manual prices, live Interactive Brokers prices, historical prices. Implement only manual prices now.
4. Link 'Priceserver' class with UI. 

5. Bug in PriceControlPanel - setting MinPrice higher than MaxPrice makes the slider disappear - fixed


#current commit:
Added "Experimental Number" in PriceServer - this is set randomly from Mainloop which runs in a seperate thread.

Changed "Button" in PositionsWindow to "Get Ask Price" - this takes ask price from PriceServer and prints it in a TextField; also gets and prints the "Experimental Number" from PriceServer.

