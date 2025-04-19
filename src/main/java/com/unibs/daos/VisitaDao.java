package com.unibs.daos;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;
import com.unibs.models.PuntoIncontro;
import com.unibs.models.TipoVisita;
import com.unibs.models.Visita;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisitaDao {

    /**
     * Ritorna una lista di visite con alcuni campi valorizzati.
     * Gli altri campi non sono inizializzati.
     */
    public static List<Visita> getVisitePreview(Visita.StatoVisita stato) {
        List<Visita> visite = new ArrayList<>();
        Map<Integer, TipoVisita> tipoVisitaCache = new HashMap<>();

        String sql = """
                SELECT v.tipo_visita_id, t.indirizzo_incontro, t.comune_incontro, t.provincia_incontro, t.titolo, t.descrizione, t.ora_inizio, t.entrata_libera, v.data_svolgimento, v.stato
                FROM visite v
                JOIN tipi_visita t ON v.tipo_visita_id = t.id
                WHERE v.stato = ?
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stato.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int tipoVisitaId = rs.getInt("tipo_visita_id");
                String indirizzo = rs.getString("indirizzo_incontro");
                String comune = rs.getString("comune_incontro");
                String provincia = rs.getString("provincia_incontro");
                String titolo = rs.getString("titolo");
                String descrizione = rs.getString("descrizione");
                LocalTime oraInizio = rs.getTime("ora_inizio").toLocalTime();
                boolean entrataLibera = rs.getBoolean("entrata_libera");
                LocalDate dataSvolgimento = rs.getDate("data_svolgimento").toLocalDate();
                Visita.StatoVisita statoVisita = Visita.StatoVisita.valueOf(rs.getString("stato"));

                // Utilizzo una cache per evitare di creare un nuovo tipo visita per ogni istanza di visita
                TipoVisita tipoVisita = tipoVisitaCache.get(tipoVisitaId);
                if (tipoVisita == null) {
                    PuntoIncontro puntoIncontro = new PuntoIncontro(indirizzo, comune, provincia);
                    tipoVisita = new TipoVisita(titolo, descrizione, puntoIncontro, oraInizio, entrataLibera);
                    tipoVisitaCache.put(tipoVisitaId, tipoVisita);
                }

                visite.add(new Visita(tipoVisita, dataSvolgimento, statoVisita));
            }

        } catch (Exception e) {
            throw new DatabaseException("Errore nel recupero delle visite per stato: " + e.getMessage());
        }

        return visite;
    }

}
