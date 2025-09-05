package com.unibs.facades;

import com.unibs.models.*;
import com.unibs.services.*;
import com.unibs.utils.DatabaseException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TipoVisitaFacade implements ITipoVisitaFacade {

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

    // Implementazione di tutti i metodi definiti in ITipoVisiteFacade
    @Override
    public void aggiungiTipoVisita(String titolo, String descrizione, String dataInizio, String dataFine,
                                   String oraInizio, String durata, boolean entrataLibera,
                                   String numeroMin, String numeroMax, Luogo luogo,
                                   Set<CoppiaIdUsername> volontari, Set<Giorno> giorni,
                                   String indirizzo, String comune, String provincia) throws Exception {
        tipoVisitaService.aggiungiTipoVisita(titolo, descrizione, dataInizio, dataFine,
                oraInizio, durata, entrataLibera, numeroMin, numeroMax, luogo,
                volontari, giorni, indirizzo, comune, provincia);
    }

    @Override
    public List<String> getNomiTipiVisita() throws DatabaseException {
        return tipoVisitaService.getNomiTipiVisita();
    }

    @Override
    public List<String> getNomiTipiVisita(String luogoNome) throws DatabaseException {
        return tipoVisitaService.getNomiTipiVisita(luogoNome);
    }

    @Override
    public boolean esisteAlmenoUnaVisita() throws DatabaseException {
        return tipoVisitaService.esisteAlmenoUnaVisita();
    }

    @Override
    public Optional<Integer> cercaIDTipoVisitaPerNome(String nome) throws DatabaseException {
        return tipoVisitaService.getIdByNome(nome);
    }

    @Override
    public List<TipoVisita> cercaVisiteDelVolontario(int volontarioId) {
        return tipoVisitaService.findByVolontario(volontarioId);
    }

    @Override
    public List<String> cercaNomiDeiTipiVisitaDelVolontario(int volontarioId) {
        return tipoVisitaService.getTitoliByVolontarioId(volontarioId);
    }

    @Override
    public List<String> cercaTipiVisitaNelLuogo(int luogoId) {
        return tipoVisitaService.getTitoliByLuogoId(luogoId);
    }

    @Override
    public List<String> cercaTuttiTipiVisita() {
        return tipoVisitaService.getAllTitoli();
    }

    @Override
    public void inserisciTipoVisitaDaRimuovere(String titolo) throws Exception {
        tipoVisitaService.inserisciTVDaRimuovere(titolo);
    }

    @Override
    public void rimuoviVolontariNonAssociati() {
        volontarioService.rimuoviNonAssociati();
    }

    @Override
    public List<Volontario> cercaTuttiVolontari() throws DatabaseException {
        return volontarioService.findAllVolontari();
    }

    @Override
    public void aggiungiVolontario(String username) throws Exception {
        volontarioService.aggiungiVolontario(username);
    }

    @Override
    public void associaVolontariAlTipoVisita(Set<CoppiaIdUsername> volontari, int tipoVisitaId) {
        volontarioService.associaATipoVisita(volontari, tipoVisitaId);
    }

    @Override
    public Set<CoppiaIdUsername> cercaVolontariAssociabiliAlTipoVisita(int tipoVisitaId) {
        return volontarioService.getVolontariNonAssociatiByTipoVisitaId(tipoVisitaId);
    }

    @Override
    public void inserisciVolontarioDaRimuovere(String username) throws Exception {
        volontarioService.inserisciVolontarioDaRimuovere(username);
    }

    @Override
    public List<Luogo> cercaTuttiLuoghi() throws DatabaseException {
        return luogoService.findAll();
    }

    @Override
    public Luogo aggiungiLuogo(String nome, String descrizione, Comune comune) throws Exception {
        return luogoService.aggiungiLuogo(nome, descrizione, comune);
    }

    @Override
    public List<Comune> getAmbitoTerritoriale() throws DatabaseException {
        return configService.getAmbitoTerritoriale();
    }

    @Override
    public List<Giorno> getGiorni() throws DatabaseException {
        return giornoService.getGiorni();
    }
}
