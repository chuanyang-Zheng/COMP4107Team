package PCS.CollectorHandler.Emulator;

import PCS.CollectorHandler.CollectorHandler;
import PCS.PCSStarter;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class CollectorEmulator extends CollectorHandler {
    private Stage myStage;
    private CollectorEmulatorController collectorEmulatorController;
    private final PCSStarter pcsStarter;
    private final String id;
    private final int gateOpenTime;
    private final int gateCloseTime;
    private final int GateOpenTimerID = 1;
    private final int GateCloseTimerID = 2;
    private boolean autoOpen;
    private boolean autoClose;
    private boolean autoPoll;

    public CollectorEmulator(String id, PCSStarter pcsStarter){
        super(id, pcsStarter);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";
        this.gateOpenTime = Integer.parseInt(this.pcsStarter.getProperty("Gate.GateOpenTime"));
        this.gateCloseTime = Integer.parseInt(this.pcsStarter.getProperty("Gate.GateCloseTime"));
        this.autoOpen = true;
        this.autoClose = true;
        this.autoPoll = true;
    }

    //------------------------------------------------------------
    // start
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "CollectorEmulator.fxml";
        loader.setLocation(CollectorEmulator.class.getResource(fxmlName));
        root = loader.load();
        collectorEmulatorController = (CollectorEmulatorController) loader.getController();
        collectorEmulatorController.initialize(id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 420, 470));
        myStage.setTitle("Collector Emulator");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // CollectorEmulator

    @Override
    protected void sendStartAlarmSignal() {
        logWarning("The Ticket Is Wrong. Ring Alarm!");
    }

    @Override
    protected void sendStopAlarmSignal() {
        logFine("Already Solve Problem. Stop Alarm!");
    }

    //------------------------------------------------------------
    // logFine
    private final void logFine(String logMsg) {
        collectorEmulatorController.appendTextArea("[FINE]: " + logMsg);
        log.fine(id + ": " + logMsg);
    } // logFine


    //------------------------------------------------------------
    // logInfo
    private final void logInfo(String logMsg) {
        collectorEmulatorController.appendTextArea("[INFO]: " + logMsg);
        log.info(id + ": " + logMsg);
    } // logInfo


    //------------------------------------------------------------
    // logWarning
    private final void logWarning(String logMsg) {
        collectorEmulatorController.appendTextArea("[WARNING]: " + logMsg);
        log.warning(id + ": " + logMsg);
    } // logWarning


    //------------------------------------------------------------
    // logSevere
    private final void logSevere(String logMsg) {
        collectorEmulatorController.appendTextArea("[SEVERE]: " + logMsg);
        log.severe(id + ": " + logMsg);
    } // logSevere
}
