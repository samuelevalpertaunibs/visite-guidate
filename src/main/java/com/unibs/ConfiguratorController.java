package com.unibs;

import com.unibs.models.User;

import java.util.ArrayList;
import java.util.List;

public class ConfiguratorController implements IUserController {

    private final View view;
    private final User user;
    private Configuration config;
    private List<Place> places;
    private TerritorialEnvironment selectedEnvironment;
    private boolean enviromentLocked = false;

    public ConfiguratorController(View currentView, User currentUser) {
        this.view = currentView;
        this.user = currentUser;
        this.config = new Configuration();
        this.places = new ArrayList<>();
    }

    @Override
    public void start() {
        view.clearScreen();
        view.showMessage("Accesso effettuato come CONFIGURATORE\nBenvenuto, " + user.getUsername() + "!");
        showMenu();
    }

    @Override
    public void showMenu() {
        boolean exit = false;
        while (!exit) {
            view.clearScreen();
            view.showMessage("1. Imposta un ambiente territoriale");
            view.showMessage("2. Imposta un luogo");
            view.showMessage("3. Imposta un numero massimo di persone");
            view.showMessage("4. Imposta un tipo di visita");
            view.showMessage("5. Mostra configurazione corrente");
            view.showMessage("6. Inizializzazione Corpo Dati");

            int choice = view.getIntInput("Seleziona un'opzione: ", 1, 6);
            switch (choice) {
                case 1:
                    setEnvironmentMenu();
                    break;
                case 2:
                    setPlaceMenu();
                    break;
                case 3:
                    setMaxPeopleMenu();
                    break;
                case 4:
                    setTypeOfVisitMenu();
                    break;
                case 5:
                    showCurrentConfig();
                    break;
                case 6:
                    initDataBase();
                    break;

                default:
                    exit = true;
            }
        }

    }

    private void setEnvironmentMenu() {
        if (enviromentLocked)
            view.showMessage("L'ambiente territoriale è già stato impostato e non può essere modificato");

        view.clearScreen();
        view.showTitle("Impostazione Ambiente Territoriale");
        view.showMessage("1.NAZIONALE");
        view.showMessage("2.REGIONALE");
        view.showMessage("3.PROVINCIALE");
        view.showMessage("4.COMUNALE");
        view.showMessage("5.INDIRIZZO");

        int choice = view.getIntInput("Seleziona un ambiente territoriale: ", 1, 5);
        TerritorialEnvironment environment = TerritorialEnvironment.values()[choice - 1];

        if (!enviromentLocked) {
            this.selectedEnvironment = environment;
            this.config.setTerritorialEnvironment(selectedEnvironment);
            view.clearScreen("Ambiente Territoriale impostato: " + selectedEnvironment);
        } else {
            view.clearScreen("Impossibile impostatare l'ambiente territoriale");
        }
    }

    public void setPlaceMenu() {
        if (selectedEnvironment == null) {
            view.showMessage("Inserisci un ambiente territoriale");
            setEnvironmentMenu();
        }
        view.showTitle("Inserisci un luogo");
        String name = view.getInput("Nome: ");
        String description = view.getInput("Descrizione: ");
        String region = view.getInput("Regione: ");
        String district = view.getInput("Provincia: ");
        String city = view.getInput("Città: ");
        String address = view.getInput("Indirizzo: ");

        Place place = new Place(name, description, region, district, city, address);

        if (addPlace(place))
            view.showMessage("Luogo aggiunto con successo");
        else
            view.showMessage("Luogo non aggiunto");
    }

    private boolean addPlace(Place place) {
        if (places.isEmpty() && selectedEnvironment != null)
            enviromentLocked = true;
        if (isPlaceConsistentWithEnvironment(place, selectedEnvironment)) {
            places.add(place);
            return true;
        }
        return false;
    }

    private boolean isPlaceConsistentWithEnvironment(Place place, TerritorialEnvironment selectedEnvironment) throws IndexOutOfBoundsException {
        try {
            switch (selectedEnvironment) {
                case NAZIONALE:
                    return true;

                case REGIONALE:
                    return place.getRegion().equals(places.get(0).getRegion());

                case PROVINCIALE:
                    return place.getRegion().equals(places.get(0).getRegion()) &&
                            place.getDistrict().equals(places.get(0).getDistrict());

                case COMUNALE:
                    return place.getRegion().equals(places.get(0).getRegion()) &&
                            place.getCity().equals(places.get(0).getCity());

                case INDIRIZZO:
                    return place.getRegion().equals(places.get(0).getRegion()) &&
                            place.getCity().equals(places.get(0).getCity()) &&
                            place.getAddress().equals(places.get(0).getAddress());
                default:
                    return false;
            }
        } catch (IndexOutOfBoundsException e) {
            throw e;
        }
    }

    public void setMaxPeopleMenu() {
        if (selectedEnvironment == null) {
            view.showMessage("Inserisci un ambiente territoriale");
            setEnvironmentMenu();
        }

        view.clearScreen();
        view.showTitle("Imposta il numero massimo di persone");

        int maxPeople = view.getIntInput("Inserissci il numero massimo di persone: ", 0, 100);
        //config.setMaxPeople(maxPeople);
        setMaxPeople(maxPeople);
        view.clearScreen("Numero massimo di persone: " + maxPeople);
    }

    private void setMaxPeople(int maxPeople) {
        config.setMaxPeople(maxPeople);
    }

    public void setTypeOfVisitMenu(){}

    public void showCurrentConfig(){}

    public void initDataBase(){}
    
}