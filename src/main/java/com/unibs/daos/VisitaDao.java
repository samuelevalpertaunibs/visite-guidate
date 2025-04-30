package com.unibs.daos;

import com.unibs.utils.DatabaseManager;
import com.unibs.models.PuntoIncontro;
import com.unibs.models.TipoVisita;
import com.unibs.models.Visita;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                        v.stato
                    FROM
                        visite v
                    JOIN
                        tipi_visita t
                    ON
                        v.tipo_visita_id = t.id
                    WHERE
                        v.stato = ?
                
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

                // Utilizzo una cache per evitare di creare un nuovo tipo visita per ogni istanza di visita
                TipoVisita tipoVisita = tipoVisitaCache.get(tipoVisitaId);
                if (tipoVisita == null) {
                    PuntoIncontro puntoIncontro = new PuntoIncontro(indirizzo, comune, provincia);
                    tipoVisita = new TipoVisita(tipoVisitaId, titolo, descrizione, dataInizio, dataFine, oraInizio, durataMinuti, entrataLibera, numMinPartecipanti, numMaxPartecipanti, puntoIncontro);
                    tipoVisitaCache.put(tipoVisitaId, tipoVisita);
                }

                visite.add(new Visita(tipoVisita, dataSvolgimento, statoVisita));
            }
        }
        return visite;
    }

}
