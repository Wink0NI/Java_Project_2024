import java.util.List;
import java.util.ArrayList;

public class Avatar {
    private String name;
    private int pointsOfLife;
    private List<Question> questions;

    public Avatar(String name, int pointsOfLife) {
        this.name = name;
        this.pointsOfLife = pointsOfLife;
        this.questions = new ArrayList<>();
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public int getPointsOfLife() {
        return pointsOfLife;
    }

    public void setPointsOfLife(int pointsOfLife) {
        this.pointsOfLife = pointsOfLife;
    }
}