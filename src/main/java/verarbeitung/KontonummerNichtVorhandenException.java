package verarbeitung;

/**
 * tritt bei einem Zugriff auf eine nicht vorhandene Kontonummer
 * 
 * @author Sunan Regi Maunakea
 *
 */
@SuppressWarnings("serial")
public class KontonummerNichtVorhandenException extends Exception {

	/**
	 * Zugriff auf eine nicht vorhandene Kontonummer
	 * 
	 * @param kontonummer die nicht vorhandene Kontonummer
	 */
	public KontonummerNichtVorhandenException(long kontonummer) {
		super("Zugriff auf nicht vorhandene Kontonummer " + kontonummer);
	}
}
