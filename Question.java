public class Question {
    private String question;
    private String response;
    private int points;

    public Question(String question, String response, int points) {
        this.question = question;
        this.response = response;
        this.points = points;
    }

    public String getQuestion() {
        return question;
    }

    public String getResponse() {
        return response;
    }

    public int getPoints() {
        return points;
    }
}