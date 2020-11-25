package streams;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import verarbeitung.Girokonto;
import verarbeitung.Konto;
import verarbeitung.Kunde;

public class StreamsUebung {

	public static void main(String[] args) {
		Kunde hans = new Kunde("Hans", "Meier", "Unterm Regenbogen 19", LocalDate.of(1990, 1, 5));
		Kunde otto = new Kunde("Otto", "Kar", "Hoch ueber den Wolken 7", LocalDate.of(1992, 2, 25));
		Kunde sabrina = new Kunde("Sabrina", "August", "Im Wald 15", LocalDate.of(1988, 3, 21));
		Konto eins = new Girokonto(hans, 123, 0);
		eins.einzahlen(100);
		Konto zwei = new Girokonto(otto, 234, 0);
		zwei.einzahlen(200);
		Konto drei = new Girokonto(sabrina, 333, 0);
		drei.einzahlen(100);
		Konto vier = new Girokonto(sabrina, 432, 0);
		vier.einzahlen(500);
		Konto fuenf = new Girokonto(otto, 598, 0);
		fuenf.einzahlen(600);

		Map<Long, Konto> kontenliste = new HashMap<Long, Konto>();
		kontenliste.put(123L, eins);
		kontenliste.put(234L, zwei);
		kontenliste.put(333L, drei);
		kontenliste.put(432L, vier);
		kontenliste.put(598L, fuenf);

		// Liste aller Kunden, sortiert nach Nachname, ohne doppelte:
		Stream<Konto> stream1 = kontenliste.values().stream();
		Stream<Kunde> stream2 = stream1.map(konto -> konto.getInhaber()).distinct().sorted();
		List<Kunde> a = stream2.collect(Collectors.toList());
		System.out.println(a);

		// Liste aller Kunden, sortiert nach ihrem Kontostand:
		List<Kunde> b = kontenliste.values().stream()
				.sorted((konto1, konto2) -> Double.compare(konto1.getKontostand(), konto2.getKontostand()))
				.map(konto -> konto.getInhaber()).collect(Collectors.toList());
		System.out.println(b);

		// faengt mindestens ein Kunde mit 'A' an?
		boolean c = kontenliste.values().stream().map(konto -> konto.getInhaber())
				.anyMatch(kunde -> kunde.getNachname().startsWith("A"));
		System.out.println(c);

		// alle Kundennamen in einem String:
		Stream<String> streamD = kontenliste.values().stream().map(konto -> konto.getInhaber().getName());
		String d = streamD.reduce("",
				(stringBisher, naechsterName) -> stringBisher + System.lineSeparator() + naechsterName);
//		StringBuilder x = kontenliste.values().stream().map(konto -> konto.getInhaber().getName()).collect(
//				() -> new StringBuilder(), (builder, name) -> builder.append(name),
//				(builder1, builder2) -> builder1.append(builder2));
		System.out.println(d);

		// Haben alle Kunden im Jahr 1990 Geburtstag?
		boolean e = kontenliste.values().stream().map(konto -> konto.getInhaber())
				.allMatch(kunde -> kunde.getGeburtstag().getYear() == 1990);
		System.out.println(e);
	}

}
