package main;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.HashMap;

/**
 * Cette classe permet de gérer les bases de données SQL et csv.
 * 
 * @author Moi
 * @version 1.0
 */
public class DBProcess {
    // URL pour se connecter
    private String db_url = "jdbc:sqlite:src/db/db_jeu.db";
    private String nom_admin = "admin";

    /**
     * Permet de rajouter un utilisateur dans la bdd.
     * 
     * @param avatar Utilisateur à rajouter
     * @throws SQLException Pour gérer les traitements SQL
     */
    public void inscrire(Avatar avatar) {
        // Si l'utilisateur n'existe pas
        if (!isUser(avatar.getName())) {
            try {
                Connection inscription_conn = DriverManager.getConnection(db_url);
                Statement inscription_stmt = inscription_conn.createStatement();

                // On ajoute l'utilisateur dans la BDD Joueurs
                inscription_stmt.executeUpdate(String.format(
                        "INSERT INTO Joueurs (user_id, name, mdp, pv) VALUES ('%s','%s', '%s', 0)",
                        avatar.getId(),
                        avatar.getName(),
                        avatar.getMdp(),
                        avatar.getPV()));

                // On rajoute également l'id de l'utilisateur dans les Stats
                inscription_stmt.executeUpdate(String.format(
                        "INSERT INTO Stats (user_id) VALUES ('%s')",
                        avatar.getId()));

                inscription_conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("WARNING: Ce nom d'utilisateur existe déjà !");
        }
    }

    /**
     * Permet de vérifier si l'utilisateur est correct.
     * 
     * @param username nom d'utilisateur
     * @param password mot de passe de l'utilisateur
     * @return boolean true si le mot de passe de l'utilisateur est correct sinon false
     * @throws SQLException Pour gérer les traitements SQL
     */
    public boolean connecter(String username, String password) {
        // Si l'utilisateur existe
        if (isUser(username)) {
            try {
                Connection connexion_conn = DriverManager.getConnection(db_url);
                Statement connexion_stmt = connexion_conn.createStatement();

                // On récupère les utilisateurs dans la BDD Joueurs
                ResultSet users = connexion_stmt
                        .executeQuery(String.format("SELECT name, mdp FROM Joueurs WHERE name = '%s'", username));

                while (users.next()) {
                    // Si le mot de passe est correct on retourne true -> connexion réussie
                    if (users.getString("name").equals(username) && users.getString("mdp").equals(password)) {
                        connexion_conn.close();
                        return true;
                    }

                }
                connexion_conn.close();
                // dans ce cas, le mot de passe est incorrect
                System.out.println("Identifiant incorrect !");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("WARNING: Ce nom d'utilisateur n'existe pas !");
        }
        return false;
    }

    /**
     * Permet de vérifier si l'utilisateur existe.
     * 
     * @param user nom d'utilisateur
     * @return boolean true si l'utilisateur existe sinon false
     * @throws SQLException Pour gérer les traitements SQL
     */
    public boolean isUser(String user) {
        try {
            Connection isUser_conn = DriverManager.getConnection(db_url);
            Statement isUser_stmt = isUser_conn.createStatement();

            // si il existe un utilisateur qui a ce nom, retourne vrai
            if (isUser_stmt.executeQuery(String.format("SELECT name FROM Joueurs WHERE name = '%s'", user)).next()) {
                isUser_conn.close();
                return true;
            }
            isUser_conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Permet de récupérer un utilisateur par son id.
     * 
     * @param user_id identifiant d'un utilisateur
     * @return Un avatar si l'utilisateur existe sinon null
     * @throws SQLException Pour gérer les traitements SQL
     */
    public Avatar getUserById(String user_id) {
        try {
            Connection getUser_conn = DriverManager.getConnection(db_url);
            Statement getUser_stmt = getUser_conn.createStatement();

            // requete sql pour chercher id d'un utilisateur
            ResultSet resp = getUser_stmt
                    .executeQuery(String.format("SELECT * FROM Joueurs WHERE user_id = '%s'", user_id));

            // si c'est le cas
            if (resp.next()) {
                // On transforme la réponse SQL en une classe Avatar
                Avatar avatar = new Avatar(resp.getString("user_id"), resp.getString("name"), resp.getString("mdp"),
                        resp.getInt("pv"));
                getUser_conn.close();
                return avatar;
            }
            getUser_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        // sinon on retourne un utilisateur vide
        return null;
    }

    /**
     * Permet de récupérer un utilisateur par son pseudonyme.
     * 
     * @param name nom d'utilisateur
     * @return Un avatar si l'utilisateur existe sinon null
     * @throws SQLException Pour gérer les traitements SQL
     */
    public Avatar getUserByName(String name) {
        try {
            Connection getUser_conn = DriverManager.getConnection(db_url);
            Statement getUser_stmt = getUser_conn.createStatement();

            // requête sql pour récupérer les lignes de données qui ont le nom voulu
            ResultSet resp = getUser_stmt
                    .executeQuery(String.format("SELECT * FROM Joueurs WHERE name = '%s'", name));

            // si c'est le cas
            if (resp.next()) {
                // On transforme la réponse sql en classe Avatar
                Avatar avatar = new Avatar(resp.getString("user_id"), resp.getString("name"), resp.getString("mdp"),
                        resp.getInt("pv"));
                getUser_conn.close();
                return avatar;
            }
            getUser_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Sinon on ne retourne rien
        return null;
    }

    /**
     * Permet de changer le nom d'un utilisateur.
     * 
     * @param user_id id d'utilisateur
     * @param user nouveau nom d'utilisateur
     * @throws SQLException Pour gérer les traitements SQL
     */
    public void updateUsername(String user_id, String new_username) {

        try {
            Connection updateUsername_conn = DriverManager.getConnection(db_url);
            Statement updateUsername_stmt = updateUsername_conn.createStatement();

            // requete sql pour modifier le pseudonyme d'un utilisateur
            updateUsername_stmt.executeUpdate(
                    String.format("UPDATE Joueurs SET name = '%s' WHERE user_id = '%s'", new_username, user_id));

            updateUsername_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
    * Méthode qui renvoie le classement des utilisateurs en fonction de leurs points de vie.
    * 
    * @return Une liste de chaînes contenant les identifiants des utilisateurs.
    */
    public List<String> get_classement_points() {
        List<String> classement = new ArrayList<>();

        try {
            Connection get_duel_conn = DriverManager.getConnection(db_url);
            Statement get_duel_stmt = get_duel_conn.createStatement();

            ResultSet resultat = get_duel_stmt.executeQuery("SELECT user_id FROM Joueurs ORDER BY pv DESC");

            while (resultat.next()) {

                classement.add(resultat.getString("user_id"));

            }
            get_duel_conn.close();
            return classement;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classement;
    }
    /**
     * Méthode qui renvoie le classement des utilisateurs en fonction de leurs victoires.
     * 
     * @return Une liste de chaînes contenant les identifiants des utilisateurs.
     */
    public List<String> get_classement_victoire() {
        List<String> classement = new ArrayList<>();

        try {
            Connection get_duel_conn = DriverManager.getConnection(db_url);
            Statement get_duel_stmt = get_duel_conn.createStatement();

            ResultSet resultat = get_duel_stmt.executeQuery("SELECT user_id FROM Stats ORDER BY victoire_vs DESC");

            while (resultat.next()) {

                classement.add(resultat.getString("user_id"));

            }
            get_duel_conn.close();
            return classement;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classement;
    }

    /**
     * Méthode qui renvoie le nombre de questions pour un thème donné.
     * 
     * @param theme Le thème pour lequel on souhaite obtenir le nombre de questions.
     * @return Le nombre de questions pour le thème spécifié.
     */
    public int get_nb_questions(String theme) {
        int nb_questions = 0;
        try {
            Connection get_nb_questions_conn = DriverManager.getConnection(db_url);
            Statement get_nb_questions_stmt = get_nb_questions_conn.createStatement();
            String query = "SELECT COUNT(*) FROM Questions";
            if (theme.length() > 0)
                query += " WHERE theme = '" + theme + "';";
            ResultSet resultat = get_nb_questions_stmt.executeQuery(query);

            if (resultat.next()) {
                nb_questions = resultat.getInt("COUNT(*)");
            }
            get_nb_questions_conn.close();
            return nb_questions;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nb_questions;
    }
    /**
     * Méthode qui met à jour les points de vie d'un utilisateur.
     * 
     * @param user_id L'identifiant de l'utilisateur dont les points de vie doivent être mis à jour.
     * @param value La nouvelle valeur des points de vie.
     */
    public void updatePV(String user_id, int value) {

        try {
            Connection maj_pv_conn = DriverManager.getConnection(db_url);
            Statement maj_pv_stmt = maj_pv_conn.createStatement();

            maj_pv_stmt.executeUpdate(String.format("UPDATE Joueurs SET pv = %d WHERE user_id='%s'", value, user_id));

            maj_pv_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Méthode qui génère une liste de questions pour un thème donné.
     * 
     * @param nb_questions_totales Le nombre total de questions à générer.
     * @param th Le thème pour lequel on souhaite générer les questions.
     * @return Une liste de questions.
     */
    public List<Question> generate_question(int nb_questions_totales, String th) {
        List<Question> questions = new ArrayList<Question>();

        try {
            Connection generate_question_conn = DriverManager.getConnection(db_url);
            Statement generate_question_stmt = generate_question_conn.createStatement();

            String where = "";
            if (th.length() > 0)
                where = String.format("WHERE theme = '%s'", th);
            ResultSet resultat = generate_question_stmt.executeQuery(String
                    .format("SELECT * FROM Questions %s ORDER BY RANDOM() LIMIT %d;", where, nb_questions_totales));

            while (resultat.next()) {

                int id = resultat.getInt("question_id");
                String texte = resultat.getString("question");
                int val = resultat.getInt("point");
                List<String> choix = new ArrayList<String>();
                choix.add(resultat.getString("choix1"));
                choix.add(resultat.getString("choix2"));
                if (resultat.getString("choix3") != null)
                    choix.add(resultat.getString("choix3"));
                if (resultat.getString("choix4") != null)
                    choix.add(resultat.getString("choix4"));
                String reponse = resultat.getString("response");
                String theme = resultat.getString("theme");

                questions.add(new Question(id, texte, choix, reponse, val, theme));

            }
            generate_question_conn.close();
            return questions;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }
    /**
     * Méthode qui renvoie la liste des questions d'un duel donné.
     * 
     * @param duel_id L'identifiant du duel pour lequel on souhaite obtenir les questions.
     * @return Une liste de questions.
     */
    public List<Question> get_question_duel(String duel_id) {
        List<Question> questions = new ArrayList<Question>();

        try {
            Connection get_question_duel_conn = DriverManager.getConnection(db_url);
            Statement get_question_duel_stmt = get_question_duel_conn.createStatement();

            ResultSet resultat = get_question_duel_stmt.executeQuery(String
                    .format("SELECT * FROM duel d JOIN Questions_duel qd ON d.duel_id = qd.duel_id JOIN Questions q ON q.question_id = qd.question_id WHERE d.duel_id = '%s' ORDER BY RANDOM()",
                            duel_id));

            while (resultat.next()) {

                int id = resultat.getInt("question_id");
                String texte = resultat.getString("question");
                int val = resultat.getInt("point");
                List<String> choix = new ArrayList<String>();
                choix.add(resultat.getString("choix1"));
                choix.add(resultat.getString("choix2"));
                if (resultat.getString("choix3") != null)
                    choix.add(resultat.getString("choix3"));
                if (resultat.getString("choix4") != null)
                    choix.add(resultat.getString("choix4"));
                String reponse = resultat.getString("response");
                String theme = resultat.getString("theme");

                questions.add(new Question(id, texte, choix, reponse, val, theme));

            }
            get_question_duel_conn.close();
            return questions;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }
    /**
     * Méthode qui renvoie la liste des questions en attente de réponse.
     * 
     * @return Une liste de questions.
     */
    public List<Question> get_question_attente() {
        List<Question> questions = new ArrayList<>();
        try {
            Connection get_question_attente_conn = DriverManager.getConnection(db_url);
            Statement get_question_attente_stmt = get_question_attente_conn.createStatement();

            ResultSet resultat = get_question_attente_stmt
                    .executeQuery("SELECT * FROM Questions_request WHERE status IS NULL");

            while (resultat.next()) {
                int id = resultat.getInt("question_id");
                String texte = resultat.getString("question");
                int val = resultat.getInt("point");
                List<String> choix = new ArrayList<String>();
                choix.add(resultat.getString("choix1"));
                choix.add(resultat.getString("choix2"));
                if (resultat.getString("choix3") != null)
                    choix.add(resultat.getString("choix3"));
                if (resultat.getString("choix4") != null)
                    choix.add(resultat.getString("choix4"));
                String reponse = resultat.getString("response");
                String theme = resultat.getString("theme");

                String user_id = resultat.getString("user_id");

                questions.add(new Question(id, texte, choix, reponse, val, theme, user_id));

            }
            get_question_attente_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }
    /**
     * Méthode qui renvoie la liste des questions d'un utilisateur donné.
     * 
     * @param user_id L'identifiant de l'utilisateur pour lequel on souhaite obtenir les questions.
     * @return Une liste de questions.
     */
    public List<Question> get_question_user(String user_id) {
        List<Question> questions = new ArrayList<Question>();

        try {
            Connection generate_question_conn = DriverManager.getConnection(db_url);
            Statement generate_question_stmt = generate_question_conn.createStatement();

            ResultSet resultat = generate_question_stmt.executeQuery(String
                    .format("SELECT * FROM Questions_request WHERE user_id = '%s' AND status IS NULL ", user_id));

            while (resultat.next()) {

                int id = resultat.getInt("question_id");
                String texte = resultat.getString("question");
                int val = resultat.getInt("point");
                List<String> choix = new ArrayList<String>();
                choix.add(resultat.getString("choix1"));
                choix.add(resultat.getString("choix2"));
                if (resultat.getString("choix3") != null)
                    choix.add(resultat.getString("choix3"));
                if (resultat.getString("choix4") != null)
                    choix.add(resultat.getString("choix4"));
                String reponse = resultat.getString("response");
                String theme = resultat.getString("theme");

                questions.add(new Question(id, texte, choix, reponse, val, theme));

            }
            generate_question_conn.close();
            return questions;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }
    /**
     * Méthode qui renvoie la liste des questions répondues par un utilisateur.
     * 
     * @param user_id L'identifiant de l'utilisateur pour lequel on souhaite obtenir les questions.
     * @return Une liste de questions.
     */
    public List<Question> get_question_response(String user_id) {
        List<Question> questions = new ArrayList<>();
        try {
            Connection get_question_attente_conn = DriverManager.getConnection(db_url);
            Statement get_question_attente_stmt = get_question_attente_conn.createStatement();

            ResultSet resultat = get_question_attente_stmt
                    .executeQuery(
                            String.format(
                                    "SELECT * FROM Questions_request WHERE status IS NOT NULL AND user_id = '%s'",
                                    user_id));

            while (resultat.next()) {
                int id = resultat.getInt("question_id");
                String texte = resultat.getString("question");
                int val = resultat.getInt("point");
                List<String> choix = new ArrayList<String>();
                choix.add(resultat.getString("choix1"));
                choix.add(resultat.getString("choix2"));
                if (resultat.getString("choix3") != null)
                    choix.add(resultat.getString("choix3"));
                if (resultat.getString("choix4") != null)
                    choix.add(resultat.getString("choix4"));
                String reponse = resultat.getString("response");
                String theme = resultat.getString("theme");

                String status = resultat.getString("status");

                questions.add(new Question(id, texte, choix, reponse, val, theme, status));

            }
            get_question_attente_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }
    /**
     * Méthode qui supprime une demande de question.
     * 
     * @param question_id L'identifiant de la question à supprimer.
     */
    public void removeQuestionRequest(int question_id) {
        try {
            Connection removeQuestionRequest_conn = DriverManager.getConnection(db_url);
            Statement removeQuestionRequest_stmt = removeQuestionRequest_conn.createStatement();

            removeQuestionRequest_stmt.executeUpdate(String
                    .format("DELETE FROM Questions_Request WHERE question_id = %d",
                            question_id));

            removeQuestionRequest_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Méthode qui met à jour le statut d'une demande de question.
     * 
     * @param question_id L'identifiant de la question dont le statut doit être mis à jour.
     * @param status Le nouveau statut de la question.
     */
    public void updateQuestionRequestStatus(int question_id, String status) {

        try {
            Connection updateQuestionRequestStatus_conn = DriverManager.getConnection(db_url);
            Statement updateQuestionRequestStatus_stmt = updateQuestionRequestStatus_conn.createStatement();

            updateQuestionRequestStatus_stmt.executeUpdate(
                    String.format("UPDATE Questions_request SET status = '%s' WHERE question_id = %d", status,
                            question_id));

            updateQuestionRequestStatus_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    /**
     * Méthode qui renvoie les informations d'un duel donné.
     * 
     * @param duel_id L'identifiant du duel pour lequel on souhaite obtenir les informations.
     * @return Un objet HashMap contenant les informations du duel.
     */
    public HashMap<String, Object> get_duel(String duel_id) {

        try {
            Connection get_duel_conn = DriverManager.getConnection(db_url);
            Statement get_duel_stmt = get_duel_conn.createStatement();

            ResultSet resultat = get_duel_stmt.executeQuery(String
                    .format("SELECT * FROM duel WHERE duel_id = '%s'",
                            duel_id));

            if (resultat.next()) {
                HashMap<String, Object> line = new HashMap<>();
                line.put("duel_id", resultat.getString("duel_id"));
                line.put("user_atq", resultat.getString("user_atq"));
                line.put("temps_limite", resultat.getTimestamp("temps_limite"));
                line.put("score_atq", resultat.getInt("score_atq"));

                get_duel_conn.close();
                return line;

            }
            get_duel_conn.close();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Méthode qui renvoie la liste des duels où l'attaquant est le joueur spécifié.
     * 
     * @param attaquant L'identifiant du joueur attaquant.
     * @return Une liste d'objets HashMap contenant les informations des duels.
     */
    public List<HashMap<String, String>> get_duels_resultat(String attaquant) {
        List<HashMap<String, String>> duels = new ArrayList<>();

        try {
            Connection get_duels_resultat_conn = DriverManager.getConnection(db_url);
            Statement get_duels_resultat_stmt = get_duels_resultat_conn.createStatement();

            ResultSet resultat = get_duels_resultat_stmt.executeQuery(String
                    .format("SELECT * FROM duel WHERE user_atq = '%s' AND vainqueur IS NOT NULL",
                            attaquant));

            while (resultat.next()) {
                HashMap<String, String> line = new HashMap<>();
                line.put("duel_id", resultat.getString("duel_id"));
                line.put("user_cible", resultat.getString("user_cible"));
                line.put("vainqueur", resultat.getString("vainqueur"));

                duels.add(line);

            }
            get_duels_resultat_conn.close();

            for (HashMap<String, String> duel : duels) {
                removeQuestionDuel(duel.get("duel_id"));
            }
            return duels;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return duels;
    }
    /**
     * Méthode qui renvoie la liste des duels où le joueur cible est le joueur spécifié.
     * 
     * @param cible L'identifiant du joueur cible.
     * @return Une liste d'objets HashMap contenant les informations des duels.
     */
    public List<HashMap<String, Object>> get_duels(String cible) {
        List<HashMap<String, Object>> duels = new ArrayList<>();

        try {
            Connection get_duel_conn = DriverManager.getConnection(db_url);
            Statement get_duel_stmt = get_duel_conn.createStatement();

            ResultSet resultat = get_duel_stmt.executeQuery(String
                    .format("SELECT * FROM duel WHERE user_cible = '%s'AND vainqueur IS NULL ORDER BY temps_limite",
                            cible));

            while (resultat.next()) {
                HashMap<String, Object> line = new HashMap<>();
                line.put("duel_id", resultat.getString("duel_id"));
                line.put("user_atq", resultat.getString("user_atq"));
                line.put("temps_limite", resultat.getTimestamp("temps_limite"));
                line.put("score_atq", resultat.getInt("score_atq"));

                duels.add(line);

            }
            get_duel_conn.close();
            return duels;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return duels;
    }
    /**
     * Méthode qui ajoute une question à la base de données.
     * 
     * @param question La question à ajouter.
     */
    public void addQuestion(Question question) {
        try {
            Connection addQuestionConn = DriverManager.getConnection(db_url);

            String requeteSql = "INSERT INTO Questions (question, point, choix1, choix2, choix3, choix4, response, theme) VALUES (?,?,?,?,?,?,?,?)";

            PreparedStatement addQuestionStmt = addQuestionConn.prepareStatement(requeteSql);

            // Définir les paramètres de la requête
            addQuestionStmt.setString(1, question.getQuestion()); // Pour le champ question
            addQuestionStmt.setInt(2, question.getPoints()); // Pour le champ point
            addQuestionStmt.setString(3, question.getChoices().get(0)); // Pour le champ choix1
            addQuestionStmt.setString(4, question.getChoices().get(1)); // Pour le champ choix2
            if (question.getChoices().size() >= 3)
                addQuestionStmt.setString(5, question.getChoices().size() >= 3 ? question.getChoices().get(2) : null);
            addQuestionStmt.setString(6, question.getChoices().size() >= 4 ? question.getChoices().get(3) : null);
            addQuestionStmt.setString(7, question.getResponse()); // Pour le champ response
            addQuestionStmt.setString(8, question.getTheme()); // Pour le champ theme

            // Exécuter la requête
            addQuestionStmt.executeUpdate();

            addQuestionConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Méthode qui ajoute plusieurs questions à la base de données.
     * 
     * @param questions La liste des questions à ajouter.
     */
    public void addQuestions(List<Question> questions) {
        String sql = "INSERT INTO Questions (question, point, choix1, choix2, choix3, choix4, response, theme) VALUES (?,?,?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(db_url);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Question question : questions) {
                pstmt.setString(1, question.getQuestion()); // Pour le champ question
                pstmt.setInt(2, question.getPoints()); // Pour le champ point
                pstmt.setString(3, question.getChoices().get(0)); // Pour le champ choix1
                pstmt.setString(4, question.getChoices().get(1)); // Pour le champ choix2
                pstmt.setString(5, question.getChoices().size() >= 3 ? question.getChoices().get(2) : null);
                pstmt.setString(6, question.getChoices().size() >= 4 ? question.getChoices().get(3) : null);
                pstmt.setString(7, question.getResponse()); // Pour le champ response
                pstmt.setString(8, question.getTheme()); // Pour le champ theme

                pstmt.addBatch(); // Ajoute la requête à la batch pour exécution ultérieure
            }

            pstmt.executeBatch(); // Exécute toutes les requêtes en batch

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Méthode qui ajoute un duel à la base de données.
     * 
     * @param questions La liste des questions du duel.
     * @param userAtq L'identifiant du joueur attaquant.
     * @param userCible L'identifiant du joueur cible.
     * @param scoreAtq Le score de l'attaquant.
     * @param nbJours Le nombre de jours pour le duel.
     */
    public void addQuestionDuel(List<Question> questions, String userAtq, String userCible, int scoreAtq, int nbJours) {
        String sqlInsertions = "INSERT INTO Questions_duel (duel_id, question_id) VALUES (?,?)";
        String sqlDuel = "INSERT INTO duel (duel_id, user_atq, user_cible, temps_limite, score_atq) VALUES (?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(db_url);
                PreparedStatement pstmtQuestions = conn.prepareStatement(sqlInsertions);
                PreparedStatement pstmtDuel = conn.prepareStatement(sqlDuel)) {

            String duelId = UUID.randomUUID().toString();
            while (isDuel(duelId)) {
                duelId = UUID.randomUUID().toString();
            }

            // Insérer les questions dans Questions_duel
            for (Question question : questions) {
                pstmtQuestions.setString(1, duelId);
                pstmtQuestions.setInt(2, question.getId());
                pstmtQuestions.addBatch();
            }
            pstmtQuestions.executeBatch();

            // Enregistrer le duel
            pstmtDuel.setString(1, duelId);
            pstmtDuel.setString(2, userAtq);
            pstmtDuel.setString(3, userCible);
            pstmtDuel.setTimestamp(4, new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000 * nbJours));
            pstmtDuel.setInt(5, scoreAtq);
            pstmtDuel.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Méthode qui ajoute une demande de question à la base de données.
     * 
     * @param question La question à ajouter.
     * @param userId L'identifiant de l'utilisateur.
     */
    public void addQuestionRequest(Question question, String userId) {
        String sql = "INSERT INTO Questions_request (user_id, question, point, choix1, choix2, choix3, choix4, response, theme) VALUES (?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(db_url);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Définir les paramètres de la requête
            pstmt.setString(1, userId); // Pour le champ user_id
            pstmt.setString(2, question.getQuestion()); // Pour le champ question
            pstmt.setInt(3, question.getPoints()); // Pour le champ point
            pstmt.setString(4, question.getChoices().get(0)); // Pour le champ choix1
            pstmt.setString(5, question.getChoices().get(1)); // Pour le champ choix2
            pstmt.setString(6, question.getChoices().size() >= 3 ? question.getChoices().get(2) : null);
            pstmt.setString(7, question.getChoices().size() >= 4 ? question.getChoices().get(3) : null);
            pstmt.setString(8, question.getResponse()); // Pour le champ response
            pstmt.setString(9, question.getTheme()); // Pour le champ theme

            // Exécuter la requête
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Méthode qui vérifie si une question existe dans la base de données.
     * 
     * @param question La question à vérifier.
     * @return True si la question existe, false sinon.
     */
    public boolean isQuestion(String question) {
        try {
            Connection isQuestion_conn = DriverManager.getConnection(db_url);
            Statement isQuestion_stmt = isQuestion_conn.createStatement();

            ResultSet resp = isQuestion_stmt
                    .executeQuery(String.format("SELECT question FROM Questions WHERE question = '%s'", question));

            if (resp.next()) {
                isQuestion_conn.close();
                return true;
            }
            isQuestion_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    /**
     * Méthode qui supprime une question de la base de données.
     * 
     * @param question La question à supprimer.
     */
    public void removeQuestion(String question) {
        try {
            Connection removeQuestion_conn = DriverManager.getConnection(db_url);
            Statement removeQuestion_stmt = removeQuestion_conn.createStatement();

            removeQuestion_stmt.executeUpdate(String
                    .format("DELETE FROM Questions WHERE question = '%s'", question));

            removeQuestion_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Méthode qui supprime un duel et toutes les questions associées de la base de données.
     * 
     * @param duel_id L'identifiant du duel à supprimer.
     */
    public void removeQuestionDuel(String duel_id) {
        try {
            Connection removeQuestionDuel_conn = DriverManager.getConnection(db_url);
            Statement removeQuestionDuel_stmt = removeQuestionDuel_conn.createStatement();

            removeQuestionDuel_stmt.executeUpdate(String
                    .format("DELETE FROM duel WHERE duel_id = '%s'",
                            duel_id));

            removeQuestionDuel_stmt
                    .executeUpdate(String.format("DELETE FROM Questions_duel WHERE duel_id = '%s'", duel_id));

            removeQuestionDuel_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Méthode qui vérifie si un utilisateur est administrateur.
     * 
     * @param name Le nom de l'utilisateur.
     * @return True si l'utilisateur est administrateur, false sinon.
     */
    public boolean isAdmin(String name) {
        try {
            Connection isAdmin_conn = DriverManager.getConnection(db_url);
            Statement isAdmin_stmt = isAdmin_conn.createStatement();

            ResultSet resp = isAdmin_stmt.executeQuery(String
                    .format("SELECT * FROM Joueurs j JOIN admin a ON j.user_id = a.user_id WHERE name = '%s'", name));

            if (resp.next()) {
                isAdmin_conn.close();
                return true;// Return true if the user was found
            }
            isAdmin_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    /**
     * Méthode qui ajoute un administrateur à la base de données.
     * 
     * @param name Le nom de l'administrateur à ajouter.
     */
    public void addAdmin(String name) {
        try {
            Connection add_admin_conn = DriverManager.getConnection(db_url);
            Statement add_admin_stmt = add_admin_conn.createStatement();

            Avatar user_Avatar = getUserByName(name);

            add_admin_stmt.executeUpdate(String.format("INSERT INTO admin VALUES ('%s')", user_Avatar.getId()));

            add_admin_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Méthode qui supprime un administrateur de la base de données.
     * 
     * @param name Le nom de l'administrateur à supprimer.
     */
    public void removeAdmin(String name) {
        try {
            Connection add_admin_conn = DriverManager.getConnection(db_url);
            Statement add_admin_stmt = add_admin_conn.createStatement();

            add_admin_stmt.executeUpdate(String
                    .format("DELETE FROM admin WHERE user_id = (SELECT user_id FROM Joueurs WHERE name = '%s')", name));

            add_admin_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Méthode qui vérifie si un thème existe dans la base de données.
     * 
     * @param theme Le thème à vérifier.
     * @return True si le thème existe, false sinon.
     */
    public boolean isTheme(String theme) {
        try {
            Connection isTheme_conn = DriverManager.getConnection(db_url);
            Statement isTheme_stmt = isTheme_conn.createStatement();

            ResultSet resp = isTheme_stmt
                    .executeQuery(String.format("SELECT DISTINCT theme FROM Questions WHERE theme = '%s'", theme));

            if (resp.next()) {
                isTheme_conn.close();
                return true;
            }
            isTheme_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    /**
     * Méthode qui renvoie la liste des thèmes utilisés dans la base de données.
     * 
     * @return Une chaîne de caractères contenant la liste des thèmes.
     */
    public String getThemes() {
        List<String> list = new ArrayList<String>();
        String res = "";
        try {
            Connection getTheme_conn = DriverManager.getConnection(db_url);
            Statement getTheme_stmt = getTheme_conn.createStatement();

            ResultSet resp = getTheme_stmt.executeQuery("SELECT DISTINCT theme FROM Questions");

            while (resp.next()) {
                list.add(resp.getString("theme"));
            }
            getTheme_conn.close();

            for (String text : list)
                res += "- " + text + "\n";
            return res;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }
    /**
     * Méthode qui renvoie les statistiques d'un utilisateur.
     * 
     * @param user_id L'identifiant de l'utilisateur.
     * @return Un résultat de requête contenant les statistiques de l'utilisateur.
     */
    public ResultSet getStat(String user_id) {
        try {
            Connection getStat_conn = DriverManager.getConnection(db_url);
            Statement getStat_stmt = getStat_conn.createStatement();

            ResultSet resp = getStat_stmt
                    .executeQuery(String.format("SELECT * FROM Stats WHERE user_id = '%s'", user_id));

            if (resp.next()) {
                return resp;// Return true if the user was found
            }
            getStat_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    /**
     * Méthode qui met à jour les statistiques d'un utilisateur.
     * 
     * @param user_id L'identifiant de l'utilisateur.
     * @param questions Le nombre de questions.
     * @param question_juste Le nombre de questions justes.
     * @param type_defi Le type de défis.
     * @param pt_gagne Le nombre de points gagnés.
     * @param pt_perdu Le nombre de points perdus.
     */
    public void updateStats(String user_id, int questions, int question_juste, String type_defi, int pt_gagne,
            int pt_perdu) {

        try {
            Connection updateStats_conn = DriverManager.getConnection(db_url);
            Statement updateStats_stmt = updateStats_conn.createStatement();

            String stats = "";

            switch (type_defi) {
                case "solo":
                    stats = String.format(
                            "tot_question_defi_solo = tot_question_defi_solo + %d, jus_question_defi_solo = jus_question_defi_solo + %d, pt_gagne_defi_solo = pt_gagne_defi_solo + %d, pt_perdu_defi_solo = pt_perdu_defi_solo + %d, defi_solo = defi_solo + 1",
                            questions, question_juste, pt_gagne, pt_perdu);
                    break;

                case "vs":
                    stats = String.format(
                            "tot_question_defi_vs = tot_question_defi_vs + %d, jus_question_defi_vs = jus_question_defi_vs + %d, pt_gagne_defi_vs = pt_gagne_defi_vs + %d, pt_perdu_defi_vs = pt_perdu_defi_vs + %d, defi_vs = defi_vs + 1",
                            questions, question_juste, pt_gagne, pt_perdu);
                    break;

                default:
                    break;
            }

            updateStats_stmt.executeUpdate(String.format("UPDATE Stats SET %s WHERE user_id = '%s'", stats, user_id));

            updateStats_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    /**
     * Méthode qui met à jour les statistiques d'un utilisateur en cas d'oubli.
     * 
     * @param user_atq L'identifiant de l'utilisateur attaquant.
     * @param user_cible L'identifiant de l'utilisateur cible.
     * @param duel_id L'identifiant du duel.
     */
    public void updateStatsOubli(String user_atq, String user_cible, String duel_id) {

        try {
            Connection updateStatsOubli_conn = DriverManager.getConnection(db_url);
            Statement updateStatsOubli_stmt = updateStatsOubli_conn.createStatement();

            updateStatsOubli_stmt.executeUpdate(
                    String.format("UPDATE Stats SET oubli_vs = oubli_vs + 1 WHERE user_id = '%s'", user_cible));

            updateStatsOubli_conn.close();

            updateStatsVictoire(user_atq, duel_id, true);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    /**
     * Méthode qui met à jour les statistiques d'un utilisateur en cas de victoire.
     * 
     * @param user_atq L'identifiant de l'utilisateur attaquant.
     * @param duel_id L'identifiant du duel.
     * @param delete True si le duel doit être supprimé.
     */
    public void updateStatsVictoire(String user_atq, String duel_id, boolean delete) {

        try {
            Connection updateStatsVictoire_conn = DriverManager.getConnection(db_url);
            Statement updateStatsVictoire_stmt = updateStatsVictoire_conn.createStatement();

            updateStatsVictoire_stmt.executeUpdate(
                    String.format(
                            "UPDATE Stats SET victoire_vs = victoire_vs + 1, match_vs = match_vs + 1 WHERE user_id = '%s'",
                            user_atq));

            updateStatsVictoire_conn.close();

            if (delete)
                removeQuestionDuel(duel_id);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    /**
     * Met à jour les statistiques de défaites d'un utilisateur.
     * 
     * @param user_atq L'identifiant de l'utilisateur.
     * @param duel_id L'identifiant du duel.
     * @param delete Si vrai, supprime la question du duel.
     */
    public void updateStatsDefaite(String user_atq, String duel_id, boolean delete) {

        try {
            Connection updateStatsDefaite_conn = DriverManager.getConnection(db_url);
            Statement updateStatsDefaite_stmt = updateStatsDefaite_conn.createStatement();

            updateStatsDefaite_stmt.executeUpdate(
                    String.format(
                            "UPDATE Stats SET match_vs = match_vs + 1 WHERE user_id = '%s'",
                            user_atq));

            updateStatsDefaite_conn.close();

            if (delete)
                removeQuestionDuel(duel_id);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    /**
     * Met à jour le vainqueur d'un duel.
     * 
     * @param duel_id L'identifiant du duel.
     * @param vainqueur Le vainqueur du duel.
     */
    public void updateDuelVainqueur(String duel_id, String vainqueur) {

        try {
            Connection updateDuelVainqueur_conn = DriverManager.getConnection(db_url);
            Statement updateDuelVainqueur_stmt = updateDuelVainqueur_conn.createStatement();

            updateDuelVainqueur_stmt.executeUpdate(
                    String.format(
                            "UPDATE duel SET vainqueur = '%s' WHERE duel_id = '%s'",
                            vainqueur, duel_id));

            updateDuelVainqueur_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    /**
     * Vérifie si un duel existe.
     * 
     * @param duel_id L'identifiant du duel.
     * 
     * @return True si le duel existe, false sinon.
     */
    public boolean isDuel(String duel_id) {
        try {
            Connection isDuel_conn = DriverManager.getConnection(db_url);
            Statement isDuel_stmt = isDuel_conn.createStatement();

            ResultSet resp = isDuel_stmt
                    .executeQuery(String.format("SELECT * FROM duel WHERE duel_id = '%s'", duel_id));

            if (resp.next()) {
                isDuel_conn.close();
                return true;
            }
            isDuel_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
