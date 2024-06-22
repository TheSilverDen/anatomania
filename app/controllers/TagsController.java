package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Inject;
import models.Question;
import models.User;
import models.factories.TagFactory;
import models.factories.UserFactory;
import play.mvc.Http;
import play.mvc.Result;

import java.util.HashMap;
import java.util.List;

import static play.mvc.Results.ok;
/**
 * Die Klasse TagsController erweitert die GeneralController-Klasse
 * und stellt Methoden zur Verwaltung von Tags zur Verfuegung.
 */
public class TagsController extends GeneralController {
    TagFactory tags;
    UserFactory users;

    /**
     * Konstruktor der TagsController-Klasse, der eine TagFactory und eine UserFactory injiziert.
     *
     * @param tags Die injizierte TagFactory
     * @param users Die injizierte UserFactory
     */
    @Inject
    public TagsController(TagFactory tags, UserFactory users){
        this.tags = tags;
        this.users = users;
    }

    /**
     * Gibt die Gesamtzahl der Fragen zurueck, die mit Tags versehen wurden.
     *
     * @param request Das Http.Request-Objekt
     * @return Ein Result-Objekt, das die Gesamtzahl der Fragen als String enthaelt
     */
    public Result getTotalQuestionCount(Http.Request request){
        return ok(String.valueOf(tags.getTotalQuestionCount()));
    }

    /**
     * Gibt die Anzahl der Fragen pro Tag zurueck, abhaengig von der Anfrageansicht (Game oder Profile).
     * Wenn die Anfrageansicht "Game" ist, wird die Methode getQuestionCountPerTagGame() aufgerufen,
     * andernfalls wird die Methode getQuestionCountPerTagProfile() aufgerufen.
     *
     * @param request Das Http.Request-Objekt
     * @return Ein Result-Objekt, das die Anzahl der Fragen pro Tag als JSON-String enthaelt
     * @throws JsonProcessingException Wenn ein Problem beim Verarbeiten der JSON-Anfrage auftritt
     */
    public Result getQuestionCountPerTag(Http.Request request) throws JsonProcessingException {
        JsonNode jsonNode = request.body().asJson();
        String view = jsonNode.get("view").asText();
        if(view.equals("Game")){
            return getQuestionCountPerTagGame(request);
        }else{
            return getQuestionCountPerTagProfile(request);
        }
    }

    /**
     * Gibt die Anzahl der Fragen pro Tag zurueck, die derzeit im Spiel verwendet werden.
     * Die Methode zaehlt die Anzahl der Tags (Smile, Meh, Frown), die der Spieler im aktuellen Spiel verwendet hat.
     *
     * @param request Das Http.Request-Objekt
     * @return Ein Result-Objekt, das die Anzahl der Fragen pro Tag als JSON-String enthaelt
     * @throws JsonProcessingException Wenn ein Problem beim Verarbeiten der JSON-Anfrage auftritt
     */
    public Result getQuestionCountPerTagGame(Http.Request request) throws JsonProcessingException {
        String tagTracker = request.session().get("tagTracker").orElse("");
        String[]  tagTrackerArray = tagTracker.split(",");
        HashMap<Integer, Integer> countPerTag = new HashMap<>();
        int smileTagCount = 0;
        int mehTagCount = 0;
        int frownTagCount = 0;
        for (int i = 0; i < tagTrackerArray.length; i++) {
            if(tagTrackerArray[i].equals("1")){
                smileTagCount++;
            }else if(tagTrackerArray[i].equals("2")){
                mehTagCount++;
            }else if(tagTrackerArray[i].equals("3")){
                frownTagCount++;
            }
        }
        countPerTag.put(1, smileTagCount);
        countPerTag.put(2, mehTagCount);
        countPerTag.put(3, frownTagCount);
        return ok(mapper.writeValueAsString(countPerTag));
    }

    /**
     * Gibt die Anzahl der Fragen pro Tag fuer das Profil des Benutzers zurueck.
     *
     * @param request Http-Anforderung, die den Benutzernamen des eingeloggten Benutzers enthaelt.
     * @return Die Anzahl der Fragen pro Tag als JSON-String in einem HTTP-Antwortobjekt.
     * @throws JsonProcessingException Wenn ein Fehler beim Konvertieren der Ergebnis-HashMap in einen JSON-String auftritt.
     */
    public Result getQuestionCountPerTagProfile(Http.Request request) throws JsonProcessingException {
        int user_ID = users.getUserByUsername(request.session().get("username").orElse("")).getUser_ID();
        HashMap<Integer, Integer> countPerTag = new HashMap<>();
        countPerTag.put(1, tags.getQuestionCountPerTag(1, user_ID));
        countPerTag.put(2, tags.getQuestionCountPerTag(2, user_ID));
        countPerTag.put(3, tags.getQuestionCountPerTag(3, user_ID));
        return ok(mapper.writeValueAsString(countPerTag));
    }

    /**
     * Setzt den Tag fuer eine bestimmte Frage fuer den eingeloggten Benutzer.
     *
     * @param request Http-Anforderung, die den Benutzernamen des eingeloggten Benutzers und die ID der Frage und den Tag enthaelt.
     * @return Ein HTTP-Antwortobjekt, das angibt, ob der Tag erfolgreich gesetzt wurde oder nicht.
     */
    public Result setQuestionTag(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        User user = users.getUserByUsername(request.session().get("username").orElse(""));
        int questionID = jsonNode.get("questionID").asInt();
        String tag = jsonNode.get("tag").asText();
        int tagID = 0;
        if (tag.equals("smile")) {
            tagID = 1;
        } else if (tag.equals("meh")) {
            tagID = 2;
        } else {
            tagID = 3;
        }
        boolean isSet = false;
        if (tags.checkIfUser_has_Tag(user.getUser_ID(), questionID)) {
            isSet = tags.updateQuestionTag(user.getUser_ID(), questionID, tagID);
        } else {
            isSet = tags.insertQuestionTag(user.getUser_ID(), questionID, tagID);
        }
        return ok(String.valueOf(isSet));
    }

    /**
     * Diese Methode gibt eine Liste von Fragen zurueck, die mit bestimmten Tags verknuepft sind, abhaengig von den angegebenen Parametern level und organ.
     *
     * @param request die Anfrage, die das JSON-Objekt mit den Parametern level und organ enthaelt.
     * @return die Antwort, die eine JSON-Liste von Fragen enthaelt, die mit den Tags verknuepft sind.
     * @throws JsonProcessingException wenn das Mapping des Objekts fehlschlaegt.
     */
    public Result getQuestionsByTag(Http.Request request) throws JsonProcessingException {
        JsonNode jsonNode = request.body().asJson();
        int level = jsonNode.get("level").asInt() + 1;
        String organ = jsonNode.get("organ").asText();
        List<Question> smileQuestions = tags.getQuestionByTagLevelOrgan(1, level, organ);
        List<Question> mehQuestions = tags.getQuestionByTagLevelOrgan(2, level, organ);
        List<Question> frownQuestions = tags.getQuestionByTagLevelOrgan(3, level, organ);
        HashMap<Integer, List<Question>> questionMap = new HashMap<>();
        questionMap.put(1, smileQuestions);
        questionMap.put(2, mehQuestions);
        questionMap.put(3, frownQuestions);
        return ok(mapper.writeValueAsString(questionMap));
    }

}
