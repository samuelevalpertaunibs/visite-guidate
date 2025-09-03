package com.unibs.facade;

import com.unibs.models.*;
import com.unibs.services.*;
import com.unibs.utils.DatabaseException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TipoVisiteFacade {

    private final TipoVisitaService tipoVisitaService;
    private final VolontarioService volontarioService;
    private final LuogoService luogoService;
    private final ConfigService configService;
    private final GiornoService giornoService;

    public TipoVisiteFacade(ServiceFactory serviceFactory) {
        this.tipoVisitaService = serviceFactory.getTipoVisitaService();
        this.volontarioService = serviceFactory.getVolontarioService();
        this.luogoService = serviceFactory.getLuogoService();
        this.configService = serviceFactory.getConfigService();
        this.giornoService = serviceFactory.getGiornoService();
    }

    // -------------------- TIPO VISITA --------------------
    public void aggiungiTipoVisita(String titolo, String descrizione, String dataInizio, String dataFine,
                               String oraInizio, String durata, boolean entrataLibera,
                               String numeroMin, String numeroMax, Luogo luogo,
                               Set<Volontario> volontari, Set<Giorno> giorni,
                               String indirizzo, String comune, String provincia) throws Exception {
        tipoVisitaService.aggiungiTipoVisita(titolo, descrizione, dataInizio, dataFine,
                oraInizio, durata, entrataLibera, numeroMin, numeroMax, luogo,
                volontari, giorni, indirizzo, comune, provincia);
    }

    public List<String> getPreviewTipiVisita() throws DatabaseException {
        return tipoVisitaService.getPreviewTipiVisita();
    }

    public List<String> getPreviewTipiVisita(String luogoNome) throws DatabaseException {
        return tipoVisitaService.getPreviewTipiVisita(luogoNome);
    }

    public boolean esisteAlmenoUnaVisita() throws DatabaseException {
        return tipoVisitaService.esisteAlmenoUnaVisita();
    }

    public Optional<Integer> getIdTipoVisitaByNome(String nome) throws DatabaseException {
        return tipoVisitaService.getIdByNome(nome);
    }

    public List<TipoVisita> getVisiteByVolontario(int volontarioId) {
        return tipoVisitaService.findByVolontario(volontarioId);
    }

    public List<String> getTitoliTipiVisitaByVolontarioId(int volontarioId) {
        return tipoVisitaService.getTitoliByVolontarioId(volontarioId);
    }

    public List<String> getTitoloTipiVisitaByLuogoId(int luogoId) {
        return tipoVisitaService.getTitoliByLuogoId(luogoId);
    }

    public List<String> getAllTitoliTipiVisita() {
        return tipoVisitaService.getAllTitoli();
    }

    public void inserisciTipoVisitaTVDaRimuovere(String titolo) throws Exception {
        tipoVisitaService.inserisciTVDaRimuovere(titolo);
    }

    // -------------------- VOLONTARI --------------------
    public void rimuoviVolontariNonAssociati() {
        volontarioService.rimuoviNonAssociati();
    }

    public List<Volontario> findAllVolontari() throws DatabaseException {
        return volontarioService.findAllVolontari();
    }

    public void aggiungiVolontario(String username) throws Exception {
        volontarioService.aggiungiVolontario(username);
    }

    public void associaVolontariATipoVisita(Set<Volontario> volontari, int tipoVisitaId) {
        volontarioService.associaATipoVisita(volontari, tipoVisitaId);
    }

    public Set<Volontario> getVolontariNonAssociatiByTipoVisitaId(int tipoVisitaId) {
        return volontarioService.getVolontariNonAssociatiByTipoVisitaId(tipoVisitaId);
    }

    public void inserisciVolontarioDaRimuovere(String username) throws Exception {
        volontarioService.inserisciVolontarioDaRimuovere(username);
    }

    // -------------------- LUOGHI --------------------
    public List<Luogo> getTuttiLuoghi() throws DatabaseException {
        return luogoService.findAll();
    }

    public Luogo aggiungiLuogo(String nome, String descrizione, Comune comune) throws Exception {
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
