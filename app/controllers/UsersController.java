package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import models.User;
import models.factories.*;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Der UsersController ist ein Controller, der fuer die Verwaltung von Benutzern zustaendig ist.
 */
public class UsersController extends GeneralController {

    UserFactory users;

    /**
     * Erstellt einen neuen UsersController mit der angegebenen UserFactory.
     *
     * @param users die UserFactory, die verwendet werden soll
     */
    @Inject
    public UsersController(UserFactory users) {
        super();
        this.users = users;

    }

    /**
     * Erstellt einen neuen Benutzer auf Basis der Informationen, die im JSON-Request-Body enthalten sind.
     *
     * @param request der HTTP-Request, der die Informationen enthaelt
     * @return ein HTTP-Response-Objekt, das an den Client zurueckgesendet werden soll
     * @throws JsonProcessingException falls ein Fehler beim Parsen des JSON-Requests auftritt
     */
    public Result createUser(Http.Request request) throws JsonProcessingException {
        JsonNode jsonNode = request.body().asJson();
        String username = jsonNode.get("username").asText();
        String email = jsonNode.get("email").asText();
        String password = jsonNode.get("password").asText();
        String first_name = jsonNode.get("first_name").asText();
        String surname = jsonNode.get("surname").asText();
        int highscore = 500;
        String profilePicture = getRandomImgPath();
        List<User> allUsers = users.getAllUsers();
        for(User u : allUsers){
            if(username.equalsIgnoreCase(u.getUsername())){
                return ok("300");
            }
        }
        User newUser = new User(username, surname, first_name, email, password, profilePicture, highscore);
        User user = users.createUser(newUser);
        if (user != null) {
            return ok("200").addingToSession(request, "username", username);
        } else {
            return ok("400");
        }
    }

    /**
     * Gibt einen zufaelligen Pfad zu einem Profilbild zurueck.
     *
     * @return ein String, der den Pfad zum Profilbild enthaelt
     */
    public String getRandomImgPath() {
        File dir = new File("/assets/images/profile_pictures");
        int randomnumber = getRandomNumber(15);
        dir = new File(dir + "/" + String.valueOf(randomnumber) + ".png");
        return dir.getPath();
    }

    /**
     * Generiert eine Zufallszahl zwischen 0 und dem angegebenen Index.
     *
     * @param index der maximale Wert, den die Zufallszahl haben kann
     * @return eine Zufallszahl zwischen 0 und dem angegebenen Index
     */
    public int getRandomNumber(int index) {
        return (int) Math.floor(Math.random() * (index + 1));
    }

    /**
     * Validiert den Benutzernamen und das Passwort,
     * die im JSON-Request-Body enthalten sind, und meldet den Benutzer an, wenn sie korrekt sind.
     *
     * @param request der HTTP-Request, der die Informationen enthaelt
     * @return ein HTTP-Response-Objekt, das an den Client zurueckgesendet werden soll
     */
    public Result validateLogin(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String username = jsonNode.get("username").asText();
        String password = jsonNode.get("password").asText();
        User user = users.authenticate(username, password);
        if (user != null) {
            return ok("200").addingToSession(request, "username", username);
        } else {
            return ok("400");
        }
    }

    /**
     * Aktualisiert die Benutzerdaten.
     *
     * @param request der HTTP-Anfrage, die die aktualisierten Benutzerdaten enthaelt
     * @return ein Result-Objekt mit einem Statuscode 200, wenn die Aktualisierung erfolgreich war, oder mit einem Statuscode 400, wenn die Aktualisierung fehlgeschlagen ist
     */
    public Result updateUserData(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String username = jsonNode.get("username").asText();
        List<User> allUsers = users.getAllUsers();
        String password = jsonNode.get("password").asText();
        String first_name = jsonNode.get("first_name").asText();
        String surname = jsonNode.get("surname").asText();
        String usernameOld = request.session().get("username").orElse("");
        User user = users.getUserByUsername(usernameOld);
        for(User u : allUsers){
            if(username.equalsIgnoreCase(u.getUsername()) && u.getUser_ID()!=user.getUser_ID()){
                return ok("300");
            }
        }
        if (username != null && !username.equals("")) {
            user.setUsername(username);
        }
        if (password != null && !password.equals("")) {
            user.setPassword(password);
        }
        if (first_name != null && !first_name.equals("")) {
            user.setFirst_name(first_name);
        }
        if (surname != null && !surname.equals("")) {
            user.setSurname(surname);
        }
        boolean updatedUserData = users.updateUserData(user);
        if (updatedUserData) {
            return ok("200").addingToSession(request, "username", user.getUsername());
        } else {
            return ok("400");
        }
    }

