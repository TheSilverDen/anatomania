package models;
/**
 * Diese Klasse repraesentiert eine Verbindung zwischen einem User,
 * einer Frage und dem gesetztem Tag.
 */
public class User_Question_Tag {
    private User user;
    private Question question;
    private Tag tag;

    /**
     * Konstruktor fuer ein User_Question_Tag-Objekt.
     *
     * @param user der User, dem die Frage mit dem Tag zugeordnet ist
     * @param question die Frage, die mit dem Tag zugeordnet ist
     * @param tag der Tag, mit dem die Frage dem User zugeordnet ist
     */
    public User_Question_Tag(User user, Question question, Tag tag) {
        this.user = user;
        this.question = question;
        this.tag = tag;
    }

    /**
     * Getter fuer den User.
     *
     * @return der User, dem die Frage mit dem Tag zugeordnet ist
     */
    public User getUser() {
        return user;
    }

    /**
     * Setter fuer den User.
     *
     * @param user der neue User, dem die Frage mit dem Tag zugeordnet ist
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Getter fuer die Frage.
     *
     * @return die Frage, die mit dem Tag zugeordnet ist
     */
    public Question getQuestion() {
        return question;
    }

    /**
     * Setter fuer die Frage.
     *
     * @param question die neue Frage, die mit dem Tag zugeordnet ist
     */
    public void setQuestion(Question question) {
        this.question = question;
    }

    /**
     * Getter fuer den Tag.
     *
     * @return der Tag, mit dem die Frage dem User zugeordnet ist
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * Setter fuer den Tag.
     *
     * @param tag der neue Tag, mit dem die Frage dem User zugeordnet ist
     */
    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
