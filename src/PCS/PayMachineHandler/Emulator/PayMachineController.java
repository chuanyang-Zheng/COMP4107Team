package PCS.PayMachineHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import javax.swing.*;


//======================================================================
// GateEmulatorController
public class PayMachineController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private PayMachineEmulator PayMachineEmulator;
    private MBox gateMBox;
    public TextArea gateTextArea;
    public TextArea PayMachineTextAreaInput;
    private int lineNo = 0;
    private String ticket_id;

    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, PayMachineEmulator PayMachineEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.PayMachineEmulator = PayMachineEmulator;
        this.gateMBox = appKickstarter.getThread(id).getMBox();
    } // initialize


    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            case "Insert the ticket":
                ticket_id = PayMachineTextAreaInput.getText();
                PayMachineTextAreaInput.setText("");
                gateMBox.send(new Msg(id, null, Msg.Type.TicketRequest, ticket_id));
                break;
            case "Pay by Oct":
                if(ticket_id.isEmpty())
                    JOptionPane.showConfirmDialog(null,"Please Insert Ticket first :)");
                else
                    gateMBox.send(new Msg(id, null, Msg.Type.PaymentACK, ticket_id));
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
