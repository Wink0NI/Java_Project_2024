import java.util.List;

/**
 * Cette classe représente une question dans un quiz.
 */
public class Question {
    private int question_id; // L'identifiant unique de la question
    private String question; // Le texte de la question
    private List<String> choix; // Les choix possibles pour la réponse à la question
    private String response; // La réponse correcte à la question
    private int points; // Le nombre de points attribués pour la réponse correcte

    
    public Question(int question_id, String question, List<String> choix, String response, int points, String theme) {
        this.question_id = question_id;
        this.question = question;
        this.response = response;
        this.points = points;
        this.choix = choix;
    }

    /**
     * Constructeur pour créer une nouvelle instance de Question.
     *
     * @param question_id L'identifiant unique de la question
     * @param question Le texte de la question
     * @param choix Les choix possibles pour la réponse à la question
     * @param response La réponse correcte à la question
     * @param points Le nombre de points attribués pour la réponse correcte
     * @param theme Theme de la question
     */
    public Question(String question, List<String> choix, String response, int points, String theme) {
        this.question = question;
        this.response = response;
        this.points = points;
        this.choix = choix;
    }

    /**
     * Obtient le texte de la question.
     *
     * @return Le texte de la question
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Obtient la liste des choix possibles pour la réponse à la question.
     *
     * @return La liste des choix possibles
     */
    public List<String> getChoices() {
        return choix;
    }

    /**
     * Obtient l'identifiant unique de la question.
     *
     * @return L'identifiant unique de la question
     */
    public int getId() {
        return question_id;
    }

    /**
     * Obtient la réponse correcte à la question.
     *
     * @return La réponse correcte à la question
     */
    public String getResponse() {
        return response;
    }

    /**
     * Obtient le nombre de points attribués pour la réponse correcte.
     *
     * @return Le nombre de points attribués pour la réponse correcte
     */
    public int getPoints() {
        return points;
    }
}
