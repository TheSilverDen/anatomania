package models.factories;

import com.google.inject.Inject;
import models.Question;
import models.Tag;
import play.db.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * Die TagFactory-Klasse bietet Methoden zur Manipulation von Tags und Fragen in der Datenbank.
 * Die Klasse verwendet Dependency Injection, um eine Datenbankverbindung zu erhalten und arbeitet mit einer
 * play.db.Database-Instanz.
 *
 */
public class TagFactory {

    Database db;

    /**
     * Konstruktor der Klasse, der die Abhaengigkeit von der Datenbank injiziert.
     *
     * @param db Die Datenbank, auf die zugegriffen werden soll.
     */
    @Inject
    public TagFactory(Database db){
        this.db = db;
    }

    /**
     * Ueberprueft, ob ein Benutzer einen Tag fuer eine bestimmte Frage gesetzt hat.
     *
     * @param user_id     Die ID des Benutzers.
     * @param question_id Die ID der Frage.
     * @return true, wenn der Benutzer den Tag hat, false, wenn nicht.
     */
    public boolean checkIfUser_has_Tag(int user_id, int question_id){
        return db.withConnection(conn -> {
            int count = 0;
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS Count FROM user_has_question_tag WHERE user_ID = ? AND question_ID = ?");
            stmt.setInt(1, user_id);
            stmt.setInt(2, question_id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                count = rs.getInt("Count");
            }
            return count > 0;
        });
    }

    /**
     * Aktualisiert den Tag fuer eine bestimmte Frage und einen bestimmten Benutzer.
     *
     * @param user_id     Die ID des Benutzers.
     * @param question_id Die ID der Frage.
     * @param tag_id      Die ID des Tags.
     * @return true, wenn die Aktualisierung erfolgreich war, false, wenn nicht.
     */
    public boolean updateQuestionTag(int user_id, int question_id, int tag_id){
        return db.withConnection(conn -> {
            PreparedStatement stmt = conn.prepareStatement("UPDATE user_has_question_tag SET tag_ID = ? WHERE user_ID = ? AND question_ID = ?");
            stmt.setInt(1, tag_id);
            stmt.setInt(2, user_id);
            stmt.setInt(3, question_id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected>0;
        });
    }

    /**
     * Fuegt einen Tag fuer eine bestimmte Frage und einen bestimmten Benutzer hinzu.
     *
     * @param user_id     Die ID des Benutzers.
     * @param question_id Die ID der Frage.
     * @param tag_id      Die ID des Tags.
     * @return true, wenn das Hinzufuegen erfolgreich war, false, wenn nicht.
     */
    public boolean insertQuestionTag(int user_id, int question_id, int tag_id){
        return db.withConnection(conn -> {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO user_has_question_tag (user_ID, question_ID, tag_ID) VALUES (?, ?, ?)");
            stmt.setInt(1, user_id);
            stmt.setInt(2, question_id);
            stmt.setInt(3, tag_id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected >0;
        });
    }

    /**
     * Gibt eine Liste von Fragen zurueck, die mit einem bestimmten Tag versehen sind und einem Benutzer zugeordnet sind.
     *
     * @param user_id ID des Benutzers
     * @return Liste von Fragen, die mit einem bestimmten Tag versehen sind und einem Benutzer zugeordnet sind
     */
    public List<Question> getAllTaggedQuestions(int user_id){
        return db.withConnection(conn ->{
            List<Question> taggedQuestions = new ArrayList<>();
            PreparedStatement stmt = conn.prepareStatement("SELECT questions.* FROM questions q JOIN user_question_tag uqt ON q.question_ID = uqt.question_ID WHERE uqt.user_ID = ?");
            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                Question question = new Question(rs);
                taggedQuestions.add(question);
            }
            stmt.close();
            return taggedQuestions;
        });
    }

    /**
     * Gibt eine Liste von Fragen zurueck, die mit einem bestimmten Tag, einem bestimmten Schwierigkeitsgrad und einem bestimmten Organ verbunden sind.
     *
     * @param tagID ID des Tags
     * @param level Schwierigkeitsgrad der Frage
     * @param organ Name des Organs
     * @return Liste von Fragen, die mit einem bestimmten Tag, einem bestimmten Schwierigkeitsgrad und einem bestimmten Organ verbunden sind
     */
    public List<Question> getQuestionByTagLevelOrgan(int tagID, int level, String organ){
        return db.withConnection(conn ->{
            List<Question> taggedQuestions = new ArrayList<>();
            PreparedStatement stmt = conn.prepareStatement("SELECT q.* FROM questions q JOIN user_has_question_tag uqt ON q.question_ID = uqt.question_ID WHERE uqt.tag_ID = ? AND q.fk_level_ID = ? AND q.fk_organ_ID = ?");
            stmt.setInt(1, tagID);
            stmt.setInt(2, level);
            stmt.setString(3, organ);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                Question question = new Question(rs);
                taggedQuestions.add(question);
            }
            stmt.close();
            return taggedQuestions;
        });
    }

    /**
     * Gibt die Gesamtzahl aller Fragen in der Datenbank zurueck.
     *
     * @return Gesamtzahl aller Fragen in der Datenbank
     */
    public int getTotalQuestionCount(){
        return db.withConnection(conn ->{
            int countAllQuestions = 0;
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS CountQuestions FROM questions");
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                countAllQuestions = rs.getInt("CountQuestions");
            }
            return countAllQuestions;
        });
    }

    /**
     * Gibt die Anzahl der Fragen zurueck, die einem bestimmten Tag und einem bestimmten Benutzer zugeordnet sind.
     *
     * @param tag_ID  ID des Tags
     * @param user_ID ID des Benutzers
     * @return Anzahl der Fragen, die einem bestimmten Tag und einem bestimmten Benutzer zugeordnet sind
     */
    public int getQuestionCountPerTag(int tag_ID, int user_ID){
        return db.withConnection(conn->{
            int countPerTag = 0;
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS CountQuestions FROM user_has_question_tag WHERE tag_ID = ? AND user_ID = ?");
            stmt.setInt(1, tag_ID);
            stmt.setInt(2, user_ID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                countPerTag = rs.getInt("CountQuestions");
            }
            return countPerTag;
        });
    }
    /**
     * Gibt einer Liste aller vorhandenen Tags zurück.
     *
     * @return eine Liste aller in der Datenbank vorhandenen Tags
     */
    public List<Tag> getAllTags(){
        return db.withConnection(conn->{
            List<Tag> allTags = new ArrayList<>();
            PreparedStatement stmt = conn.prepareStatement("SELECT *  FROM tags");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Tag tag = new Tag(rs);
                allTags.add(tag);
            }
            return allTags;
        });
    }
}
