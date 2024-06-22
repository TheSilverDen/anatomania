package models.factories;

import models.User;
import play.db.Database;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * Die UserFactory-Klasse implementiert Methoden zur Erstellung, Abfrage und Verwaltung von Usern.
 * Die Klasse verwendet Dependency Injection, um eine Datenbankverbindung zu erhalten und arbeitet mit einer
 * play.db.Database-Instanz.
 *
 */
public class UserFactory {

    private Database db;

    /**
     * Konstruktor der Klasse, der die Abhaengigkeit von der Datenbank injiziert.
     *
     * @param db Die Datenbank, auf die zugegriffen werden soll.
     */
    @Inject
    UserFactory(Database db) {
        this.db = db;
    }

    /**
     * Ueberprueft, ob ein Benutzer mit dem angegebenen Benutzernamen und Passwort vorhanden ist.
     *
     * @param username Benutzername.
     * @param password Passwort.
     * @return Das Benutzerobjekt oder null, wenn der Benutzer nicht gefunden wurde.
     */
    public User authenticate(String username, String password) {
        return db.withConnection(conn -> {
            User user = null;
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs);
            }
            stmt.close();
            return user;
        });
    }

    /**
     * Erstellt einen neuen Benutzer mit den angegebenen Daten und speichert ihn in der Datenbank.
     *
     * @param user  User.
     * @return Das erstellte Benutzerobjekt oder null, wenn ein Fehler aufgetreten ist.
     */
    public User createUser(User user) {
        return db.withConnection(conn -> {
            User createdUser = null;
            String sql = "INSERT INTO user ( username, surname, first_name, email, password, profile_picture, highscore) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getSurname());
            stmt.setString(3, user.getFirst_name());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getProfile_picture());
            stmt.setInt(7, user.getHighscore());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                createdUser = getUserById(rs.getInt(1));
            }
            stmt.close();
            return createdUser;
        });
    }

    /**
     * Liefert das Benutzerobjekt mit der angegebenen Benutzer-ID.
     *
     * @param id Benutzer-ID.
     * @return Das Benutzerobjekt oder null, wenn der Benutzer nicht gefunden wurde.
     */
    public User getUserById(int id) {
        return db.withConnection(conn -> {
            User user = null;
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE user_ID = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs);
            }
            stmt.close();
            return user;
        });
    }

    /**
     * Diese Methode sucht einen Benutzer in der Datenbank anhand seiner ID und gibt ihn zurueck, ohne eine SQLException zu werfen, wenn der Benutzer nicht gefunden wird.
     *
     * @param id Die ID des gesuchten Benutzers.
     * @return Ein User-Objekt, das den gefundenen Benutzer darstellt. Wenn der Benutzer nicht gefunden wird, wird null zurueckgegeben.
     */
    public User getUserByIdForDelete(int id) {
        return db.withConnection(conn -> {
            User user = null;
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE user_ID = ?");
            stmt.setInt(1, id);
            ResultSet rs = null;
            try {
                rs = stmt.executeQuery();
            }catch (SQLException sqle){
                return null;
            }
            if (rs.next()) {
                user = new User(rs);
            }
            stmt.close();
            return user;
        });
    }

    /**
     * Sucht einen Benutzer in der Datenbank anhand seiner ID und gibt ihn zurueck.
     *
     * @param id Die ID des gesuchten Benutzers als String.
     * @return Ein User-Objekt, das den gefundenen Benutzer darstellt. Wenn der Benutzer nicht gefunden wird, wird null zurueckgegeben.
     */
    public User getUserById(String id) {
        return getUserById(Integer.parseInt(id));
    }

    /**
     * Gibt den Benutzer mit dem angegebenen Benutzernamen aus der Datenbank zurueck.
     *
     * @param username Der Benutzername des Benutzers, der zurueckgegeben werden soll
     * @return Der Benutzer mit dem angegebenen Benutzernamen oder null, wenn kein Benutzer mit diesem Benutzernamen gefunden wurde
     */
    public User getUserByUsername(String username){
        return db.withConnection(conn ->{
            User user = null;
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs);
            }
            stmt.close();
            return user;
        });
    }

    /**
     * Gibt den Highscore eines Benutzers zurueck, der anhand seines Benutzernamens aus der Datenbank abgerufen wird.
     *
     * @param username der Benutzername des gesuchten Benutzers
     * @return der Highscore des Benutzers, oder 0 wenn der Benutzer nicht gefunden wurde
     */
    public int getHighscoreByUsername(String username){
        return db.withConnection(conn ->{
            int highscore = 0;
            PreparedStatement stmt = conn.prepareStatement("SELECT highscore FROM user WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                highscore = rs.getInt(1) ;
            }
            stmt.close();
            return highscore;
        });
    }

    /**
     * Ruft alle Benutzer aus der Datenbank ab und gibt sie als Liste von User-Objekten zurueck.
     *
     * @return Liste von User-Objekten aus der Datenbank.
     */
    public List<User> getAllUsers() {
        return db.withConnection(conn -> {
            List<User> users = new ArrayList<>();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User(rs);
                users.add(user);
            }
            stmt.close();
            return users;
        });
    }

    /**
     * Gibt eine Liste von User-Objekten zurueck, die alle Freunde eines bestimmten Benutzers aus der Datenbank abrufen.
     *
     * @param user die ID des Benutzers, dessen Freunde abgerufen werden sollen.
     * @return Eine Liste von User-Objekten, die die Daten aller Freunde des Benutzers enthalten.
     */
    public List<User> getAllFriendsByUser(User user){
        return db.withConnection(conn -> {
            List<User> friends = new ArrayList<>();
            PreparedStatement stmt = conn.prepareStatement("SELECT user.user_ID, user.username, user.surname, user.first_name, user.email, user.password, user.profile_picture, user.highscore\n" +
                    "FROM user_has_user\n" +
                    "JOIN user ON user_has_user.user_user_ID1 = user.user_ID\n" +
                    "WHERE user_has_user.user_user_ID = ?;");
            stmt.setInt(1, user.getUser_ID());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User friend = new User(rs);
                friends.add(friend);
            }
            stmt.close();
            return friends;
        });
    }

    /**
     * Aktualisiert die Daten eines vorhandenen Benutzers in der Datenbank.
     *
     * @param user das Benutzerobjekt mit den aktualisierten Daten
     * @return true, wenn die Aktualisierung erfolgreich war, false, wenn nicht
     */
    public boolean updateUserData(User user){
        return db.withConnection(conn -> {
            String sql = "UPDATE user SET username = ?, surname = ?, first_name = ?, password = ? WHERE user_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if(user.getUsername()!=null){
                stmt.setString(1, user.getUsername());
            }
            if(user.getSurname()!=null){
                stmt.setString(2, user.getSurname());
            }
            if(user.getFirst_name()!=null){
                stmt.setString(3, user.getFirst_name());
            }
            stmt.setString(4, user.getPassword());
            stmt.setInt(5, user.getUser_ID());
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected>0;
        });
    }

    /**
     * Aktualisiert den Highscore des Benutzers in der Datenbank.
     *
     * @param highscore Der neue Highscore des Benutzers.
     * @param username  Der Benutzername des Benutzers, dessen Highscore aktualisiert werden soll.
     * @return true, wenn die Aktualisierung erfolgreich war, andernfalls false.
     */
    public boolean updateHighscore(int highscore, String username ){
        return db.withConnection(conn -> {
            String sql = "UPDATE user SET highscore = ? WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, highscore);
            stmt.setString(2, username);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected>0;
        });
    }

    /**
     * Aktualisiert die Tabelle 'user_has_questions' in der Datenbank, indem eine neue Verbindung zwischen einem Benutzer und einer Frage erstellt wird.
     *
     * @param user        der Benutzer, fuer den die Verbindung erstellt werden soll
     * @param question_ID die ID der Frage, fuer die die Verbindung erstellt werden soll
     * @return true, wenn die Verbindung erfolgreich erstellt wurde, andernfalls false
     */
    public boolean updateUserHasQuestion(User user, int question_ID){
        return db.withConnection(conn -> {
            String sql = "INSERT INTO user_has_questions (user_user_ID, questions_question_ID) SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM user_has_questions WHERE user_user_ID = ? AND questions_question_ID = ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, user.getUser_ID());
            stmt.setInt(2, question_ID);
            stmt.setInt(3, user.getUser_ID());
            stmt.setInt(4, question_ID);
            stmt.executeUpdate();
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected>0;
        });
    }


    /**
     * Loescht den uebergebenen Benutzer aus der Datenbank.
     *
     * @param user Der zu loeschende Benutzer.
     */
    public void delete(User user) {
        db.withConnection(conn -> {
            String sql = "DELETE FROM user WHERE user_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user.getUser_ID());
            stmt.executeUpdate();
            stmt.close();
        });
    }
}
