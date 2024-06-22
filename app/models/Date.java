package models;
/**
 * Die Klasse Date repraesentiert ein Datum mit Uhrzeit.
 */
public class Date {
    private int year;
    private int monthOfYear;
    private int dayOfMonth;
    private int hour;
    private int minute;
    private int second;

    /**
     * Konstruktor fuer ein Date-Objekt.
     */
    public Date(){

    }

    /**
     * Konstruktor fuer ein Date-Objekt mit den Uebergebenen Werten.
     *
     * @param year das Jahr
     * @param monthOfYear der Monat (1-12)
     * @param dayOfMonth der Tag im Monat (1-31)
     * @param hour die Stunde (0-23)
     * @param minute die Minute (0-59)
     * @param second die Sekunde (0-59)
     */
    public Date(int year, int monthOfYear, int dayOfMonth, int hour, int minute, int second) {
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    /**
     * Getter fuer das Jahr.
     *
     * @return das Jahr
     */
    public int getYear() {
        return year;
    }

    /**
     * Setter fuer das Jahr.
     *
     * @param year das Jahr
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Getter fuer den Monat (1-12).
     *
     * @return der Monat
     */
    public int getMonthOfYear() {
        return monthOfYear;
    }

    /**
     * Setter fuer den Monat (1-12).
     *
     * @param monthOfYear der Monat
     */
    public void setMonthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    /**
     * Getter fuer den Tag im Monat (1-31).
     *
     * @return der Tag im Monat
     */
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * Setter fuer den Tag im Monat (1-31).
     *
     * @param dayOfMonth der Tag im Monat
     */
    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    /**
     * Getter fuer die Stunde (0-23).
     *
     * @return die Stunde
     */
    public int getHour() {
        return hour;
    }

    /**
     * Setter fuer die Stunde (0-23).
     *
     * @param hour die Stunde
     */
    public void setHour(int hour) {
        this.hour = hour;
    }

    /**
     * Getter fuer die Minute (0-59).
     *
     * @return die Minute
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Setter fuer die Minute (0-59).
     *
     * @param minute die Minute
     */
    public void setMinute(int minute) {
        this.minute = minute;
    }

    /**
     * Getter fuer die Sekunde (0-59).
     *
     * @return die Sekunde
     */
    public int getSecond() {
        return second;
    }

    /**
     * Setter fuer die Sekunde (0-59).
     *
     * @param second die Sekunde
     */
    public void setSecond(int second) {
        this.second = second;
    }
}
