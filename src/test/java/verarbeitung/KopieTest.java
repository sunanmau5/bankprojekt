package verarbeitung;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import verarbeitung.Bank;
import verarbeitung.Girokontofabrik;
import verarbeitung.Kontofabrik;
import verarbeitung.KontonummerNichtVorhandenException;
import verarbeitung.Kunde;

class KopieTest {

	Kunde k1 = new Kunde("James", "Monterrey", "Berlin", LocalDate.parse("1976-07-13"));
	Kunde k2 = new Kunde("Ted", "Mosby", "New York", LocalDate.parse("1978-04-25"));
	Kunde k3 = new Kunde("Ross", "Geller", "Manhattan", LocalDate.parse("1966-11-02"));
	Bank bank = new Bank(100900);

	@BeforeEach
	void setup() {
		Kontofabrik kf = new Girokontofabrik();
		bank.kontoErstellen(kf, k1);
		bank.kontoErstellen(kf, k2);
		bank.kontoErstellen(kf, k3);
	}

	@Test
	void cloneTest() throws KontonummerNichtVorhandenException, CloneNotSupportedException {
		Bank bank = this.bank;
		List<Long> konten = bank.getAllerKontonummern();
		int geld = 100;

		for (int i = 0; i < konten.size(); i++) {
			bank.geldEinzahlen(konten.get(i), geld);
			geld = geld + 250;
		}

		// Clone erstellen
		Bank clone = bank.clone();
		konten.stream().forEach(k -> {
			try {
				assertEquals(bank.getKontostand(k), clone.getKontostand(k), 0);
			} catch (KontonummerNichtVorhandenException e) {
			}
		});

		// Auf jedes Konto im Original 1000 Euro einzahlen
		Iterator<Long> it = konten.iterator();
		while (it.hasNext()) {
			bank.geldEinzahlen(it.next(), 1000);
		}

		konten.stream().forEach(k -> {
			try {
				assertTrue(bank.getKontostand(k) != clone.getKontostand(k));
			} catch (KontonummerNichtVorhandenException e) {
			}
		});
	}

}
