package controllers;

import java.util.*;

import com.google.inject.Inject;
import models.factories.AchievementsFactory;
import models.factories.QuestionFactory;
import models.Question;
import models.User;
import models.factories.TagFactory;
import models.factories.UserFactory;
import play.mvc.*;
import play.twirl.api.Html;

/**
 * Die Klasse UserController verwaltet die Routen fuer die Anzeige der verschiedenen Nutzerseiten.
 * Dazu gehoeren die Highscore-Liste, die Freundesliste, Einstelleungen, der Chat, das Profil und die Spieleseite.
 * Der UserController greift dabei auf Instanzen der Klassen Users, Questions und SessionController zu, um die notwendigen Daten zu erhalten.
 */
public class ViewController extends GeneralController {
    UserFactory users;
    QuestionFactory questions;
    SessionController sessionController;
    GameLogicController logicController;
    @Inject
    public ViewController(UserFactory users, QuestionFactory questions, AchievementsFactory achievements, TagFactory tags) {
        this.users=users;
        this.questions = questions;
        this.sessionController = new SessionController(questions);
        this.logicController = new GameLogicController(questions, achievements, tags);
    }

    /**
     * Ueberprueft, ob der Benutzer angemeldet ist. Wenn der Benutzer angemeldet ist, wird die angegebene HTML-Seite zurueckgegeben.
     * Andernfalls wird die Login-Seite zurueckgegeben.
     *
     * @param html die HTML-Seite, die zurueckgegeben werden soll, wenn der Benutzer angemeldet ist.
     * @param request die HTTP-Anforderung, die den Anmeldestatus des Benutzers enthaelt.
     * @return die angegebene HTML-Seite, wenn der Benutzer angemeldet ist, sonst die Login-Seite.
     */
   public Result redirectNotLoggedInUsers(Html html, Http.Request request){
        if (!checkIfLoggedIn(request)){
            return ok(html);
        }
        else return ok(views.html.login.render());
    }

    /**
     * Ueberprueft, ob der Benutzer angemeldet ist.
     *
     * @param request die HTTP-Anforderung, die den Anmeldestatus des Benutzers enthaelt.
     * @return true, wenn der Benutzer angemeldet ist, sonst false.
     */
    public boolean checkIfLoggedIn(Http.Request request){
        User user = users.getUserByUsername(request.session().get("username").orElse(""));
       return user == null;
    }

    /**
     * Zeigt die Startseite des Spiels an und initialisiert die Spielsitzung.
     *
     * @param request die HTTP-Anforderung, die den Anmeldestatus des Benutzers enthaelt.
     * @return die Startseite des Spiels.
     */
    public Result index(Http.Request request) {
        HashMap<String, String> sessionFields = sessionController.initializeSessionData();
        return redirectNotLoggedInUsers(views.html.index.render(), request).addingToSession(request, sessionFields);
    }

    /**
     * Zeigt die Login-Seite an.
     *
     * @return die Login-Seite.
     */
    public Result login() {
        return ok(views.html.login.render());
    }

    /**
     * Meldet den Benutzer ab und zeigt die Login-Seite an.
     *
     * @param request die HTTP-Anforderung, die den Anmeldestatus des Benutzers enthaelt.
     * @return die Login-Seite.
     */
    public Result logout(Http.Request request){
        return ok(views.html.login.render()).addingToSession(request, "username", "");
    }

    /**
     * Zeigt die Registrierungsseite an.
     *
     * @return die Registrierungsseite.
     */
    public Result signup() {
        return ok(views.html.signup.render());
    }

    /**
     * Zeigt die Spielende-Seite an, wenn der Benutzer das Spiel nicht geschafft hat.
     *
     * @param request die HTTP-Anforderung, die den aktuellen Zustand der Spielsitzung enthaelt.
     * @return die Spielende-Seite.
     */
    public Result failure(Http.Request request) {
        HashMap<String, String> sessionData = sessionController.updateSessionAfterReload(request);
        int correctAnswerCount = Integer.parseInt(sessionData.get("correctAnswerCount"));
        int totalScore = Integer.parseInt(sessionData.get("totalScore"));
        int level = Integer.parseInt(sessionData.get("level"));
        sessionData.putAll(sessionController.resetSessionAfterFailure(sessionData, correctAnswerCount, totalScore));
        return redirectNotLoggedInUsers(views.html.failure.render(String.valueOf(level+1)), request).addingToSession(request, sessionData);
    }

    /**
     * Zeigt die Spielende-Seite an, wenn der Benutzer eine Runde geschafft hat.
     *
     * @param request die HTTP-Anforderung, die den aktuellen Zustand der Spielsitzung enthaelt.
     * @return die Spielende-Seite.
     */
    public Result success(Http.Request request) {
        HashMap<String, String> sessionData = sessionController.updateSessionAfterReload(request);
        int correctAnswerCount = Integer.parseInt(sessionData.get("correctAnswerCount"));
        String oldOrgan = request.queryString("oldOrgan").orElse("");
        sessionData= sessionController.resetSessionAfterSuccess(sessionData, oldOrgan);
        return redirectNotLoggedInUsers(views.html.success.render(Integer.parseInt(sessionData.get("level")), correctAnswerCount, Integer.parseInt(sessionData.get("totalScore")), oldOrgan), request).addingToSession(request, sessionData);
    }

