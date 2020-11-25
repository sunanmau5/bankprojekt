package verarbeitung;

/**
 * Eine Fabrik, die Girokonto erstellt
 * 
 * @author Sunan Regi Maunakea
 *
 */
public class Girokontofabrik extends Kontofabrik {

	public Konto erzeugen(Kunde inhaber, long nummer) {
		Konto neu = new Girokonto(inhaber, nummer, 2000);
		return neu;
	}
}
