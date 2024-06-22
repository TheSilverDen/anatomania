package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import models.Question;
import models.factories.QuestionFactory;
import play.mvc.Http;
import play.mvc.Result;

import java.util.HashMap;
import java.util.List;

import static play.mvc.Results.ok;
/**
 * Der SessionController verwaltet die Session-Daten des Benutzers und bietet
 * Methoden zum Abrufen, Aktualisieren und Zuruecksetzen der Session-Daten.
 */
public class SessionController extends GeneralController {

    QuestionFactory questions;

    /**
     * Konstruktor des SessionControllers, der eine Fragefabrik als Parameter annimmt.
     *
     * @param questions - die Fragefabrik, die fuer die Erzeugung von Fragen verwendet wird
     */
    @Inject
    public SessionController(QuestionFactory questions) {
        this.questions = questions;
    }

    /**
     * Initialisiert die Sitzungsdaten mit Standardwerten und gibt eine Hashmap zurueck, die die
     * Sitzungsfelder enthaelt.
     *
     * @return - eine Hashmap mit den initialisierten Sitzungsdaten
     */
    public HashMap<String, String> initializeSessionData() {
        HashMap<String, String> sessionFields = new HashMap<>();
        sessionFields.put("level", "0");
        sessionFields.put("totalScore", "0");
        sessionFields.put("questionCount", "-1");
        sessionFields.put("correctAnswerCount", "0");
        sessionFields.put("failCounter", "0,0,0");
        sessionFields.put("questionTracker", "");
        sessionFields.put("tagTracker", "");
        sessionFields.put("skipJokerCount", "0");
        sessionFields.put("timer", "300");
        int level = Integer.parseInt(sessionFields.get("level")) + 1;
        String organ = sessionFields.get("organ");
        List<Question> questions = this.questions.getQuestionsByLevelAndOrgan(level, organ);
        sessionFields.put("answeredQuestions", "");
        return sessionFields;
    }

    /**
     * Aktualisiert die Session-Daten basierend auf den Parametern in der
     * angegebenen Anforderung und gibt die aktualisierten Daten als HashMap zurueck.
     *
     * @param request - die HTTP-Anfrage, die die aktualisierten Sitzungsdaten enthaelt
     * @return - eine Hashmap mit den aktualisierten Sitzungsdaten
     */
    public HashMap<String, String> updateSessionAfterReload(Http.Request request){
        int level = Integer.parseInt(request.queryString("level").orElse("ELSE in updateLevel"));
        int totalScore = Integer.parseInt(request.queryString("totalScore").orElse("ELSE in updateScore"));
        int questionCount = Integer.parseInt(request.queryString("questionCount").orElse("ELSE in updateCount"));
        int correctAnswerCount = Integer.parseInt(request.queryString("correctAnswerCount").orElse("1000"));
        String failCounter = request.queryString("failCounter").orElse("0");
        String organ = String.valueOf(request.queryString("organ").orElse(null));
        int questionId = Integer.parseInt(request.queryString("question_ID").orElse(""));
        String answeredQuestionsString = request.queryString("answeredQuestions").orElse("");
        String questionTracker = request.queryString("questionTracker").orElse("");
        String tagTracker = request.queryString("tagTracker").orElse("");
        String timer = request.queryString("timer").orElse("");
        //QueryStringBindable.bind(request.queryString("questionTracker").orElse(""));
        //System.out.println("In updateSessionAfterReload: level: " + level + " totalScore: " + totalScore + " Question count: " + questionCount + " Correct Answers: " + correctAnswerCount + "Organ: " + organ);
        HashMap<String, String> sessionData = new HashMap<>();
        sessionData.put("level", String.valueOf(level));
        sessionData.put("totalScore", String.valueOf(totalScore));
        sessionData.put("questionCount", String.valueOf(questionCount));
        sessionData.put("correctAnswerCount", String.valueOf(correctAnswerCount));
        sessionData.put("organ", organ);
        sessionData.put("failCounter", failCounter);
        sessionData.put("question_ID", String.valueOf(questionId));
        sessionData.put("answeredQuestions", answeredQuestionsString);
        sessionData.put("questionTracker", questionTracker);
        sessionData.put("tagTracker", tagTracker);
        sessionData.put("timer", timer);
        return sessionData;
    }

    /**
     * Gibt die aktuellen Session-Daten als JSON zurueck.
     *
     * @param request Die HTTP-Anforderung, fuer die die Session-Daten abgerufen werden sollen.
     * @return Ein HTTP-Ergebnis mit den aktuellen Session-Daten als JSON.
     * @throws JsonProcessingException Wenn ein Fehler beim Parsen der Session-Daten in JSON auftritt.
     */
    public Result getSessionData(Http.Request request) throws JsonProcessingException {
        String session = request.session().data().toString();
        HashMap<String, String> sessionData = new HashMap<>();
        sessionData.put("username", request.session().get("username").orElse(""));
        sessionData.put("level", request.session().get("level").orElse("0"));
        sessionData.put("totalScore", request.session().get("totalScore").orElse("0"));
        sessionData.put("questionCount", request.session().get("questionCount").orElse("0"));
        sessionData.put("correctAnswerCount", request.session().get("correctAnswerCount").orElse("0"));
        sessionData.put("organ", request.session().get("organ").orElse("0"));
        sessionData.put("failCounter", request.session().get("failCounter").orElse("0"));
        sessionData.put("question_ID", request.session().get("question_ID").orElse(""));
        sessionData.put("answeredQuestions", request.session().get("answeredQuestions").orElse(""));
        sessionData.put("questionTracker", request.session().get("questionTracker").orElse(""));
        sessionData.put("tagTracker", request.session().get("tagTracker").orElse(""));
        sessionData.put("skipJokerCount", request.session().get("skipJokerCount").orElse("100"));
        sessionData.put("timer", request.session().get("timer").orElse(""));
        return ok(mapper.writeValueAsString(sessionData));
    }

