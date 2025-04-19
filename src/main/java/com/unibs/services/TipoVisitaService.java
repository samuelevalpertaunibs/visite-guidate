package com.unibs.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.unibs.DatabaseException;
import com.unibs.daos.GiorniSettimanaDao;
import com.unibs.daos.TipoVisitaDao;
import com.unibs.daos.LuogoDao;
import com.unibs.daos.VolontarioDao;
import com.unibs.models.Luogo;

import java.util.List;

public class TipoVisitaService {

	private static final int MINUTES_PER_DAY = 24 * 60;

	public void aggiungiTipoVisita(String titolo, String descrizione, String dataInizioString, String dataFineString,
			String oraInizioString, String durataMinutiString, String entrataLibera, String numeroMinPartecipanti,
			String numeroMaxPartecipanti, String nomeLuogoSelezionato, String[] volontari, String[] giorni, String indirizzoPuntoIncontro, String comunePuntoIncontro, String provinciaPuntoIncontro)  {
		if (titolo == null || titolo.isEmpty())
			throw new IllegalStateException("Il campo Titolo non può essere vuoto");
		if (descrizione == null || descrizione.isEmpty())
			throw new IllegalStateException("Il campo Descrizione non può essere vuoto");
		if (dataInizioString == null || dataInizioString.isEmpty())
			throw new IllegalStateException("Il campo Data inizio non può essere vuoto");
		if (dataFineString == null || dataFineString.isEmpty())
			throw new IllegalStateException("Il campo Data fine non può essere vuoto");
		if (oraInizioString == null || oraInizioString.isEmpty())
			throw new IllegalStateException("Il campo Ora inzio non può essere vuoto");
		if (durataMinutiString == null || durataMinutiString.isEmpty())
			throw new IllegalStateException("Il campo Durata non può essere vuoto");
		if (entrataLibera == null || entrataLibera.isEmpty())
			throw new IllegalStateException("Il campo Entrata libera non può essere vuoto");
		if (numeroMinPartecipanti == null || numeroMinPartecipanti.isEmpty())
			throw new IllegalStateException("Il campo Numero minimo partecipanti non può essere vuoto");
		if (numeroMaxPartecipanti == null || numeroMaxPartecipanti.isEmpty())
			throw new IllegalStateException("Il campo Numero massimo partecipanti non può essere vuoto");
		if (nomeLuogoSelezionato == null || nomeLuogoSelezionato.isEmpty())
			throw new IllegalStateException("Seleziona un luogo prima di preseguire");
		if (volontari == null || volontari.length < 1)
			throw new IllegalStateException("Seleziona almeno un volontario da associare al tipo di visita.");
		if (giorni == null || giorni.length < 1)
			throw new IllegalStateException("Seleziona almeno un giorno della settimana.");
		if (indirizzoPuntoIncontro == null || indirizzoPuntoIncontro.isEmpty() || comunePuntoIncontro == null || comunePuntoIncontro.isEmpty() || provinciaPuntoIncontro == null || provinciaPuntoIncontro.isEmpty())
			throw new IllegalStateException("Inserisci tutti i campi relativi al punto d'incontro.");

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate dataInizio;
		LocalDate dataFine;
		try {
			dataInizio = LocalDate.parse(dataInizioString + "/" + LocalDate.now().getYear(), dateFormatter);
		} catch (Exception e) {
			throw new IllegalArgumentException("Il formato della data di inizio non è corretto");
		}
		try {
			dataFine = LocalDate.parse(dataFineString + "/" + LocalDate.now().getYear(), dateFormatter);
		} catch (Exception e) {
			throw new IllegalArgumentException("Il formato della data di fine non è corretto");
		}

		if (dataInizio.isAfter(dataFine))
			throw new IllegalArgumentException("La data di fine deve essere successiva a quella di inizio");

		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		LocalTime oraInizio;
		try {
			oraInizio = LocalTime.parse(oraInizioString, timeFormatter);
		} catch (Exception e) {
			throw new IllegalArgumentException("Il formato dell'ora di inizio non è corretto");
		}

		int durataMinuti;
		try {
			durataMinuti = Integer.parseInt(durataMinutiString);
		} catch (Exception e) {
			throw new IllegalArgumentException("Il formato della durata non è corretto");
		}

		if (oraInizio.getHour() * 60 + oraInizio.getMinute() + durataMinuti > MINUTES_PER_DAY)
			throw new IllegalArgumentException("La visita deve conludersi ento le 24 del giorno stesso");

		int numeroMax;
		try {
			numeroMax = Integer.parseInt(numeroMaxPartecipanti);
			if (numeroMax < 1 || numeroMax > 100)
				throw new IllegalArgumentException("Il numero massimo deve essere positivo e minore di 100.");
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Il numero massimo di partecipanti non è valido.");
		}

		int numeroMin;
		try {
			numeroMin = Integer.parseInt(numeroMinPartecipanti);
			if (numeroMin < 1 || numeroMin > 100)
				throw new IllegalArgumentException("Il numero minimo deve essere positivo e minore di 100.");
			if (numeroMin > numeroMax)
				throw new IllegalArgumentException("Il numero minimo deve essere minore del numero massimo.");
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Il numero minimo di partecipanti non è valido.");
		}

		boolean entrataLiberaBool = entrataLibera.equals("Sì");

		Luogo luogoDaAssociare = LuogoDao.getLuogoByNome(nomeLuogoSelezionato);
		if (luogoDaAssociare == null) {
			throw new DatabaseException("Luogo non trovato.");
		}

		int[] volontariIds = new int[volontari.length];
		for (int i = 0; i < volontari.length; i++) {
			volontariIds[i] = VolontarioDao.getIdByUsername(volontari[i]);
		}

		int[] giorniIds = new int[giorni.length];
		for (int i = 0; i < giorni.length; i++) {
			giorniIds[i] = GiorniSettimanaDao.getIdByNome(giorni[i]);
		}

		// Controllo overlap
		if (TipoVisitaDao.siSovrappone(luogoDaAssociare.getId(), giorniIds, oraInizio, durataMinuti, dataInizio, dataFine)) {
			throw new  IllegalArgumentException("La visita si sovrappone ad un'altra.");
		}

		// Controlli fatti, aggiungere al DB
		TipoVisitaDao.aggiungiVisita(titolo, descrizione, dataInizio, dataFine,
				oraInizio, durataMinuti, entrataLiberaBool, numeroMin,
				numeroMax, luogoDaAssociare, volontariIds, giorniIds, indirizzoPuntoIncontro, comunePuntoIncontro, provinciaPuntoIncontro);
	}

	public List<String> getGiorniSettimana() {
		return GiorniSettimanaDao.getGiorniSettimana();
	}

	public boolean isEmpty() {
		return TipoVisitaDao.isEmpty();
	}

	public List<String> getTitoliByVolontarioId(int volontarioId) {
		return TipoVisitaDao.getTitoliByVolontarioId(volontarioId);
	}

	public List<String> getTitoliByLuogoId(int luogoId) {
		return TipoVisitaDao.getTitoliByLuogoId(luogoId);
	}
}
