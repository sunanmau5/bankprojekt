package verarbeitung;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.PropertyChangeListener;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import verarbeitung.GesperrtException;
import verarbeitung.Girokonto;
import verarbeitung.Kunde;

public class GirokontoTest {

	private Girokonto gk;
	private Kunde sunan = new Kunde("Sunan", "Maunakea", "zuhause", LocalDate.parse("2003-01-09"));
	private long nummer = 17;
	private double dispo = 1000;
	private PropertyChangeListener kb = Mockito.mock(PropertyChangeListener.class);

	@BeforeEach
	public void setUp() throws Exception {
		Mockito.doNothing().when(kb).propertyChange(ArgumentMatchers.any());
	}

	@Test
	void konstruktorTest() {
		gk = new Girokonto();
		assertEquals(gk.getKontostand(), 0);
		assertEquals(gk.getKontostandFormatiert(), "      0,00 EUR");
		assertFalse(gk.isGesperrt());
		assertEquals(gk.getGesperrtText(), "");
		assertEquals(gk.getInhaber(), Kunde.MUSTERMANN);
	}

	@Test
	void konstruktorMitParameternTest() {
		gk = new Girokonto(sunan, nummer, dispo);
		assertEquals(gk.getKontostand(), 0);
		assertEquals(gk.getKontostandFormatiert(), "      0,00 EUR");
		assertFalse(gk.isGesperrt());
		assertEquals(gk.getGesperrtText(), "");
		assertEquals(gk.getInhaber(), sunan);
		assertEquals(gk.getDispo(), dispo);
		assertEquals(gk.getKontonummer(), nummer);
		assertEquals(gk.getKontonummerFormatiert(), "        17");
	}

	@Test
	void einzahlenTest() {
		gk = new Girokonto();
		gk.anmelden(kb);
		gk.einzahlen(100);
		assertEquals(gk.getKontostand(), 100);
		assertEquals(gk.getKontostandFormatiert(), "    100,00 EUR");
		assertFalse(gk.isGesperrt());
		assertEquals(gk.getGesperrtText(), "");
		assertEquals(gk.getInhaber(), Kunde.MUSTERMANN);

		Mockito.verify(kb).propertyChange(ArgumentMatchers
				.argThat(o -> o.getPropertyName().equals("Kontostand") && (Double) o.getNewValue() == 100));
	}

	@Test
	void einzahlenNegativTest() {
		gk = new Girokonto();
		gk.anmelden(kb);
		try {
			gk.einzahlen(-100);
			fail("Keine Exception!");
		} catch (IllegalArgumentException e) {
		}
		assertEquals(gk.getKontostand(), 0);
		assertEquals(gk.getKontostandFormatiert(), "      0,00 EUR");
		assertFalse(gk.isGesperrt());
		assertEquals(gk.getGesperrtText(), "");
		assertEquals(gk.getInhaber(), Kunde.MUSTERMANN);

		Mockito.verifyNoInteractions(kb);
	}

	@Test
	void anUndAbmeldenTest() {
		gk = new Girokonto();
		gk.anmelden(kb);
		gk.einzahlen(100);

		Mockito.verify(kb).propertyChange(ArgumentMatchers
				.argThat(o -> o.getPropertyName().equals("Kontostand") && (Double) o.getNewValue() == 100));

		gk.abmelden(kb);
		gk.einzahlen(100);

		Mockito.verifyNoMoreInteractions(kb);
	}

	@Test
	void einzahlenNaNTest() {
		gk = new Girokonto();
		try {
			gk.einzahlen(Double.NaN);
			fail("Keine Exception!");
		} catch (IllegalArgumentException e) {
		}
		assertEquals(gk.getKontostand(), 0);
		assertEquals(gk.getKontostandFormatiert(), "      0,00 EUR");
		assertFalse(gk.isGesperrt());
		assertEquals(gk.getGesperrtText(), "");
		assertEquals(gk.getInhaber(), Kunde.MUSTERMANN);
	}

