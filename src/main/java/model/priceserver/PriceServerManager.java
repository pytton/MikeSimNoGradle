package main.java.model.priceserver;


import java.util.Map;

/**
 * Used to create and manage priceservers.
 *
 */
public class PriceServerManager {
    private Map<String, PriceServer> priceServerMap;

    public void createPriceServer(){

    }

    public PriceServer getPriceserver(String instrumentName){
        PriceServer priceServer;
        priceServer = priceServerMap.get(instrumentName);


        return null;
    }

}