    /**
     * Setzt die Session-Daten auf die Standardwerte zurueck, nachdem der Benutzer eine Frage falsch beantwortet hat.
     *
     * @param sessionData Eine HashMap mit den aktuellen Session-Daten.
     * @param correctAnswerCount Die Anzahl der korrekten Antworten des Benutzers.
     * @param totalScore Der Gesamtscore des Benutzers.
     * @return Eine HashMap mit den zurueckgesetzten Session-Daten.
     */
    public HashMap<String, String> resetSessionAfterFailure(HashMap<String, String> sessionData, int correctAnswerCount, int totalScore) {
        sessionData.put("questionCount", "-1");
        sessionData.put("correctAnswerCount", "0");
        sessionData.put("totalScore",String.valueOf(totalScore - correctAnswerCount));
        sessionData.put("skipJokerCount", "0");
        return sessionData;
    }

    /**
     * Setzt die Session-Daten auf die Standardwerte zurueck, nachdem der Benutzer alle Fragen einer Ebene richtig beantwortet hat.
     *
     * @param sessionData Eine HashMap mit den aktuellen Session-Daten.
     * @param oldOrgan Der Name des Organs, fuer das der Benutzer die Fragen richtig beantwortet hat.
     * @return Eine HashMap mit den zurueckgesetzten Session-Daten.
     */
    public HashMap<String, String> resetSessionAfterSuccess(HashMap<String, String> sessionData, String oldOrgan) {
        sessionData.put("questionCount", "-1");
        sessionData.put("correctAnswerPerLevel" + sessionData.get("level"), sessionData.get("correctAnswerCount"));
        sessionData.put("organLevel" + sessionData.get("level"), oldOrgan);
        sessionData.put("correctAnswerCount", "0");
        sessionData.put("skipJokerCount", "0");
        return sessionData;
    }

    /**
     * Gibt eine HashMap mit den Organen pro Level zurueck, die in der aktuellen Sitzung gespeichert sind.
     *
     * @param request Das Http.Request-Objekt der aktuellen Anfrage.
     * @return Eine HashMap mit den Organen pro Level.
     */
    public HashMap<String, String> getOrgansPerLevel(Http.Request request) {
        HashMap<String, String> organsPerLevel = new HashMap<>();
        String organsPerLevel1 = request.session().get("organLevel1").orElse("0");
        String organsPerLevel2 = request.session().get("organLevel2").orElse("0");
        String organsPerLevel3 = request.session().get("organLevel3").orElse("0");
        organsPerLevel.put("1", organsPerLevel1);
        organsPerLevel.put("2", organsPerLevel2);
        organsPerLevel.put("3", organsPerLevel3);
        return organsPerLevel;
    }

    /**
     * Gibt eine HashMap mit der Anzahl korrekter Antworten pro Level zurueck, die in der aktuellen Sitzung gespeichert sind.
     *
     * @param request Das Http.Request-Objekt der aktuellen Anfrage.
     * @return Eine HashMap mit der Anzahl korrekter Antworten pro Level.
     */
    public HashMap<String, String> getCorrectAnswerCountPerLevel(Http.Request request) {
        String correctAnswerCountPerLevel1 = request.session().get("correctAnswerPerLevel1").orElse("0");
        String correctAnswerCountPerLevel2 = request.session().get("correctAnswerPerLevel2").orElse("0");
        String correctAnswerCountPerLevel3 = request.session().get("correctAnswerPerLevel3").orElse("0");
        HashMap<String, String> correctAnswerCountPerLevel = new HashMap<>();
        correctAnswerCountPerLevel.put("1", correctAnswerCountPerLevel1);
        correctAnswerCountPerLevel.put("2", correctAnswerCountPerLevel2);
        correctAnswerCountPerLevel.put("3", correctAnswerCountPerLevel3);
        return correctAnswerCountPerLevel;
    }

    /**
     * Aktualisiert den Jokerzaehler in der Sitzung und gibt den aktualisierten Zaehler als Result zurueck.
     *
     * @param request Das Http.Request-Objekt der aktuellen Anfrage.
     * @return Ein Result-Objekt mit dem aktualisierten Zaehler und den aktualisierten Sitzungsdaten.
     */
    public Result updateJokerCounter(Http.Request request){
        JsonNode jsonNode = request.body().asJson();
        String counter = jsonNode.get("counter").asText();
        HashMap<String, String> sessionData = new HashMap<>();
        sessionData.put("skipJokerCount", counter);
        return ok(counter).addingToSession(request, sessionData);
    }
}
