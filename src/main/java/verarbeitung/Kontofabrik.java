package verarbeitung;

/**
 * Eine allgemeine Kontofabrik, wird von Kontotypenfabrik vererbt, wie
 * Girokontofabrik, Sparbuchfabrik, usw.
 * 
 * @author Sunan Regi Maunakea
 *
 */
public abstract class Kontofabrik {

	/**
	 * Erzeugt ein neues Konto
	 * @param inhaber Inhaber des Kontos
	 * @param nummer die zu vergebene Kontonummer des neuen Kontos
	 * @return das neue Konto
	 * @throws IllegalArgumentException, wenn inhaber null ist
	 */
	public abstract Konto erzeugen(Kunde inhaber, long nummer);
}
