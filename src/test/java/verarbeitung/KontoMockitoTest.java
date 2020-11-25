package verarbeitung;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import verarbeitung.Girokonto;
import verarbeitung.Konto;

class KontoMockitoTest {
	Konto giro = new Girokonto();
	Statement state = Mockito.mock(Statement.class);
	ResultSet rs = Mockito.mock(ResultSet.class);

	@Test
	void vorhandenesKontoSpeichern() throws Exception {
		// Setup:
		Mockito.when(state.executeQuery(ArgumentMatchers.anyString())).thenReturn(rs);
		Mockito.when(rs.next()).thenReturn(true);

		// Exercise:
		giro.speichern(state);

		// Verify:
		Mockito.verify(rs, Mockito.times(1)).updateRow();
	}

	@Test
	void nichtVorhandenesKontoSpeichern() throws Exception {
		// Setup:
		Mockito.when(state.executeQuery(ArgumentMatchers.anyString())).thenReturn(rs);
		Mockito.when(rs.next()).thenReturn(false);

		// Exercise:
		giro.speichern(state);

		// Verify:
		Mockito.verify(rs, Mockito.times(1)).insertRow();
		;
	}

	@Test
	void exceptionBeiSpeichern() throws Exception {
		// Setup:
		Mockito.when(state.executeQuery(ArgumentMatchers.anyString())).thenReturn(rs);
		Mockito.when(rs.next()).thenReturn(true);
		Mockito.when(state.executeQuery(ArgumentMatchers.anyString())).thenThrow(SQLException.class);

		// Exercise:
		try {
			giro.speichern(state);
			fail("Keine Exception geworfen bei DB-Fehler");
		} catch (SQLException e) {
		} catch (Exception e) {
			fail("Falsche Exception geworfen bei DB-Fehler");
		}

		// Verify:
		Mockito.verify(rs, Mockito.times(0)).updateRow();
		Mockito.verify(rs, Mockito.times(0)).insertRow();

	}
}
