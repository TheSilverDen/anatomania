package models.factories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Question;
import models.User;
import play.db.Database;
import javax.inject.Inject;

/**
 *
 * Die Klasse QuestionFactory ermoeglicht den Zugriff auf die Datenbanktabelle "questions" und bietet Methoden zum Abrufen von Fragen.
 * Die Klasse verwendet Dependency Injection, um eine Datenbankverbindung zu erhalten und arbeitet mit einer
 * play.db.Database-Instanz.
 *
 */
public class QuestionFactory {
	private Database db;

	/**
	 * Konstruktor der Klasse, der die Abhaengigkeit von der Datenbank injiziert.
	 *
	 * @param db Die Datenbank, auf die zugegriffen werden soll.
	 */
	@Inject
	QuestionFactory(Database db) {
		this.db = db;
	}

	/**
	 * Gibt eine Liste von Fragen zurueck, die zu einem bestimmten Level und Organ gehoeren.
	 *
	 * @param level Das Level, fuer das die Fragen abgerufen werden sollen.
	 * @param organ Das Organ, fuer das die Fragen abgerufen werden sollen.
	 * @return Eine Liste von Fragen, die zu dem angegebenen Level und Organ gehoeren.
	 */
	public List<Question> getQuestionsByLevelAndOrgan(int level, String organ){
		return db.withConnection(conn -> {
			List<Question> questions = new ArrayList<>();
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM questions WHERE fk_level_ID = ? AND fk_organ_ID = ?");
			stmt.setInt(1, level);
			stmt.setString(2, organ);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Question question = new Question(rs);
				questions.add(question);
			}
			stmt.close();
			return questions;
		});
	}

	/**
	 * Gibt eine Frage anhand ihrer ID zurueck.
	 *
	 * @param question_ID Die ID der gesuchten Frage.
	 * @return Die Frage mit der angegebenen ID.
	 */
	public Question getQuestionById(int question_ID){
		return db.withConnection(conn -> {
			Question question = null;
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM questions WHERE question_ID = ?");
			stmt.setInt(1, question_ID);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				question = new Question(rs);
			}
			stmt.close();
			return question;
		});
	}

	/**
	 * Gibt eine Liste von Fragen zurueck, die von einem bestimmten Benutzer auf einem bestimmten Level und in einem bestimmten Organ korrekt beantwortet wurden.
	 *
	 * @param user   der Benutzer, dessen beantwortete Fragen zurueckgegeben werden sollen
	 * @param level  das Level, auf dem die Fragen korrekt beantwortet wurden
	 * @param organ  das Organ, zu dem die Fragen gehoeren
	 * @return eine Liste von Fragen, die von dem Benutzer auf dem angegebenen Level und Organ korrekt beantwortet wurden
	 */
	public List<Question> getCorrectlyAnsweredQuestionsPerLevel(User user, int level, String organ){
		return db.withConnection(conn -> {
			List<Question> correctlyAnsweredQuestions = new ArrayList<>();
			PreparedStatement stmt = conn.prepareStatement("SELECT questions.* FROM questions JOIN user_has_questions ON user_has_questions.questions_question_ID = questions.question_ID WHERE questions.fk_level_ID = ? AND user_has_questions.user_user_ID = ? AND questions.fk_organ_ID = ?;");
			stmt.setInt(1, level);
			stmt.setInt(2, user.getUser_ID());
			stmt.setString(3, organ);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Question question = new Question(rs);
				correctlyAnsweredQuestions.add(question);
			}
			stmt.close();
			return correctlyAnsweredQuestions;
		});
	}

	/**
	 * Gibt eine Liste aller Fragen zurueck, die von einem Benutzer richtig beantwortet wurden und zu einem bestimmten Organ gehoeren.
	 *
	 * @param user  Der Benutzer, dessen richtig beantwortete Fragen abgerufen werden sollen.
	 * @param organ Das Organ, zu dem die Fragen gehoeren sollen.
	 * @return Eine Liste von Question-Objekten, die von dem Benutzer richtig beantwortet wurden und zu dem angegebenen Organ gehoeren.
	 */
	public List<Question> getCorrectlyAnsweredQuestions(User user, String organ){
		return db.withConnection(conn -> {
			List<Question> correctlyAnsweredQuestions = new ArrayList<>();
			PreparedStatement stmt = conn.prepareStatement("SELECT questions.* FROM questions JOIN user_has_questions ON user_has_questions.questions_question_ID = questions.question_ID WHERE user_has_questions.user_user_ID = ? AND questions.fk_organ_ID = ?;");
			stmt.setInt(1, user.getUser_ID());
			stmt.setString(2, organ);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Question question = new Question(rs);
				correctlyAnsweredQuestions.add(question);
			}
			stmt.close();
			return correctlyAnsweredQuestions;
		});
	}
}
