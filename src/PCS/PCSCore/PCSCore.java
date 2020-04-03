package PCS.PCSCore;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.util.ArrayList;


//======================================================================
// PCSCore
public class PCSCore extends AppThread {
    private MBox entranceGateBox;
    private MBox exitGateBox;
    private MBox collectorMbox;
    private MBox payMBox;
    private final int pollTime;
    private final int PollTimerID=1;
    private final int openCloseGateTime;		// for demo only!!!
    private final int OpenCloseGateTimerID=2;		// for demo only!!!
	private final int collectorSolveProblemGateWaitTimeID=3;
    private boolean gateIsClosed = true;		// for demo only!!!
	private ArrayList<Ticket> ticketList=new ArrayList<>();
	private long exitTimeCoefficient=Long.parseLong(appKickstarter.getProperty("Ticket.exitTimeCoefficient"));
	private float calculateFeeCoefficient=Float.parseFloat(appKickstarter.getProperty("Ticket.calculateFeeCoefficient"));
	private int collectorSolveProblemGateWaitTime=Integer.parseInt(appKickstarter.getProperty("PCSCore.CollectorSolveProblemGateWaitTime"));
	private int gateOpenTime=Integer.parseInt(appKickstarter.getProperty("Gate.GateOpenTime"));

//	private long exitTimeCoefficient=0;
//	private float calculateFeeCoefficient=0;
//	private int collectorSolveProblemGateWaitTime=0;
//	private int gateOpenTime=0;


    //------------------------------------------------------------
    // PCSCore
    public PCSCore(String id, AppKickstarter appKickstarter) throws Exception {
	super(id, appKickstarter);
	this.pollTime = Integer.parseInt(appKickstarter.getProperty("PCSCore.PollTime"));
	this.openCloseGateTime = Integer.parseInt(appKickstarter.getProperty("PCSCore.OpenCloseGateTime"));		// for demo only!!!
	Ticket trueTicket=new Ticket();
	trueTicket.setExitInformation(exitTimeCoefficient,0,calculateFeeCoefficient);
	Ticket falseTicket=new Ticket();
	ticketList.add(trueTicket);
	ticketList.add(falseTicket);
	Ticket EricVIPTick = new Ticket();
	EricVIPTick.setParkingFee(9999999);
	ticketList.add(EricVIPTick);
    } // PCSCore


    //------------------------------------------------------------
    // run
    public void run() {
	Thread.currentThread().setName(id);
//	Timer.setTimer(id, mbox, pollTime, PollTimerID);
//	Timer.setTimer(id, mbox, openCloseGateTime, OpenCloseGateTimerID);	// for demo only!!!
	log.info(id + ": starting...");

	entranceGateBox = appKickstarter.getThread("EntranceGateHandler").getMBox();
	exitGateBox=appKickstarter.getThread("ExitGateHandler").getMBox();
	collectorMbox = appKickstarter.getThread("CollectorHandler").getMBox();
	payMBox = appKickstarter.getThread("PayMachineHandler").getMBox();
	for (boolean quit = false; !quit;) {
	    Msg msg = mbox.receive();

	    log.fine(id + ": message received: [" + msg + "].");

	    try {
			switch (msg.getType()) {
				case TimesUp:
					handleTimesUp(msg);
					break;

				case GateOpenReply:
					log.info(id + ": "+msg.getSender()+" is opened.");
					gateIsClosed = false;
					break;

				case GateCloseReply:
					log.info(id + ": "+msg.getSender()+" is closed.");
					gateIsClosed = true;
					break;

				case PollAck:
					log.info("PollAck: " + msg.getDetails());
					break;

				case Terminate:
					quit = true;
					break;

				case CollectorValidRequest:
					handleCollectorValidRequest(msg);
					break;
				case CollectorSolveProblem:
					exitGateBox.send(new Msg(id, mbox, Msg.Type.GateOpenRequest, "GateOpenReq"));
					log.fine(id + ": Collector Solve Problem Now. Collector Is Available Now!");
					log.info(id+": inform Exit Gate Open");
					Timer.setTimer(id,mbox,gateOpenTime+collectorSolveProblemGateWaitTime,collectorSolveProblemGateWaitTimeID);
					break;
				case TicketRequest:
					log.info(id + ":PCS Sent the fee already");
					SendTicketFee(msg.getDetails());
					break;
				case PaymentACK:
					log.info(id + ":Payment ACK received");
					PayStateUpdate(msg.getDetails());
					break;
				default:
					log.warning(id + ": unknown message type: [" + msg + "]");
			}
		}
	    catch (Exception e){
	    	e.printStackTrace();
		}
	}



	// declaring our departure
	appKickstarter.unregThread(this);
	log.info(id + ": terminating...");
    } // run

