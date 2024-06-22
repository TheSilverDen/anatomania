package models;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Diese Klasse repraesentiert einen Tag, der einer Frage zugeordnet werden kann.
 */
public class Tag {
    private int tag_ID;
    private String tagName;

    /**
     * Konstruktor fuer ein Tag-Objekt.
     *
     * @param tag_ID die ID des Tags
     * @param tagName der Name des Tags
     */
    public Tag(int tag_ID, String tagName) {
        this.tag_ID = tag_ID;
        this.tagName = tagName;
    }

    public Tag(ResultSet rs) throws SQLException {
        this.tag_ID = rs.getInt("tag_ID");
        this.tagName = rs.getString("tag_name");
    }

    /**
     * Getter fuer die ID des Tags.
     *
     * @return die ID des Tags
     */
    public int getTag_ID() {
        return tag_ID;
    }

    /**
     * Setter fuer die ID des Tags.
     *
     * @param tag_ID die neue ID des Tags
     */
    public void setTag_ID(int tag_ID) {
        this.tag_ID = tag_ID;
    }

    /**
     * Getter fuer den Namen des Tags.
     *
     * @return der Name des Tags
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Setter fuer den Namen des Tags.
     *
     * @param tagName der neue Name des Tags
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
