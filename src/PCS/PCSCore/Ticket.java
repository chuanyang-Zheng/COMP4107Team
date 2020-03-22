package PCS.PCSCore;

public class Ticket {
    protected final long enterTime;
    protected final int ticketID;

    //if smaller than 0, it means that it is not valid.
    protected long exitTime=-1;
    protected float parkingFee=-1;
    protected int payMachineID=-1;


    protected static int TicketCount=0;

    public Ticket(){
        enterTime=System.currentTimeMillis();
        ticketID=TicketCount++;
    }


    public void setInformation(long exitTime,int payMachineID,float calculateFeeCoeficient){

    }
}
