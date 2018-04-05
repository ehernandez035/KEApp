package es.ehu.ehernandez035.kea;

public class Quizz {
    private int quizzid;
    private String description;

    public int getQuizzid() {
        return quizzid;
    }


    public String getDescription() {
        return description;
    }


    public Quizz(int quizzid, String description){
        this.quizzid = quizzid;
        this.description = description;
    }
}
