package verarbeitung;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Beobachter implements PropertyChangeListener {

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Konto k = (Konto) evt.getSource();
		String geaendert = evt.getPropertyName();
		Object alt = evt.getOldValue();
		Object neu = evt.getNewValue();

		System.out.println("Änderung an Konto mit Kontonummer " + k.getKontonummer() + " vorgenommen. "
				+ System.lineSeparator() + geaendert + " wurde von " + alt + " auf " + neu + " gesetzt.");
	}
}
