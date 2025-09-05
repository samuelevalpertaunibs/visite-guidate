package com.unibs.mappers;

import com.unibs.models.PuntoIncontro;
import com.unibs.models.TipoVisitaCore;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoVisitaCoreMapper implements RowMapper<TipoVisitaCore> {

    @Override
    public TipoVisitaCore map(ResultSet rs) throws SQLException {

        PuntoIncontro puntoIncontro = new PuntoIncontro(
                rs.getString("tv_indirizzo_incontro"),
                rs.getString("tv_comune_incontro"),
                rs.getString("tv_provincia_incontro")
        );

        return new TipoVisitaCore(
                rs.getInt("tv_id"),
                rs.getString("tv_titolo"),
                rs.getString("tv_descrizione"),
                rs.getDate("tv_data_inizio").toLocalDate(),
                rs.getDate("tv_data_fine").toLocalDate(),
                rs.getTime("tv_ora_inizio").toLocalTime(),
                rs.getInt("tv_durata_minuti"),
                rs.getBoolean("tv_entrata_libera"),
                rs.getInt("tv_num_min_partecipanti"),
                rs.getInt("tv_num_max_partecipanti"),
                puntoIncontro
        );
    }
}