package main.java.model.positionsorders;

import main.java.model.MikeSimLogger;

public class MikePosition {

    private int price = 0;
    private int open_amount = 0;//positive open_amount means the position is 'long'. negative open_amount means the position is 'short'.
    private int open_pl = 0;
    private int closed_pl = 0;
    private int total_pl = 0;

    private int prevbidprice = -10;  //to check if open_pl needs to be recalculated
    private int prevaskprice = -10;

    public MikePosition(int price) {
        this.price = price;
    }

    /**
     *
     * positive open_amount means position is long
     * negative open_amount means position is short
     * @param price
     * @param open_amount
     * @param open_pl
     * @param closed_pl
     * @param total_pl
     */
    public MikePosition(int price, int open_amount, int open_pl, int closed_pl, int total_pl) {
        this.price = price;
        this.open_amount = open_amount;
        this.open_pl = open_pl;
        this.closed_pl = closed_pl;
        this.total_pl = total_pl;
    }

    //positive amount for buy orders, negative amount for sell orders!
    void fill(int fillprice, int filledamount)
    {

        MikeSimLogger.addLogEvent("Testing. Filling position in MikePosition");
        //this will modify the closed_pl by:
        //difference in fill price and price of this position
        //multiplied by amount
        int tempclosed_pl = closed_pl;
        int profitloss;
        ////////////////////////////////////////////////
        profitloss = (price - fillprice) * filledamount;
        closed_pl = tempclosed_pl + profitloss;
        ////////////////////////////////////////////////
        //this updates the current open_amount by the amount that was filled
        int tempopenamount = open_amount;
        open_amount = tempopenamount + filledamount;
        //recalculate PL because the size of position has changed:
        //need to temporarily change prevbidprice otherwise calculatePL
        //will think no price change and no reason to recalculate:
        prevbidprice = prevbidprice -1;
        calculatePL(prevbidprice+1, prevaskprice);
    }
    void calculatePL(int bidprice, int askprice)
    {
        if (bidprice != prevbidprice || askprice != prevaskprice)
        {
            open_pl = 0;
            //if position is 'long':
            if (open_amount >= 0) open_pl = (bidprice - price) * open_amount;
            //if position is 'short':
            if (open_amount < 0) open_pl = (askprice - price) * open_amount;

            //update total_pl with new open_pl
            total_pl = closed_pl + open_pl;
            //update bidprice and askprice to make future calculations faster:
            prevbidprice = bidprice;
            prevaskprice = askprice;
        }
    }

    protected void setClosedPL(int amount){
        closed_pl = amount;
    }

    public int getPrice() {
        return price;
    }

    public int getOpen_amount() {
        return open_amount;
    }

    public int getOpen_pl() {
        return open_pl;
    }

    public int getClosed_pl() {
        return closed_pl;
    }

    public int getTotal_pl() {
        return total_pl;
    }

    public void zeroOut(){
        open_amount = 0;
        open_pl = 0;
        closed_pl = 0;
        total_pl = 0;
    }
}
