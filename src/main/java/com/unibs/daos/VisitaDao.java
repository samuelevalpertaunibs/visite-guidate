package com.unibs.daos;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;
import com.unibs.models.Visita;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VisitaDao {

    public static List<Visita> getVisiteByStato(Visita.StatoVisita stato) {
        List<Visita> visite = new ArrayList<>();

        String sql = "SELECT v.id, v.tipo_visita_id, v.data_svolgimento, v.stato, t.titolo AS tipo_nome " +
                "FROM visite v " +
                "JOIN tipi_visita t ON v.tipo_visita_id = t.id " +
                "WHERE v.stato = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stato.name());  // enum to SQL string

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int tipoId = rs.getInt("tipo_visita_id");
                String tipoNome = rs.getString("tipo_nome");
                LocalDate data = rs.getObject("data_svolgimento", LocalDate.class);
                Visita.StatoVisita statoVisita = Visita.StatoVisita.valueOf(rs.getString("stato"));

                //TODO: tipoVisita è null, da sistemare
                //TipoVisita tipoVisita = new TipoVisita(tipoId, tipoNome);
                //visite.add(new Visita(id, null, data, statoVisita));
            }

        } catch (Exception e) {
            throw new DatabaseException("Errore nel recupero delle visite per stato: " + e.getMessage());
        }

        return visite;
    }

}
