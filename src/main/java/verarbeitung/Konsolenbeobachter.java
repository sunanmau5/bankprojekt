package verarbeitung;

public class Konsolenbeobachter implements BeobachterBank {

	@Override
	public void aktualisieren(Bank bank) {
		System.out.println("Account lists wurde geaendert");
	}

}
