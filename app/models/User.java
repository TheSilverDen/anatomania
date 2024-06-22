package models;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Die Klasse User repraesentiert einen Benutzer in der Datenbank.
 * Ein Benutzer hat einen Benutzernamen, einen Vornamen, einen Nachnamen,
 * eine E-Mail-Adresse, ein Passwort, ein Profilbild und einen Highscore.
 */
public class User {
    private int user_ID;
    private String username;
    private String surname;
    private String first_name;
    private String email;
    private String password;
    private String profile_picture;
    private int highscore;

    /**
     * Konstruktor der Klasse User.
     * Erzeugt einen Benutzer ohne Parameter.
     */
    public User(){
    }

    /**
     * Konstruktor der Klasse User.
     * Erzeugt einen Benutzer mit den uebergebenen Parametern.
     *
     * @param user_ID Die ID des Benutzers.
     * @param username Der Benutzername des Benutzers.
     * @param surname Der Nachname des Benutzers.
     * @param first_name Der Vorname des Benutzers.
     * @param email Die E-Mail-Adresse des Benutzers.
     * @param password Das Passwort des Benutzers.
     * @param profile_picture Das Profilbild des Benutzers.
     * @param highscore Der Highscore des Benutzers.
     */
    public User(int user_ID, String username, String surname, String first_name, String email, String password, String profile_picture, int highscore) {
        this.user_ID = user_ID;
        this.username = username;
        this.email = email;
        this.surname = surname;
        this.first_name = first_name;
        this.password = password;
        this.profile_picture = profile_picture;
        this.highscore = highscore;
    }

    /**
     * Konstruktor der Klasse User.
     * Erzeugt einen Benutzer mit den uebergebenen Parametern.
     *
     * @param username Der Benutzername des Benutzers.
     * @param surname Der Nachname des Benutzers.
     * @param first_name Der Vorname des Benutzers.
     * @param email Die E-Mail-Adresse des Benutzers.
     * @param password Das Passwort des Benutzers.
     * @param profile_picture Das Profilbild des Benutzers.
     * @param highscore Der Highscore des Benutzers.
     */
    public User(String username, String surname, String first_name, String email, String password, String profile_picture, int highscore) {
        this.username = username;
        this.email = email;
        this.surname = surname;
        this.first_name = first_name;
        this.password = password;
        this.profile_picture = profile_picture;
        this.highscore = highscore;
    }

    /**
     * Konstruktor der Klasse User.
     * Erzeugt einen Benutzer mit den uebergebenen Parametern.
     *
     * @param user_ID Die ID des Benutzers.
     * @param username Der Benutzername des Benutzers.
     * @param surname Der Nachname des Benutzers.
     * @param first_name Der Vorname des Benutzers.
     * @param email Die E-Mail-Adresse des Benutzers.
     * @param password Das Passwort des Benutzers.
     * @param profile_picture Das Profilbild des Benutzers.
     */
    public User(int user_ID, String username, String surname, String first_name, String email, String password, String profile_picture) {
        this.user_ID = user_ID;
        this.username = username;
        this.email = email;
        this.surname = surname;
        this.first_name = first_name;
        this.password = password;
        this.profile_picture = profile_picture;
    }

    /**
     * Konstruktor fuer einen Benutzer mit minimalen Attributen.
     *
     * @param user_ID  die ID des Benutzers
     * @param username der Benutzername
     * @param email    die E-Mail-Adresse
     * @param password das Passwort
     */
    public User(int user_ID, String username, String email, String password) {
        this.user_ID = user_ID;
        this.username = username;
        this.email = email;
        this.password = password;
        this.highscore = 0;
    }

    /**
     * Erstellt ein neues User-Objekt mit der uebergebenen ID, dem Benutzernamen und Passwort.
     * Setzt den Highscore auf 0.
     *
     * @param id Die ID des Benutzers
     * @param username Der Benutzername
     * @param password Das Passwort des Benutzers
     */
    public User(int id, String username, String password){
        this.user_ID = id;
        this.username = username;
        this.password = password;
        this.highscore = 0;
    }

    /**
     * Erstellt ein neues User-Objekt aus dem ResultSet.
     *
     * @param rs Das ResultSet, aus dem das User-Objekt erstellt werden soll
     * @throws SQLException Wenn das ResultSet keine gueltigen Daten enthaelt
     */
    public User(ResultSet rs) throws SQLException {
        this.user_ID = rs.getInt("user_ID");
        this.username = rs.getString("username");
        this.email = rs.getString("email");
        this.surname = rs.getString("surname");
        this.first_name = rs.getString("first_name");
        this.password = rs.getString("password");
        this.profile_picture = rs.getString("profile_picture");
        this.highscore = rs.getInt("highscore");
    }

    /**
     * Getter fuer den Benutzernamen.
     *
     * @return Benutzername
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter fuer den Benutzernamen.
     *
     * @param username neuer Benutzername
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter fuer den Highscore des Benutzers.
     *
     * @return Highscore des Benutzers
     */
    public int getHighscore() {
        return highscore;
    }

    /**
     * Setter fuer den Highscore des Benutzers.
     *
     * @param highscore neuer Highscore
     */
    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }

    /**
     * Getter fuer die ID des Benutzers.
     *
     * @return user_ID des Benutzers
     */
    public int getUser_ID() {
        return user_ID;
    }

    /**
     * Getter fuer den Nachnamen des Benutzers.
     *
     * @return Nachname des Benutzers
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Setter fuer den Nachnamen des Benutzers.
     *
     * @param surname neuer Nachname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Getter fuer den Vornamen des Benutzers.
     *
     * @return first_name des Benutzers
     */
    public String getFirst_name() {
        return first_name;
    }

    /**
     * Setter fuer den Vornamen des Benutzers.
     *
     * @param first_name neuer Vorname
     */
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    /**
     * Getter fuer die E-Mail-Adresse des Benutzers.
     *
     * @return email des Benutzers
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter fuer die E-Mail-Adresse des Benutzers.
     *
     * @param email neue E-Mail-Adresse
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter fuer das Passwort des Benutzers.
     *
     * @return Passwort des Benutzers
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter fuer das password des Benutzers.
     *
     * @param password des Benutzers
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter fuer das profile_picture des Benutzers.
     *
     * @return profile_picture des Benutzers
     */
    public String getProfile_picture() {
        return profile_picture;
    }

    /**
     * Setter fuer das profile_picture des Benutzers.
     *
     * @param profile_picture neue E-Mail-Adresse
     */
    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }
}