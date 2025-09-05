package com.unibs.facades;

import com.unibs.models.*;
import com.unibs.services.*;
import com.unibs.utils.DatabaseException;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TipoVisitaFacade {

    private final TipoVisitaService tipoVisitaService;
    private final VolontarioService volontarioService;
    private final LuogoService luogoService;
    private final ConfigService configService;
    private final GiornoService giornoService;

    public TipoVisitaFacade(ServiceFactory serviceFactory) {
        this.tipoVisitaService = serviceFactory.getTipoVisitaService();
        this.volontarioService = serviceFactory.getVolontarioService();
        this.luogoService = serviceFactory.getLuogoService();
        this.configService = serviceFactory.getConfigService();
        this.giornoService = serviceFactory.getGiornoService();
    }

    // -------------------- TIPO VISITA --------------------
    public void aggiungiTipoVisita(java.lang.String titolo, java.lang.String descrizione, java.lang.String dataInizio, java.lang.String dataFine,
                                   java.lang.String oraInizio, java.lang.String durata, boolean entrataLibera,
                                   java.lang.String numeroMin, java.lang.String numeroMax, Luogo luogo,
                                   Set<Volontario> volontari, Set<Giorno> giorni,
                                   java.lang.String indirizzo, java.lang.String comune, java.lang.String provincia) throws Exception {
        tipoVisitaService.aggiungiTipoVisita(titolo, descrizione, dataInizio, dataFine,
                oraInizio, durata, entrataLibera, numeroMin, numeroMax, luogo,
                volontari, giorni, indirizzo, comune, provincia);
    }

    public List<java.lang.String> getNomiTipiVisita() throws DatabaseException {
        return tipoVisitaService.getNomiTipiVisita();
    }

    public List<java.lang.String> getNomiTipiVisita(java.lang.String luogoNome) throws DatabaseException {
        return tipoVisitaService.getNomiTipiVisita(luogoNome);
    }

    public boolean esisteAlmenoUnaVisita() throws DatabaseException {
        return tipoVisitaService.esisteAlmenoUnaVisita();
    }

    public Optional<Integer> cercaIDTipoVisitaPerNome(java.lang.String nome) throws DatabaseException {
        return tipoVisitaService.getIdByNome(nome);
    }

    public List<TipoVisita> cercaVisiteDelVolontario(int volontarioId) {
        return tipoVisitaService.findByVolontario(volontarioId);
    }

    public List<java.lang.String> cercaNomiDeiTipiVisitaDelVolontario(int volontarioId) {
        return tipoVisitaService.getTitoliByVolontarioId(volontarioId);
    }

    public List<java.lang.String> cercaTipiVisitaNelLuogo(int luogoId) {
        return tipoVisitaService.getTitoliByLuogoId(luogoId);
    }

    public List<java.lang.String> cercaTuttiTipiVisita() {
        return tipoVisitaService.getAllTitoli();
    }

    public void inserisciTipoVisitaDaRimuovere(java.lang.String titolo) throws Exception {
        tipoVisitaService.inserisciTVDaRimuovere(titolo);
    }

    // -------------------- VOLONTARI --------------------
    public void rimuoviVolontariNonAssociati() {
        volontarioService.rimuoviNonAssociati();
    }

    public List<Volontario> cercaTuttiVolontari() throws DatabaseException {
        return volontarioService.findAllVolontari();
    }

    public void aggiungiVolontario(java.lang.String username) throws Exception {
        volontarioService.aggiungiVolontario(username);
    }

    public void associaVolontariAlTipoVisita(Set<Volontario> volontari, int tipoVisitaId) {
        volontarioService.associaATipoVisita(volontari, tipoVisitaId);
    }

    public HashMap<Integer, String> cercaVolontariAssociabiliAlTipoVisita(int tipoVisitaId) {
        return volontarioService.getVolontariNonAssociatiByTipoVisitaId(tipoVisitaId);
    }

    public void inserisciVolontarioDaRimuovere(java.lang.String username) throws Exception {
        volontarioService.inserisciVolontarioDaRimuovere(username);
    }

    // -------------------- LUOGHI --------------------
    public List<Luogo> cercaTuttiLuoghi() throws DatabaseException {
        return luogoService.findAll();
    }

    public Luogo aggiungiLuogo(java.lang.String nome, java.lang.String descrizione, Comune comune) throws Exception {
        return luogoService.aggiungiLuogo(nome, descrizione, comune);
    }

    // -------------------- COMUNI --------------------
    public List<Comune> getAmbitoTerritoriale() throws DatabaseException {
        return configService.getAmbitoTerritoriale();
    }

    // -------------------- GIORNI --------------------
    public List<Giorno> getGiorni() throws DatabaseException {
        return giornoService.getGiorni();
    }
}
