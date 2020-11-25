package oberflaeche;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import verarbeitung.GesperrtException;
import verarbeitung.Girokonto;

public class KontoController2 extends Application {

	@FXML
	private Girokonto gk;
	@FXML
	private CheckBox gesperrt;
	@FXML
	private Label stand;
	@FXML
	private TextArea adresse;
	@FXML
	private Button einzahlen, abheben;
	@FXML
	private TextField betrag;

	private Stage stage;

	@FXML
	public void initialize() {
		stand.textProperty().bind(gk.kontostandProperty().asString("%+.2f"));
		gk.minusProperty().addListener(o -> formatieren());
		gesperrt.selectedProperty().bindBidirectional(gk.gesperrtProperty());
		adresse.textProperty().bindBidirectional(gk.getInhaber().adresseProperty());
		einzahlen.setOnAction(o -> {
			try {
				einzahlen(Double.parseDouble(betrag.getText()));
			} catch (NumberFormatException e) {
				meldungZeigen("Buchstaben statt Zahlen");
			}
		});
		abheben.setOnAction(o -> {
			try {
				abheben(Double.parseDouble(betrag.getText()));
			} catch (NumberFormatException e) {
				meldungZeigen("Buchstaben statt Zahlen");
			}
		});
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../kontoEinstellung.fxml"));
		loader.setController(this);
		Parent lc = loader.load();
		Scene scene = new Scene(lc, 600, 400);
		stage.setTitle("Kontoverwaltung");
		stage.setScene(scene);
		stage.show();
	}

	private void formatieren() {
		if (gk.minusProperty().get()) {
			stand.setTextFill(Color.BLACK);
		} else {
			stand.setTextFill(Color.RED);
		}
	}

	private void meldungZeigen(String text) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Meldung");
		alert.setHeaderText(text);
		alert.showAndWait();
	}

	/**
	 * Ruft einzahlen methode des Kontos auf
	 * 
	 * @param betrag
	 * @throws IllegalArgumentException
	 */
	protected void einzahlen(double betrag) throws IllegalArgumentException {
		try {
			gk.einzahlen(betrag);
		} catch (IllegalArgumentException e) {
			meldungZeigen("Betrag war negativ");
			return;
		}
		meldungZeigen("Einzahlung erfolgreich");
	}

	/**
	 * Ruft abheben methode des Kontos auf
	 * 
	 * @param betrag
	 * @throws IllegalArgumentException
	 */
	protected void abheben(double betrag) {
		try {
			if (gk.abheben(betrag))
				meldungZeigen("Abhebung erfolgreich");
			else
				meldungZeigen("Kontostand zu gering");
		} catch (IllegalArgumentException | GesperrtException e) {
			meldungZeigen("Exception beim Abheben");
		}
	}
}
