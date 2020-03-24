package PCS.CollectorHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.AppThread;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import PCS.GateHandler.GateHandler;

public class CollectorHandler extends AppThread {
    protected final MBox pcsCore;
    private CollectorStatus collectorStatus;
    public CollectorHandler(String id, AppKickstarter appKickstarter){
        super(id, appKickstarter);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
        collectorStatus = CollectorStatus.CollectorAvailable;
    }


    //*Input: Nothing. Return:Nothing.
    //** Run Part of handler. It deal with logic commannds*/
    public void run() {
        Thread.currentThread().setName(id);
        log.info(id + ": starting...");

        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            quit = processMsg(msg);
        }
    }

    //*Input:Msg msg*/
    //**Return: boolean*/
    //**Receive a msg and handle the logic*/
    public boolean processMsg(Msg msg){
        boolean quit = false;

        switch (msg.getType()) {
            case CollectorValidRequest:  handleCollectorValidRequest(msg);  break;
            case CollectorPositive:   handleCollectorPositive();   break;
            case CollectorNegative: handleCollectorNegative(); break;
            case CollectorSolveProblem:	   handleCollctorSolveProblem();    break;
//            case Poll:		   handlePollReq();	     break;
//            case PollAck:	   handlePollAck();	     break;
            case Terminate:	   quit = true;		     break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
        return quit;
    }

    public void handleCollectorValidRequest(Msg msg){
        log.info(id + ": collector valid request received");
        CollectorStatus oldStatus=collectorStatus;

        switch (collectorStatus){
            case CollectorAvailable:
                int ticketID = -1;
                try {
                    try {
                        ticketID = Integer.parseInt(msg.getDetails());
                    } catch (Exception e) {
                        throw new Exception(id + ": collector receive invalid message: [" + msg.getDetails() + "]");
                    }
                    pcsCore.send(new Msg(id, mbox, Msg.Type.CollectorValidRequest, msg.getDetails()));
                    collectorStatus=CollectorStatus.CollectorWaitValidation;
                } catch (Exception e) {
                    log.warning(e.getMessage());
                    e.printStackTrace();
                }
                break;

            case CollectorWaitValidation:
                log.warning(id+" Collector is waiting validation result. Ignore Validation Request");
                break;
            case CollectorWarning:
                log.warning(id+" Collector is warning. Ignore Validation Request");
        }
        log.fine(id+": Collector Status from "+oldStatus+"-> "+collectorStatus);
    }

    //** Input:Nothing*/
    //** Return:Nothing*/
    //A simple function. The ticket is valid. Therefore, we log received message.
    public void handleCollectorPositive(){
        log.info(id + ": collector receive positive validation");
        CollectorStatus oldStatus=collectorStatus;
        switch (collectorStatus){
            case CollectorAvailable:
                log.warning(id+": Collector is Available Now. Wrong State!");
                break;
            case CollectorWaitValidation:
                collectorStatus=CollectorStatus.CollectorAvailable;
                break;
            case CollectorWarning:
                log.warning(id+": Collector is Warning Now. Wrong State!");
                break;
        }
        log.fine(id+": Collector Status from "+oldStatus+"-> "+collectorStatus);
    }

    //**Input:Nothing*/
    //**Return Nothing*/
    //PCS believe that the ticket is invalid. Therefore, in the method, ring alrams, ask staff to solve problem.
    public void handleCollectorNegative(){
        log.info(id+": collector receive negative validation");
        CollectorStatus oldStatus=collectorStatus;
        switch (collectorStatus){
            case CollectorAvailable:
                log.warning(id+": Collector is Available Now. Wrong State!");
                break;
            case CollectorWaitValidation:
                log.fine(id+": Ring ALram!");
                collectorStatus=CollectorStatus.CollectorWarning;
                break;
            case CollectorWarning:
                log.warning(id+": Collector is Warning Now. Wrong State");
        }
        log.fine(id+": Collector Status from "+oldStatus+"-> "+collectorStatus);
    }

    //**Input:No thing
    //**Return: Nothing
    //**After solve problem, we use the function to tell PCS Core.
    public void handleCollctorSolveProblem(){
        log.info(id+": collector receive Problem-Solve Message");
        CollectorStatus oldStatus=collectorStatus;
        switch (collectorStatus){
            case CollectorAvailable:
                log.warning(id+": collector is Now "+collectorStatus+" Wrong State!");
                break;
            case CollectorWaitValidation:
                log.warning(id+": collector is Now "+collectorStatus+" Wrong State!");
                break;
            case CollectorWarning:
                log.fine("Stop Ringing Alram!");
                pcsCore.send(new Msg(id,mbox, Msg.Type.CollectorSolveProblem,""));
                collectorStatus=CollectorStatus.CollectorAvailable;
                break;
        }
        log.fine(id+": Collector Status from "+oldStatus+"-> "+collectorStatus);

    }

    private enum CollectorStatus {
        CollectorAvailable,
        CollectorWaitValidation,
        CollectorWarning,
    }
}
