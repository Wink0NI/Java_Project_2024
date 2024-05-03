import java.util.List;

public class Question {
    private int question_id;
    private String question;
    private List<String> choix;
    private String response;
    private int points;

    public Question(int question_id, String question, List<String> choix, String response, int points) {
        this.question_id = question_id;
        this.question = question;
        this.response = response;
        this.points = points;
        this.choix = choix;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getChoices() {
        return choix;
    }

    public int getId() {
        return question_id;
    }

    public String getResponse() {
        return response;
    }

    public int getPoints() {
        return points;
    }
}