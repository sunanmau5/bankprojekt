package verarbeitung;

/**
 * An Enum of currencies
 * 
 * @author Sunan Regi Maunakea
 *
 */
public enum Waehrung {
	/**
	 * Stand for European Union Euro
	 */
	EUR(1.0),

	/**
	 * Stands for Bulgarische Leva
	 */
	BGN(1.95583),

	/**
	 * Stands for Litauische Litas
	 */
	LTL(3.4528),

	/**
	 * Stands for Konvertible Mark
	 */
	KM(1.95583);

	/**
	 * The conversion rate to Euro
	 */
	private double umrechnungKurs;

	/**
	 * Contructor with given parameter
	 * 
	 * @param kurs Conversion rate to Euro
	 */
	Waehrung(double kurs) {
		this.umrechnungKurs = kurs;
	}

	/**
	 * Convert the given amount in Euro to each currencies
	 * 
	 * @param betrag the amount in Euro
	 * @return the converted amount
	 */
	public double euroInWaehrungUmrechnen(double betrag) {
		return this.umrechnungKurs * betrag;
	}

	/**
	 * Convert the given amount in this-currency to Euro
	 * 
	 * @param betrag the amount in other currency
	 * @return the converted amount
	 */
	public double waehrungInEuroUmrechnen(double betrag) {
		return betrag / this.umrechnungKurs;
	}

	/**
	 * Einen Betrag in eine beliebige Waehrung umzurechnen. Mach die beiden Methoden
	 * zwar obsolet, sie werden jedoch von der Aufgabe gefordert.
	 * 
	 * @param betrag der umgerechnet werden soll
	 * @param von    Waehrung von der umgerechnet werden soll
	 * @param zu     Waehrung in die umgerechnet werden soll
	 * @return das Ergebnis
	 */
	public static double waehrungZuWaehrung(double betrag, Waehrung von, Waehrung zu) {
		return zu.euroInWaehrungUmrechnen(von.waehrungInEuroUmrechnen(betrag));
	}

}
