package com.unibs.models.builders;

import com.unibs.models.*;
import com.unibs.utils.DateService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class TipoVisitaBuilder {
    private String titolo;
    private String descrizione;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private LocalTime oraInizio;
    private int durataMinuti;
    private boolean entrataLibera;
    private int numeroMinPartecipanti;
    private int numeroMaxPartecipanti;
    private Luogo luogo;
    private PuntoIncontro puntoIncontro;
    private Set<Giorno> giorni;
    private Set<CoppiaIdUsername> volontari;

    private static final int MINUTES_PER_DAY = 24 * 60;

    public TipoVisitaBuilder conTitolo(String titolo) {
        if (titolo == null || titolo.isBlank()) {
            throw new IllegalArgumentException("Il campo Titolo non può essere vuoto");
        }
        this.titolo = titolo;
        return this;
    }

    public TipoVisitaBuilder conDescrizione(String descrizione) {
        if (descrizione == null || descrizione.isBlank()) {
            throw new IllegalArgumentException("Il campo Descrizione non può essere vuoto");
        }
        this.descrizione = descrizione;
        return this;
    }

    public TipoVisitaBuilder conPeriodo(String dataInizioString, String dataFineString) {
        if (dataInizioString == null || dataFineString == null ||
                dataInizioString.isBlank() || dataFineString.isBlank()) {
            throw new IllegalArgumentException("Le date non possono essere vuote");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            this.dataInizio = LocalDate.parse(dataInizioString + "/" + DateService.today().getYear(), formatter);
            this.dataFine = LocalDate.parse(dataFineString + "/" + DateService.today().getYear(), formatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato data non valido. Usa dd/MM.");
        }

        if (dataInizio.isAfter(dataFine)) {
            throw new IllegalArgumentException("La data di fine deve essere successiva a quella di inizio");
        }
        return this;
    }

    public TipoVisitaBuilder conOrario(String oraInizioString, String durataMinutiString) {
        if (oraInizioString == null || oraInizioString.isBlank() ||
                durataMinutiString == null || durataMinutiString.isBlank()) {
            throw new IllegalArgumentException("Ora e durata non possono essere vuote");
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            this.oraInizio = LocalTime.parse(oraInizioString, timeFormatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato ora non valido. Usa HH:mm.");
        }

        try {
            this.durataMinuti = Integer.parseInt(durataMinutiString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Durata non valida.");
        }

        if (oraInizio.getHour() * 60 + oraInizio.getMinute() + durataMinuti > MINUTES_PER_DAY) {
            throw new IllegalArgumentException("La visita deve terminare entro la mezzanotte.");
        }
        return this;
    }

    public TipoVisitaBuilder conNumeroPartecipanti(String minString, String maxString) {
        int max;
        try {
            max = Integer.parseInt(maxString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Numero massimo partecipanti non valido.");
        }

        if (max < 1 || max > 100) {
            throw new IllegalArgumentException("Il numero massimo deve essere compreso tra 1 e 100.");
        }
        this.numeroMaxPartecipanti = max;

        int min;
        try {
            min = Integer.parseInt(minString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Numero minimo partecipanti non valido.");
        }

        if (min < 1 || min > max) {
            throw new IllegalArgumentException("Il numero minimo deve essere positivo e <= massimo.");
        }
        this.numeroMinPartecipanti = min;

        return this;
    }

    public TipoVisitaBuilder conEntrataLibera(boolean entrataLibera) {
        this.entrataLibera = entrataLibera;
        return this;
    }

    public TipoVisitaBuilder conLuogo(Luogo luogo) {
        if (luogo == null) {
            throw new IllegalArgumentException("Il luogo non può essere nullo.");
        }
        this.luogo = luogo;
        return this;
    }

    public TipoVisitaBuilder conPuntoIncontro(String indirizzo, String comune, String provincia) {
        if (indirizzo == null || indirizzo.isBlank() ||
                comune == null || comune.isBlank() ||
                provincia == null || provincia.isBlank()) {
            throw new IllegalArgumentException("Tutti i campi del punto di incontro sono obbligatori.");
        }
        this.puntoIncontro = new PuntoIncontro(indirizzo, comune, provincia);
        return this;
    }

    public TipoVisitaBuilder neiGiorni(Set<Giorno> giorni) {
        if (giorni == null || giorni.isEmpty()) {
            throw new IllegalArgumentException("Seleziona almeno un giorno della settimana.");
        }
        this.giorni = giorni;
        return this;
    }

    public TipoVisitaBuilder conVolontari(Set<CoppiaIdUsername> volontari) {
        if (volontari == null || volontari.isEmpty()) {
            throw new IllegalArgumentException("Seleziona almeno un volontario.");
        }
        this.volontari = volontari;
        return this;
    }

    public TipoVisita build() {
        return new TipoVisita(
                0, // id generato dal DB
                titolo,
                descrizione,
                dataInizio,
                dataFine,
                oraInizio,
                durataMinuti,
                entrataLibera,
                numeroMinPartecipanti,
                numeroMaxPartecipanti,
                puntoIncontro,
                luogo,
                giorni,
                volontari
        );
    }
}