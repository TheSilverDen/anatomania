package models.factories;

import models.Chat;
import models.Message;
import models.User;
import play.db.Database;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Die FriendsFactory implementiert Methoden zum Lesen und Schreiben von Chatverlaeufen
 * Die Klasse verwendet Dependency Injection, um eine Datenbankverbindung zu erhalten und arbeitet mit einer
 * play.db.Database-Instanz.
 *
 */
public class FriendsFactory {
    private Database db;
    private UserFactory users;

    /**
     * Konstruktor der Klasse, der die Abhaengigkeit von der Datenbank injiziert.
     *
     * @param db    Die Datenbank, auf die zugegriffen werden soll.
     * @param users Das UserFactory-Objekt, das von dieser Klasse verwendet wird.
     */
    @Inject
    FriendsFactory(Database db, UserFactory users) {
        this.db = db;
        this.users = users;
    }

    /**
     * Fuegt eine Freundschaft zwischen zwei Nutzern hinzu.
     *
     * @param user1 der Nutzer, der die Freundschaftsanfrage gesendet hat
     * @param user2 der Nutzer, der die Freundschaftsanfrage erhalten hat
     */
    public void addFriendship(User user1, User user2){
        db.withConnection(conn -> {
            String sql = "INSERT INTO user_has_user (user_user_ID, user_user_ID1, chat_history) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user1.getUser_ID());
            stmt.setInt(2, user2.getUser_ID());
            stmt.setString(3, null);
            stmt.executeUpdate();
            stmt.close();
        });
    }

    /**
     * Loescht die Freundschaft zwischen zwei Benutzern aus der Datenbank.
     *
     * @param user1   der erste Benutzer
     * @param user2 der zweite Benutzer, mit dem der erste Benutzer befreundet ist
     */
    public void deleteFriendship(User user1, User user2){
        db.withConnection(conn -> {
            String sql = "DELETE FROM user_has_user WHERE (user_user_ID = ? AND user_user_ID1 = ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user1.getUser_ID());
            stmt.setInt(2, user2.getUser_ID());
            stmt.executeUpdate();
            stmt.close();
        });
    }

