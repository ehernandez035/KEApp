package es.ehu.ehernandez035.kea;

public class Quizz {
    private int quizzid;
    private String description;
    private int correctAnswers;
    private int amount;

    public Quizz(int quizzid, String description, int correctAnswers, int amount) {
        this.quizzid = quizzid;
        this.description = description;
        this.correctAnswers = correctAnswers;
        this.amount = amount;
    }

    public int getQuizzid() {
        return quizzid;
    }

    public int getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

}
