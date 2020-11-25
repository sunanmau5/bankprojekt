package oberflaeche;

import verarbeitung.GesperrtException;
import verarbeitung.Girokonto;
import verarbeitung.Konto;
import verarbeitung.Kunde;

import java.time.LocalDate;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class KontoController1 extends Application {

	private Kunde me = new Kunde("Johnny", "Goode", "Hardenbergstrasse", LocalDate.parse("1987-06-28"));
	private long nummer = 1704591000L;

	/**
	 * Das aktuelle Girokonto
	 */
	private Konto k = new Girokonto(me, nummer, 1000);

	/**
	 * Das Hauptfenster der Anwendung
	 */
	private Stage stage;
	private KontoOberflaeche view;

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		view = new KontoOberflaeche(k, this);
		Scene scene = new Scene(view, 400, 300);
		stage.setTitle("Konto Einstellung");
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Ruft einzahlen methode des Kontos auf
	 * 
	 * @param betrag
	 * @throws IllegalArgumentException
	 */
	protected void einzahlen(double betrag) throws IllegalArgumentException {
		try {
			k.einzahlen(betrag);
		} catch (IllegalArgumentException e) {
			view.meldungZeigen("Betrag war negativ");
			return;
		}
		view.meldungZeigen("Einzahlung erfolgreich");
	}

	/**
	 * Ruft abheben methode des Kontos auf
	 * 
	 * @param betrag
	 * @throws IllegalArgumentException
	 */
	protected void abheben(double betrag) {
		try {
			if (k.abheben(betrag))
				view.meldungZeigen("Abhebung erfolgreich");
			else
				view.meldungZeigen("Kontostand zu gering");
		} catch (IllegalArgumentException | GesperrtException e) {
			view.meldungZeigen("Exception beim Abheben");
		}
	}

}
