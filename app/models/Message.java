package models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
/**
 * Diese Klasse repraesentiert eine Nachricht, die von einem Benutzer an einen anderen gesendet wird.
 */
public class Message {
    private Date date;
    private User adressee;
    private User sender;
    private String message;

    /**
     * Erstellt eine neue Nachricht ohne Inhalt und ohne Empfaenger oder Sender.
     */
    public Message(){

    }

    /**
     * Erstellt eine neue Nachricht mit dem angegebenen Datum, Empfaenger, Sender und Inhalt.
     *
     * @param date Datum der Nachricht
     * @param adressee Empfaenger der Nachricht
     * @param sender Sender der Nachricht
     * @param message Inhalt der Nachricht
     */
    public Message(Date date, User adressee, User sender, String message) {
        this.date = date;
        this.adressee = adressee;
        this.sender = sender;
        this.message = message;
    }

    /**
     * Getter fuer das Datum der Nachricht.
     *
     * @return Datum der Nachricht
     */
    public Date getDate() {
        return date;
    }

    /**
     * Setter fuer das Datum der Nachricht.
     *
     * @param date Datum der Nachricht
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Getter fuer den Empfaenger der Nachricht.
     *
     * @return Der Empfaenger der Nachricht.
     */
    public User getAdressee() {
        return adressee;
    }

    /**
     * Setter fuer den Empfaenger der Nachricht.
     *
     * @param adressee Der Empfaenger der Nachricht.
     */
    public void setAdressee(User adressee) {
        this.adressee = adressee;
    }

    /**
     * Getter fuer den Absender der Nachricht.
     *
     * @return Der Absender der Nachricht.
     */
    public User getSender() {
        return sender;
    }

    /**
     * Setter fuer den Absender der Nachricht.
     *
     * @param sender Der Absender der Nachricht.
     */
    public void setSender(User sender) {
        this.sender = sender;
    }

    /**
     * Getter fuer den Inhalt der Nachricht.
     *
     * @return Der Inhalt der Nachricht.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter fuer den Inhalt der Nachricht.
     *
     * @param message Der Inhalt der Nachricht.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Konvertiert eine Nachricht in ein JSON-Format.
     *
     * @return Die Nachricht als JSON-String.
     * @throws JsonProcessingException Wenn ein Fehler beim Konvertieren auftritt.
     */
    public String convertMessageToJson() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(this);
    }
}
