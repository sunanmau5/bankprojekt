package verarbeitung;

/**
 * Eine Sammlung aller angebotenen Kontoarten
 * 
 * Eine enum ist eine abgeschlossene Sammlung von vordefinierten Konstanten.
 * Wichtig ist, dass man davon ausgehen kann, dass sich die Menge der Konstanten
 * in Zukunft nicht ver�ndern wird (das Beispiel Kontoart ist also nicht so
 * treffend gew�hlt). Au�erdem sollten es nicht zu viele Konstanten sein, sonst
 * wird das un�bersichtlich. Gute Beispiel sind die Wochentage, die Monate oder
 * die Bunderl�nder in Deutschland. Ja, letztere haben sich vor 30 Jahren
 * ver�ndert. Es ist allerdings davon auszugehen, dass das f�r die Lebensdauer
 * der jetzigen Programme wohl nicht noch mal vorkommt. Programmierer k�nnen
 * auch nur begrenzt hellsehen...
 * 
 * @author Dorothea Hubrich
 *
 */
public enum Kontoart {
	// statt class steht hier enum
	// wichtig: Als Erstes m�ssen in der Enum die Konstanten definiert werden.
	// Alles andere kommt danach (sonst recht kryptische Fehlermeldung).

	// Im einfachsten Fall schreibt man nur die Namen der Konstanten hin,
	// die man haben will: GIROKONTO, SPARBUCH, FESTGELDKONTO;
	// Bei den Konstanten handelt es sich um Objekte vom Typ Kontoart.
	// Die enum ist also eine Klasse.
	// Da es hier einen Konstruktor gibt, der einen String-Parameter
	// erwartet, wird der Wert f�r den Parameter jeweils in Klammern
	// hinter den Konstantennamen geschrieben.
	/**
	 * steht f�r ein Girokonto
	 */
	GIROKONTO("voll gro�er Dispo"),
	// In Wirklichkeit ist das ein sehr verk�rzter Konstruktoraufruf.
	// Der Compiler macht daraus in etwa das:
	// public static final Kontoart GIROKONTO = new Kontoart("voll gro�er Dispo");
	// Sie sehen: Die Konstanten sind statisch, konstant, Objekte und haben
	// einen genau definierten Datentyp
	// Namenskonvention: statisch, final = komplett in Gro�buchstaben
	/**
	 * ein Sparbuch
	 */
	SPARBUCH("viele Zinsen"),
	/**
	 * ein Festgeldkonto, was wir hier aber gar nicht haben...
	 */
	FESTGELDKONTO("gibts eh nich");

	private String werbebotschaft;
	// Wie in jeder anderen Klasse kann es in einer Enum Attribute geben.
	// Das bedeutet, jede einzelne Konstante hat ihre eigene werbebotschaft.

	// Wie jede andere Klasse kann auch die Enum einen (oder mehrere)
	// Konstruktoren haben. Im Unterschied zu anderen Klassen ist dieser
	// aber automatisch private - auch dann, wenn Sie nichts hinschreiben.
	// Wenn Sie versuchen, dem Konstruktor eine andere Sichtbarkeit zu geben,
	// f�hrt das zu einer Fehlermeldung des Compilers.
	// Warum? Damit niemand von au�erhalb der Enum neue Objekte erzeugen kann.
	// Die Enum soll eine abgeschlossene Sammlung bilden, die nicht weiter
	// erg�nzt werden kann.
	Kontoart(String werbung) {
		this.werbebotschaft = werbung;
	}

	// Genau wie jede andere Klasse auch, kann die Enum Methoden haben.
	// Sinnvollerweise arbeiten die Methoden auch bei einer Enum mit
	// this, um auf die Attribute des jeweiligen Objektes (also der
	// gerade aktuellen Konstanten) zuzugreifen.
	public String getWerbebotschaft() {
		return this.werbebotschaft;
		// Achtung: Leider sehr beliebt, aber viel komplizierter
		// und unn�tig:
//		switch (this) {
//		case GIROKONTO: return "voll gro�er Dispo";
//		case SPARBUCH: return "viele Zinsen";
//		case FESTGELDKONTO: return "gibts eh nich";
//		}

	}
}