    /**
     * Ueberprueft, ob zwei Benutzer bereits befreundet sind.
     *
     * @param user1   der eine Benutzer
     * @param user2 der andere Benutzer
     * @return true, wenn sie bereits befreundet sind; false, wenn nicht
     */
    public boolean hasFriendship(User user1, User user2){
        return db.withConnection(conn -> {
            String sql = "SELECT EXISTS(SELECT * FROM user_has_user WHERE (user_user_ID = ? AND  user_user_ID1 = ?) OR (user_user_ID = ? AND user_user_ID1 = ?))";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user1.getUser_ID());
            stmt.setInt(2, user2.getUser_ID());
            stmt.setInt(3, user2.getUser_ID());
            stmt.setInt(4, user1.getUser_ID());
            ResultSet rs = stmt.executeQuery();
            boolean exists = false;
            if (rs.next()) {
                exists = rs.getBoolean(1);
            }
            stmt.close();
            return exists;
        });
    }

    /**
     * Fuegt eine Nachricht zum Chatverlauf zwischen zwei Freunden hinzu.
     *
     * @param message Die Nachricht, die hinzugefuegt werden soll.
     * @param user    Der Benutzer, der die Nachricht sendet.
     * @param friend  Der Freund, an den die Nachricht gesendet wird.
     * @return true, wenn die Nachricht erfolgreich hinzugefuegt wurde, false sonst.
     */
    public boolean sendMessage(Message message, User user, User friend){
        Chat chat = getChatByUsers(user, friend);
        String chatHistoryJson = getUpdatedChatHistory(message, chat);
        return db.withConnection(conn -> {
            User newUser = null;
            String sql = "UPDATE user_has_user SET chat_history = ? WHERE user_user_ID = ? AND user_user_ID1 = ?";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, chatHistoryJson);
            stmt.setInt(2, user.getUser_ID());
            stmt.setInt(3, friend.getUser_ID());
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected>0;
        });
    }

    /**
     * Holt den Chatverlauf zwischen zwei Freunden aus der Datenbank.
     *
     * @param user   Der Benutzer, der den Chatverlauf anfordert.
     * @param friend Der Freund, mit dem der Chat stattfindet.
     * @return Der Chatverlauf als Chat-Objekt oder null, wenn kein Verlauf gefunden wurde.
     */
    public Chat getChatByUsers(User user, User friend){
        return db.withConnection(conn -> {
            Chat chat = null;
            String sql = "SELECT chat_history FROM user_has_user WHERE user_user_ID = ? AND user_user_ID1 = ?";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, user.getUser_ID());
            stmt.setInt(2, friend.getUser_ID());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                chat = new Chat(user, friend, rs);
            }
            stmt.close();
            return chat;
        });
    }

    /**
     * Aktualisiert den Chatverlauf mit einer neuen Nachricht und gibt den aktualisierten Verlauf als JSON-String zurueck.
     *
     * @param message Die hinzuzufuegende Nachricht.
     * @param chat    Der Chatverlauf, der aktualisiert werden soll.
     * @return Der aktualisierte Chatverlauf als JSON-String.
     */
    public String getUpdatedChatHistory(Message message, Chat chat){
        chat.addMessageToHistory(message);
        return chat.convertMessageListToJson(chat.getChat_history());
    }

    /**
     * Gibt den Chatverlauf zwischen dem User und einem Freund zurueck
     *
     * @param user Der User, dessen Chatverlauf abgerufen werden soll
     * @param friend Der Freund, mit dem der Chatverlauf gefuehrt wurde
     * @return Der Chatverlauf zwischen dem User und dem Freund
     */
    public Chat getChat(User user, User friend){
        return db.withConnection(conn -> {
            Chat chat = null;
            PreparedStatement stmt = conn.prepareStatement("SELECT chat_history FROM user_has_user WHERE user_user_ID = ? AND user_user_ID1 = ?;");
            stmt.setInt(1, user.getUser_ID());
            stmt.setInt(2, friend.getUser_ID());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                chat = new Chat(user, friend, rs);
            }
            stmt.close();
            return chat;
        });
    }

    /**
     * Gibt den Chatverlauf zwischen dem User und einem Freund im JSON-Format zurueck
     *
     * @param user Der User, dessen Chatverlauf abgerufen werden soll
     * @param friend Der Freund, mit dem der Chatverlauf gefuehrt wurde
     * @return Der Chatverlauf zwischen dem User und dem Freund im JSON-Format
     */
    public String getChatHistoryAsJson(User user, User friend){
        return db.withConnection(conn -> {
            String chatHistory = null;
            PreparedStatement stmt = conn.prepareStatement("SELECT chat_history FROM user_has_user WHERE user_user_ID = ? AND user_user_ID1 = ?;");
            stmt.setInt(1, user.getUser_ID());
            stmt.setInt(2, friend.getUser_ID());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                chatHistory = rs.getString("chat_history");
            }
            stmt.close();
            if(chatHistory==null){
                chatHistory="";
            }
            return chatHistory;
        });
    }

    /**
     * Gibt alle Chats des Users zurueck
     *
     * @param user Der User, dessen Chats abgerufen werden sollen
     * @return Eine Liste von Chat-Objekten, die alle Chats des Users enthalten
     */
    public List<Chat> getAllChats(User user){
        return db.withConnection(conn -> {
            List<Chat> chats = new ArrayList<>();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_has_user WHERE user_user_ID = ?;");
            stmt.setInt(1, user.getUser_ID());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int friend_ID = rs.getInt("user_user_ID1");
                User friend = users.getUserById(friend_ID);
                String chat_history = rs.getString("chat_history");
                chats.add(new Chat(user, friend, chat_history));
            }
            stmt.close();
            return chats;
        });
    }
}
