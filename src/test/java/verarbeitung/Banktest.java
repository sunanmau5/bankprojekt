package verarbeitung;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import verarbeitung.Bank;
import verarbeitung.GesperrtException;
import verarbeitung.Girokonto;
import verarbeitung.Konto;
import verarbeitung.KontonummerNichtVorhandenException;
import verarbeitung.Kunde;
import verarbeitung.Ueberweisungsfaehig;

class Banktest {
	Bank bank = new Bank(12345678);
	Konto konto = Mockito.mock(Konto.class);
	Konto konto2 = Mockito.mock(Konto.class);
	Kunde kunde = Mockito.mock(Kunde.class);
	Kunde kunde2 = Mockito.mock(Kunde.class);
//	long temp = bank.mockEinfuegen(konto);
//	long temp2 = bank.mockEinfuegen(konto2);

	@Test
	void geldAbhebenTest() throws GesperrtException, KontonummerNichtVorhandenException {
		// Setup:
		Mockito.when(konto.abheben(ArgumentMatchers.anyDouble())).thenReturn(true);
		Mockito.when(konto.getKontostand()).thenReturn(15000.00);
		Mockito.when(konto.getKontonummer()).thenReturn(10000L);
		Mockito.when(konto.isGesperrt()).thenReturn(false);

		// Exercise:
		assertFalse(bank.isGesperrt(konto.getKontonummer()));
		assertTrue(bank.geldAbheben(konto.getKontonummer(), 10));

		// Verify:
		Mockito.verify(konto, Mockito.times(1)).abheben(ArgumentMatchers.anyDouble());
		Mockito.verify(konto, Mockito.times(1)).isGesperrt();
	}

	@Test
	void geldEinzahlenTest() throws GesperrtException, KontonummerNichtVorhandenException {
		// Setup:
		Mockito.doNothing().when(konto).einzahlen(ArgumentMatchers.anyDouble());
		Mockito.when(konto.getKontostand()).thenReturn(15100.00);
		Mockito.when(konto.getKontonummer()).thenReturn(10000L);
		Mockito.when(konto.isGesperrt()).thenReturn(false);

		// Exercise:
		assertFalse(bank.isGesperrt(konto.getKontonummer()));
		bank.geldEinzahlen(konto.getKontonummer(), 100);

		// Verify:
		Mockito.verify(konto, Mockito.times(1)).einzahlen(ArgumentMatchers.anyDouble());
		Mockito.verify(konto, Mockito.times(1)).isGesperrt();
	}

	@Test
	void geldUeberweisenTest() throws GesperrtException, KontonummerNichtVorhandenException {
		// Setup:
		Girokonto abs = Mockito.mock(Girokonto.class,
				Mockito.withSettings().extraInterfaces(Ueberweisungsfaehig.class));
		Girokonto emp = Mockito.mock(Girokonto.class,
				Mockito.withSettings().extraInterfaces(Ueberweisungsfaehig.class));

		Mockito.when(abs.ueberweisungAbsenden(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(true);
		Mockito.doNothing().when(emp).ueberweisungEmpfangen(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
		Mockito.when(abs.getKontonummer()).thenReturn(10002L);
		Mockito.when(emp.getKontonummer()).thenReturn(10003L);
		Mockito.when(abs.getInhaber()).thenReturn(kunde);
		Mockito.when(emp.getInhaber()).thenReturn(kunde2);

		Mockito.when(kunde.getName()).thenReturn("James, Morrison");
		Mockito.when(kunde2.getName()).thenReturn("Sam, Sepiol");

		// Exercise:
//		bank.mockEinfuegen(abs);
//		bank.mockEinfuegen(emp);
		bank.geldUeberweisen(abs.getKontonummer(), emp.getKontonummer(), 100, "Mietezahlung");

		// Verify:
		Mockito.verify(abs, Mockito.times(1)).ueberweisungAbsenden(ArgumentMatchers.anyDouble(),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(),
				ArgumentMatchers.anyString());
		Mockito.verify(emp, Mockito.times(1)).ueberweisungEmpfangen(ArgumentMatchers.anyDouble(),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(),
				ArgumentMatchers.anyString());
	}

}