    /**
     * Zeigt die Seite an, die angezeigt wird, wenn der Benutzer das Spiel abgeschlossen hat.
     *
     * @param request die HTTP-Anforderung, die den aktuellen Zustand der Spielsitzung enthaelt.
     * @return die Seite, die angezeigt wird, wenn der Benutzer das Spiel abgeschlossen hat.
     */
    public Result complete(Http.Request request){
        HashMap<String, String> correctAnswerCountPerLevel = sessionController.getCorrectAnswerCountPerLevel(request);
        HashMap<String, String> organsPerLevel = sessionController.getOrgansPerLevel(request);
        String failCounter = request.session().get("failCounter").orElse("0");
        ArrayList<String> failCountList = new ArrayList<>(List.of(failCounter.split(",")));
        ArrayList<String> tryCountList = getTryCountList(failCountList);
        int totalScore = Integer.parseInt(request.session().get("totalScore").orElse("0"));
        int timeBonus = logicController.getTotalScoreWithTime(request, totalScore);
        return redirectNotLoggedInUsers(views.html.complete.render(correctAnswerCountPerLevel, totalScore, timeBonus, organsPerLevel, tryCountList), request).addingToSession(request, "level", "4");
    }
    /**
     * Hilfsmethode für complete()
     * Generiert aus der Liste mit Misserfolgen pro Level eine Liste mit Anzahl der Versuche pro Level.
     *
     * @param failCountList die Liste mit der Anzahl der Misserfolge pro Level.
     * @return die Liste Anzahl mit der Anzahl der Versuche pro Level .
     */
    private ArrayList<String> getTryCountList(ArrayList<String> failCountList){
        ArrayList<String> tryCountList = new ArrayList<>();
        for(String failCount : failCountList){
            tryCountList.add(String.valueOf(Integer.parseInt(failCount)+1));
        }
        return tryCountList;
    }

    /**
     * Zeigt die Settings an.
     *
     * @param request die HTTP-Anforderung, die den Anmeldestatus des Benutzers enthaelt.
     * @return die Einstellungsseite.
     */
    public Result settings(Http.Request request) {
        return redirectNotLoggedInUsers(views.html.settings.render(), request);
    }

    /**
     * Zeigt die Achievements an.
     *
     * @param request die HTTP-Anforderung, die den Anmeldestatus des Benutzers enthaelt.
     * @return die Erfolgsseite.
     */
    public Result achievements(Http.Request request){
        return redirectNotLoggedInUsers(views.html.achievements.render(), request);
    }

    /**
     * Zeigt die Highscore Seite an.
     *
     * @param request Http.Request-Objekt mit Informationen ueber den aktuellen Request.
     * @return Result-Objekt mit der gerenderten Highscore-Liste.
     */
    public Result highscores(Http.Request request){
        return redirectNotLoggedInUsers(views.html.highscores.render(), request);
    }

    /**
     * Zeigt die Freundesliste an.
     *
     * @param request Http.Request-Objekt mit Informationen ueber den aktuellen Request.
     * @return Result-Objekt mit der gerenderten Freundesliste.
     */
    public Result friends(Http.Request request){
        String username = request.session().get("username").orElse("");
        return redirectNotLoggedInUsers(views.html.friends.render(username), request);
    }

    /**
     * Zeigt den Chat an.
     *
     * @param request Http.Request-Objekt mit Informationen ueber den aktuellen Request.
     * @return Result-Objekt mit dem gerenderten Chat.
     */
    public Result chat(Http.Request request){
        String friendString = request.queryString("usernameFriend").orElse("");
        User friend = users.getUserByUsername(friendString);
        return redirectNotLoggedInUsers(views.html.chat.render(friend.getUsername(), friend.getProfile_picture()), request);
    }

    /**
     * Zeigt das eigene Profil an.
     * Wenn es keinen Benutzer mit diesem Benutzernamen gibt, wird der Benutzer zur Login-Seite weitergeleitet.
     *
     * @param request Http.Request-Objekt mit Informationen ueber den aktuellen Request.
     * @return Result-Objekt mit dem gerenderten Profil.
     */
    public Result profileView(Http.Request request){
        String username = request.session().get("username").orElse("");
        User user = users.getUserByUsername(username);
        if(user==null){
            return ok(views.html.login.render());
        }
        return ok(views.html.profileView.render(user.getFirst_name(), user.getSurname(), user.getUsername(), String.valueOf(user.getHighscore()), user.getProfile_picture()));
    }

