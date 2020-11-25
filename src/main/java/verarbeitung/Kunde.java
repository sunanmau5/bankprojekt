package verarbeitung;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Kunde einer Bank
 * 
 * @author Dorothea Hubrich
 * @version 1.0
 */
public class Kunde implements Comparable<Kunde>, Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getInstance();

	/**
	 * Ein Musterkunde
	 */
	public static final Kunde MUSTERMANN = new Kunde("Max", "Mustermann", "zuhause", LocalDate.now());

	/**
	 * englische oder deutsche Anrede, je nach den Systemeinstellungen
	 */
	private static String ANREDE;
	// Noch ein statisches Attribut. Es wird allerdings nicht hier
	// initialisiert, sondern im statischen Block unten.

	/**
	 * liefert die systemspezifische Anrede
	 * 
	 * @return systemspezifische Anrede
	 */
	public static String getANREDE() {
		return ANREDE;
	}

	/**
	 * der Vorname
	 */
	private String vorname;

	/**
	 * Der Nachname
	 */
	private String nachname;

	/**
	 * Die Adresse als Property
	 */
	private StringProperty adresse;

	public StringProperty adresseProperty() {
		return this.adresse;
	}

	/**
	 * Geburtstag
	 */
	private LocalDate geburtstag;

	/**
	 * erzeugt einen Standardkunden
	 */
	public Kunde() {
		this("Max", "Mustermann", "Adresse", LocalDate.now());
	}

	/**
	 * Erzeugt einen Kunden mit den �bergebenen Werten
	 * 
	 * @param vorname  Vorname
	 * @param nachname Nachname
	 * @param adresse  Adresse
	 * @param gebdat   Geburtstag
	 * @throws IllegalArgumentException wenn einer der Parameter null ist
	 */
	public Kunde(String vorname, String nachname, String adresse, LocalDate gebdat) {
		try {
			if (vorname == null || nachname == null || adresse == null || gebdat == null)
				throw new IllegalArgumentException("Null als Parameter nicht erlaubt");
		} catch (IllegalArgumentException e) {
			logger.logException(e.toString());
			throw e;
		}
		this.vorname = vorname;
		this.nachname = nachname;
		this.adresse = new SimpleStringProperty(adresse);
		this.geburtstag = gebdat;

		// Die folgenden Zeilen sorgen daf�r, dass beim Programmende
		// (ShutDown) Code ausgef�hrt wird. Dazu braucht man ein
		// Objekt der Klasse Thread. Diesem Thread-Objekt wird der auszuf�hrende
		// Code mit Hilfe eines Objektes �bergeben, das das Interface
		// Runnable implementiert - da haben wir also noch eine
		// Anwendung von Interfaces zur Weitergabe von Code-St�ckchen.
		// Die Klasse Zerstoerer, die das Interface implementiert,
		// findet sich ganz unten in dieser Datei.
		Zerstoerer z = new Zerstoerer();
		Thread t = new Thread(z);
		Runtime.getRuntime().addShutdownHook(t);
	}

	/**
	 * Erzeugt einen Kunden mit den �bergebenen Werten
	 * 
	 * @param vorname  Vorname
	 * @param nachname Nachname
	 * @param adresse  Adresse
	 * @param gebdat   Geburtstag im Format tt.mm.yy
	 * @throws DateTimeParseException   wenn das Format des �bergebenen Datums nicht
	 *                                  korrekt ist
	 * @throws IllegalArgumentException wenn einer der Parameter null ist
	 */
	public Kunde(String vorname, String nachname, String adresse, String gebdat) {
		this(vorname, nachname, adresse, LocalDate.parse(gebdat, DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
	}

	/**
	 * gibt alle Daten des Kunden aus
	 */
	@Override
	public String toString() {
		String ausgabe;
		DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
		ausgabe = this.vorname + " " + this.nachname + System.getProperty("line.separator");
		ausgabe += this.adresse + System.getProperty("line.separator");
		ausgabe += df.format(this.geburtstag) + System.getProperty("line.separator");
		return ausgabe;
	}

	/**
	 * vollst�ndiger Name des Kunden in der Form "Nachname, Vorname"
	 * 
	 * @return vollst�ndiger Name des Kunden
	 */
	public String getName() {
		return this.nachname + ", " + this.vorname;
	}

	/**
	 * Adresse des Kunden
	 * 
	 * @return Adresse des Kunden
	 */
	public String getAdresse() {
		return adresse.get();
	}

	/**
	 * setzt die Adresse auf den angegebenen Wert
	 * 
	 * @param adresse neue Adresse
	 * @throws IllegalArgumentException wenn adresse null ist
	 */
	public void setAdresse(String adresse) {
		try {
			if (adresse == null)
				throw new IllegalArgumentException("Adresse darf nicht null sein");
		} catch (IllegalArgumentException e) {
			logger.logException(e.toString());
			throw e;
		}
		this.adresse.set(adresse);
	}

	/**
	 * Nachname des Kunden
	 * 
	 * @return Nachname des Kunden
	 */
	public String getNachname() {
		return nachname;
	}

	/**
	 * setzt den Nachnamen auf den angegebenen Wert
	 * 
	 * @param nachname neuer Nachname
	 * @throws IllegalArgumentException wenn nachname null ist
	 */
	public void setNachname(String nachname) {
		try {
			if (nachname == null)
				throw new IllegalArgumentException("Nachname darf nicht null sein");
		} catch (IllegalArgumentException e) {
			logger.logException(e.toString());
			throw e;
		}
		this.nachname = nachname;
	}

	/**
	 * Vorname des Kunden
	 * 
	 * @return Vorname des Kunden
	 */
	public String getVorname() {
		return vorname;
	}

	/**
	 * setzt den Vornamen auf den angegebenen Wert
	 * 
	 * @param vorname neuer Vorname
	 * @throws IllegalArgumentException wenn vorname null ist
	 */
	public void setVorname(String vorname) {
		try {
			if (vorname == null)
				throw new IllegalArgumentException("Vorname darf nicht null sein");
		} catch (IllegalArgumentException e) {
			logger.logException(e.toString());
			throw e;
		}
		this.vorname = vorname;
	}

	/**
	 * Geburtstag des Kunden
	 * 
	 * @return Geburtstag des Kunden
	 */
	public LocalDate getGeburtstag() {
		return geburtstag;
	}

	@Override
	public int compareTo(Kunde arg0) {
		return this.getName().compareTo(arg0.getName());
	}

	// statischer Block oder statischer Initialisierer. Dieser
	// Code wird "irgendwann" ausgef�hrt, auf jeden Fall aber, bevor
	// zum ersten Mal auf die Klasse Kunde zugegriffen wird. Hier
	// sollte man also nichts unterbringen, bei dem einem der Zeitpunkt
	// der Ausf�hrung wirklich wichtig ist.
	static {
		if (Locale.getDefault().getCountry().equals("DE"))
			ANREDE = "Hallo Benutzer!";
		else
			ANREDE = "Dear Customer!";
	}

	/**
	 * der Destruktor der Klasse. Er wird "irgendwann" aufgerufen, wenn der Garbage
	 * Collector ein Objekt aus dem Speicher entfernt. Wann dies genau sein wird und
	 * ob es �berhaupt in einem Programm geschieht, ist unvorhersagbar. Deshalb wird
	 * die Verwendung der finalize-Methode nicht (mehr) empfohlen. Stattdessen kann
	 * die folgende Klasse verwendet werden.
	 */
	protected void finalize() {
		// Hier Aufr�umarbeiten erledigen, z.B. Datenbankverbindung schlie�en!!!
		System.out.println("geschafft, ist zu");
		// Diese Ausgabe werden Sie wohl in diesem kleinen Programm
		// nie zu Gesicht bekommen, weil zu wenig Speicher
		// gebraucht wird, als dass der Garbage Collector arbeiten m�sste.
	}

	/**
	 * Klasse f�r Aufr�umarbeiten
	 * 
	 * Hier macht sie nur eine Ausgabe, weil in diesem Beispiel einfach keine
	 * Aufr�umarbeiten zu erledigen sind. Es w�re hier aber m�glich, offene
	 * Verbindungen zu Datenbanken/Dateien/Netzwerken zu schlie�en oder auch die
	 * Daten des Objektes noch irgendwo zu speichern.
	 * 
	 * Diese Klasse soll Sie an die verwendung von Interfaces erinnern. Der
	 * Konstruktor der Klasse Thread erwartet n�mlich ein Objekt, das das Interface
	 * Runnable implementiert. Also tut diese Klasse das.
	 * 
	 * Ja, es gibt private innere Klassen, also Klassen, die innerhalb von anderen
	 * Klassen definiert sind. Wir werden uns hier nicht ausf�hrlich damit
	 * besch�ftigen. Wichtig ist: Man hat in der inneren Klasse Zugriff auf alle
	 * Attribute und Methoden der �u�eren Klasse, auch auf die privaten.
	 * 
	 * @author Doro
	 *
	 */
	private class Zerstoerer implements Runnable {
		/**
		 * In der Methode run steht, was passieren soll, wenn das aktuelle Programm
		 * beendet wird.
		 */
		@Override
		public void run() {
			System.out.println("Kunde " + getName() + " zerst�rt");
			// Hier nur eine Ausgabe, damit Sie die Arbeit des Zerst�rers
			// sehen. Sonst werden hier Aufr�umarbeiten erledigt.
		}
	}

	public int speichern() {
		return 0;
	}

}