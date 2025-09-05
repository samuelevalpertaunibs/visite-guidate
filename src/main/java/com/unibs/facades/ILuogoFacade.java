package com.unibs.facades;

import com.unibs.models.Luogo;

import java.util.List;

public interface ILuogoFacade {

    List<Luogo> findAll() ;

    void inserisciLuogoDaRimuovere(int idLuogo) throws Exception;
}