    /**
     * Loescht den Benutzer-Account, der mit der aktuellen Session verknuepft ist.
     *
     * @param request das HTTP-Anfrageobjekt
     * @return ein HTTP-Antwortobjekt mit dem Statuscode 200, wenn das Loeschen erfolgreich war,
     *         oder 400, wenn es fehlgeschlagen ist
     */
    public Result deleteAccount(Http.Request request) {
        String username = request.session().get("username").orElse("");
        User user = users.getUserByUsername(username);
        int user_ID = user.getUser_ID();
        users.delete(user);
        User deletedUser = users.getUserByIdForDelete(user_ID);
        if (deletedUser == null) {
            return ok("200").addingToSession(request, "username", username);
        } else {
            return ok("400");
        }
    }

    /**
     * Gibt eine Liste aller Benutzer als JSON-String zurueck.
     *
     * @return ein HTTP-Antwortobjekt mit einem JSON-String, der alle Benutzer enthaelt
     * @throws JsonProcessingException wenn es ein Problem beim Parsen des JSON-Strings gibt
     */
    public Result getAllUsers() throws JsonProcessingException {
        List<User> allUsers = users.getAllUsers();
        return ok(getUsersAsMap(allUsers));
    }

    /**
     * Wandelt eine Liste von Benutzern in einen JSON-String um, der die Benutzer als HashMap mit ihren Benutzernamen enthaelt.
     *
     * @param users eine Liste von Benutzern
     * @return ein JSON-String, der die Benutzer als HashMap mit ihren Benutzernamen enthaelt
     * @throws JsonProcessingException wenn es ein Problem beim Parsen des JSON-Strings gibt
     */
    public String getUsersAsMap(List<User> users) throws JsonProcessingException {
        HashMap<String, User> usersMap = new HashMap<>();
        for (User user : users) {
            usersMap.put(user.getUsername(), user);
        }
        return mapper.writeValueAsString(usersMap);
    }

    /**
     * Gibt den Highscore des aktuellen Benutzers und den aktuellen Timerwert als JSON-String zurueck.
     *
     * @param request das HTTP-Anfrageobjekt
     * @return ein HTTP-Antwortobjekt mit einem JSON-String, der den Highscore und den Timerwert enthaelt
     * @throws JsonProcessingException wenn es ein Problem beim Parsen des JSON-Strings gibt
     */
    public Result getHighscore(Http.Request request) throws JsonProcessingException {
        String username = request.session().get("username").orElse("");
        int highscore = users.getHighscoreByUsername(username);
        int timerValue = Integer.parseInt(request.session().get("timer").orElse(""));
        HashMap<String, Integer> scoreTimerMap = new HashMap<>();
        scoreTimerMap.put("highscore", highscore);
        scoreTimerMap.put("timer", timerValue);
        return ok(mapper.writeValueAsString(scoreTimerMap));
    }

    /**
     * Aktualisiert den Highscore des aktuellen Benutzers.
     *
     * @param request das HTTP-Anfrageobjekt
     * @return ein HTTP-Antwortobjekt mit dem Boolean-Wert true, wenn das Aktualisieren erfolgreich war, sonst false
     */
    public Result updateHighscore(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        int highscore = Integer.parseInt(jsonNode.get("highscore").asText());
        boolean b = users.updateHighscore(highscore, request.session().get("username").orElse(""));
        return ok(String.valueOf(b));
    }

    /**
     * Aktualisiert die Fragen, die der aktuelle Benutzer beantwortet hat.
     *
     * @param request das HTTP-Anfrageobjekt
     * @return ein HTTP-Antwortobjekt mit dem Statuscode 200, wenn das Aktualisieren erfolgreich war
     */
    public Result updateUserHasQuestion(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String questionsMap = jsonNode.get("questionsMap").toString();
        questionsMap = questionsMap.replaceAll("\"", "").replaceAll("\\{", "").replaceAll("\\}", "");
        String[] questionBlocks = questionsMap.split(",");
        ArrayList<String> questionsCorrectlyAnswered = new ArrayList<>();
        for (int i = 0; i < questionBlocks.length; i++) {
            String[] questionNumBool = questionBlocks[i].split(":");
            if (Boolean.parseBoolean(questionNumBool[1])) {
                questionsCorrectlyAnswered.add(questionNumBool[0]);
            }
        }
        User user = users.getUserByUsername(request.session().get("username").orElse(""));
        for (String questionID : questionsCorrectlyAnswered) {
            users.updateUserHasQuestion(user, Integer.parseInt(questionID));
        }
        return ok();
    }

}
