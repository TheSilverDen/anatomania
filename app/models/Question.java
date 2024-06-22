package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * Die Klasse Question repraesentiert eine Frage mit ihren Antworten und
 * anderen relevanten Informationen wie z.B. der ID, der Schwierigkeitsstufe und der Organregion.
 */
public class Question {

	private int question_ID;
	private String question;
	private LinkedHashMap<String, Boolean> answers; //map mit 4 answer, boolean
	private int level;
	private String organ;

	/**
	 * Konstruktor fuer das Question-Objekt mit einer Frage, einer LinkedHashMap von Antworten und einem Schwierigkeitsgrad.
	 *
	 * @param myQuestion die Frage
	 * @param myAnswer eine LinkedHashMap mit den Antworten und ihren Wahrheitswerten
	 * @param level der Schwierigkeitsgrad der Frage
	 */
	public Question(String myQuestion, LinkedHashMap<String, Boolean> myAnswer, int level) {
		question = myQuestion;
		answers = myAnswer;
		this.level = level;
	}

	/**
	 * Konstruktor fuer das Question-Objekt mit einer Frage, einer LinkedHashMap von Antworten, einem Schwierigkeitsgrad und dem betreffenden Organ.
	 *
	 * @param myQuestion die Frage
	 * @param myAnswer eine LinkedHashMap mit den Antworten und ihren Wahrheitswerten
	 * @param level der Schwierigkeitsgrad der Frage
	 * @param organ das betreffende Organ
	 */
	public Question(String myQuestion, LinkedHashMap<String, Boolean> myAnswer, int level, String organ) {
		question = myQuestion;
		answers = myAnswer;
		this.level = level;
		this.organ = organ;
	}

	/**
	 * Konstruktor fuer das Question-Objekt, das Informationen aus einem ResultSet liest.
	 *
	 * @param rs das ResultSet, aus dem die Informationen gelesen werden sollen
	 * @throws SQLException wenn es ein Problem mit dem ResultSet gibt
	 */
	public Question(ResultSet rs) throws SQLException {
		this.question_ID = rs.getInt("question_ID");
		this.question = rs.getString("question");
		this.answers = new LinkedHashMap<>();
		this.answers.put(rs.getString("correct_answer"), true);
		this.answers.put(rs.getString("wrong_answer1"), false);
		this.answers.put(rs.getString("wrong_answer2"), false);
		this.answers.put(rs.getString("wrong_answer3"), false);
		this.level = rs.getInt("fk_level_ID");
		this.organ = rs.getString("fk_organ_ID");
	}

	/**
	 * Getter fuer die Frage-ID.
	 *
	 * @return Die Frage-ID als Integer.
	 */
	public int getQuestion_ID() {
		return question_ID;
	}

	/**
	 * Setter fuer die Frage-ID.
	 *
	 * @param question_ID Die Frage-ID als Integer.
	 */
	public void setQuestion_ID(int question_ID) {
		this.question_ID = question_ID;
	}

	/**
	 * Setter fuer die Frage.
	 *
	 * @param question Die Frage als String.
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * Gibt die LinkedHashMap mit den Antworten des Quiz-Objekts zurueck.
	 *
	 * @return die LinkedHashMap mit den Antworten des Quiz-Objekts
	 */
	public LinkedHashMap<String, Boolean> getAnswers() {
		return answers;
	}

	/**
	 * Setzt die LinkedHashMap mit den Antworten des Quiz-Objekts auf den gegebenen Wert.
	 *
	 * @param answers die neue LinkedHashMap mit den Antworten
	 */
	public void setAnswers(LinkedHashMap<String, Boolean> answers) {
		this.answers = answers;
	}

	/**
	 * Setter fuer die Level.
	 *
	 * @param level Die Level als Integer.
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Getter fuer das Organ.
	 *
	 * @return Organ das organ der Frage.
	 */
	public String getOrgan() {
		return organ;
	}

	/**
	 * Setter fuer das Organ.
	 *
	 * @param organ das organ der Frage.
	 */
	public void setOrgan(String organ) {
		this.organ = organ;
	}

	/**
	 * Getter fuer das Level.
	 *
	 * @return Das Level als Integer.
	 */
	public int getLevel(){
		return level;
	}

	/**
	 * Getter fuer die Frage.
	 *
	 * @return Die Frage als String.
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * Gibt den Schluessel fuer die Antwort an der gegebenen Indexposition zurueck.
	 *
	 * @param i der Index der Antwort
	 * @return der Schluessel fuer die Antwort an der gegebenen Indexposition
	 */
	public String getAnswerKey(int i) {
		return (String) answers.keySet().toArray()[i];
	}

	/**
	 * Gibt den Wert (true/false) fuer die Antwort an der gegebenen Indexposition zurueck.
	 *
	 * @param i der Index der Antwort
	 * @return der Wert (true/false) fuer die Antwort an der gegebenen Indexposition
	 */
	public Boolean getAnswerValue(int i) {
		return (Boolean) answers.values().toArray()[i];
	}

	/**
	 * Gibt den Schluessel (also die Textantwort) fuer die richtige Antwort zurueck.
	 *
	 * @return der Schluessel (also die Textantwort) fuer die richtige Antwort oder null, wenn keine richtige Antwort gefunden wurde
	 */
	public String getCorrectAnswer(){
		for(Map.Entry<String, Boolean> answer : answers.entrySet()){
			if(answer.getValue()){
				return answer.getKey();
			}
		}
		return null;
	}

}
