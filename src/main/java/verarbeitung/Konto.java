package verarbeitung;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * stellt ein allgemeines Konto dar
 */
public abstract class Konto implements Comparable<Konto>, Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getInstance();

	private transient PropertyChangeSupport prop = new PropertyChangeSupport(this);

	/**
	 * der Kontoinhaber
	 */
	private Kunde inhaber;

	/**
	 * die Kontonummer
	 */
	private final long nummer;

	/**
	 * die Waehrung
	 */
	private Waehrung waehrung = Waehrung.EUR;

	/**
	 * Setzt die beiden Eigenschaften kontoinhaber und kontonummer auf die
	 * angegebenen Werte, der anf�ngliche Kontostand wird auf 0 gesetzt.
	 *
	 * @param inhaber     der Inhaber
	 * @param kontonummer die gew�nschte Kontonummer
	 * @throws IllegalArgumentException wenn der Inhaber null
	 */
	public Konto(Kunde inhaber, long kontonummer) {
		try {
			if (inhaber == null)
				throw new IllegalArgumentException("Inhaber darf nicht null sein!");
		} catch (IllegalArgumentException e) {
			logger.logException(e.toString());
			throw e;
		}
		this.inhaber = inhaber;
		this.nummer = kontonummer;
		this.kontostand.set(0.0);
		this.gesperrt.set(false);
	}

	/**
	 * setzt alle Eigenschaften des Kontos auf Standardwerte
	 */
	public Konto() {
		this(Kunde.MUSTERMANN, 1234567);
	}

	/**
	 * der aktuelle Kontostand
	 */
	private ReadOnlyDoubleWrapper kontostand = new ReadOnlyDoubleWrapper();

	/**
	 * liefert den aktuellen Kontostand
	 * 
	 * @return double
	 */
	public double getKontostand() {
		return kontostand.get();
	}

	/**
	 * setzt den aktuellen Kontostand
	 * 
	 * @param kontostand neuer Kontostand
	 */
	protected void setKontostand(double kontostand) {
		double alt = this.kontostand.get();
		this.kontostand.set(kontostand);
		prop.firePropertyChange("Kontostand", alt, kontostand);
		if (this.getKontostand() < 0)
			this.minus.set(false);
		else
			this.minus.set(true);
	}

	/**
	 * Kontostand soll nicht von aussen veraenderbar sein
	 * 
	 * @return kontostand property
	 */
	public ReadOnlyDoubleProperty kontostandProperty() {
		return this.kontostand.getReadOnlyProperty();
	}

	/**
	 * Wenn das Konto gesperrt ist (gesperrt = true), k�nnen keine Aktionen daran
	 * mehr vorgenommen werden, die zum Schaden des Kontoinhabers w�ren (abheben,
	 * Inhaberwechsel)
	 */
	private BooleanProperty gesperrt = new SimpleBooleanProperty(false);

	public BooleanProperty gesperrtProperty() {
		return this.gesperrt;
	}

	/**
	 * eine boolesche Property, die anzeigt, ob der Kontostand im Plus oder im Minus
	 * ist
	 */
	private ReadOnlyBooleanWrapper minus = new ReadOnlyBooleanWrapper(false);

	public boolean isMinus() {
		return minus.get();
	}

	public ReadOnlyBooleanProperty minusProperty() {
		return this.minus.getReadOnlyProperty();
	}

	/**
	 * liefert den Kontoinhaber zur�ck
	 * 
	 * @return der Inhaber
	 */
	public Kunde getInhaber() {
		return this.inhaber;
	}

	/**
	 * setzt den Kontoinhaber
	 * 
	 * @param kinh neuer Kontoinhaber
	 * @throws GesperrtException        wenn das Konto gesperrt ist
	 * @throws IllegalArgumentException wenn kinh null ist
	 */
	public final void setInhaber(Kunde kinh) throws GesperrtException {
		try {
			if (kinh == null)
				throw new IllegalArgumentException("Der Inhaber darf nicht null sein!");
			if (this.isGesperrt())
				throw new GesperrtException(this.nummer);
		} catch (Exception e) {
			logger.logException(e.toString());
			throw e;
		}
		Kunde alt = this.inhaber;
		this.inhaber = kinh;
		prop.firePropertyChange("Inhaber", alt, kinh);
	}

	/**
	 * liefert die Kontonummer zur�ck
	 * 
	 * @return long
	 */
	public long getKontonummer() {
		return nummer;
	}

	/**
	 * liefert zur�ck, ob das Konto gesperrt ist oder nicht
	 * 
	 * @return true, wenn das Konto gesperrt ist
	 */
	public boolean isGesperrt() {
		return gesperrt.get();
	}

	/**
	 * Erh�ht den Kontostand um den eingezahlten Betrag.
	 *
	 * @param betrag double
	 * @throws IllegalArgumentException wenn der betrag negativ ist
	 */
	public void einzahlen(double betrag) {
		try {
			if (betrag < 0 || Double.isNaN(betrag)) {
				throw new IllegalArgumentException("Falscher Betrag");
			}
		} catch (IllegalArgumentException e) {
			logger.logException(e.toString());
			throw e;
		}
		setKontostand(getKontostand() + betrag);
	}

	/**
	 * Gibt eine Zeichenkettendarstellung der Kontodaten zur�ck.
	 */
	@Override
	public String toString() {
		String ausgabe;
		ausgabe = "Kontonummer: " + this.getKontonummerFormatiert() + System.getProperty("line.separator");
		ausgabe += "Inhaber: " + this.inhaber;
		ausgabe += "Aktueller Kontostand: " + this.getKontostandFormatiert();
		ausgabe += this.getGesperrtText() + System.getProperty("line.separator");
		return ausgabe;
	}

	/**
	 * Mit dieser Methode wird der geforderte Betrag vom Konto abgehoben, wenn es
	 * nicht gesperrt ist.
	 *
	 * @param betrag double
	 * @throws GesperrtException        wenn das Konto gesperrt ist
	 * @throws IllegalArgumentException wenn der betrag negativ ist
	 * @return true, wenn die Abhebung geklappt hat, false, wenn sie abgelehnt wurde
	 */
	public boolean abheben(double betrag) throws GesperrtException, IllegalArgumentException {
		try {
			if (betrag < 0 || Double.isNaN(betrag)) {
				throw new IllegalArgumentException("Betrag ungueltig");
			}
			if (this.isGesperrt()) {
				throw new GesperrtException(this.getKontonummer());
			}
		} catch (Exception e) {
			logger.logException(e.toString());
			throw e;
		}
		if (!pruefeAbheben(betrag)) {
			return false;
		}
		setKontostand(getKontostand() - betrag);
		return true;
	}

	/**
	 * Prueft, ob Konditionen zur Abhebung eingehalten werden
	 * 
	 * @param betrag Betrag in double
	 * @return true, wenn die Abhebung moeglich ist, false, wenn nicht
	 */
	public abstract boolean pruefeAbheben(double betrag);

	/**
	 * sperrt das Konto, Aktionen zum Schaden des Benutzers sind nicht mehr m�glich.
	 */
	public final void sperren() {
		this.gesperrt.set(true);
	}

	/**
	 * entsperrt das Konto, alle Kontoaktionen sind wieder m�glich.
	 */
	public final void entsperren() {
		this.gesperrt.set(false);
	}

	/**
	 * liefert eine String-Ausgabe, wenn das Konto gesperrt ist
	 * 
	 * @return "GESPERRT", wenn das Konto gesperrt ist, ansonsten ""
	 */
	public final String getGesperrtText() {
		if (this.isGesperrt()) {
			return "GESPERRT";
		} else {
			return "";
		}
	}

	/**
	 * liefert die ordentlich formatierte Kontonummer
	 * 
	 * @return auf 10 Stellen formatierte Kontonummer
	 */
	public String getKontonummerFormatiert() {
		return String.format("%10d", this.nummer);
	}

	/**
	 * liefert den ordentlich formatierten Kontostand
	 * 
	 * @return formatierter Kontostand mit 2 Nachkommastellen und W�hrungssymbol �
	 */
	public String getKontostandFormatiert() {
		return String.format("%10.2f " + waehrung.name(), this.getKontostand());
	}

	/**
	 * Vergleich von this mit other; Zwei Konten gelten als gleich, wen sie die
	 * gleiche Kontonummer haben
	 * 
	 * @param other das Vergleichskonto
	 * @return true, wenn beide Konten die gleiche Nummer haben
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (this.getClass() != other.getClass())
			return false;
		if (this.nummer == ((Konto) other).nummer)
			return true;
		else
			return false;
	}

	@Override
	public int hashCode() {
		return 31 + (int) (this.nummer ^ (this.nummer >>> 32));
		// Verstaendnis ist hier nicht wichtig. Das ist halt eine
		// Moeglichkeit, einen mehr oder weniger sinnvoll verteilten
		// Hashcode aus der Kontonummer zu berechnen.
	}

	@Override
	public int compareTo(Konto other) {
		if (other.getKontonummer() > this.getKontonummer())
			return -1;
		if (other.getKontonummer() < this.getKontonummer())
			return 1;
		return 0;
	}

	/**
	 * gibt dieses Konto auf der Konsole aus
	 */
	public void ausgeben() {
		System.out.println(this.toString());
	}

	/**
	 * Mit dieser Methode wird der angegebene Betrag in der gewuenschten Waehrung
	 * vom Konto abgehoben, wenn es nicht gesperrt ist.
	 * 
	 * @param betrag Betrag in double
	 * @param w      die gewuenschte Waehrung
	 * @throws GesperrtException wenn das Konto gesperrt ist
	 * @return true, wenn die Abhebung erfolgreich ist, false, wenn nicht
	 */
	public boolean abheben(double betrag, Waehrung w) throws GesperrtException {
		// Hier wird Geld aus dem Konto mit der angegebene Waehrung abgehoben
		betrag = Waehrung.waehrungZuWaehrung(betrag, w, waehrung);
		return abheben(betrag);
	}

	/**
	 * Mit dieser Methode wird der angegebene Betrag in der Waehrung auf das Konto
	 * eingezaehlt.
	 * 
	 * @param betrag Betrag in double
	 * @param w      die Waehrung
	 * @throws IllegalArgumentException wenn der betrag negativ ist
	 */
	public void einzahlen(double betrag, Waehrung w) throws IllegalArgumentException {
		betrag = Waehrung.waehrungZuWaehrung(betrag, w, waehrung);
		einzahlen(betrag);
	}

	/**
	 * Diese Methode fragt die aktuelle Waehrung des Kontos.
	 * 
	 * @return die aktuelle Waehrung
	 */
	public Waehrung getAktuelleWaehrung() {
		return this.waehrung;
	}

	/**
	 * Diese Methode wechselt die aktuelle Waehrung mit einer neuen Waehrung.
	 * Kontostand, Dispo und bereitsAbgehoben werden umgerechnet.
	 * 
	 * @param neu die neue Waehrung
	 * @throws NullPointerException, wenn neu = null ist
	 */
	public final void waehrungswechsel(Waehrung neu) {
		kontostand.set(Waehrung.waehrungZuWaehrung(this.getKontostand(), waehrung, neu));
		this.spezifischeUmrechnungen(neu);
		Waehrung alt = this.waehrung;
		this.waehrung = neu;
		prop.firePropertyChange("Währung", alt, neu);
	}

	/**
	 * Diese Methode rechnet bei einem Waehrungswechsel alle weiteren Attribute des
	 * Kontos um ausser dem Kontostand
	 * 
	 * @param neu die neue Waehrung
	 */
	protected abstract void spezifischeUmrechnungen(Waehrung neu);

	public void speichern(Statement stmt) throws Exception {
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("SELECT * FROM konto WHERE kontonr=" + this.nummer);
			if (rs.next()) {
				rs.updateDouble("kontostand", this.getKontostand());
				rs.updateBoolean("gesperrt", this.isGesperrt());
				rs.updateRow();
			} else {
				rs.moveToInsertRow();
				rs.updateDouble("kontostand", this.getKontostand());
				rs.updateBoolean("gesperrt", this.isGesperrt());
				rs.updateLong("kontonr", this.nummer);
				int nummer = this.inhaber.speichern();
				// in Kunde einfach eine speichern-Methode erzeugen, die nichts tut. Um sie soll
				// es hier nicht gehen.
				rs.updateInt("kunde", nummer);
				rs.insertRow();
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				throw ex;
			}
		}
	}

	/**
	 * Fuegt den Beobachter b in die Liste
	 * 
	 * @param b Beobachter
	 */
	public void anmelden(PropertyChangeListener l) {
		prop.addPropertyChangeListener(l);
	}

	/**
	 * Loescht den Beobachter von der Liste
	 * 
	 * @param b Beobachter
	 */
	public void abmelden(PropertyChangeListener l) {
		prop.removePropertyChangeListener(l);
	}

	/**
	 * benachrichtigt alle Beobachter in der Liste
	 */
	protected void firePropertyChange(String name, Object alt, Object neu) {
		prop.firePropertyChange(name, alt, neu);
	}

}
