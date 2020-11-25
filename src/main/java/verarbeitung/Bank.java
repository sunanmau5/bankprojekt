package verarbeitung;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * verwaltet die Konten
 * 
 * @author Sunan Regi Maunakea
 *
 */
public class Bank implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getInstance();

	/**
	 * die Bankleitzahl
	 */
	private long bankleitzahl;

	/**
	 * die Liste der verfuegbare Kontonummer
	 */
	protected Map<Long, Konto> kontenliste = new TreeMap<>();

	public List<BeobachterBank> accountlist = new LinkedList<>();

	/**
	 * die noch nicht vergebene Kontonummer
	 */
	private static final long FIRST_KONTONUMMER = 10000L;

	/**
	 * setzt die Bankleitzahl der Bank auf Standardwerte
	 * 
	 * @throws IOException
	 */
	public Bank() {
		this(12345678);
	}

	/**
	 * Setzt die Bankleitzahl auf den angegebenen Wert
	 * 
	 * @param bankleitzahl die Bankleitzahl
	 * @throws IOException
	 * @throws IllegalArgumentException wenn bankleitzahl kleiner als null ist
	 */
	public Bank(long bankleitzahl) {
		try {
			if (bankleitzahl < 0) {
				throw new IllegalArgumentException("Negative Zahl als Bankleitzahl ist nicht zulaessig");
			}
		} catch (Exception e) {
			logger.logException(e.toString());
			throw e;
		}
		this.bankleitzahl = bankleitzahl;
	}

	/**
	 * liefert die Bankleitzahl zurueck
	 * 
	 * @return die Bankleitzahl
	 */
	public long getBankleitzahl() {
		return this.bankleitzahl;
	}

	/**
	 * erstellt ein Konto (Girokonto oder Sparbuch) aus der Kontofabrik mit der
	 * angegebenen Inhaber
	 * 
	 * @param fabrik  der von Benutzer gewaehlte Kontofabrik
	 * @param inhaber der Inhaber
	 * @return die neue gegebene Kontonummer
	 */
	public long kontoErstellen(Kontofabrik fabrik, Kunde inhaber) {
		long neueKontonummer = maxKontonummer();
		Konto k = fabrik.erzeugen(inhaber, neueKontonummer);
		kontenliste.put(neueKontonummer, k);
		benachrichtigen();
		return neueKontonummer;
	}

	/**
	 * liefert eine Auflistung von Kontoinformationen aller Konten
	 * 
	 * @return die Auflistung aller Konten (Kontonummer und Kontostand)
	 */
	public String getAlleKonten() {
		String auflistung = "ID \t Kontostand" + System.getProperty("line.separator");
		for (Konto konto : kontenliste.values()) {
			auflistung += konto.getKontonummer() + " \t " + konto.getKontostand()
					+ System.getProperty("line.separator");
		}
		return auflistung;
	}

	/**
	 * liefert eine Liste aller gueltigen Kontonummern in der Bank
	 * 
	 * @return die Liste aller gueltigen Kontonummern
	 */
	public List<Long> getAllerKontonummern() {
		return new LinkedList<Long>(kontenliste.keySet());
	}

	/**
	 * hebt den Betrag vom Konto mit der Nummer <code>von</code> ab und gibt
	 * zurueck, ob die Abhebung geklappt hat
	 * 
	 * @param von    die Kontonummer
	 * @param betrag der Betrag
	 * @return true, wenn die Abhebung geklappt hat, false, wenn sie abgelehnt wurde
	 * @throws GesperrtException                  wenn das Konto gesperrt ist
	 * @throws KontonummerNichtVorhandenException wenn die Kontonummer nicht
	 *                                            vorhanden ist
	 */
	public boolean geldAbheben(long von, double betrag) throws GesperrtException, KontonummerNichtVorhandenException {
		Konto k = kontenliste.get(von);
		try {
			if (k == null) {
				throw new KontonummerNichtVorhandenException(von);
			}
		} catch (Exception e) {
			logger.logException(e.getMessage());
			throw e;
		}
		return k.abheben(betrag);
	}

	/**
	 * zahlt den angegebenen Betrag auf das Konto mit der Nummer auf ein
	 * 
	 * @param auf    die Kontonummer
	 * @param betrag der Betrag
	 * @throws KontonummerNichtVorhandenException wenn die Kontonummer nicht
	 *                                            vorhanden ist
	 */
	public void geldEinzahlen(long auf, double betrag) throws KontonummerNichtVorhandenException {
		Konto k = kontenliste.get(auf);
		try {
			if (k == null) {
				throw new KontonummerNichtVorhandenException(auf);
			}
		} catch (Exception e) {
			logger.logException(e.getMessage());
			throw e;
		}
		k.einzahlen(betrag);
	}

	/**
	 * loescht das Konto mit der angegebenen nummer und gibt zurueck, ob die
	 * Loeschung geklappt hat
	 * 
	 * @param nummer die Kontonummer
	 * @return true, wenn die Loeschung geklappt hat, false, wenn nicht
	 */
	public boolean kontoLoeschen(long nummer) {
		if (kontenliste.remove(nummer) == null) {
			return false;
		} else {
			benachrichtigen();
			return true;
		}
	}

	/**
	 * liefert den Kontostand des Kontos mit der angegebenen nummer zurueck
	 * 
	 * @param nummer die Kontonummer
	 * @return der Kontostand des Kontos
	 * @throws KontonummerNichtVorhandenException wenn die Kontonummer nicht
	 *                                            vorhanden ist
	 */
	public double getKontostand(long nummer) throws KontonummerNichtVorhandenException {
		Konto k = kontenliste.get(nummer);
		try {
			if (k == null) {
				throw new KontonummerNichtVorhandenException(nummer);
			}
		} catch (Exception e) {
			logger.logException(e.getMessage());
			throw e;
		}
		return k.getKontostand();
	}

	/**
	 * ueberweist den genannten Betrag vom ueberweisungsfaehigen Konto mit der
	 * Nummer <code>vonKontonr</code> zum ueberweisungsfaehigen Konto mit der Nummer
	 * <code>nachKontonr</code> und gibt zurueck, ob die Ueberweisung geklappt hat
	 * 
	 * @param vonKontonr       die Kontonummer des Senders
	 * @param nachKontonr      die Kontonummer des Empfaengers
	 * @param betrag           der Betrag
	 * @param verwendungszweck der Verwendungszweck
	 * @return true, wenn die Ueberweisung geklappt hat, false, wenn nicht
	 * @throws KontonummerNichtVorhandenException wenn Kontonummer nichht vorhanden
	 *                                            ist
	 * @throws GesperrtException                  wenn Konto gesperrt ist
	 */
	public boolean geldUeberweisen(long vonKontonr, long nachKontonr, double betrag, String verwendungszweck)
			throws KontonummerNichtVorhandenException, GesperrtException {
		Konto tmpAbsender = kontenliste.get(vonKontonr);
		Konto tmpEmpfaenger = kontenliste.get(nachKontonr);

		if (!(tmpAbsender instanceof Ueberweisungsfaehig) || !(tmpEmpfaenger instanceof Ueberweisungsfaehig)) {
			return false;
		}
		Ueberweisungsfaehig absender = (Ueberweisungsfaehig) tmpAbsender;
		Ueberweisungsfaehig empfaenger = (Ueberweisungsfaehig) tmpEmpfaenger;
		if (!absender.ueberweisungAbsenden(betrag, tmpEmpfaenger.getInhaber().getName(), nachKontonr,
				this.getBankleitzahl(), verwendungszweck)) {
			return false;
		}
		empfaenger.ueberweisungEmpfangen(betrag, tmpAbsender.getInhaber().getName(), vonKontonr, this.getBankleitzahl(),
				verwendungszweck);
		return true;

	}

	/**
	 * liefert die groesste bisher vergebene Kontonummer
	 * 
	 * @return die groesste bisher vergebene Kontonummer plus eins
	 */
	public long maxKontonummer() {
		if (kontenliste.isEmpty()) {
			return FIRST_KONTONUMMER;
		}
		return Collections.max(kontenliste.keySet()) + 1;
	}

	/**
	 * Diese Methode gibt zurueck ob eine Kontonummer gesperrt ist
	 * 
	 * @param kontonummer Kontonummer, die geprueft werden soll
	 * @return true wenn Konto gesperrt ist
	 * @throws KontonummerNichtVorhandenException wenn Kontonummer nicht gefunden
	 *                                            wurde
	 */
	public boolean isGesperrt(long kontonummer) throws KontonummerNichtVorhandenException {
		Konto k = kontenliste.get(kontonummer);
		try {
			if (k == null) {
				throw new KontonummerNichtVorhandenException(kontonummer);
			}
		} catch (Exception e) {
			logger.logException(e.getMessage());
			throw e;
		}
		return k.isGesperrt();
	}

	/**
	 * die Methode sperrt alle Konten, deren Kontostand im Minus ist
	 */
	public void pleitegeierSperren() {
		Predicate<Konto> filterKontostandImMinus = konto -> konto.getKontostand() < 0;
		Consumer<Konto> actionKontoSperren = konto -> konto.sperren();
		kontenliste.values().stream().filter(filterKontostandImMinus).forEach(actionKontoSperren);
	}

	/**
	 * liefert eine Liste aller Kunden, die auf einem Konto einen Kontostand haben,
	 * der mindestens <code>minimum</code> betraegt
	 * 
	 * @param minimum Mindestbetrag
	 * @return Liste aller Kunden, die auf ihrem Konto Mindestbetrag haben.
	 */
	public List<Kunde> getKundenMitVollemKonto(double minimum) {
		List<Kunde> kundeliste = kontenliste.values().stream().filter(konto -> konto.getKontostand() >= minimum)
				.map(konto -> konto.getInhaber()).collect(Collectors.toList());
		return kundeliste;
	}

	/**
	 * liefert die Namen und Geburtstage aller Kunden der Bank. Doppelte Namen
	 * sollen dabei aussortiert werden. Sortieren Sie die Liste nach dem
	 * Geburtsdatum.
	 * 
	 * @return Namen und Geburtstage aller Kunden der Bank
	 */
	public String getKundengeburtstage() {
		Stream<Kunde> kundeStream = kontenliste.values().stream().map(konto -> konto.getInhaber())
				.sorted((kunde1, kunde2) -> kunde1.getGeburtstag().compareTo(kunde2.getGeburtstag())).distinct();
		Stream<String> nameUndGeburtstagStream = kundeStream
				.map(kunde -> kunde.getName() + "\t" + kunde.getGeburtstag());
		String kundengeburtstage = nameUndGeburtstagStream.reduce("",
				(stringBisher, naechsteKunde) -> stringBisher + System.lineSeparator() + naechsteKunde);
		return kundengeburtstage;
	}

	/**
	 * liefert eine Liste aller freien Kontonummern, die im von Ihnen vergebenen
	 * Bereich liegen
	 * 
	 * @return Liste aller freien Kontonummern
	 */
	public List<Long> getKontonummernLuecken() {
		List<Long> kontonummerlueckenliste = new ArrayList<>();
		Long max = maxKontonummer();
		for (long i = FIRST_KONTONUMMER; i < max; i++) {
			kontonummerlueckenliste.add(i);
		}
		List<Long> keys = kontenliste.keySet().stream().collect(Collectors.toList());
		kontonummerlueckenliste.removeIf(key -> keys.contains(key));
		return kontonummerlueckenliste;
	}

	public List<Long> getKontonummernLuecken2() {
		Long max = maxKontonummer();
		return Stream.iterate(FIRST_KONTONUMMER, x -> x + 1).limit(max).filter(x -> !kontenliste.containsKey(x))
				.collect(Collectors.toList());
	}

	/**
	 * liefert eine Liste aller Kunden, deren Gesamteinlage auf all ihren Konten
	 * mehr als <code>minimum</code> betraegt
	 * 
	 * @param minimum Mindestbetrag der Gesamteinlage
	 * @return Liste aller Kunden, die auf all ihren Konten Mindestbetrag haben.
	 */
	public List<Kunde> getAlleReichenKunden(double minimum) {
		Map<Kunde, Double> temp = new TreeMap<>();
		Stream<Konto> kontolist = kontenliste.values().stream();
		kontolist.forEach((konto) -> {
			Kunde tmpKunde = konto.getInhaber();
			Double tmpKontostand = konto.getKontostand();
			if (temp.containsKey(tmpKunde)) {
				temp.put(tmpKunde, temp.get(tmpKunde) + tmpKontostand);
			} else {
				temp.put(tmpKunde, konto.getKontostand());
			}
		});
		Stream<Kunde> stream = temp.entrySet().stream().filter(set1 -> set1.getValue() > minimum)
				.map(key -> key.getKey());
		List<Kunde> reicheKunden = stream.collect(Collectors.toList());
		return reicheKunden;
	}

	public List<Kunde> getAlleReichenKunden2(double minimum) {
		return kontenliste.values().stream().map(Konto::getInhaber).distinct()
				.filter(aktuellerKunde -> kontenliste.values().stream().filter(k -> k.getInhaber() == aktuellerKunde)
						.map(k -> k.getKontostand()).reduce(0.0, (a, b) -> a + b) > minimum)
				.collect(Collectors.toList());
	}

	/**
	 * liefert eine vollstaendige Kopie von this zueruck
	 * 
	 * @throws CloneNotSupportedException, wenn die Bank nicht serialisierbare
	 *                                     Konten verwaltet, die den Clone-Vorgang
	 *                                     stoeren
	 */
	public Bank clone() throws CloneNotSupportedException {
		Bank kopie = null;
		try {

			// Serialisierung von Objekt
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(this);
			out.close();

			// Deserialisierung von Objekt
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream in = new ObjectInputStream(bis);
			kopie = (Bank) in.readObject();
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return kopie;
	}

	/**
	 * Fuegt den Beobachter b in die Liste
	 * 
	 * @param b Beobachter
	 */
	public void addBeobachter(BeobachterBank b) {
		accountlist.add(b);
	}

	/**
	 * Loescht den Beobachter von der Liste
	 * 
	 * @param b Beobachter
	 */
	public void removeBeobachter(BeobachterBank b) {
		accountlist.add(b);
	}

	/**
	 * benachrichtigt alle Beobachter in der Liste
	 */
	protected void benachrichtigen() {
		accountlist.forEach(b -> b.aktualisieren(this));
	}

}
