package com.unibs.services;

import com.unibs.mappers.ComuneMapper;
import com.unibs.mappers.LuogoMapper;
import com.unibs.utils.DatabaseException;
import com.unibs.daos.LuogoDao;
import com.unibs.models.Comune;
import com.unibs.models.Luogo;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LuogoService {

    private final LuogoDao luogoDao;
    private final ComuneService comuneService;
    private static final Logger LOGGER = Logger.getLogger(LuogoService.class.getName());

    public LuogoService() {
        this.luogoDao = new LuogoDao(new LuogoMapper(new ComuneMapper()));
        this.comuneService = new ComuneService();
    }

    public List<Luogo> findAll() throws DatabaseException {
        Map<Integer, Comune> comuniCache = new HashMap<>();
        try {
            List<Luogo> luoghi = luogoDao.findAll();

            for (Luogo luogo : luoghi) {
                int comuneId = luogo.getComuneId();
                // Se esiste nella cache un comune con questo Id allora lo utilizzo, altrimento prima lo aggiungo e poi lo utilizzo
                Comune comune = comuniCache.computeIfAbsent(comuneId, comuneService::findById);
                luogo.setComune(comune);
            }

            return luoghi;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dei luoghi", e);
            throw new DatabaseException("Errore durante il recupero dei luoghi.");
        }
    }

    public Luogo aggiungiLuogo(String nome, String descrizione, Comune comune) throws DatabaseException, IllegalArgumentException {
        if (comune == null)
            throw new IllegalArgumentException("Seleziona un comune.");

        if (nome.isBlank() || descrizione.isBlank())
            throw new IllegalArgumentException("I campi non possono essere vuoti.");

        if (findByNome(nome).isPresent()) {
            throw new IllegalArgumentException("Il luogo è già presente.");
        }

        try {
            Luogo luogoDaAggiungere = new Luogo(null, nome, descrizione, comune);
            return luogoDao.aggiungiLuogo(luogoDaAggiungere);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'aggiunta del luogo", e);
            throw new DatabaseException("Errore durante l'aggiunta del luogo.");
        }
    }

    // Ritorna Optional perchè non è detto che il Luogo esista
    public Optional<Luogo> findByNome(String nome) throws DatabaseException {
        try {
            Optional<Luogo> optionalLuogo = luogoDao.findByNome(nome);

            if (optionalLuogo.isPresent()) {
                Luogo luogo = optionalLuogo.get();
                Comune comune = comuneService.findById(luogo.getComuneId());
                luogo.setComune(comune);
                return Optional.of(luogo);
            }

            return Optional.empty();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero del luogo per nome: " + nome, e);
            throw new DatabaseException("Errore durante il recupero del luogo.");
        }
    }

    public void rimuoviNonAssociati() {
        try {
            List<Integer> luoghiNonAssociati = luogoDao.getIdNonAssociati();
            for (Integer id : luoghiNonAssociati) {
                luogoDao.rimuovi(id);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la rimozione dei volontari non associati", e);
            throw new DatabaseException("Impossibile rimuovere i volontari non associati ad alcuna visita.");
        }
    }

    public void applicaRimozioneLuoghi() {
        try {
            luogoDao.applicaRimozioneLuoghi();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la rimozione dei luoghi eliminati", e);
            throw new DatabaseException("Impossibile rimuovere i luoghi eliminati.");
        }
    }

    public void inserisciLuogoDaRimuovere(Integer idLuogo) {
        try {
            luogoDao.inserisciLuogoDaRimuovere(idLuogo);
            luogoDao.terminaTVAssociatiAlLuogo(idLuogo);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la rimozione dei luoghi eliminati", e);
            throw new DatabaseException("Impossibile rimuovere il luogo selezionato.");
        }
    }

}
