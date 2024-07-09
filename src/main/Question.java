package main;

import java.util.List;

/**
 * Cette classe représente une question dans un quiz.
 * <p>
 * Une question est constituée d'un texte, de choix possibles, d'une réponse correcte, de points attribués, et d'un thème. 
 * Elle peut également avoir un auteur et un identifiant unique pour la gestion des questions dans le système.
 * </p>
 */
public class Question {
    private int question_id; // L'identifiant unique de la question
    private String question; // Le texte de la question
    private List<String> choix; // Les choix possibles pour la réponse à la question
    private String response; // La réponse correcte à la question
    private int points; // Le nombre de points attribués pour la réponse correcte
    private String theme; // Le thème de la question
    private String user_id; // L'identifiant de l'auteur de la question (utilisé pour les questions en attente de vérification)

    /**
     * Constructeur pour créer une nouvelle instance de Question sans spécifier l'auteur (utilisé lors de la création des questions).
     *
     * @param question_id L'identifiant unique de la question
     * @param question Le texte de la question
     * @param choix Les choix possibles pour la réponse à la question
     * @param response La réponse correcte à la question
     * @param points Le nombre de points attribués pour la réponse correcte
     * @param theme Le thème de la question
     */
    public Question(int question_id, String question, List<String> choix, String response, int points, String theme) {
        this.question_id = question_id;
        this.question = question;
        this.choix = choix;
        this.response = response;
        this.points = points;
        this.theme = theme;
    }

    /**
     * Constructeur pour créer une nouvelle instance de Question (utilisé lors de la soumission d'une question par un utilisateur).
     *
     * @param question Le texte de la question
     * @param choix Les choix possibles pour la réponse à la question
     * @param response La réponse correcte à la question
     * @param points Le nombre de points attribués pour la réponse correcte
     * @param theme Le thème de la question
     */
    public Question(String question, List<String> choix, String response, int points, String theme) {
        this.question = question;
        this.choix = choix;
        this.response = response;
        this.points = points;
        this.theme = theme;
    }

    /**
     * Constructeur pour créer une nouvelle instance de Question en attente d'une vérification admin (utilisé lors de la soumission de nouvelles questions).
     *
     * @param question_id L'identifiant unique de la question
     * @param question Le texte de la question
     * @param choix Les choix possibles pour la réponse à la question
     * @param response La réponse correcte à la question
     * @param points Le nombre de points attribués pour la réponse correcte
     * @param theme Le thème de la question
     * @param user_id L'identifiant de l'auteur de la question
     */
    public Question(int question_id, String question, List<String> choix, String response, int points, String theme, String user_id) {
        this.question_id = question_id;
        this.question = question;
        this.choix = choix;
        this.response = response;
        this.points = points;
        this.theme = theme;
        this.user_id = user_id;
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

    /**
     * Obtient le thème de la question.
     *
     * @return Le thème de la question
     */
    public String getTheme() {
        return theme;
    }

    /**
     * Obtient l'identifiant de l'auteur de la question.
     *
     * @return L'identifiant de l'auteur de la question
     */
    public String getUserId() {
        return user_id;
    }
}
