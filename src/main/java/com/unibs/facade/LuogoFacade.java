package com.unibs.facade;

import com.unibs.models.Luogo;
import com.unibs.services.LuogoService;
import com.unibs.services.ServiceFactory;

import java.util.List;

public class LuogoFacade implements ILuogoFacade {

    private final LuogoService luogoService;

    public LuogoFacade(ServiceFactory serviceFactory) {
        this.luogoService = serviceFactory.getLuogoService();
    }

    @Override
    public List<Luogo> findAll() {
         return luogoService.findAll();
    }

    @Override
    public void inserisciLuogoDaRimuovere(int idLuogo) throws Exception {
        luogoService.inserisciLuogoDaRimuovere(idLuogo);
    }
}
