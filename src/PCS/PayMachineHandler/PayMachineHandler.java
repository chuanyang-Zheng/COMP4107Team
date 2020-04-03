package PCS.PayMachineHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import PCS.PCSStarter;
import PCS.PayMachineHandler.Emulator.PayMachineController;
import PCS.PayMachineHandler.Emulator.PayMachineEmulator;


//======================================================================
// GateHandler
public class PayMachineHandler extends AppThread {
    protected final MBox pcsCore;
    private GateStatus gateStatus;
    private PayMachineEmulator PayEmu;
    protected float TicketFee;

    //------------------------------------------------------------
    // GateHandler
    public PayMachineHandler(String id,AppKickstarter pcss) {
        super(id, pcss);

        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
        gateStatus = GateStatus.GateClosed;
    } // GateHandler


    //------------------------------------------------------------
    // run
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


    //------------------------------------------------------------
    // processMsg
    protected boolean processMsg(Msg msg) {
        boolean quit = false;

        switch (msg.getType()) {
            case TicketRequest:  SendRequest(msg.getDetails());  break;
            case TicketFee: FeeReceive(msg.getDetails()); break;
            case PaymentACK:	   SendPaymentACK(msg.getDetails());    break;
            case GateCloseReply:   handleGateCloseReply();   break;
            case Poll:		   handlePollReq();	     break;
            case PollAck:	   handlePollAck();	     break;
            case Terminate:	   quit = true;		     break;

        }
        return quit;
    } // processMsg

//------------------------
// Push FEE
    protected void SendRequest(String mymsg){
        pcsCore.send(new Msg(id, mbox, Msg.Type.TicketRequest, mymsg));
        TicketFee = Float.parseFloat(mymsg);
    }
    protected void FeeReceive(String mymsg){
        TicketFee = Float.parseFloat(mymsg);

    }
    protected void SendPaymentACK(String mymsg){
        log.fine(id+ ":ticket"+ mymsg + "Paid already.");

    }
    //------------------------------------------------------------
    // handleGateOpenRequest
    protected final void handleGateOpenRequest() {
        log.info(id + ": gate open request received");

        GateStatus oldGateStatus = gateStatus;
        switch (gateStatus) {
            case GateOpening:
                log.warning(id + ": gate is already opening!!  Ignore request.");
                break;

            case GateOpened:
                log.warning(id + ": gate is already opened!!  Ignore request.");
                break;

            case GateClosing:
                log.info(id + ": gate is closing.  Change direction.");
                // falls through

            case GateClosed:
                log.info(id + ": send signal to open the gate now.");
                sendGateOpenSignal();
                gateStatus = GateStatus.GateOpening;
                break;
        }

        if (oldGateStatus != gateStatus) {
            log.fine(id + ": gate status change: " + oldGateStatus + " --> " + gateStatus);
        }
    } // handleGateOpenRequest


    //------------------------------------------------------------
    // handleGateCloseRequest
    protected final void handleGateCloseRequest() {
        log.info(id + ": gate close request received");

        GateStatus oldGateStatus = gateStatus;
        switch (gateStatus) {
            case GateOpening:
                log.info(id + ": gate is opening.  Change direction.");
                // falls through

            case GateOpened:
                log.info(id + ": send signal to close the gate now.");
                sendGateCloseSignal();
                gateStatus = GateStatus.GateClosing;
                break;

            case GateClosing:
                log.warning(id + ": gate is already closing!!  Ignore request.");
                break;

            case GateClosed:
                log.warning(id + ": gate is already closed!!  Ignore request.");
                break;
        }

        if (oldGateStatus != gateStatus) {
            log.fine(id + ": gate status change: " + oldGateStatus + " --> " + gateStatus);
        }
    } // handleGateCloseRequest


    //------------------------------------------------------------
    // handleGateOpenReply
    protected final void handleGateOpenReply() {
        log.info(id + ": gate open reply received");

        GateStatus oldGateStatus = gateStatus;
        switch (gateStatus) {
            case GateOpening:
                log.info(id + ": inform PCS Core that gate has finished opening.");
                pcsCore.send(new Msg(id, mbox, Msg.Type.GateOpenReply, ""));
                gateStatus = GateStatus.GateOpened;
                break;

            case GateOpened:
                log.warning(id + ": gate is already opened!!  Ignore reply.");
                break;

            case GateClosing:
                log.warning(id + ": gate should be closing!!  *** CHK ***");
                break;

            case GateClosed:
                log.warning(id + ": gate should be closed!!  *** CHK ***");
                break;
        }

        if (oldGateStatus != gateStatus) {
            log.fine(id + ": gate status change: " + oldGateStatus + " --> " + gateStatus);
        }
    } // handleGateOpenReply


    //------------------------------------------------------------
    // handleGateCloseReply
    protected final void handleGateCloseReply() {
        log.info(id + ": gate close reply received");

        GateStatus oldGateStatus = gateStatus;
        switch (gateStatus) {
            case GateOpening:
                log.warning(id + ": gate should be opening!!  *** CHK ***");
                break;

            case GateOpened:
                log.warning(id + ": gate should be opened!!  *** CHK ***");
                break;

            case GateClosing:
                log.info(id + ": inform PCS Core that gate has finished closing.");
                pcsCore.send(new Msg(id, mbox, Msg.Type.GateCloseReply, ""));
                gateStatus = GateStatus.GateClosed;
                break;

            case GateClosed:
                log.warning(id + ": gate is already closed!!  Ignore reply.");
                break;
        }

        if (oldGateStatus != gateStatus) {
            log.fine(id + ": gate status change: " + oldGateStatus + " --> " + gateStatus);
        }
    } // handleGateCloseReply


    //------------------------------------------------------------
    // handlePollReq
    protected final void handlePollReq() {
        log.info(id + ": poll request received.  Send poll request to hardware.");
        sendPollReq();
    } // handlePollReq


    //------------------------------------------------------------
    // handlePollAck
    protected final void handlePollAck() {
        log.info(id + ": poll ack received.  Send poll ack to PCS Core.");
        pcsCore.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is up!"));
    } // handlePollAck


    //------------------------------------------------------------
    // sendGateOpenSignal
    protected void sendGateOpenSignal() {
        // fixme: send gate open signal to hardware
        log.info(id + ": sending gate open signal to hardware.");
    } // sendGateOpenSignal


    //------------------------------------------------------------
    // sendGateCloseSignal
    protected void sendGateCloseSignal() {
        // fixme: send gate close signal to hardware
        log.info(id + ": sending gate close signal to hardware.");
    } // sendGateCloseSignal


    //------------------------------------------------------------
    // sendPollReq
    protected void sendPollReq() {
        // fixme: send gate poll request to hardware
        log.info(id + ": poll request received");
    } // sendPollReq


    //------------------------------------------------------------
    // Gate Status
    private enum GateStatus {
        GateOpened,
        GateClosed,
        GateOpening,
        GateClosing,
    }
} // GateHandler
