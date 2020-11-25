package verarbeitung;

/**
 * Eine Fabrik, die Sparbuch erstellt
 * 
 * @author Sunan Regi Maunakea
 *
 */
public class Sparbuchfabrik extends Kontofabrik {

	public Konto erzeugen(Kunde inhaber, long nummer) {
		Konto neu = new Sparbuch(inhaber, nummer);
		return neu;
	}
}
