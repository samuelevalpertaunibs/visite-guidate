package com.unibs.daos;

import com.unibs.models.*;
import com.unibs.utils.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisitaDao {

    /**
     * Ritorno una lista di Visite, i cui tipi di visita associati sono solo PARZIALMENTE inizializzati
     */
    public List<Visita> getVisitePreview(Visita.StatoVisita stato) throws SQLException {
        List<Visita> visite = new ArrayList<>();
        Map<Integer, TipoVisita> tipoVisitaCache = new HashMap<>();

        String sql = """
                                    SELECT
                                        v.tipo_visita_id,
                                        t.indirizzo_incontro,
                                        t.comune_incontro,
                                        t.provincia_incontro,
                                        t.titolo,
                                        t.descrizione,
                                        t.data_inizio,
                                        t.data_fine,
                                        t.ora_inizio,
                                        t.durata_minuti,
                                        t.entrata_libera,
                                        t.num_min_partecipanti,
                                        t.num_max_partecipanti,
                                        v.data_svolgimento,
                                        v.stato,
                                        u.username,
                                        l.nome
                                    FROM
                                        visite v
                                    JOIN tipi_visita t ON v.tipo_visita_id = t.id
                                    JOIN utenti u ON volontario_id = u.id
                                    JOIN luoghi l ON t.luogo_id = l.id
                                    WHERE
                                        v.stato = ?
                ORDER BY v.data_svolgimento
                """;

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stato.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int tipoVisitaId = rs.getInt("tipo_visita_id");
                String indirizzo = rs.getString("indirizzo_incontro");
                String comune = rs.getString("comune_incontro");
                String provincia = rs.getString("provincia_incontro");
                String titolo = rs.getString("titolo");
                String descrizione = rs.getString("descrizione");
                LocalDate dataInizio = rs.getDate("data_inizio").toLocalDate();
                LocalDate dataFine = rs.getDate("data_fine").toLocalDate();
                LocalTime oraInizio = rs.getTime("ora_inizio").toLocalTime();
                int durataMinuti = rs.getInt("durata_minuti");
                boolean entrataLibera = rs.getBoolean("entrata_libera");
                int numMinPartecipanti = rs.getInt("num_min_partecipanti");
                int numMaxPartecipanti = rs.getInt("num_max_partecipanti");
                LocalDate dataSvolgimento = rs.getDate("data_svolgimento").toLocalDate();
                Visita.StatoVisita statoVisita = Visita.StatoVisita.valueOf(rs.getString("stato"));
                String nomeVolontario = rs.getString("username");
                String luogoNome = rs.getString("nome");

                // Utilizzo una cache per evitare di creare un nuovo tipo visita per ogni istanza di visita
                TipoVisita tipoVisita = tipoVisitaCache.get(tipoVisitaId);
                if (tipoVisita == null) {
                    PuntoIncontro puntoIncontro = new PuntoIncontro(indirizzo, comune, provincia);
                    Luogo luogo = new Luogo(null, luogoNome, null, null);
                    tipoVisita = new TipoVisita(tipoVisitaId, titolo, descrizione, dataInizio, dataFine, oraInizio, durataMinuti, entrataLibera, numMinPartecipanti, numMaxPartecipanti, luogo, puntoIncontro, null, null);
                    tipoVisitaCache.put(tipoVisitaId, tipoVisita);
                }

                visite.add(new Visita(tipoVisita, dataSvolgimento, new Volontario(null, nomeVolontario, null, null, null), statoVisita));
            }
        }
        return visite;
    }

    public void inserisciVisite(List<Visita> tutteLeVisite) throws SQLException {
        String sql = "INSERT INTO visite (tipo_visita_id, volontario_id, data_svolgimento, stato) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Visita visita : tutteLeVisite) {
                stmt.setInt(1, visita.getTipoVisita().getId());
                stmt.setInt(2, visita.getVolontario().getId());
                stmt.setDate(3, Date.valueOf(visita.getDataSvolgimento()));
                stmt.setString(4, visita.getStato().name());
                stmt.addBatch();
            }

            // Eseguo tutte le insert una sola volta assieme
            stmt.executeBatch();
        }
    }

    @SuppressWarnings("SqlWithoutWhere")
    public void rimuoviDisponibilita() throws SQLException {
        String sql = "DELETE FROM disponibilita";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate(); // Esegue direttamente la DELETE
        }
    }
}
