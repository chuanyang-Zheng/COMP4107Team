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

        int ticketID=-1;
        try{
            try {
                ticketID = Integer.parseInt(msg.getDetails());
            }
            catch (Exception e){
                throw new Exception(id + ": collector receive invalid message: ["+msg.getDetails()+"]");
            }
            pcsCore.send(new Msg(id,mbox, Msg.Type.CollectorValidRequest,msg.getDetails()));
        }
        catch (Exception e){
            log.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    //** Input:Nothing*/
    //** Return:Nothing*/
    //A simple function. The ticket is valid. Therefore, we log received message.
    public void handleCollectorPositive(){

    }

    //**Input:Nothing*/
    //**Return Nothing*/
    //PCS believe that the ticket is invalid. Therefore, in the method, ring alrams, ask staff to solve problem.
    public void handleCollectorNegative(){

    }

    //**Input:No thing
    //**Return: Nothing
    //**After solve problem, we use the function to tell PCS Core.
    public void handleCollctorSolveProblem(){

    }

    private enum CollectorStatus {
        CollectorAvailable,
        CollectorReceivePositive,
        CollectorReceiveNegative,
    }
}
