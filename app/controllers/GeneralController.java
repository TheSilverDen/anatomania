package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import play.mvc.Controller;
/**
 * Die Klasse GeneralController ist eine abstrakte Klasse,
 * die von anderen Controllern verwendet wird, um die Jackson
 * ObjectMapper-Konfiguration zu ermoeglichen.
 */
public class GeneralController extends Controller {

    protected ObjectMapper mapper = new ObjectMapper();

    /**
     * Der Konstruktor von GeneralController, der die Konfiguration der ObjectMapper initialisiert.
     */
    public GeneralController() {
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

}