	public boolean checkStringToInt(String detail) {
    	try{
    		Integer.parseInt(detail);
		}
    	catch (Exception e){
    		log.warning(Msg.bracketString(id+" PCSCore")+ "Integer.parseInt fails: "+Msg.quoteString(detail));
    		return false;
		}
    	return true;
	}
	public void PayStateUpdate(String TicketID){
    	int z = FindTicketByID(Integer.parseInt(TicketID));
    	ticketList.get(z).setParkingFee(0);
    	log.fine(id+ ":Payment Updated");
	}
	public void SendTicketFee(String TicketID){
    	int z = FindTicketByID(Integer.parseInt(TicketID));
    	payMBox.send(new Msg(id,mbox,Msg.Type.TicketFee,TicketID + "," + Float.toString(ticketList.get(z).getParkingFee()) + "," + Long.toString(ticketList.get(z).getEnterTime())));
	}

	public void handleCollectorValidRequest(Msg msg){
		log.info(id+" Collector Valid Request Receive");
		if(checkStringToInt(msg.getDetails())){

			boolean valid=validTicket(Integer.parseInt(msg.getDetails()));

			if(valid){

				ticketList.remove(Integer.parseInt(msg.getDetails()));
				collectorMbox.send(new Msg(id,mbox, Msg.Type.CollectorPositive,""));
				exitGateBox.send(new Msg(id, mbox, Msg.Type.GateOpenRequest, "GateOpenReq"));
				Timer.setTimer(id,mbox,gateOpenTime+collectorSolveProblemGateWaitTime,collectorSolveProblemGateWaitTimeID);

				// do something. Such as:
				// delete the ticket.
				//Ask Gate to Open
				//After several seconds, ask gate to closed.

				log.fine(Msg.bracketString(id+" PCSCore valid a true ticket. Get message "+Msg.quoteString(msg.getDetails())));
			}
			else {
				collectorMbox.send(new Msg(id,mbox,Msg.Type.CollectorNegative,""));
				log.warning(Msg.bracketString(id+" PCSCore valid a false ticket. Get message "+Msg.quoteString(msg.getDetails())));
			}
		}
		else {
			collectorMbox.send(new Msg(id, mbox, Msg.Type.CollectorNegative, ""));
			log.warning(Msg.bracketString(id+" PCSCore valid a false ticket. Get message "+Msg.quoteString(msg.getDetails())));
		}
	}

    //------------------------------------------------------------
    // run
    private void handleTimesUp(Msg msg) {
	log.info("------------------------------------------------------------");
        switch (Timer.getTimesUpMsgTimerId(msg)) {
	    case PollTimerID:
		log.info("Poll: " + msg.getDetails());
		entranceGateBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		Timer.setTimer(id, mbox, pollTime, PollTimerID);
	        break;

//	    case OpenCloseGateTimerID:					// for demo only!!!
//	        if (gateIsClosed) {
//		    log.info(id + ": Open the gate now (for demo only!!!)");
//		    entranceGateBox.send(new Msg(id, mbox, Msg.Type.GateOpenRequest, ""));
//		} else {
//		    log.info(id + ": Close the gate now (for demo only!!!)");
//		    entranceGateBox.send(new Msg(id, mbox, Msg.Type.GateCloseRequest, ""));
//		}
		case collectorSolveProblemGateWaitTimeID:
			exitGateBox.send(new Msg(id,mbox, Msg.Type.GateCloseRequest,"Close Gate"));
			log.info(id+": Inform Exit Gate To Close");
			break;

	    default:
	        log.severe(id + ": why am I receiving a timeout with timer id " + Timer.getTimesUpMsgTimerId(msg));
	        break;
	}
    } // handleTimesUp

/** A Function to search a ticket by TicketID */
	public int FindTicketByID(int TargetID){
    	for(int i = 0; i <ticketList.size();++i)
    		if(ticketList.get(i).ticketID == TargetID)
    			return i;
    	log.warning(id+"No such Ticket =_= called" + TargetID);
    	return -1;
	}
	public boolean validTicket(int ticketID){ // To Cheung Yeung: 这里直接用FindByID吧  因为根据ID找Ticket 在很多情况要用
    	log.info(id+ " valid ticket "+ticketID);
    	for(int i=0;i<ticketList.size();i++){
    		if(ticketList.get(i).getTicketID()==ticketID){
    			return ticketList.get(i).valid(log,id);
			}
		}
    	log.warning(id+": No Ticket With ID "+ticketID);
    	return false;
	}
} // PCSCore
