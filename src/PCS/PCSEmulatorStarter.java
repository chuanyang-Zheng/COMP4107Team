package PCS;

import AppKickstarter.timer.Timer;

import PCS.CollectorHandler.CollectorHandler;
import PCS.CollectorHandler.Emulator.CollectorEmulator;
import PCS.PCSCore.PCSCore;
import PCS.GateHandler.GateHandler;
import PCS.GateHandler.Emulator.GateEmulator;

import PCS.PayMachineHandler.Emulator.PayMachineEmulator;
import PCS.PayMachineHandler.PayMachineHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

//======================================================================
// PCSEmulatorStarter
public class PCSEmulatorStarter extends PCSStarter {
    //------------------------------------------------------------
    // main
    public static void main(String [] args) {
	new PCSEmulatorStarter().startApp();
    } // main


    //------------------------------------------------------------
    // startHandlers
    @Override
    protected void startHandlers() {
        Emulators.pcsEmulatorStarter = this;
        new Emulators().start();
    } // startHandlers


    //------------------------------------------------------------
    // Emulators
    public static class Emulators extends Application {
        private static PCSEmulatorStarter pcsEmulatorStarter;

	//----------------------------------------
	// start
        public void start() {
            launch();
	} // start

	//----------------------------------------
	// start
        public void start(Stage primaryStage) {
	    Timer timer = null;
	    PCSCore pcsCore = null;
	    GateEmulator entranceGateEmulator = null;
        GateEmulator exitGateEmulator = null;
        CollectorEmulator collectorEmulator=null;
	    PayMachineEmulator payMachineEmulator = null;
	    // create emulators
	    try {
	        timer = new Timer("timer", pcsEmulatorStarter);
	        pcsCore = new PCSCore("PCSCore", pcsEmulatorStarter);
            collectorEmulator=new CollectorEmulator("CollectorHandler",pcsEmulatorStarter);
	        entranceGateEmulator = new GateEmulator("EntranceGateHandler", pcsEmulatorStarter);
            exitGateEmulator = new GateEmulator("ExitGateHandler", pcsEmulatorStarter);
            payMachineEmulator = new PayMachineEmulator("PayMachineHandler",pcsEmulatorStarter);

            // start emulator GUIs
		entranceGateEmulator.start();
		exitGateEmulator.start();
		collectorEmulator.start();
		payMachineEmulator.start();
	    } catch (Exception e) {
		System.out.println("Emulators: start failed");
		e.printStackTrace();
		Platform.exit();
	    }
	    pcsEmulatorStarter.setTimer(timer);
	    pcsEmulatorStarter.setPCSCore(pcsCore);
	    pcsEmulatorStarter.setGateHandler(entranceGateEmulator);
	    pcsEmulatorStarter.setGateHandler(exitGateEmulator);
	    pcsEmulatorStarter.setCollectorHandler(collectorEmulator);
	    pcsEmulatorStarter.setPayMachineHandler(payMachineEmulator);

	    // start threads
	    new Thread(timer).start();
	    new Thread(pcsCore).start();
	    new Thread(entranceGateEmulator).start();
	    new Thread(exitGateEmulator).start();
	    new Thread(collectorEmulator).start();
	    new Thread(payMachineEmulator).start();
	} // start
    } // Emulators


    //------------------------------------------------------------
    //  setters
    private void setTimer(Timer timer) {
        this.timer = timer;
    }
    private void setPCSCore(PCSCore pcsCore) {
        this.pcsCore = pcsCore;
    }
    private void setGateHandler(GateHandler gateHandler) {
	this.gateHandler = gateHandler;
    }
    private void setCollectorHandler(CollectorHandler collectorHandler){
        this.collectorHandler=collectorHandler;
    }
    private void setPayMachineHandler(PayMachineHandler payMachineHandler){
        this.payHandler=payMachineHandler;
    }
} // PCSEmulatorStarter
