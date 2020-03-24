package PCS.PayMachineHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;


//======================================================================
// GateEmulatorController
public class PayMachineController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private PayMachineEmulator gateEmulator;
    private MBox gateMBox;
    public TextArea gateTextArea;

    private int lineNo = 0;


    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, PayMachineEmulator gateEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.gateEmulator = gateEmulator;
        this.gateMBox = appKickstarter.getThread("GateHandler").getMBox();
    } // initialize


    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            case "Gate Open Request":
                gateMBox.send(new Msg(id, null, Msg.Type.GateOpenRequest, "GateOpenReq"));
                break;

            case "Gate Open Reply":
                gateMBox.send(new Msg(id, null, Msg.Type.GateOpenReply, "GateOpenReply"));
                break;

            case "Gate Close Request":
                gateMBox.send(new Msg(id, null, Msg.Type.GateCloseRequest, "GateCloseReq"));
                break;

            case "Gate Close Reply":
                gateMBox.send(new Msg(id, null, Msg.Type.GateCloseReply, "GateCloseReply"));
                break;

            case "Poll Request":
                appendTextArea("Send poll request.");
                gateMBox.send(new Msg(id, null, Msg.Type.Poll, ""));
                break;

            case "Poll ACK":
                appendTextArea("Send poll ack.");
                gateMBox.send(new Msg(id, null, Msg.Type.PollAck, ""));
                break;


            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed


    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
        Platform.runLater(() -> gateTextArea.appendText(String.format("[%04d] %s\n", ++lineNo, status)));
    } // appendTextArea
} // GateEmulatorController
