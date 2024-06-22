package models;

import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Diese Klasse repraesentiert ein Achievement-Objekt mit Eigenschaften wie
 * achievement_ID, name, description, punkte, difficulty_level_ID, organ,
 * lockedImgPath und unlockedImgPath.
 */
public class Achievement {

    private int achievement_ID;
    private String achievement_name;
    private int level;
    private String organ;
    private String description;
    private String lockedImgPath;
    private String unlockedImgPath;
    private int points;

    /**
     * Konstruktor der Klasse Achievement. Erzeugt ein Achievement-Objekt aus den
     * Werten, die aus der Datenbank gelesen werden.
     *
     * @param rs Result Set, das aus der Datenbank ausgelesen wird
     * @throws SQLException wenn ein SQL-Fehler auftritt
     */
    public Achievement(ResultSet rs) throws SQLException {
        this.achievement_ID = rs.getInt("achievement_ID");
        this.achievement_name = rs.getString("name");
        this.level = rs.getInt("difficulty_level_ID");
        this.organ = rs.getString("organ");
        this.description = rs.getString("description");
        this.lockedImgPath = rs.getString("lockedImgPath");
        this.unlockedImgPath = rs.getString("unlockedImgPath");
        this.points = rs.getInt("punkte");
    }

    /**
     * Konstruktor der Klasse Achievement. Erzeugt ein Achievement-Objekt mit den
     * uebergebenen Attributen.
     *
     * @param achievement_ID ID des Achievements
     * @param achievement_name Name des Achievements
     * @param level ID des Schwierigkeitsgrades
     * @param organ Organ, zu dem das Achievement gehoert
     * @param lockedImgPath Pfad zur gesperrten Grafik
     * @param unlockedImgPath Pfad zur entsperrten Grafik
     */
    public Achievement(int achievement_ID, String achievement_name, int level, String organ, String lockedImgPath, String unlockedImgPath) {
        this.achievement_ID = achievement_ID;
        this.achievement_name = achievement_name;
        this.level = level;
        this.organ = organ;
        this.unlockedImgPath = unlockedImgPath;
        this.lockedImgPath = lockedImgPath;
    }

    /**
     * Getter fuer die achievement_ID.
     *
     * @return achievement_ID
     */
    public int getAchievement_ID() {
        return achievement_ID;
    }

    /**
     * Setter fuer die achievement_ID.
     *
     * @param achievement_ID ID des Achievements
     */
    public void setAchievement_ID(int achievement_ID) {
        this.achievement_ID = achievement_ID;
    }

    /**
     * Getter fuer den name.
     *
     * @return achievement_name
     */
    public String getAchievement_name() {
        return achievement_name;
    }

    /**
     * Setter fuer den name.
     *
     * @param achievement_name Name des Achievements
     */
    public void setAchievement_name(String achievement_name) {
        this.achievement_name = achievement_name;
    }

    /**
     * Getter fuer die level.
     *
     * @return level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Setter fuer die level.
     *
     * @param level schwierigkeits level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Getter fuer das organ.
     *
     * @return organ
     */
    public String getOrgan() {
        return organ;
    }

    /**
     * Setter fuer das organ.
     *
     * @param organ fuer das achievement
     */
    public void setOrgan(String organ) {
        this.organ = organ;
    }

    /**
     * Getter fuer lockedImgPath.
     *
     * @return lockedImgPath
     */
    public String getLockedImgPath() {
        return lockedImgPath;
    }

    /**
     * Setter fuer lockedImgPath.
     *
     * @param lockedImgPath fuer das Achievement Bild
     */
    public void setLockedImgPath(String lockedImgPath) {
        this.lockedImgPath = lockedImgPath;
    }

    /**
     * Getter fuer unlockedImgPath.
     *
     * @return unlockedImgPath
     */
    public String getUnlockedImgPath() {
        return unlockedImgPath;
    }

    /**
     * Setter fuer unlockedImgPath
     *
     * @param unlockedImgPath fuer das Achievement Bild
     */
    public void setUnlockedImgPath(String unlockedImgPath) {
        this.unlockedImgPath = unlockedImgPath;
    }

    /**
     * Getter fuer die description
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter fuer die description
     *
     * @param description fuer das Achievements
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter fuer die punkte
     *
     * @return points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Setter fuer die punkte
     *
     * @param points fuer das Achievements
     */
    public void setPoints(int points) {
        this.points = points;
    }
}
