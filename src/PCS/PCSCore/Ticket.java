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

    public boolean valid(Logger log,String id){
        if(ticketID<0)
        {
            log.warning(id+": "+ticketID+" is not valid");
            return false;
        }
        if( System.currentTimeMillis()>exitTime)
        {
            log.warning(id+": "+"Current time"+System.currentTimeMillis()+" is larger than Exit time: "+exitTime);
            return false;
        }
        if(payMachineID<0)
        {
            log.warning(id+": "+payMachineID+" is smaller than 0");
            return false;
        }

        if( parkingFee<0){
            log.warning(id+": "+parkingFee+" is smaller than 0");
            return false;
        }


        return true;
    }

    public void setExitInformation(long exitTimeCoefficient,int payMachineID, float calculateFeeCoefficient){
        long currentTime=System.currentTimeMillis();
        this.exitTime=currentTime+exitTimeCoefficient;
        this.payMachineID=payMachineID;
        this.parkingFee=(System.currentTimeMillis()-enterTime)*calculateFeeCoefficient;
    }
}
