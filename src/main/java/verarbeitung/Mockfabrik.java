package verarbeitung;

import org.mockito.Mockito;

public class Mockfabrik extends Kontofabrik {

	private Konto neu;
	public Kontoart art = Kontoart.GIROKONTO;

	@Override
	public Konto erzeugen(Kunde inhaber, long nummer) {
		switch (art) {
		case SPARBUCH:
			neu = Mockito.mock(Sparbuch.class);
			break;
		case GIROKONTO:
			neu = Mockito.mock(Girokonto.class);
			break;
		default:
			throw new IllegalArgumentException();
		}
		Mockito.when(neu.getInhaber()).thenReturn(inhaber);
		Mockito.when(neu.getKontonummer()).thenReturn(nummer);
		return neu;
	}

	public Konto getLetztesKonto() {
		return neu;
	}

}
