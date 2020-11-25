package verarbeitung;

/**
 * Ein Girokonto
 * 
 * @author Doro
 *
 */
public class Girokonto extends Konto implements Ueberweisungsfaehig {
	// Die Klasse Girokonto erbt von Konto, �bernimmt also
	// alle Attribute und Methoden von Konto. Sie kann
	// ihrerseits weitere Attribute und Methoden definieren oder
	// vorhandene Methoden �berschreiben. Objekte der Klasse
	// Girokonto sind zuweisungskompatibel zu Konto, d�rfen also
	// �berall eingesetzt werden, wo ein Konto-Objekt erwartet
	// wird.
	// Syntaxhinweis: erst erben, dann Interfaces implementieren,
	// nicht andersherum

	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getInstance();

	/**
	 * Wert, bis zu dem das Konto �berzogen werden darf
	 */
	private double dispo;

	/**
	 * erzeugt ein leeres, nicht gesperrtes Standard-Girokonto von Herrn MUSTERMANN
	 */
	public Girokonto() {
		super(Kunde.MUSTERMANN, 99887766);
		// Aufruf des Konstruktors der Oberklasse. Diese Zeile
		// darf nur als erste Anweisung in einem Konstruktor stehen.
		// Schreibt man selbst keinen super-Konstruktoraufruf, wird
		// automatisch der Oberklassenkonstruktor ohne Parameter aufgerufen
		// -> Compilerfehler, wenn es keinen gibt
		this.dispo = 500;
	}

	/**
	 * erzeugt ein Girokonto mit den angegebenen Werten
	 * 
	 * @param inhaber Kontoinhaber
	 * @param nummer  Kontonummer
	 * @param dispo   Dispo
	 * @throws IllegalArgumentException wenn der inhaber null ist oder der
	 *                                  angegebene dispo negativ bzw. NaN ist
	 */
	public Girokonto(Kunde inhaber, long nummer, double dispo) {
		super(inhaber, nummer);
		try {
			if (dispo < 0 || Double.isNaN(dispo))
				throw new IllegalArgumentException("Der Dispo ist nicht gueltig!");
		} catch (IllegalArgumentException e) {
			logger.logException(e.toString());
			throw e;
		}
		this.dispo = dispo;
	}

	/**
	 * liefert den Dispo
	 * 
	 * @return Dispo von this
	 */
	public double getDispo() {
		return dispo;
	}

	/**
	 * setzt den Dispo neu
	 * 
	 * @param dispo muss gr��er sein als 0
	 * @throws IllegalArgumentException wenn dispo negativ bzw. NaN ist
	 */
	public void setDispo(double dispo) {
		try {
			if (dispo < 0 || Double.isNaN(dispo))
				throw new IllegalArgumentException("Der Dispo ist nicht gueltig!");
		} catch (IllegalArgumentException e) {
			logger.logException(e.toString());
			throw e;
		}
		this.firePropertyChange("Dispo", this.dispo, dispo);
		this.dispo = dispo;
	}

	@Override
	public boolean ueberweisungAbsenden(double betrag, String empfaenger, long nachKontonr, long nachBlz,
			String verwendungszweck) throws GesperrtException, IllegalArgumentException {
		try {
			if (this.isGesperrt())
				throw new GesperrtException(this.getKontonummer());
			if (betrag < 0 || Double.isNaN(betrag) || empfaenger == null || verwendungszweck == null)
				throw new IllegalArgumentException("Parameter fehlerhaft");
		} catch (Exception e) {
			logger.logException(e.toString());
			throw e;
		}
		if (getKontostand() - betrag >= -dispo) {
			setKontostand(getKontostand() - betrag);
			return true;
		} else
			return false;
	}

	@Override
	public void ueberweisungEmpfangen(double betrag, String vonName, long vonKontonr, long vonBlz,
			String verwendungszweck) throws GesperrtException, IllegalArgumentException {
		try {
			if (betrag < 0 || Double.isNaN(betrag) || vonName == null || verwendungszweck == null)
				throw new IllegalArgumentException("Parameter fehlerhaft");
		} catch (IllegalArgumentException e) {
			logger.logException(e.toString());
			throw e;
		}
		setKontostand(getKontostand() + betrag);
	}

	@Override
	public String toString() {
		String ausgabe = "-- GIROKONTO --" + System.lineSeparator() + super.toString() + "Dispo: "
				+ this.getDispoFormatiert() + System.lineSeparator();
		return ausgabe;
		// mit super.toString() kann die geerbte und hier eigentlich
		// �berschriebene Implementierung der Methode toString()
		// aufgerufen werden.
	}

	/**
	 * liefert den ordentlich formatierten Dispo
	 * 
	 * @return formatierter Dispo mit 2 Nachkommastellen und Waehrungssymbol
	 */
	public String getDispoFormatiert() {
		return String.format("%5.2f %s", this.getDispo(), this.getAktuelleWaehrung());
	}

	@Override
	public boolean pruefeAbheben(double betrag) {
		// Hier wird die in Konto abstrakte Methode abheben �berschrieben.
		// Ohne diese Implementierung w�re Girokonto selbst abstrakt.
		if (getKontostand() - betrag >= -dispo) {
			return true;
		}
		return false;
	}

	@Override
	public void spezifischeUmrechnungen(Waehrung neu) {
		dispo = Waehrung.waehrungZuWaehrung(dispo, this.getAktuelleWaehrung(), neu);
	}

}
