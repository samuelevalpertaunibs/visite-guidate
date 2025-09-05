package com.unibs.facade;

import com.unibs.models.*;

import com.unibs.utils.DatabaseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ITipoVisiteFacade {

    // -------------------- TIPO VISITA --------------------
    void aggiungiTipoVisita(String titolo, String descrizione, String dataInizio, String dataFine,
                            String oraInizio, String durata, boolean entrataLibera,
                            String numeroMin, String numeroMax, Luogo luogo,
                            Set<Volontario> volontari, Set<Giorno> giorni,
                            String indirizzo, String comune, String provincia) throws Exception;

    List<String> getPreviewTipiVisita() throws DatabaseException;
    List<String> getPreviewTipiVisita(String luogoNome) throws DatabaseException;
    boolean esisteAlmenoUnaVisita() throws DatabaseException;
    Optional<Integer> getIdTipoVisitaByNome(String nome) throws DatabaseException;
    List<TipoVisita> getVisiteByVolontario(int volontarioId);
    List<String> getTitoliTipiVisitaByVolontarioId(int volontarioId);
    List<String> getTitoloTipiVisitaByLuogoId(int luogoId);
    List<String> getAllTitoliTipiVisita();
    void inserisciTipoVisitaTVDaRimuovere(String titolo) throws Exception;

    // -------------------- VOLONTARI --------------------
    void rimuoviVolontariNonAssociati();
    List<Volontario> findAllVolontari() throws DatabaseException;
    void aggiungiVolontario(String username) throws Exception;
    void associaVolontariATipoVisita(Set<Volontario> volontari, int tipoVisitaId);
    Set<Volontario> getVolontariNonAssociatiByTipoVisitaId(int tipoVisitaId);
    void inserisciVolontarioDaRimuovere(String username) throws Exception;

    // -------------------- LUOGHI --------------------
    List<Luogo> getTuttiLuoghi() throws DatabaseException;
    Luogo aggiungiLuogo(String nome, String descrizione, Comune comune) throws Exception;

    // -------------------- COMUNI --------------------
    List<Comune> getAmbitoTerritoriale() throws DatabaseException;

    // -------------------- GIORNI --------------------
    List<Giorno> getGiorni() throws DatabaseException;
}
