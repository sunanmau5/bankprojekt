package verarbeitung;

/**
 * testet die M�glichkeiten einer Enum aus.
 * 
 * @author Dorothea Hubrich
 *
 */
public class EnumTest {

	/**
	 * gibt alle Eintr�ge der Enum Kontoart aus
	 * 
	 * @param args wird nicht verwendet
	 */
	public static void main(String[] args) {
		// Man kann Variablen von der Enum deklarieren - sie ist
		// ein Datentyp
		Kontoart k;

		// k = new Kontoart(); geht nicht!!!
		// man greift auf die Konstane �ber den Namen der Enum zu, genauso
		// wie auf andere statische Elemente in Klassen
		k = Kontoart.FESTGELDKONTO;

		// F�r jede Enum gibt es die statische Methode valueOf, die die
		// zum Namen geh�rende Konstante liefert. Wenn man sich beim Namen
		// verschreibt, wirft sie eine IllegalArgumentException.
		k = Kontoart.valueOf("GIROKONTO");

		// Jede Enum-Konstante hat automatisch die beiden Methoden
		// name() und ordinal. Sie liefern den Namen der Konstanten (hier
		// also "GIROKONTO" und die Nummer der Konstanten (hier 0).
		// intern nummeriert Java die Konstanten also durch, obwohl es
		// sich tats�chlich um Objekte handelt und nicht einfach um int-Zahlen
		// wie in anderen Programmiersprachen
		// name() und ordinal() sind in der Enum nicht �berschreibbar.
		System.out.println(k.name() + " " + k.ordinal());

		// Jede Enum hat automatisch die Methode values(), die ein
		// Array aller enthaltenen Konstanten liefert. Das ist praktisch, wenn
		// man mit allen etwas machen will. Hier z.B. kann man eine
		// Werbebrosch�re mit allen angebotenen Kontoarten schreiben.
		Kontoart[] alle = Kontoart.values();
		for (int i = 0; i < alle.length; i++) {
			// Wie bei allen anderen Objekten auch kann man auf die
			// Methoden der Konstanten zugreifen.
			// toString ist nat�rlich �berschreibbar, in der Standard-
			// implementierung liefert sie nur den Konstantennamen
			System.out.println(alle[i] + alle[i].getWerbebotschaft());
		}

	}

}
