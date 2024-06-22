package controllers;

import models.Achievement;
import models.Question;
import models.Tag;
import models.factories.AchievementsFactory;
import models.factories.QuestionFactory;
import models.factories.TagFactory;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static play.mvc.Results.ok;

/**
 * Diese Klasse stellt Methoden bereit, um die Spiellogik zu steuern.
 */
public class GameLogicController {

	QuestionFactory questions;
	AchievementsFactory achievements;
	TagFactory tags;
	@Inject
	GameLogicController(QuestionFactory questions, AchievementsFactory achievements, TagFactory tags){
		this.questions = questions;
		this.achievements = achievements;
		this.tags = tags;
	}

	/**
	 * Berechnet den Zeitbonus basierend auf dem Timerwert und dem Gesamtpunktestand.
	 *
	 * @param request HTTP-Anfrageobjekt, das den Timerwert enthaelt.
	 * @param totalScore der Gesamtpunktestand, fuer den der Zeitbonus berechnet werden soll.
	 * @return der Zeitbonus, der dem Gesamtpunktestand hinzugefuegt werden soll.
	 */
	public int getTotalScoreWithTime(Http.Request request, int totalScore){
		int timeBonus = 0;
		int timerVal = Integer.parseInt(request.session().get("timer").orElse(""));
		double timerPercentage = ((timerVal/240.0)*100);
		if(timerPercentage>60.0){
			timeBonus = (int) Math.floor(totalScore * 1.25) - totalScore;
		}else if (timerPercentage<20.0){
			timeBonus = timeBonus - (totalScore - (int) Math.floor(totalScore * 0.75));
		}
		return timeBonus;
	}

	/**
	 * Prueft ob alle für das Spiel essentiellen Datenbankelemente in der Datenbank vorhanden sind
	 *
	 * @return Wahrheitswert, der angibt, ob alle Elemente vorhanden sind
	 */
	public Result checkDB(){
		return ok(String.valueOf(checkQuestions() && checkAchievements() && checkTags()));
	}

	/**
	 * Prueft ob für alle Fragen je Level und Organ mindestens fuenf in der Datenbank vorhanden sind
	 *
	 * @return Wahrheitswert, der angibt, ob genug Fragen vorhanden sind
	 */
	public boolean checkQuestions(){
		List<Question> brain1 = questions.getQuestionsByLevelAndOrgan(1, "Gehirn");
		List<Question> brain2 = questions.getQuestionsByLevelAndOrgan(2, "Gehirn");
		List<Question> brain3 = questions.getQuestionsByLevelAndOrgan(3, "Gehirn");
		List<Question> heart1 = questions.getQuestionsByLevelAndOrgan(1, "Herz");
		List<Question> heart2 = questions.getQuestionsByLevelAndOrgan(2, "Herz");
		List<Question> heart3 = questions.getQuestionsByLevelAndOrgan(3, "Herz");
		List<Question> lungs1 = questions.getQuestionsByLevelAndOrgan(1, "Lunge");
		List<Question> lungs2 = questions.getQuestionsByLevelAndOrgan(2, "Lunge");
		List<Question> lungs3 = questions.getQuestionsByLevelAndOrgan(3, "Lunge");
		List<Question> liver1 = questions.getQuestionsByLevelAndOrgan(1, "Leber");
		List<Question> liver2 = questions.getQuestionsByLevelAndOrgan(2, "Leber");
		List<Question> liver3 = questions.getQuestionsByLevelAndOrgan(3, "Leber");
		List<Question> stomach1 = questions.getQuestionsByLevelAndOrgan(1, "Magen");
		List<Question> stomach2 = questions.getQuestionsByLevelAndOrgan(2, "Magen");
		List<Question> stomach3 = questions.getQuestionsByLevelAndOrgan(3, "Magen");

		List<List<Question>> questionsPerLevelAndOrgan = new ArrayList<>();
		questionsPerLevelAndOrgan.add(brain1);
		questionsPerLevelAndOrgan.add(brain2);
		questionsPerLevelAndOrgan.add(brain3);
		questionsPerLevelAndOrgan.add(heart1);
		questionsPerLevelAndOrgan.add(heart2);
		questionsPerLevelAndOrgan.add(heart3);
		questionsPerLevelAndOrgan.add(lungs1);
		questionsPerLevelAndOrgan.add(lungs2);
		questionsPerLevelAndOrgan.add(lungs3);
		questionsPerLevelAndOrgan.add(liver1);
		questionsPerLevelAndOrgan.add(liver2);
		questionsPerLevelAndOrgan.add(liver3);
		questionsPerLevelAndOrgan.add(stomach1);
		questionsPerLevelAndOrgan.add(stomach2);
		questionsPerLevelAndOrgan.add(stomach3);
		for(List<Question> list : questionsPerLevelAndOrgan){
			if(list.size()<5){
				return false;
			}
		}
		return true;
	}
	/**
	 * Prueft für alle Achievements, ob diese in der Datenbank vorhanden sind
	 *
	 * @return Wahrheitswert, der angibt, ob alle Achievements vorhanden sind
	 */
	public boolean checkAchievements(){
		List<Achievement> novize = achievements.getAchievementsByName("Novize");
		List<Achievement> profi = achievements.getAchievementsByName("Profi");
		List<Achievement> experte = achievements.getAchievementsByName("Experte");
		List<Achievement> meister = achievements.getAchievementsByName("Meister");
		Achievement fehlerfrei = achievements.getAchievementByName("Fehlerfrei");
		List<Achievement> test = achievements.getAchievementsByName("Test");
		if(novize.size()<15){
			return false;
		}else if(profi.size()<15){
			return false;
		}else if(experte.size()<15){
			return false;
		}else if(meister.size()<5){
			return false;
		}else if(fehlerfrei==null){
			return false;
		}else if(test.size()<2){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * Prueft für alle Tags, ob diese in der Datenbank vorhanden sind
	 *
	 * @return Wahrheitswert, der angibt, ob alle Tags vorhanden sind
	 */
	public boolean checkTags(){
		List<Tag> allTags = tags.getAllTags();
		if(allTags.size()<4){
			return false;
		}
		for(Tag tag : allTags){
			if(tag.getTag_ID()!=0 && tag.getTag_ID()!=1 && tag.getTag_ID()!=2 && tag.getTag_ID()!=3){
				return false;
			}
		}
		return true;
	}
}
