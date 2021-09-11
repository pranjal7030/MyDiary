package jain.pranjal.mydiary;

/**
 * Created by hp on 11/10/2019.
 */

public class Data {
    private String date;
    private String salutation;
    private String diaryEntry;

    public Data()
    {

    }

    public Data(String date, String salutation, String diaryEntry) {
        this.date = date;
        this.salutation = salutation;
        this.diaryEntry = diaryEntry;

    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getDiaryEntry() {
        return diaryEntry;
    }

    public void setDiaryEntry(String diaryEntry) {
        this.diaryEntry = diaryEntry;
    }
}
