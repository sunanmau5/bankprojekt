package verarbeitung;

import java.time.LocalDate;

/**
 * ein Sparbuch
 * 
 * @author Doro
 *
 */
public class Sparbuch extends Konto {

	private static final long serialVersionUID = 1L;

	/**
	 * Zinssatz, mit dem das Sparbuch verzinst wird. 0,03 entspricht 3%
	 */
	private double zinssatz;

	/**
	 * Monatlich erlaubter Gesamtbetrag f�r Abhebungen
	 */
	public static final double ABHEBESUMME = 2000;

	/**
	 * Betrag, der im aktuellen Monat bereits abgehoben wurde
	 */
	private double bereitsAbgehoben = 0;

	/**
	 * Monat und Jahr der letzten Abhebung
	 */
	private LocalDate zeitpunkt = LocalDate.now();

	/**
	 * ein Standard-Sparbuch
	 */
	public Sparbuch() {
		zinssatz = 0.03;
	}

	/**
	 * ein Standard-Sparbuch, das inhaber geh�rt und die angegebene Kontonummer hat
	 * 
	 * @param inhaber     der Kontoinhaber
	 * @param kontonummer die Wunsch-Kontonummer
	 * @throws IllegalArgumentException wenn inhaber null ist
	 */
	public Sparbuch(Kunde inhaber, long kontonummer) {
		super(inhaber, kontonummer);
		zinssatz = 0.03;
	}

	@Override
	public String toString() {
		String ausgabe = "-- SPARBUCH --" + System.lineSeparator() + super.toString() + "Zinssatz: "
				+ this.zinssatz * 100 + "%" + System.lineSeparator();
		return ausgabe;
	}

	@Override
	public boolean pruefeAbheben(double betrag) {
		LocalDate heute = LocalDate.now();
		if (heute.getMonth() != zeitpunkt.getMonth() || heute.getYear() != zeitpunkt.getYear()) {
			this.bereitsAbgehoben = 0;
		}
		if (getKontostand() - betrag >= 0.50
				&& bereitsAbgehoben + betrag <= getAktuelleWaehrung().euroInWaehrungUmrechnen(Sparbuch.ABHEBESUMME)) {
			bereitsAbgehoben += betrag;
			this.zeitpunkt = LocalDate.now();
			return true;
		} else
			return false;
	}

	@Override
	public void spezifischeUmrechnungen(Waehrung neu) {
		bereitsAbgehoben = Waehrung.waehrungZuWaehrung(bereitsAbgehoben, this.getAktuelleWaehrung(), neu);
	}

}
