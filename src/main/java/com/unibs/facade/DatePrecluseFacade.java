package com.unibs.facade;

import com.unibs.services.DatePrecluseService;
import com.unibs.services.ServiceFactory;

import java.time.LocalDate;

public class DatePrecluseFacade implements IDatePrecluseFacade {

    private final DatePrecluseService precluseService;

    public DatePrecluseFacade(ServiceFactory serviceFactory) {
        this.precluseService = serviceFactory.getDatePrecluseService();
    }

    @Override
    public LocalDate getPrimaDataPrecludibile() {
        return precluseService.getPrimaDataPrecludibile();
    }

    @Override
    public void aggiungiDataPreclusa(LocalDate data) throws Exception {
        precluseService.aggiungiDataPreclusa(data);
    }
}
