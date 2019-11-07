package main.java.model.orderserver;

import main.java.model.positionsorders.MikePosOrders;

public class MikeOrder {

    public int getFilledPrice() {
        return filledPrice;
    }

    public void setFilledPrice(int filledPrice) {
        this.filledPrice = filledPrice;
    }

    //BUYLMT, SELLLMT, BUYSTP, SELLSTP
    public enum MikeOrderType{
        BUYLMT,
        SELLLMT,
        BUYSTP,
        SELLSTP
    };

    private MikePosOrders posOrders;

    /**
     * Buy limit? Sell stop? etc*/
    private MikeOrderType orderType;

    /**
     * this stores which position the order is assigned to
    there can be multiple orders for one price
    once order is filled - this tells which position is updated*/
    private int assignedToPosition = 0;

    //the price of the order. priceserver are in cents.
    private int price = 0;

    //the size of the order
    private int amount = 0;

    //the price at which the order got filled:
    private int filledPrice = 0;

    //the filled amount of the order
    private int filledAmount = 0;

    //for internal use
    private long mikeOrderNumber;

    //for future use - for passing orders into outside API
    private long outsideAPIOrderNumber;

    //for checking fills - has the order been filled already?
    private boolean isFilled = false;

    //for future implementation.
    private boolean partialFill = false;

    //was the order cancelled?
    private boolean cancelled = false;

    public MikeOrder(MikeOrderType orderType, int assignedToPosition, int price, int amount) {
        this.orderType = orderType;
        this.assignedToPosition = assignedToPosition;
        this.price = price;
        this.amount = amount;
    }

    public MikePosOrders getPosOrders() {
        return posOrders;
    }

    public void setPosOrders(MikePosOrders posOrders) {
        this.posOrders = posOrders;
    }

    public void setFilledAmount(int filledAmount) {
        this.filledAmount = filledAmount;
    }

    public void setMikeOrderNumber(long mikeOrderNumber) {
        this.mikeOrderNumber = mikeOrderNumber;
    }

    public void setOutsideAPIOrderNumber(long outsideAPIOrderNumber) {
        this.outsideAPIOrderNumber = outsideAPIOrderNumber;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
    }

    public void setPartialFill(boolean partialFill) {
        this.partialFill = partialFill;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public MikeOrderType getOrderType() {
        return orderType;
    }

    public int getAssignedToPosition() {
        return assignedToPosition;
    }

    public int getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public int getFilledAmount() {
        return filledAmount;
    }

    public long getMikeOrderNumber() {
        return mikeOrderNumber;
    }

    public long getOutsideAPIOrderNumber() {
        return outsideAPIOrderNumber;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public boolean isPartialFill() {
        return partialFill;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}