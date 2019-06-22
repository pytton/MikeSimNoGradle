package main.java.model.positionsorders;

public class MikeOrder {
    enum MikeOrderType{
        BUYLMT,
        SELLLMT,
        BUYSTP,
        SELLSTP
    }
    //BUYLMT, SELLLMT, BUYSTP, SELLSTP
    MikeOrderType ordertype;

    //this stores which position the order is assigned to
    //there can be multiple orders for one price
    //once order is filled - this tells which position is updated
    long assignedToPosition = 0;

    //the price of the order. priceserver are in cents.
    long price = 0;

    //the size of the order
    long amount = 0;

    //for future use - for passing orders into outside API
    long orderId;

    //for checking fills - has the order been filled already?
    boolean isFilled = false;

    //for future implementation.
    boolean partialFill = false;

    //was the order cancelled?
    boolean cancelled = false;
}
