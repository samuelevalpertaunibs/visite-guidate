package com.unibs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import com.unibs.models.Volontario;

public class VolontarioDao {

    public static Volontario findByUsername(String username) throws DatabaseException {
        String sql = "SELECT username, password_hash, salt, role, last_login, aviability FROM volontario WHERE username = ?";
        try (
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Volontario(rs.getString("username"), rs.getString("password_hash"), rs.getBytes("salt"),
                        rs.getString("role"), rs.getObject("last_login", LocalDate.class), rs.getBoolean("aviability"));
            }
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
        return null;
    }

    public static void registerVolontario(String username, String newPassword, LocalDate last_login, boolean aviability) throws DatabaseException {
        String sql = "UPDATE volontario SET password_hash = ?, last_login = ?, disponibilita = ? WHERE username = ?";
        try (
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setDate(2, java.sql.Date.valueOf(last_login));
            stmt.setBoolean(3, aviability);
            stmt.setString(4, username);
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    //metodo che recupera le informazioni di un volontario dal database noto lo username
    public static Volontario getVolontario(String username) throws DatabaseException {
        String sql = "SELECT username, password_hash, salt, role, last_login, aviability FROM volontario WHERE username = ?";
        try (
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Volontario(rs.getString("username"), rs.getString("password_hash"), rs.getBytes("salt"), rs.getString("role"),
                        rs.getObject("last_login", LocalDate.class), rs.getBoolean("aviability"));
            } else {
                return null; // Nessun volontario trovato
            }
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    //metodo che recupera le informazioni di un volontario dal database, ponendole in una lista poiché non conosciamo nessuna info lato user (info solo lato DB)
    public static List<Volontario> getVolontari() throws DatabaseException {
        String sql = "SELECT username, password_hash, salt, role, last_login, disponibilita FROM volontario";
        List<Volontario> listaVolontari = new ArrayList<>();

        try (
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                listaVolontari.add(new Volontario(rs.getString("username"), rs.getString("password_hash"), rs.getBytes("salt"),
                        rs.getString("role"), rs.getObject("last_login", LocalDate.class), rs.getBoolean("disponibilita")));
            }
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
        return listaVolontari;
    }


    public static int updateLastLogin(String username) throws DatabaseException {
        String sql = "UPDATE volontario SET last_login = ? WHERE username = ?";
        try (
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setString(2, username);
            return stmt.executeUpdate();

        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    //metodo che ritorna il numero di righe affette dall'aggiornamento (sul DB) sulla disponibilità di un certo volontario
    public static int updateDisponibilita(String username, boolean aviability) throws DatabaseException {
        String sql = "UPDATE volontario SET disponibilita = ? WHERE username = ?";
        try (
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, aviability);
            stmt.setString(2, username);
            return stmt.executeUpdate(); //ritorna numero di righe affette da aggiornamento

        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}