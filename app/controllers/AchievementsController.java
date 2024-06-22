package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import models.Achievement;
import models.User;
import models.factories.AchievementsFactory;
import models.factories.UserFactory;
import play.mvc.Http;
import play.mvc.Result;

import java.util.HashMap;
import java.util.List;

import static play.mvc.Results.ok;
/**
 * Dieser Controller ist fuer das Freischalten von Achievements durch den Benutzer verantwortlich.
 * Er enthaelt Methoden zum Freischalten eines Achievements und zum Ueberpruefen, ob ein Achievements bereits freigeschaltet ist,
 * Abrufen aller Achievements eines Benutzers und Abrufen aller verfuegbaren Achievements.
 */
public class AchievementsController extends GeneralController {

    UserFactory users;
    AchievementsFactory achievements;

    /**
     * Konstruktor des AchievementsControllers, der die User- und Achievementsfactorys initialisiert.
     *
     * @param users Benutzerfabrik
     * @param achievements Erfolgsfabrik
     */
    @Inject
    public AchievementsController(UserFactory users, AchievementsFactory achievements) {
        super();
        this.users = users;
        this.achievements = achievements;
    }

    /**
     * Diese Methode wird aufgerufen, wenn ein Benutzer einen Erfolg freischaltet.
     *
     * @param request Die Anfrage, die den freizuschaltenden Erfolg enthaelt
     * @return Gibt einen HTTP-Response mit dem Ergebnis der Freischaltung zurueck.
     */
    public Result unlockAchievement(Http.Request request) {
        Achievement achievement = getAchievement(request);
        int user_ID = users.getUserByUsername(request.session().get("username").orElse("")).getUser_ID();
        return ok(String.valueOf(achievements.unlockAchievement(achievement, user_ID)));
    }

    /**
     * Gibt den Erfolg zurueck, der in der Anfrage angegeben wurde.
     *
     * @param request Die Anfrage, die den zu findenden Erfolg enthaelt.
     * @return Gibt den gefundenen Erfolg zurueck.
     */
    private Achievement getAchievement(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String name = jsonNode.get("name").asText();
        int level = jsonNode.get("level").asInt();
        String organ = jsonNode.get("organ").asText();
        Achievement achievement = null;
        if (organ.equals("null") && level == 0) {
            achievement = achievements.getAchievementByName(name);
        } else if (level == 0) {
            achievement = achievements.getAchievementByNameOrgan(name, organ);
        } else {
            achievement = achievements.getAchievementByNameLevelOrgan(name, level, organ);
        }
        return achievement;
    }

    /**
     * Diese Methode gibt den Erfolg zurueck, der in der Anfrage angegeben wurde, inklusive seiner Attribute.
     *
     * @param request Die Anfrage, die den zu findenden Erfolg enthaelt.
     * @return Gibt den gefundenen Erfolg inklusive seiner Attribute zurueck.
     * @throws JsonProcessingException Wenn es ein Problem bei der Verarbeitung der JSON-Daten gibt.
     */
    public Result getAchievementObject(Http.Request request) throws JsonProcessingException {
        Achievement achievement = getAchievement(request);
        HashMap<String, String> achievementMap = new HashMap<>();
        achievementMap.put("achievement_ID", String.valueOf(achievement.getAchievement_ID()));
        achievementMap.put("achievement_name", String.valueOf(achievement.getAchievement_name()));
        achievementMap.put("level", String.valueOf(achievement.getLevel()));
        achievementMap.put("organ", String.valueOf(achievement.getOrgan()));
        achievementMap.put("description", String.valueOf(achievement.getDescription()));
        achievementMap.put("lockedImgPath", String.valueOf(achievement.getLockedImgPath()));
        achievementMap.put("unlockedImgPath", String.valueOf(achievement.getUnlockedImgPath()));
        return ok(mapper.writeValueAsString(achievementMap));
    }

    /**
     * Ueberprueft, ob ein Achievement fuer den Benutzer freigeschaltet wurde.
     *
     * @param request Der HTTP-Request, der den JSON-Body enthaelt, der die Informationen zum Achievement enthaelt.
     * @return Das Ergebnis der Ueberpruefung, ob das Achievement fuer den Benutzer freigeschaltet wurde, als JSON-String.
     */
    public Result checkAchievement(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String name = jsonNode.get("name").asText();
        int level = jsonNode.get("level").asInt();
        String organ = jsonNode.get("organ").asText();
        Achievement achievement = achievements.getAchievementByNameLevelOrgan(name, level, organ);
        int user_ID = users.getUserByUsername(request.session().get("username").orElse("")).getUser_ID();
        return ok(String.valueOf(achievements.isUnlocked(achievement, user_ID)));
    }

    /**
     * Gibt eine Liste aller Achievements fuer einen bestimmten Benutzer zurueck.
     *
     * @param request Der HTTP-Request, der den JSON-Body enthaelt, der den Benutzernamen des Benutzers enthaelt,
     *                fuer den die Achievements zurueckgegeben werden sollen.
     * @return Eine Liste aller Achievements fuer den Benutzer als JSON-String.
     * @throws JsonProcessingException Wenn das Objekt nicht als JSON-String serialisiert werden kann.
     */
    public Result getAllAchievementsPerUser(Http.Request request) throws JsonProcessingException {
        User user = null;
        JsonNode jsonNode = request.body().asJson();
        String username = jsonNode.get("user").asText();
        if(username.equals("me")){
            user = users.getUserByUsername(request.session().get("username").orElse(""));
        }else{
            user = users.getUserByUsername(username);
        }
        List<Achievement> achievementsList = achievements.getAllAchievementsPerUser(user);
        HashMap<Integer, Achievement> achievementsMap = new HashMap<>();
        for (Achievement a : achievementsList) {
            achievementsMap.put(a.getAchievement_ID(), a);
        }
        return ok(mapper.writeValueAsString(achievementsMap));
    }

    /**
     * Gibt eine Liste aller Achievements zurueck.
     *
     * @param request Der HTTP-Request.
     * @return Eine Liste aller Achievements als JSON-String.
     * @throws JsonProcessingException Wenn das Objekt nicht als JSON-String serialisiert werden kann.
     */
    public Result getAllAchievements(Http.Request request) throws JsonProcessingException {
        List<Achievement> achievementsList = achievements.getAllAchievements();
        HashMap<Integer, Achievement> achievementsMap = new HashMap<>();
        for (Achievement a : achievementsList) {
            achievementsMap.put(a.getAchievement_ID(), a);
        }
        return ok(mapper.writeValueAsString(achievementsMap));
    }
}
