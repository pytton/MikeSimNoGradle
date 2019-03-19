package main.java.model;

public class MikePosition {

    long price = 0;
    long open_amount = 0;//positive open_amount means the position is 'long'. negative open_amount means the position is 'short'.
    long open_pl = 0;
    long closed_pl = 0;
    long total_pl = 0;


    boolean isActive = false;
    long prevbidprice = 0;  //to check if open_pl needs to be recalculated
    long prevaskprice = 0;



    //this is for indexing purposes - set to TRUE if position was ever
    //accessed or changed. Mainly to avoid iterating through tens of thousands
    //of positions
    boolean checkifActive() { if (isActive == true) return true; else  return false; }
    void setActive() { isActive = true; }
    void setInactive() { isActive = false; }



    //positive amount for buy orders, negative amount for sell orders!
    void fill(long fillprice, long filledamount)
    {
        //this will modify the closed_pl by:
        //difference in fill price and price of this position
        //multiplied by amount
        long tempclosed_pl = closed_pl;
        long profitloss;
        ////////////////////////////////////////////////
        profitloss = (price - fillprice) * filledamount;
        closed_pl = tempclosed_pl + profitloss;
        ////////////////////////////////////////////////
        //this updates the current open_amount by the amount that was filled
        long tempopenamount = open_amount;
        open_amount = tempopenamount + filledamount;
        //recalculate PL because the size of position has changed:
        calculatePL(prevbidprice, prevaskprice);
    }
    void calculatePL(long bidprice, long askprice)
    {
        //	static long prevbidprice = 0;
        //	static long prevaskprice = 0;
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

}
