package verarbeitung;

import java.time.LocalDate;

/**
 * Testprogramm f�r Konten
 * 
 * @author Doro
 *
 */
public class Kontentest {

	/**
	 * Testprogramm f�r Konten
	 * 
	 * @param args wird nicht benutzt
	 * @throws GesperrtException                  wenn Konto gesperrt ist
	 * @throws KontonummerNichtVorhandenException wenn Kontonummer nicht vorhanden
	 *                                            ist
	 */
	public static void main(String[] args) throws GesperrtException, KontonummerNichtVorhandenException {
		Kunde ich = new Kunde("Dorothea", "Hubrich", "zuhause", LocalDate.parse("1976-07-13"));
		Kunde du = new Kunde("Sunan Regi", "Maunakea", "zuhause", LocalDate.parse("1999-04-14"));

		Girokonto meinGiro = new Girokonto(ich, 1234, 1000.0);
		
		meinGiro.einzahlen(50);
		meinGiro.abheben(20);
		meinGiro.waehrungswechsel(Waehrung.BGN);
		meinGiro.sperren();
		
//		System.out.println(meinGiro);
//
//		Sparbuch meinSpar = new Sparbuch(ich, 9876);
//		meinSpar.einzahlen(50);
//		meinSpar.waehrungswechsel(Waehrung.BGN);
//		try {
//			boolean hatGeklappt = meinSpar.abheben(70);
//			System.out.println("Abhebung hat geklappt: " + hatGeklappt);
//			System.out.println(meinSpar);
//		} catch (GesperrtException e) {
//			System.out.println("Zugriff auf gesperrtes Konto - Polizei rufen!");
//		}
//
//		Konto test = meinGiro;
//		System.out.println(test.toString());
//		test.ausgeben();
		
		Kontofabrik kontoFabrik = new Girokontofabrik();
//		
		Bank bank = new Bank(100900);
//		bank.geldEinzahlen(10000, 100);
//		System.out.println(bank.getAlleKonten());
		
		Konsolenbeobachter kb = new Konsolenbeobachter();
		bank.addBeobachter(kb);
		bank.kontoErstellen(kontoFabrik, ich);
		bank.kontoErstellen(kontoFabrik, du);
		bank.kontoLoeschen(10000);
		bank.removeBeobachter(kb);
	}

}