	@Test
	void abhebenimKontostandTest() throws GesperrtException {
		boolean geklappt;
		gk = new Girokonto();
		gk.einzahlen(100);
		geklappt = gk.abheben(50);
		assertTrue(geklappt);

		assertEquals(gk.getKontostand(), 50);
		assertEquals(gk.getKontostandFormatiert(), "     50,00 EUR");
		assertFalse(gk.isGesperrt());
		assertEquals(gk.getGesperrtText(), "");
		assertEquals(gk.getInhaber(), Kunde.MUSTERMANN);
	}

	@Test
	void abhebenGenauKontostandTest() throws GesperrtException {
		boolean geklappt;
		gk = new Girokonto();
		gk.einzahlen(100);
		geklappt = gk.abheben(100);
		assertTrue(geklappt);

		assertEquals(gk.getKontostand(), 0);
		assertEquals(gk.getKontostandFormatiert(), "      0,00 EUR");
		assertFalse(gk.isGesperrt());
		assertEquals(gk.getGesperrtText(), "");
		assertEquals(gk.getInhaber(), Kunde.MUSTERMANN);
	}

	@Test
	void abhebeninEinzelschrittenBisLeerTest() throws GesperrtException {
		boolean geklappt;
		gk = new Girokonto();
		gk.einzahlen(100);
		geklappt = gk.abheben(50);
		assertTrue(geklappt);
		geklappt = gk.abheben(50);
		assertTrue(geklappt);

		assertEquals(gk.getKontostand(), 0);
		assertEquals(gk.getKontostandFormatiert(), "      0,00 EUR");
		assertFalse(gk.isGesperrt());
		assertEquals(gk.getGesperrtText(), "");
		assertEquals(gk.getInhaber(), Kunde.MUSTERMANN);
	}

	@Test
	void abhebenInDenDispoTest() throws GesperrtException {
		boolean geklappt;
		gk = new Girokonto(sunan, nummer, dispo);
		gk.einzahlen(100);
		geklappt = gk.abheben(50 + dispo);
		assertTrue(geklappt);

		assertEquals(gk.getKontostand(), 50 - dispo);
		assertFalse(gk.isGesperrt());
		assertEquals(gk.getGesperrtText(), "");
		assertEquals(gk.getInhaber(), sunan);
		assertEquals(gk.getDispo(), dispo);
	}

	@Test
	void abhebenImDispoTest() throws GesperrtException {
		boolean geklappt;
		gk = new Girokonto(sunan, nummer, dispo);
		gk.einzahlen(100);
		geklappt = gk.abheben(50 + dispo);
		assertTrue(geklappt);
		geklappt = gk.abheben(20);
		assertTrue(geklappt);

		assertEquals(gk.getKontostand(), 30 - dispo);
		assertFalse(gk.isGesperrt());
		assertEquals(gk.getGesperrtText(), "");
		assertEquals(gk.getInhaber(), sunan);
		assertEquals(gk.getDispo(), dispo);
	}

	@Test
	void abhebenGenauImDispoTest() throws GesperrtException {
		boolean geklappt;
		gk = new Girokonto(sunan, nummer, dispo);
		gk.einzahlen(100);
		geklappt = gk.abheben(100 + dispo);
		assertTrue(geklappt);

		assertEquals(gk.getKontostand(), -dispo);
		assertFalse(gk.isGesperrt());
		assertEquals(gk.getGesperrtText(), "");
		assertEquals(gk.getInhaber(), sunan);
		assertEquals(gk.getDispo(), dispo);
	}

	@Test
	void abhebenUeberDispoTest() throws GesperrtException {
		boolean geklappt;
		gk = new Girokonto(sunan, nummer, dispo);
		gk.einzahlen(100);
		geklappt = gk.abheben(500 + dispo);
		assertFalse(geklappt);

		assertEquals(gk.getKontostand(), 100);
		assertFalse(gk.isGesperrt());
		assertEquals(gk.getGesperrtText(), "");
		assertEquals(gk.getInhaber(), sunan);
		assertEquals(gk.getDispo(), dispo);
	}

}