    /**
     * Zeigt das eigene Profil zum bearbeiten an.
     * Wenn es keinen Benutzer mit diesem Benutzernamen gibt, wird der Benutzer zur Login-Seite weitergeleitet.
     *
     * @param request Http.Request-Objekt mit Informationen ueber den aktuellen Request.
     * @return Result-Objekt mit dem gerenderten Profil.
     */
    public Result profileEdit(Http.Request request){
        String username = request.session().get("username").orElse("");
        User user = users.getUserByUsername(username);
        if(user==null){
            return ok(views.html.login.render());
        }
        return ok(views.html.profileEdit.render(user.getSurname(), user.getFirst_name(), username, user.getPassword(), user.getProfile_picture()));
    }

    /**
     * Zeigt das Profil eines Benutzers an, dessen Benutzername aus der Anfrageparameter `username` entnommen wird.
     * Wenn es keinen Benutzer mit diesem Benutzernamen gibt, wird der Benutzer zur Login-Seite weitergeleitet.
     *
     * @param request Eine HTTP-Anforderung, die den Benutzernamen des Profils enthaelt
     * @return Ein `Result`-Objekt, das eine HTML-Seite mit dem Profil des Benutzers enthaelt oder den Benutzer zur Login-Seite weiterleitet
     */
    public Result profileFriend(Http.Request request){
        String username = request.queryString("username").orElse("");
        User user = users.getUserByUsername(username);
        if(user==null){
            return ok(views.html.login.render());
        }
        return ok(views.html.profileFriend.render(user.getFirst_name(), user.getUsername(), String.valueOf(user.getHighscore()), user.getProfile_picture()));
    }

    /**
     * Zeigt das Quizspiel an. Wenn es keine Abfrageparameter gibt, wird das Quiz zum ersten Mal gestartet und eine zufaellige Frage wird ausgewaehlt.
     * Andernfalls werden die Abfrageparameter `question_ID` und `questionCount` verwendet, um die naechste Frage auszuwaehlen und das Spiel fortzusetzen.
     * Das Ergebnis enthaelt auch eine aktualisierte Session, die die Fortschritte des Spiels speichert.
     *
     * @param request Eine HTTP-Anforderung, die die Fortschritte des Spiels enthaelt
     * @return Ein `Result`-Objekt, das eine HTML-Seite mit dem Quizspiel und den Fortschritten des Benutzers enthaelt
     */
    public Result game(Http.Request request) {
        String username = request.session().get("username").orElse("");
        int highscore = users.getHighscoreByUsername(username);
        if(request.queryString().isEmpty()){ //nur 1. Mal wenn man von Index kommt
            int question_ID = Integer.parseInt(request.session().get("question_ID").orElse(""));
            Question question = randomizeAnswers(questions.getQuestionById(question_ID)); // hier ändern falls wir Reihenfolge haben wollen
            //Question question = questions.getQuestionById(question_ID); // hier ändern falls wir Reihenfolge haben wollen
            return redirectNotLoggedInUsers(views.html.game.render(question, "1", request.session().get("totalScore").orElse("0"), highscore, request.session().get("organ").orElse("0")), request); // Index anpassen für die Anzahl an Fragen
        }else {
            HashMap<String, String> sessionData = sessionController.updateSessionAfterReload(request);
            int question_ID = Integer.parseInt(request.queryString("question_ID").orElse("ELSE"));
            int questionCount = Integer.parseInt(request.queryString("questionCount").orElse("0"));
            //Question question = questions.getQuestionById(question_ID); // hier ändern falls wir Reihenfolge haben wollen
            Question question = randomizeAnswers(questions.getQuestionById(question_ID)); // hier ändern falls wir Reihenfolge haben wollen
            return redirectNotLoggedInUsers(views.html.game.render(question, String.valueOf(questionCount+2), request.session().get("totalScore").orElse("0"), highscore, request.session().get("organ").orElse("0")), request).addingToSession(request, sessionData);
        }
    }

    /**
     * Diese Methode randomisiert die Reihenfolge der Antworten in einer Frage und gibt die aktualisierte Frage zurück.
     *
     * @param question die Frage, deren Antworten zufällig sortiert werden sollen
     * @return die aktualisierte Frage mit zufällig sortierten Antworten
     */

    public Question randomizeAnswers(Question question){
        LinkedHashMap<String, Boolean> randomAnswers = new LinkedHashMap<>();
        ArrayList<Integer> indexList = new ArrayList<>();
        for(int i=0; i<4; i++){
            while(true){
                int index = randomize(3);
                if(!indexList.contains(index)){
                    indexList.add(index);
                    break;
                }
            }
        }
        for(int i=0; i<4; i++){
            int index = indexList.get(i);
            String answer = question.getAnswerKey(index);
            Boolean answerBool = question.getAnswerValue(index);
            randomAnswers.put(answer, answerBool);
        }
        question.setAnswers(randomAnswers);
        return question;
    }

    /**
     * Generiert eine Zufallszahl zwischen 0 und dem angegebenen Index.
     *
     * @param index der maximale Wert, den die Zufallszahl haben kann
     * @return eine Zufallszahl zwischen 0 und dem angegebenen Index
     */
    public int randomize(int index){
        return (int) Math.floor(Math.random() * (index +1));
    }
}
