public class Question {
    private int question_id;
    private String question;
    private String[] choix;
    private String[] responses;
    private int points;

    public Question(int question_id, String question, String[] choix, String[] response, int points) {
        this.question_id = question_id;
        this.question = question;
        this.responses = response;
        this.points = points;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getChoices() {
        return choix;
    }

    public int getId() {
        return question_id;
    }

    public String[] getResponse() {
        return responses;
    }

    public int getPoints() {
        return points;
    }
}