package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import models.Question;
import models.User;
import models.factories.QuestionFactory;
import models.factories.UserFactory;
import play.mvc.Http;
import play.mvc.Result;

import java.util.HashMap;
import java.util.List;
/**
 * Der "QuestionsController" ist fuer die Verwaltung der Fragen im Spiel zustaendig. Er enthaelt Methoden zur Steuerung
 * des Spiels, zur Abfrage von Fragen und Antworten sowie zur Verwaltung des Fortschritts des Spielers.
 */
public class QuestionsController extends GeneralController {

    QuestionFactory questions;
    UserFactory users;

    /**
     * Konstruktor zur Initialisierung von QuestionFactory- und UserFactory-Objekten.
     *
     * @param questions Die QuestionFactory, die zur Verwaltung von Fragen verwendet wird.
     * @param users Die UserFactory, die zur Verwaltung von Benutzern verwendet wird.
     */
    @Inject
    public QuestionsController(QuestionFactory questions, UserFactory users) {
        this.questions = questions;
        this.users = users;
    }

    /**
     * Methode, die die erste Frage eines Spiels festlegt und in der Sitzung speichert.
     *
     * @param request Das Http.Request-Objekt, das den Organ-Namen und die Frage-ID enthaelt.
     * @return Eine HTTP-Antwort, die das aktualisierte Http.Request-Objekt enthaelt.
     */
    public Result getInitialQuestion(Http.Request request){
        JsonNode jsonNode = request.body().asJson();
        String organ = jsonNode.get("organ").asText();
        String questionID = jsonNode.get("questionID").asText();
        HashMap<String, String> sessionFields = new HashMap<>();
        sessionFields.put("organ", organ);
        sessionFields.put("question_ID", questionID);
        return ok().addingToSession(request, sessionFields);
    }

    /**
     * Methode, die alle Fragen eines bestimmten Organs und Levels zurueckgibt.
     *
     * @param request Das Http.Request-Objekt, das den Level und den Organ-Namen enthaelt.
     * @return Eine HTTP-Antwort, die eine Liste von Question-Objekten im JSON-Format enthaelt.
     * @throws JsonProcessingException falls ein Fehler beim Serialisieren der Fragen auftritt.
     */
    public Result getQuestions(Http.Request request) throws JsonProcessingException {
        JsonNode jsonNode = request.body().asJson();
        int level = jsonNode.get("level").asInt() +1;
        String organ = jsonNode.get("organ").asText();
        return ok(mapper.writeValueAsString(questions.getQuestionsByLevelAndOrgan(level, organ)));
    }

    /**
     * Methode, die die richtige Antwort auf eine bestimmte Frage zurueckgibt.
     *
     * @param request Das Http.Request-Objekt, das die ID der Frage enthaelt.
     * @return Eine HTTP-Antwort, die die korrekte Antwort auf die Frage enthaelt.
     */
    public Result getCorrectAnswerByQuestion(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        int questionID = jsonNode.get("question_id").asInt();
        Question question = questions.getQuestionById(questionID);
        return ok(question.getCorrectAnswer());
    }

    /**
     * Gibt eine Liste von Fragen zurueck, die ein Benutzer in einem bestimmten Organ und Level korrekt beantwortet hat.
     *
     * @param request HTTP-Anforderungsobjekt
     * @return HTTP-Antwortobjekt, das eine Liste von Fragen als JSON enthaelt
     * @throws JsonProcessingException wenn ein Fehler bei der Konvertierung in JSON auftritt
     */
    public Result getCorrectlyAnsweredQuestions(Http.Request request) throws JsonProcessingException {
        JsonNode jsonNode = request.body().asJson();
        int level = jsonNode.get("level").asInt();
        String organ = jsonNode.get("organ").asText();
        User user = users.getUserByUsername(request.session().get("username").orElse(""));
        List<Question> questionsCorrectlyAnswered = null;
        if (level == 0) {
            questionsCorrectlyAnswered = questions.getCorrectlyAnsweredQuestions(user, organ);
        } else {
            questionsCorrectlyAnswered = questions.getCorrectlyAnsweredQuestionsPerLevel(user, level, organ);
        }
        return ok(mapper.writeValueAsString(questionsCorrectlyAnswered));
    }

    /**
     * Gibt den Fortschritt des Benutzers bei der Beantwortung von Fragen in einer Sitzung zurueck.
     *
     * @param request HTTP-Anforderungsobjekt
     * @return HTTP-Antwortobjekt, das einen String mit dem Fortschritt des Benutzers bei der Beantwortung von Fragen enthaelt
     */
    public Result getQuestionResults(Http.Request request) {
        return ok(request.session().get("questionTracker").orElse(""));
    }
}
