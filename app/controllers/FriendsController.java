package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import models.Chat;
import models.Date;
import models.Message;
import models.User;
import models.factories.FriendsFactory;
import models.factories.UserFactory;
import play.mvc.Http;
import play.mvc.Result;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static play.mvc.Results.ok;
/**
 * Der FriendsController behandelt alle Anfragen, die mit Freunden und Chats zu tun haben.
 */
public class FriendsController extends GeneralController {
    UserFactory users;
    FriendsFactory friends;

    /**
     * Konstruktor des FriendsControllers.
     *
     * @param users - Die UserFactory, um auf User-Objekte zugreifen zu koennen
     * @param friends - Die FriendsFactory, um auf Freundschaften und Chats zugreifen zu koennen
     */
    @Inject
    public FriendsController(UserFactory users, FriendsFactory friends) {
        super();
        this.users = users;
        this.friends = friends;
    }

    /**
     * Aendert den Freundschaftsstatus zwischen zwei Usern, indem eine Freundschaft hinzugefuegt oder geloescht wird.
     *
     * @param request - Der Request, der die Anfrage enthaelt
     * @return Ein Result-Objekt mit HTTP-Status 200 (OK), falls die Anfrage erfolgreich bearbeitet wurde
     */
    public Result editFriendshipStatus(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        User friend = users.getUserByUsername(jsonNode.get("username").asText());
        User user = users.getUserByUsername(request.session().get("username").orElse(""));
        if (!friends.hasFriendship(user, friend)) {
            friends.addFriendship(user, friend);
            friends.addFriendship(friend, user);
        }else {
            friends.deleteFriendship(user, friend);
            friends.deleteFriendship(friend, user);
        }
        return ok();
    }

    /**
     * Ueberprueft, ob zwei User befreundet sind.
     *
     * @param request - Der Request, der die Anfrage enthaelt
     * @return Ein Result-Objekt mit dem Boolean-Wert, der angibt, ob die beiden User befreundet sind
     */
    public Result checkFriendship(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        User friend = users.getUserByUsername(jsonNode.get("username").asText());
        User user = users.getUserByUsername(request.session().get("username").orElse(""));
        boolean hasFriendship = friends.hasFriendship(user, friend);
        return ok(String.valueOf(hasFriendship));
    }

    /**
     * Gibt alle Freunde eines Users zurueck.
     *
     * @param request - Der Request, der die Anfrage enthaelt
     * @return Ein Result-Objekt mit einer HashMap aller Freunde des Users
     * @throws JsonProcessingException - Falls bei der Umwandlung in einen JSON-String ein Fehler auftritt
     */
    public Result getAllFriends(Http.Request request) throws JsonProcessingException {
        User user = users.getUserByUsername(request.session().get("username").orElse(""));
        List<User> allFriendsByUser = users.getAllFriendsByUser(user);
        return ok(getFriendsMapAsJsonString(allFriendsByUser));
    }

    /**
     * Konvertiert eine Liste von Benutzern in ein JSON-String-Objekt, das einen Hash-Map enthaelt, der jedem Benutzernamen den zugehoerigen Benutzer zuweist.
     *
     * @param users Liste der Benutzer, die konvertiert werden sollen
     * @return JSON-String-Objekt, das die Hash-Map der Benutzer enthaelt
     * @throws JsonProcessingException Falls ein Fehler bei der Verarbeitung des JSON-Strings auftritt
    */
    public String getFriendsMapAsJsonString(List<User> users) throws JsonProcessingException {
        HashMap<String, User> usersMap = new HashMap<>();
        for (User user : users) {
            usersMap.put(user.getUsername(), user);
        }
        return mapper.writeValueAsString(usersMap);
    }
    /**
     * Gibt alle Chats zurueck, an denen der aktuelle Benutzer beteiligt ist, als JSON-Zeichenkette.
     *
     * @param request Eine HTTP-Anfrage.
     * @return Eine HTTP-Antwort mit einer JSON-Zeichenkette, die alle Chats des aktuellen Benutzers enthaelt.
     * @throws JsonProcessingException Wenn ein Fehler beim Serialisieren der JSON-Zeichenkette auftritt.
     */
    public Result getAllChats(Http.Request request) throws JsonProcessingException {
        User user = users.getUserByUsername(request.session().get("username").orElse(""));
        List<Chat> chats = friends.getAllChats(user);
        return ok(mapper.writeValueAsString(chats));
    }
    /**
     * Sendet eine Nachricht an einen Chat-Partner und gibt die gesendete Nachricht als JSON-Zeichenkette zurueck.
     *
     * @param request Eine HTTP-Anfrage mit der Nachricht und dem Chat-Partner.
     * @return Eine HTTP-Antwort mit einer JSON-Zeichenkette, die die gesendete Nachricht enthaelt.
     * @throws JsonProcessingException Wenn ein Fehler beim Serialisieren der JSON-Zeichenkette auftritt.
     */
    public Result sendMessage(Http.Request request) throws JsonProcessingException {
        JsonNode jsonNode = request.body().asJson();
        String messageString = jsonNode.get("message").asText();
        String chatPartner = jsonNode.get("chatPartner").asText();
        Message message = new Message();
        message.setSender(users.getUserByUsername(request.session().get("username").orElse("")));
        message.setAdressee(users.getUserByUsername(chatPartner));
        message.setMessage(messageString);
        message.setDate(getDate());
        friends.sendMessage(message, message.getSender(), message.getAdressee());
        friends.sendMessage(message, message.getAdressee(), message.getSender());
        return ok(message.convertMessageToJson());
    }
    /**
     * Gibt ein Datum-Objekt zurueck, das den aktuellen Zeitpunkt repraesentiert.
     *
     * @return Ein Datum-Objekt, das den aktuellen Zeitpunkt repraesentiert.
     */
    private static Date getDate() {
        LocalDateTime now = LocalDateTime.now();
        return new Date(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
    }
    /**
     * Gibt die Chat-Historie zwischen dem aktuellen Benutzer und einem Chat-Partner als JSON-Zeichenkette zurueck.
     *
     * @param request Eine HTTP-Anfrage mit dem Chat-Partner.
     * @return Eine HTTP-Antwort mit einer JSON-Zeichenkette, die die Chat-Historie enthaelt.
     */
    public Result getChatHistory(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        User chatPartner = users.getUserByUsername(jsonNode.get("chatPartner").asText());
        User user = users.getUserByUsername(request.session().get("username").orElse(""));
        return ok(friends.getChatHistoryAsJson(user, chatPartner));
    }

}
