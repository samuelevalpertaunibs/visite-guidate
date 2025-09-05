package com.unibs.facades;

import java.time.LocalDate;

public interface IDatePrecluseFacade {

    LocalDate getPrimaDataPrecludibile();

    void aggiungiDataPreclusa(LocalDate data) throws Exception;
}
