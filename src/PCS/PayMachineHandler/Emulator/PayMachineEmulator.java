package PCS.PayMachineHandler.Emulator;

import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import PCS.PCSCore.Ticket;
import PCS.PCSStarter;

import PCS.PayMachineHandler.PayMachineHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


//======================================================================
// GateEmulator
public class PayMachineEmulator extends PayMachineHandler {
    private Stage myStage;
    private PayMachineController PayMachineController;
    private final PCSStarter pcsStarter;
    private final String id;
//    private final int gateOpenTime;
//    private final int gateCloseTime;
    private final int GateOpenTimerID = 1;
    private final int GateCloseTimerID = 2;
    private boolean autoOpen;
    private boolean autoClose;
    private boolean autoPoll;


    //------------------------------------------------------------
    // GateEmulator
    public PayMachineEmulator(String id, PCSStarter pcsStarter) {
        super(id, pcsStarter);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";
//        this.gateOpenTime = Integer.parseInt(this.pcsStarter.getProperty("Gate.GateOpenTime"));
////        this.gateCloseTime = Integer.parseInt(this.pcsStarter.getProperty("Gate.GateCloseTime"));
//        this.autoOpen = true;
//        this.autoClose = true;
//        this.autoPoll = true;
    } // GateEmulator


    //------------------------------------------------------------
    // start
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "PayMachineEmulator.fxml";
        loader.setLocation(PayMachineEmulator.class.getResource(fxmlName));
        root = loader.load();
        PayMachineController = (PayMachineController) loader.getController();
        PayMachineController.initialize(super.id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 420, 470));
        myStage.setTitle(id);
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // GateEmulator
    @Override
    protected void FeeReceive(String mymsg){
        String []str = mymsg.split(",");
        float fee = Float.parseFloat(str[1]);
        PayMachineController.appendTextArea("You need to pay $" + fee);
        log.fine(id + ": " + mymsg);
        PayMachineController.updateTicket(str[0],str[1],str[2]);
    }
    protected void SendPaymentACK(String mymsg){
        log.fine(id+ ":ticket"+ mymsg + "Paid already.");
        PayMachineController.appendTextArea("Thank you for payment!!!!");
        pcsCore.send(new Msg(id, mbox, Msg.Type.PaymentACK, mymsg));
        pcsCore.send(new Msg(id, mbox, Msg.Type.TicketRequest, mymsg));
    }


} // GateEmulator
