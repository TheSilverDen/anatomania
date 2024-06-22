package models;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.mysql.cj.xdevapi.JsonArray;
import org.h2.util.json.JSONArray;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static play.mvc.WebSocket.Json;

/**
 * Eine Klasse, die einen Chat zwischen zwei Benutzern repraesentiert.
 */
public class Chat {
    private User user;
    private User friend;
    private List<Message> chat_history;

    /**
     * Konstruktor fuer Chat-Objekte mit Benutzern, Freund und einer Liste von Chat-Nachrichten.
     *
     * @param user Der Benutzer, der den Chat oeffnet.
     * @param friend Der Freund des Benutzers, mit dem er chattet.
     * @param chat_history Eine Liste von Chat-Nachrichten.
     */
    public Chat(User user, User friend, List<Message> chat_history) {
        this.user = user;
        this.friend = friend;
        this.chat_history = chat_history;
    }

    /**
     * Konstruktor fuer Chat-Objekte mit Benutzern, Freund und einem ResultSet-Objekt aus einer Datenbankabfrage.
     *
     * @param user Der Benutzer, der den Chat oeffnet.
     * @param friend Der Freund des Benutzers, mit dem er chattet.
     * @param rs Ein ResultSet-Objekt aus einer Datenbankabfrage.
     * @throws SQLException Wenn ein Fehler beim Zugriff auf das ResultSet-Objekt auftritt.
     */
    public Chat(User user, User friend, ResultSet rs) throws SQLException {
        this.user = user;
        this.friend = friend;
        String chatHistoryString = rs.getString("chat_history");
        this.chat_history = convertToMessageList(chatHistoryString);
    }

    /**
     * Konstruktor fuer Chat-Objekte mit Benutzern, Freund und einer Zeichenfolge mit Chat-Nachrichten im JSON-Format.
     *
     * @param user Der Benutzer, der den Chat oeffnet.
     * @param friend Der Freund des Benutzers, mit dem er chattet.
     * @param chat_history Eine Zeichenfolge mit Chat-Nachrichten im JSON-Format.
     */
    public Chat(User user, User friend, String chat_history) {
        this.user = user;
        this.friend = friend;
        this.chat_history = convertToMessageList(chat_history);
    }

    /**
     * Konvertiert eine JSON-Zeichenfolge mit Chat-Nachrichten in eine Liste von Chat-Nachrichten-Objekten.
     *
     * @param chat_history Eine JSON-Zeichenfolge mit Chat-Nachrichten.
     * @return Eine Liste von Chat-Nachrichten-Objekten.
     */
    public List<Message> convertToMessageList(String chat_history){
        List<Message> chatHistory = new ArrayList<>();
        if(chat_history == null){
            return chatHistory;
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            chatHistory = objectMapper.readValue(chat_history, new TypeReference<List<Message>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return chatHistory;
    }

    /**
     * Konvertiert eine Liste von Chat-Nachrichten-Objekten in eine JSON-Zeichenfolge.
     *
     * @param messageList Eine Liste von Chat-Nachrichten-Objekten.
     * @return Eine JSON-Zeichenfolge.
     */
    public String convertMessageListToJson(List<Message> messageList){
        String jsonList = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            jsonList = objectMapper.writeValueAsString(this.chat_history);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return jsonList;
    }

    /**
     * Fuegt eine Chat-Nachricht zur Verlaufsliste hinzu.
     *
     * @param message die hinzuzufuegende Chat-Nachricht
     */
    public void addMessageToHistory(Message message){
        chat_history.add(message);
    }

    /**
     * Getter fuer den Benutzer.
     *
     * @return Der Benutzer des Chats.
     */
    public User getUser() {
        return user;
    }

    /**
     * Setter fuer den Benutzer des Chats.
     *
     * @param user Der neue Benutzer des Chats.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Getter fuer den Freund des Benutzers.
     *
     * @return Der Freund des Benutzers.
     */
    public User getFriend() {
        return friend;
    }

    /**
     * Setter fuer den Freund des Benutzers.
     *
     * @param friend Der neue Freund des Benutzers.
     */
    public void setFriend(User friend) {
        this.friend = friend;
    }

    /**
     * Getter fuer die Chat-Historie.
     *
     * @return Die Chat-Historie.
     */
    public List<Message> getChat_history() {
        return chat_history;
    }

    /**
     * Setter fuer die Chat-Historie.
     *
     * @param chat_history Die neue Chat-Historie.
     */
    public void setChat_history(List<Message> chat_history) {
        this.chat_history = chat_history;
    }

    /**
     * Gibt eine Liste von Nachrichten zurueck, die aus dem JSON-String des Chats erstellt wurde.
     *
     * @return Eine Liste von Nachrichten.
     */
    public List<Message> getMessageList(){
        //Hier aus Chat Json eine Liste mit Message Objekten erstellen
        return null;
    }
}
