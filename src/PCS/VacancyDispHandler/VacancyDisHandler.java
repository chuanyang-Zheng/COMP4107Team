package PCS.VacancyDispHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.AppThread;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import PCS.GateHandler.GateHandler;

public class VacancyDisHandler extends AppThread {
    protected final MBox pcsCore;
    protected int[] availableSpaces;
    private  VacancyDisStatus vacancyDisStatus;
    private int firstNumber;
    private int secondNumber;

    public VacancyDisHandler(String id, AppKickstarter appKickstarter,int[] availableSpaces) {
        super(id, appKickstarter);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
        vacancyDisStatus = VacancyDisStatus.VacancyDisRunning;
        this.availableSpaces=availableSpaces;
        this.firstNumber=availableSpaces.length;
        this.secondNumber=availableSpaces.length;
    } // VacancyDisHandler

    public void run() {
        Thread.currentThread().setName(id);
        log.info(id + ": starting...");

        for (boolean quit = false; !quit;) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            quit = processMsg(msg);
        }

        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run

    public boolean processMsg(Msg msg){
        boolean quit = false;

        switch (msg.getType()) {
            case VancancyDisUpdateRequest:  handleVacancyDisUpdateRequest(msg);  break;
            case VancancyDisUpdateReply: handleVacancyDisUpdateReply(msg); break;
//            case Poll:		   handlePollReq();	     break;
//            case PollAck:	   handlePollAck();	     break;
            case Terminate:	   quit = true;		     break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
        return quit;
    }

    public void handleVacancyDisUpdateRequest(Msg msg){
        log.info(id + ": vacancy display update request received");
        //check Receive String Is correct or not.
        try{
            String[] spliFloorNumber=msg.getDetails().split("\\s+");//split.
            try {
                firstNumber = Integer.parseInt(spliFloorNumber[0]);//change string to 1,2,3....
                secondNumber = Integer.parseInt(spliFloorNumber[1]);
            }catch (Exception e) {
                throw new Exception(id + "vacancy display parseInt fail!");//receive wrong string
            }

            // Floor number =-1 means the entrance.
            if(firstNumber<availableSpaces.length&&firstNumber>=0) {//floor number range is correct
                //if car leaves floor 1 to floor 2.
//                availableSpaces[firstNumber] = availableSpaces[firstNumber] + 1;//leave floor 1. Floor 1 available spaces+1

            }
            else if(firstNumber<0){
                log.warning(id+" vacancy display: car drives in the parking lot");
            }
            else {
                firstNumber=availableSpaces.length;
                throw new Exception(id+" vacancy display receive wrong number "+firstNumber);
            }


            if(secondNumber<availableSpaces.length&&secondNumber>=0) {
                if (availableSpaces[secondNumber] > 0) {//if car leaves floor 1 to floor 2. Floor 1 available spaces should be more than 1.
//                    availableSpaces[secondNumber] = availableSpaces[secondNumber] - 1;//Go to floor 2. Floor 2 available spaces -1
                } else {
                    throw new Exception(id + " vacancy display: available spaces at " + secondNumber + " is smaller than 0");
                }
            }
            else if(secondNumber<0){
                log.warning(id+" vacancy display: car drives out the parking lot");
            }
            else {
                secondNumber=availableSpaces.length;
                throw new Exception(id+" vacancy display receive wrong number "+secondNumber);
            }

            //Above is OK
            sendVacancyDisUpdateSinal();

        }
        catch (Exception e){
            log.warning(e.getMessage());
            e.printStackTrace();
        }
    }


    public void handleVacancyDisUpdateReply(Msg msg){
        log.info(id + ": vacancy display update request received");
        int firstNumberTep;
        int secondNumberTep;
        int oldFirstNumberFloorSpaces=0;
        int oldSecondNumberFloorSpaces=0;
        try{
            String[] spliFloorNumber=msg.getDetails().split("\\s+");//split.
            try {
                firstNumberTep = Integer.parseInt(spliFloorNumber[0]);//change string to 1,2,3....
                secondNumberTep = Integer.parseInt(spliFloorNumber[1]);
            }catch (Exception e) {
                throw new Exception(id + "vacancy display parseInt fail!");//receive wrong string
            }

            if(firstNumber!=firstNumberTep ||secondNumber!=secondNumberTep){
                throw new Exception("Update Floor Number Not MATCH. Original: "+firstNumber+" "+secondNumber+ ", Now : "+firstNumberTep+" "+secondNumberTep);
            }

//            firstNumber=firstNumberTep;
//            secondNumber=secondNumber

            // Floor number =-1 means the entrance.
            if(firstNumber<availableSpaces.length&&firstNumber>=0) {//floor number range is correct
                //if car leaves floor 1 to floor 2.
                oldFirstNumberFloorSpaces=availableSpaces[firstNumber];
                availableSpaces[firstNumber] = availableSpaces[firstNumber] + 1;//leave floor 1. Floor 1 available spaces+1

            }
            else if(firstNumber<0){
                log.warning(id+" vacancy display: car drives in the parking lot");
            }
            else {
                throw new Exception(id+" vacancy display receive wrong number "+firstNumber);
            }


            if(secondNumber<availableSpaces.length&&secondNumber>=0) {
                if (availableSpaces[secondNumber] > 0) {//if car leaves floor 1 to floor 2. Floor 1 available spaces should be more than 1.
                    oldSecondNumberFloorSpaces=availableSpaces[secondNumber];
                    availableSpaces[secondNumber] = availableSpaces[secondNumber] - 1;//Go to floor 2. Floor 2 available spaces -1
                } else {
                    throw new Exception(id + " vacancy display: available spaces at " + secondNumber + " is smaller than 0");
                }
            }
            else if(secondNumber<0){
                log.warning(id+" vacancy display: car drives out the parking lot");
            }
            else {
                throw new Exception(id+" vacancy display receive wrong number "+secondNumber);
            }
            pcsCore.send(new Msg(id, mbox, Msg.Type.VancancyDisUpdateReply, ""));
            log.fine("Parking Space Change\n" +
                            "Original: ["+firstNumber+ "] "+oldFirstNumberFloorSpaces+", ["+secondNumber+"] "+oldSecondNumberFloorSpaces+"\n" +
                            "Original: ["+firstNumber+ "] "+availableSpaces[firstNumber]+", ["+secondNumber+"] "+availableSpaces[secondNumber]);

        }
        catch (Exception e){
            log.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendVacancyDisUpdateSinal(){

        // fixme: send Vacancy Update signal to hardware
        log.info(id + ": sending vacandy update signal to hardware.");

    }

    private enum VacancyDisStatus {
        VacancyDisRunning,
        VacancyDisClosed
    }
}
