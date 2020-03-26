package PCS.PCSCore;


import java.util.logging.*;

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
    public void enterPrint(){


    }
    public float calculateFee(float coefficient){
        long currentTime = System.currentTimeMillis();
        long time = currentTime-enterTime;//calculate the pay Fee time
        return coefficient*time;

    }
    public void setPayMachineID(int ID){
        payMachineID=ID;
    }
    public void setExitTime(long exitTime){
        this.exitTime=exitTime;
    }

    public void setParkingFee(float parkingFee) {
        this.parkingFee = parkingFee;
    }

    public long getEnterTime(){
        return enterTime;
    }
    public float getParkingFee(){
        return parkingFee;
    }

    public int getTicketID() {
        return ticketID;
    }

    public int getPayMachineID() {
        return payMachineID;
    }

    public long getExitTime() {
        return exitTime;
    }
//
//    public void setInformation(long exitTime, int payMachineID, float calculateFeeCoeficient){
//
//    }
}
