import java.util.List;
import java.util.ArrayList;

public class Game {
    private List<Avatar> avatars;
    private List<Question> questions;

    public Game() {
        avatars = new ArrayList<>();
        questions = new ArrayList<>();
    }

    public void addAvatar(Avatar avatar) {
        avatars.add(avatar);
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void launchChallenge(Avatar challenger, Avatar challenged) {
        // TO DO: implement challenge logic
    }
}