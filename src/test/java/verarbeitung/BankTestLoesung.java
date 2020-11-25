package verarbeitung;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import verarbeitung.Bank;
import verarbeitung.GesperrtException;
import verarbeitung.Girokonto;
import verarbeitung.Kontoart;
import verarbeitung.KontonummerNichtVorhandenException;
import verarbeitung.Kunde;
import verarbeitung.Mockfabrik;
import verarbeitung.Sparbuch;

class BankTestLoesung {

	Bank bank;
	Bank bank2;
	Kunde kunde;

	long giro1, giro2, spar1, spar2, nichtVorhanden;
	Girokonto gk1, gk2;
	Sparbuch sb1, sb2;
	Mockfabrik mockFabrik = new Mockfabrik();

	@BeforeEach
	void setup() throws Exception {
		bank = new Bank(16050000);
		bank2 = new Bank(16050001);
		kunde = new Kunde("Dorothea", "Hubrich", "zuhause", LocalDate.parse("1976-07-13"));

		giro1 = bank.kontoErstellen(mockFabrik, kunde);
		gk1 = (Girokonto) mockFabrik.getLetztesKonto();

		Mockito.when(gk1.abheben(ArgumentMatchers.anyDouble())).thenReturn(true);
		Mockito.when(gk1.getInhaber()).thenReturn(kunde);
		Mockito.when(gk1.getKontonummer()).thenReturn(1L);
		Mockito.when(gk1.getKontostand()).thenReturn(1.23);
		Mockito.when(gk1.isGesperrt()).thenReturn(false);
		Mockito.when(gk1.toString()).thenReturn("String-Repräsentation des Girokontos 1");
		Mockito.when(gk1.ueberweisungAbsenden(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(true);

		giro2 = bank.kontoErstellen(mockFabrik, kunde);
		gk2 = (Girokonto) mockFabrik.getLetztesKonto();

		Mockito.when(gk2.abheben(ArgumentMatchers.anyDouble())).thenReturn(true);
		Mockito.when(gk2.getInhaber()).thenReturn(kunde);
		Mockito.when(gk2.getKontonummer()).thenReturn(2L);
		Mockito.when(gk2.getKontostand()).thenReturn(3.23);
		Mockito.when(gk2.isGesperrt()).thenReturn(false);
		Mockito.when(gk2.toString()).thenReturn("String-Repräsentation des Girokontos 2");
		Mockito.when(gk2.ueberweisungAbsenden(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(true);

		mockFabrik.art = Kontoart.SPARBUCH;
		spar1 = bank.kontoErstellen(mockFabrik, kunde);
		sb1 = (Sparbuch) mockFabrik.getLetztesKonto();

		Mockito.when(sb1.abheben(ArgumentMatchers.anyDouble())).thenReturn(true);
		Mockito.when(sb1.getInhaber()).thenReturn(kunde);
		Mockito.when(sb1.getKontonummerFormatiert()).thenReturn("00 0000 0002");
		Mockito.when(sb1.getKontostand()).thenReturn(2.23);
		Mockito.when(sb1.isGesperrt()).thenReturn(false);
		Mockito.when(sb1.toString()).thenReturn("String-Repräsentation des Sparbuchs 1");

	}

	/**
	 * Testet die Methode mockEinfuegen() und damit auch (mehr oder weniger)
	 * girokontoErstellen() und sparbuchErstellen()
	 * 
	 * @throws KontonummerNichtVorhandenException
	 */
	@Test
	void testSparbuchErstellen() throws KontonummerNichtVorhandenException {

		mockFabrik.art = Kontoart.SPARBUCH;

		long sparKtnr1 = bank2.kontoErstellen(mockFabrik, kunde);
		sb2 = (Sparbuch) mockFabrik.getLetztesKonto();

		Mockito.when(sb2.getKontonummer()).thenReturn(sparKtnr1);
		Mockito.when(sb2.getKontostand()).thenReturn(2.23);

		List<Long> nummernliste = bank2.getAllerKontonummern();
		assertTrue(nummernliste.contains(sparKtnr1));
		assertEquals(1, nummernliste.size());
		String alleKonten = bank2.getAlleKonten();
		// Hier wird geprueft, ob die Kontonummer des Sparbuchs irgendwo im String
		// auftaucht
		assertTrue(alleKonten.indexOf(sparKtnr1 + "") != -1);
		assertEquals(2.23, bank2.getKontostand(sparKtnr1));
	}

	/**
	 * Testet das Erstellen mehrerer Konten
	 * 
	 * @throws KontonummerNichtVorhandenException
	 */
	@Test
	void testKontenErstellen() throws KontonummerNichtVorhandenException {
		List<Long> nummernliste = bank.getAllerKontonummern();
		assertTrue(nummernliste.contains(giro1));
		assertTrue(nummernliste.contains(giro2));
		assertTrue(nummernliste.contains(spar1));
		assertEquals(3, nummernliste.size());
		assertEquals(1.23, bank.getKontostand(giro1));
		assertEquals(3.23, bank.getKontostand(giro2));
		assertEquals(2.23, bank.getKontostand(spar1));

		assertTrue(giro1 != giro2);
		assertTrue(giro1 != spar1);
		assertTrue(giro2 != spar1);
		try {
			bank.geldEinzahlen(nichtVorhanden, 100);
			fail("falsche Kontonummer existiert doch");
		} catch (KontonummerNichtVorhandenException e) {
		}
	}

	/**
	 * Testet die Methode kontoLoeschen()
	 * 
	 * @throws KontonummerNichtVorhandenException
	 */
	@Test
	void testKontoLoeschen() throws KontonummerNichtVorhandenException {
		assertEquals(true, bank.kontoLoeschen(giro1));
		List<Long> nummernliste = bank.getAllerKontonummern();
		assertFalse(nummernliste.contains(giro1));
		assertEquals(2, nummernliste.size());
		String alleKonten = bank.getAlleKonten();
		assertTrue(alleKonten.indexOf(giro1 + "") == -1);
		try {
			bank.geldEinzahlen(giro1, 100);
			fail("falsche Kontonummer existiert doch");
		} catch (KontonummerNichtVorhandenException e) {
		}
		assertEquals(false, bank.kontoLoeschen(giro1));
	}

	/**
	 * Testet die Methode kontoLoeschen() mit ungueltiger Kontonummer
	 * 
	 * @throws KontonummerNichtVorhandenException
	 */
	@Test
	void testKontoLoeschenWithIllegalKontonummer() throws KontonummerNichtVorhandenException {
		assertEquals(false, bank.kontoLoeschen(nichtVorhanden));

		List<Long> nummernliste = bank.getAllerKontonummern();
		assertTrue(nummernliste.contains(giro1));
		assertTrue(nummernliste.contains(giro2));
		assertTrue(nummernliste.contains(spar1));
		assertEquals(3, nummernliste.size());
		assertEquals(1.23, bank.getKontostand(giro1));
		assertEquals(3.23, bank.getKontostand(giro2));
		assertEquals(2.23, bank.getKontostand(spar1));
	}

	@Test
	void testEinzahlenAufAlleKonten() throws KontonummerNichtVorhandenException {
		bank.geldEinzahlen(giro1, 100);
		bank.geldEinzahlen(giro2, 200);
		bank.geldEinzahlen(spar1, 300);

		Mockito.verify(gk1).einzahlen(100);
		Mockito.verify(gk2).einzahlen(200);
		Mockito.verify(sb1).einzahlen(300);
	}

	@Test
	void testEinzahlenNegativ() throws KontonummerNichtVorhandenException {
		Mockito.doThrow(new IllegalArgumentException()).when(gk1).einzahlen(ArgumentMatchers.anyDouble());
		try {
			bank.geldEinzahlen(giro1, 100);
			fail("keine Exception bei negativem Betrag");
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Testet das Abheben eines Betrags von einem Konto
	 * 
	 * @throws KontonummerNichtVorhandenException
	 * @throws GesperrtException
	 */
	@Test
	void testGeldAbhebenKlappt() throws KontonummerNichtVorhandenException, GesperrtException {
		boolean hatGeklappt = false;
		hatGeklappt = bank.geldAbheben(giro1, 60);
		assertEquals(true, hatGeklappt);
		Mockito.verify(gk1).abheben(60);
	}

	/**
	 * Testet das Abheben eines Betrag von einem Konto mit ungueltiger Kontonummer
	 * 
	 * @throws KontonummerNichtVorhandenException
	 * @throws GesperrtException
	 */
	@Test
	void testGeldAbhebenKontoWithIllegalKontonummer() throws KontonummerNichtVorhandenException, GesperrtException {
		try {
			bank.geldAbheben(nichtVorhanden, 1);
			fail("nicht vorhandene Kontonummer wurde gefunden");
		} catch (KontonummerNichtVorhandenException e) {
		}
	}

	/**
	 * Testet das Abheben eines zu grossen Betrags
	 * 
	 * @throws KontonummerNichtVorhandenException
	 * @throws GesperrtException
	 */
	@Test
	void testGeldAbhebenZuViel() throws KontonummerNichtVorhandenException, GesperrtException {
		Mockito.when(sb1.abheben(ArgumentMatchers.anyDouble())).thenReturn(false);
		boolean hatGeklappt = false;
		hatGeklappt = bank.geldAbheben(spar1, 500);
		Mockito.verify(sb1).abheben(500);
		assertEquals(false, hatGeklappt);
	}

	/**
	 * Testet das Abheben eines Betrags von einem gesperrten Konto
	 * 
	 * @throws GesperrtException
	 * @throws KontonummerNichtVorhandenException
	 */
	@Test
	void testGeldAbhebenVonGesperrtemKonto() throws GesperrtException, KontonummerNichtVorhandenException {
		Mockito.when(gk1.abheben(ArgumentMatchers.anyDouble())).thenThrow(new GesperrtException(giro1));
		try {
			bank.geldAbheben(giro1, 100);
			fail("Geld von gesperrtem Konto abgehoben");
		} catch (GesperrtException e) {
		}
	}

	/**
	 * Testet das Abehen eines negativen Betrags
	 * 
	 * @throws KontonummerNichtVorhandenException
	 * @throws GesperrtException
	 * @throws IllegalArgumentException
	 */
	@Test
	void testGeldAbhebenMitNegativemBetrag()
			throws KontonummerNichtVorhandenException, GesperrtException, IllegalArgumentException {
		Mockito.when(gk1.abheben(ArgumentMatchers.anyDouble())).thenThrow(new IllegalArgumentException());
		try {
			bank.geldAbheben(giro1, 100);
			fail("Negativer Betrag wurde abgehoben");
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Testet die Ueberweisung von einem Girokonto zu einem anderen
	 * 
	 * @throws GesperrtException
	 * @throws KontonummerNichtVorhandenException
	 */
	@Test
	void testGeldUeberweisen() throws GesperrtException, KontonummerNichtVorhandenException {
		Mockito.when(gk1.ueberweisungAbsenden(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(true);
		Mockito.doNothing().when(gk2).ueberweisungEmpfangen(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());

		// Exercise:
		bank.geldUeberweisen(giro1, giro2, 100, "Mietezahlung");

		// Verify:
		Mockito.verify(gk1).ueberweisungAbsenden(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
		Mockito.verify(gk2).ueberweisungEmpfangen(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
	}

	@Test
	void testGeldUeberweisenVonGesperrtemKonto() throws GesperrtException, KontonummerNichtVorhandenException {
		Mockito.when(gk1.ueberweisungAbsenden(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
				.thenThrow(new GesperrtException(giro1));
		try {
			bank.geldUeberweisen(giro1, giro2, 100, "Miete");
			fail("Geld von gesperrtem Konto ueberwiesen");
		} catch (GesperrtException e) {
		}
	}

	@Test
	void testGeldUeberweisenNachGesperrtemKonto() throws GesperrtException, KontonummerNichtVorhandenException {
		Mockito.doThrow(new GesperrtException(giro2)).when(gk2).ueberweisungEmpfangen(ArgumentMatchers.anyDouble(),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(),
				ArgumentMatchers.anyString());
		try {
			bank.geldUeberweisen(giro1, giro2, 100, "Miete");
			fail("Geld nach gesperrtem Konto ueberwiesen");
		} catch (GesperrtException e) {
		}
	}
}
