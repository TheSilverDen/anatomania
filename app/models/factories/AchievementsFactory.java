package models.factories;

import models.Achievement;
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
 * Die AchievementsFactory implementiert Methoden zur Erstellung, Abfrage und Verwaltung von Achievements.
 * Die Klasse verwendet Dependency Injection, um eine Datenbankverbindung zu erhalten und arbeitet mit einer
 * play.db.Database-Instanz.
 *
 */
public class AchievementsFactory {
    private Database db;

    /**
     * Konstruktor der Klasse, der die Abhaengigkeit von der Datenbank injiziert.
     *
     * @param db Die Datenbank, auf die zugegriffen werden soll.
     */
    @Inject
    AchievementsFactory(Database db) {
        this.db = db;
    }

    /**
     * Gibt das Achievement mit der angegebenen ID zurueck.
     *
     * @param id Die ID des zu suchenden Achievements
     * @return Das Achievement mit der angegebenen ID oder null, wenn kein Achievement gefunden wurde.
     */
    public Achievement getAchievementByID(int id){
        return db.withConnection(conn -> {
            Achievement achievement = null;
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM achievement WHERE achievement_ID = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                achievement = new Achievement(rs);
            }
            stmt.close();
            return achievement;
        });
    }

    /**
     * Gibt das Achievement mit dem angegebenen Namen zurueck.
     *
     * @param name Der Name des zu suchenden Achievements
     * @return Das Achievement mit dem angegebenen Namen oder null, wenn kein Achievement gefunden wurde.
     */
    public Achievement getAchievementByName(String name){
        return db.withConnection(conn ->{
            Achievement achievement = null;
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM achievement WHERE name = ?");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                achievement = new Achievement(rs);
            }
            stmt.close();
            return achievement;
        });
    }

    /**
     * Gibt eine Liste von Achievements zurueck, die den angegebenen Namen haben.
     *
     * @param name Der Name der zu suchenden Achievements
     * @return Eine Liste von Achievements mit dem angegebenen Namen oder eine leere Liste, wenn keine Achievements gefunden wurden.
     */
    public List<Achievement> getAchievementsByName(String name){
        return db.withConnection(conn ->{
            List<Achievement> achievements = new ArrayList<>();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM achievement WHERE name = ?");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                Achievement achievement = new Achievement(rs);
                achievements.add(achievement);
            }
            stmt.close();
            return achievements;
        });
    }

    /**
     * Gibt das Achievement mit dem angegebenen Namen und Organ zurueck.
     *
     * @param name  Der Name des zu suchenden Achievements
     * @param organ Das Organ des zu suchenden Achievements
     * @return Das Achievement mit dem angegebenen Namen und Organ oder null, wenn kein Achievement gefunden wurde.
     */
    public Achievement getAchievementByNameOrgan(String name, String organ){
        return db.withConnection(conn ->{
            Achievement achievement = null;
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM achievement WHERE name = ? AND organ = ?");
            stmt.setString(1, name);
            stmt.setString(2, organ);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                achievement = new Achievement(rs);
            }
            stmt.close();
            return achievement;
        });
    }

    /**
     * Gibt das Achievement mit dem angegebenen Namen, Level und Organ zurueck.
     *
     * @param name  Der Name des zu suchenden Achievements
     * @param level Der Schwierigkeitsgrad des zu suchenden Achievements
     * @param organ Das Organ des zu suchenden Achievements
     * @return Das Achievement mit dem angegebenen Namen, Level und Organ oder null, wenn kein Achievement gefunden wurde.
     */
    public Achievement getAchievementByNameLevelOrgan(String name, int level, String organ){
        return db.withConnection(conn ->{
            Achievement achievement = null;
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM achievement WHERE name = ? AND difficulty_level_ID = ? AND organ = ?");
            stmt.setString(1, name);
            stmt.setInt(2, level);
            stmt.setString(3, organ);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                achievement = new Achievement(rs);
            }
            stmt.close();
            return achievement;
        });
    }

    /**
     * Schaltet das angegebene Achievement fuer den angegebenen Benutzer frei, falls es noch nicht freigeschaltet wurde
     *
     * @param achievement Das freizuschaltende Achievement
     * @param user_ID     Die ID des Benutzers, fuer den das Achievement freigeschaltet werden soll
     * @return True, wenn das Achievement freigeschaltet wurde, sonst false.
     */
    public boolean unlockAchievement(Achievement achievement, int user_ID){
        return db.withConnection(conn -> {
            Achievement achievementsNew = null;
            String sql = "INSERT INTO user_has_achievement (user_user_ID, achievement_achievement_ID) SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM user_has_achievement WHERE user_user_ID = ? AND achievement_achievement_ID = ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, user_ID);
            stmt.setInt(2, achievement.getAchievement_ID());
            stmt.setInt(3, user_ID);
            stmt.setInt(4, achievement.getAchievement_ID());
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected>0;
        });
    }

    /**
     * Ueberprueft, ob das angegebene Achievement fuer den angegebenen Benutzer freigeschaltet ist.
     *
     * @param achievement Das zu ueberpruefende Achievement
     * @param user_ID     Die ID des Benutzers, fuer den das Achievement ueberprueft werden soll
     * @return True, wenn das Achievement freigeschaltet ist, sonst false.
     */
    public boolean isUnlocked(Achievement achievement, int user_ID){
        return db.withConnection(conn ->{
            String sql = "SELECT EXISTS(SELECT * FROM user_has_achievement WHERE user_user_ID = ? AND achievement_achievement_ID = ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, achievement.getAchievement_ID());
            stmt.setInt(2, user_ID);
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
     * Gibt eine Liste aller Achievements zurueck.
     *
     * @return Eine Liste aller Achievements oder eine leere Liste, wenn keine Achievements gefunden wurden.
     */
    public List<Achievement> getAllAchievements() {
        return db.withConnection(conn -> {
            List<Achievement> achievements = new ArrayList<>();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM achievement");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Achievement achievement = new Achievement(rs);
                achievements.add(achievement);
            }
            stmt.close();
            return achievements;
        });
    }

    /**
     * Gibt eine Liste aller Achievements zurueck, die fuer den angegebenen Benutzer freigeschaltet sind.
     *
     * @param user Der Benutzer, fuer den die freigeschalteten Achievements abgerufen werden sollen
     * @return Eine Liste aller freigeschalteten Achievements oder eine leere Liste, wenn keine freigeschalteten Achievements gefunden wurden.
     */
    public List<Achievement> getAllAchievementsPerUser(User user){
        return db.withConnection(conn ->{
            List<Achievement> achievements = new ArrayList<>();
            PreparedStatement stmt = conn.prepareStatement("SELECT achievement.* FROM user_has_achievement JOIN achievement ON user_has_achievement.achievement_achievement_ID = achievement.achievement_ID WHERE user_has_achievement.user_user_ID = ?;");
            stmt.setInt(1, user.getUser_ID());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Achievement achievement = new Achievement(rs);
                achievements.add(achievement);
            }
            stmt.close();
            return achievements;
        });
    }

}
