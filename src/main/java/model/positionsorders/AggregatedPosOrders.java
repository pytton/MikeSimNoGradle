package main.java.model.positionsorders;


import main.java.model.orderserver.MikeOrder;
import main.java.model.orderserver.OrderServer;
import main.java.model.priceserver.PriceServer;

import java.util.LinkedList;
import java.util.List;

/**
 * This class cannot place and handle orders.
 * Used only to show aggregated Position/PL and other info for a number of other MikePosOrders
 */
public class AggregatedPosOrders extends MikePosOrders {

    List<MikePosOrders> posOrdersList = new LinkedList<>();

    public AggregatedPosOrders() {
        super();
    }

    public AggregatedPosOrders(List<MikePosOrders> posOrdersList) {
        this.posOrdersList = posOrdersList;
    }

    public synchronized void setPosOrdersList(List<MikePosOrders> posOrdersList) {
        this.posOrdersList.clear();
        this.posOrdersList.addAll(posOrdersList);
        System.out.println("Setting PosOrders in AggregatedPosOrders");
    }

    @Override
    public synchronized int getOpenBuyOrdersAtPrice(int price) {
        int openBuyOrdersAtPrice = 0;
        for (MikePosOrders positions : posOrdersList) {
            openBuyOrdersAtPrice += positions.getOpenBuyOrdersAtPrice(price);
        }
        return openBuyOrdersAtPrice;
    }

    @Override
    public synchronized int getOpenSellOrdersAtPrice(int price) {
        int openSellOrdersAtPrice = 0;
        for (MikePosOrders positions : posOrdersList) {
            openSellOrdersAtPrice += positions.getOpenSellOrdersAtPrice(price);
        }
        return openSellOrdersAtPrice;
    }

    @Override
    public synchronized int getOpenAmountAtPrice(int price) {
        int openAmountAtPrice = 0;
        for (MikePosOrders postions : posOrdersList) {
            openAmountAtPrice += postions.getOpenAmountAtPrice(price);
        }
        return openAmountAtPrice;
    }

    @Override
    public synchronized void recalcutlatePL() {
        openPL = 0; closedPL = 0; totalPL = 0; totalOpenAmount = 0;
        averagePrice = 0;
        for (MikePosOrders positions : posOrdersList) {
            openPL += positions.getOpenPL();
            closedPL += positions.getClosedPL();
            totalPL += positions.getTotalPL();
            totalOpenAmount += positions.getTotalOpenAmount();
            averagePrice += positions.getAveragePrice();
        }
        averagePrice = averagePrice/posOrdersList.size();
        if (totalOpenAmount != 0) zeroProfitPoint = (averagePrice) - (closedPL / totalOpenAmount);
        else zeroProfitPoint = averagePrice;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public int getOpenPL() {
        return super.getOpenPL();
    }

    @Override
    public int getClosedPL() {
        return super.getClosedPL();
    }

    @Override
    public int getTotalPL() {
        return super.getTotalPL();
    }

    @Override
    public double getAveragePrice() {
        return super.getAveragePrice();
    }

    @Override
    public int getTotalOpenAmount() {
        return super.getTotalOpenAmount();
    }

    @Override
    public int getTickerId() {
        return super.getTickerId();
    }

    @Override
    public double getZeroProfitPoint() {
        return super.getZeroProfitPoint();
    }

}
