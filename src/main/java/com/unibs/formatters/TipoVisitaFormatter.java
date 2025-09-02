package com.unibs.formatters;

import com.unibs.models.TipoVisita;

import java.time.format.DateTimeFormatter;

public class TipoVisitaFormatter implements Formatter<TipoVisita> {
    PuntoIncontroFormatter puntoIncontroFormatter = new PuntoIncontroFormatter();
    @Override
    public String format(TipoVisita tv) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataInizioString = tv.getDataInizio().format(formatter);
        String dataFineString = tv.getDataFine().format(formatter);
        return "Titolo: " + tv.getTitolo() + "\n" +
                "Descrizione: " + tv.getDescrizione() + "\n" +
                "Data inizio: " + dataInizioString + "\n" +
                "Data fine: " + dataFineString + "\n" +
                "Ora inizio: " + tv.getOraInizio() + "\n" +
                "Durata (minuti): " + tv.getDurataMinuti() + "\n" +
                "Entrata libera: " + (tv.isEntrataLibera() ? "Sì" : "No") + "\n" +
                "Numero minimo partecipanti: " + tv.getNumMinPartecipanti() + "\n" +
                "Numero massimo partecipanti: " + tv.getNumMaxPartecipanti() + "\n" +
                "Luogo: " + (tv.getLuogo() != null ? tv.getLuogo().getNome() : "N/D") + "\n" +
                "Punto incontro: " + (tv.getPuntoIncontro() != null ? puntoIncontroFormatter.format(tv.getPuntoIncontro()) : "N/D");
    }
}
