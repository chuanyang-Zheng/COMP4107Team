package PCS.CollectorHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import PCS.PCSStarter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class CollectorEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private CollectorEmulator collectorEmulator;
    private MBox collectorMBox;
    public TextArea collectorTextArea;
    public TextArea collectorTextAreaInput;

    private int lineNo = 0;

    public void initialize(String id, AppKickstarter appKickstarter, Logger log, CollectorEmulator collectorEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.collectorEmulator =collectorEmulator ;
        this.collectorMBox = appKickstarter.getThread("CollectorHandler").getMBox();
    } // initialize

    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            case "Collector Valid Request":
                collectorMBox.send(new Msg(id, null, Msg.Type.CollectorValidRequest,collectorTextAreaInput.getText()));
                collectorTextAreaInput.setText("");
                break;

            case "Collector Positive":
                collectorMBox.send(new Msg(id, null, Msg.Type.CollectorPositive, "Collector Positive"));
                break;

            case "Collector Negative":
                collectorMBox.send(new Msg(id, null, Msg.Type.CollectorNegative, "Collector Negative"));
                break;

            case "Collector Solve Problem":
                collectorMBox.send(new Msg(id, null, Msg.Type.CollectorSolveProblem, "Collector Solve Problem"));
                break;

            case "Poll Request":
                appendTextArea("Send poll request.");
                collectorMBox.send(new Msg(id, null, Msg.Type.Poll, ""));
                break;

            case "Poll ACK":
                appendTextArea("Send poll ack.");
                collectorMBox.send(new Msg(id, null, Msg.Type.PollAck, ""));
                break;


            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed

    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
        Platform.runLater(() -> collectorTextArea.appendText(String.format("[%04d] %s\n", ++lineNo, status)));
    } // appendTextArea

}
