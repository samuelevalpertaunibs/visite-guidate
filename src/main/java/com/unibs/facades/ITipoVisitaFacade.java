package com.unibs.facades;

import com.unibs.models.*;

import com.unibs.utils.DatabaseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ITipoVisitaFacade {

    // -------------------- TIPO VISITA --------------------
    void aggiungiTipoVisita(String titolo, String descrizione, String dataInizio, String dataFine,
                            String oraInizio, String durata, boolean entrataLibera,
                            String numeroMin, String numeroMax, Luogo luogo,
                            Set<CoppiaIdUsername> volontari, Set<Giorno> giorni,
                            String indirizzo, String comune, String provincia) throws Exception;

    List<String> getNomiTipiVisita() throws DatabaseException;
    List<String> getNomiTipiVisita(String luogoNome) throws DatabaseException;
    boolean esisteAlmenoUnaVisita() throws DatabaseException;
    Optional<Integer> cercaIDTipoVisitaPerNome(String nome) throws DatabaseException;
    List<TipoVisita> cercaVisiteDelVolontario(int volontarioId);
    List<String> cercaNomiDeiTipiVisitaDelVolontario(int volontarioId);
    List<String> cercaTipiVisitaNelLuogo(int luogoId);
    List<String> cercaTuttiTipiVisita();
    void inserisciTipoVisitaDaRimuovere(String titolo) throws Exception;

    // -------------------- VOLONTARI --------------------
    void rimuoviVolontariNonAssociati();
    List<Volontario> cercaTuttiVolontari() throws DatabaseException;
    void aggiungiVolontario(String username) throws Exception;
    void associaVolontariAlTipoVisita(Set<CoppiaIdUsername> volontari, int tipoVisitaId);
    Set<CoppiaIdUsername> cercaVolontariAssociabiliAlTipoVisita(int tipoVisitaId);
    void inserisciVolontarioDaRimuovere(String username) throws Exception;

    // -------------------- LUOGHI --------------------
    List<Luogo> cercaTuttiLuoghi() throws DatabaseException;
    Luogo aggiungiLuogo(String nome, String descrizione, Comune comune) throws Exception;

    // -------------------- COMUNI --------------------
    List<Comune> getAmbitoTerritoriale() throws DatabaseException;

    // -------------------- GIORNI --------------------
    List<Giorno> getGiorni() throws DatabaseException;
}
