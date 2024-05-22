import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.HashMap;

public class DBProcess {

    private String db_url = "jdbc:sqlite:src/db/db_jeu.db";
    private String nom_admin = "admin";

    public void inscrire(Avatar avatar) {
        if (!isUser(avatar.getName())) {
            // String url = "jdbc:sqlite:BDD/BDD_MonstreDuSavoir.db";
            try {
                Connection inscription_conn = DriverManager.getConnection(db_url);
                Statement inscription_stmt = inscription_conn.createStatement();

         
                inscription_stmt.executeUpdate(String.format(
                    "INSERT INTO Joueurs (user_id, name, mdp, pv) VALUES ('%s','%s', '%s', 0)",
                    avatar.getId(),
                    avatar.getName(),
                    avatar.getMdp(),
                    avatar.getPV()));

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

    public boolean connecter(String username, String password) {
        if (isUser(username)) {
            try {
                Connection connexion_conn = DriverManager.getConnection(db_url);
                Statement connexion_stmt = connexion_conn.createStatement();

                ResultSet users = connexion_stmt
                        .executeQuery(String.format("SELECT name, mdp FROM Joueurs WHERE name = '%s'", username));

                while (users.next()) {
                    if (users.getString("name").equals(username) && users.getString("mdp").equals(password)) {
                        connexion_conn.close();
                        return true;
                    }

                }
                connexion_conn.close();
                System.out.println("Identifiant incorrect !");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("WARNING: Ce nom d'utilisateur n'existe pas !");
        }
        return false;
    }

    public boolean isUser(String user) {
        try {
            Connection isUser_conn = DriverManager.getConnection(db_url);
            Statement isUser_stmt = isUser_conn.createStatement();

            if (isUser_stmt.executeQuery(String.format("SELECT name FROM Joueurs WHERE name = '%s'", user)).next()) {
                isUser_conn.close();
                return true; // Return true if the user was found
            }
            isUser_conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Avatar getUserById(String user_id) {
        try {
            Connection getUser_conn = DriverManager.getConnection(db_url);
            Statement getUser_stmt = getUser_conn.createStatement();

            ResultSet resp = getUser_stmt
                    .executeQuery(String.format("SELECT * FROM Joueurs WHERE user_id = '%s'", user_id));

            if (resp.next()) {
                Avatar avatar = new Avatar(resp.getString("user_id"), resp.getString("name"), resp.getString("mdp"),
                        resp.getInt("pv"));
                getUser_conn.close();
                return avatar;// Return true if the user was found
            }
            getUser_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Avatar();
    }

    public Avatar getUserByName(String name) {
        try {
            Connection getUser_conn = DriverManager.getConnection(db_url);
            Statement getUser_stmt = getUser_conn.createStatement();

            ResultSet resp = getUser_stmt
                    .executeQuery(String.format("SELECT * FROM Joueurs WHERE name = '%s'", name));

            if (resp.next()) {
                Avatar avatar = new Avatar(resp.getString("user_id"), resp.getString("name"), resp.getString("mdp"),
                        resp.getInt("pv"));
                getUser_conn.close();
                return avatar;// Return true if the user was found
            }
            getUser_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateUsername(String user_id, String new_username) {

        try {
            Connection updateUsername_conn = DriverManager.getConnection(db_url);
            Statement updateUsername_stmt = updateUsername_conn.createStatement();

            updateUsername_stmt.executeUpdate(
                    String.format("UPDATE Joueurs SET name = '%s' WHERE user_id = '%s'", new_username, user_id));

                    updateUsername_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

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

    public void updateQuestionRequestStatus(int question_id, String status) {

        try {
            Connection updateQuestionRequestStatus_conn = DriverManager.getConnection(db_url);
            Statement updateQuestionRequestStatus_stmt = updateQuestionRequestStatus_conn.createStatement();

            updateQuestionRequestStatus_stmt.executeUpdate(
                    String.format("UPDATE Questions_request SET status = '%s' WHERE question_id = %d", status, question_id));

                    updateQuestionRequestStatus_conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

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
            addQuestionStmt.setString(5, question.getChoices().size() >= 3? question.getChoices().get(2) : null);
            addQuestionStmt.setString(6, question.getChoices().size() >= 4? question.getChoices().get(3) : null);
            addQuestionStmt.setString(7, question.getResponse()); // Pour le champ response
            addQuestionStmt.setString(8, question.getTheme()); // Pour le champ theme
            
            // Exécuter la requête
            addQuestionStmt.executeUpdate();
            
            addQuestionConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public void addQuestions(List<Question> questions) {
        String sql = "INSERT INTO Questions (question, point, choix1, choix2, choix3, choix4, response, theme) VALUES (?,?,?,?,?,?,?,?)";
    
        try (Connection conn = DriverManager.getConnection(db_url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            for (Question question : questions) {
                pstmt.setString(1, question.getQuestion()); // Pour le champ question
                pstmt.setInt(2, question.getPoints()); // Pour le champ point
                pstmt.setString(3, question.getChoices().get(0)); // Pour le champ choix1
                pstmt.setString(4, question.getChoices().get(1)); // Pour le champ choix2
                pstmt.setString(5, question.getChoices().size() >= 3? question.getChoices().get(2) : null);
                pstmt.setString(6, question.getChoices().size() >= 4? question.getChoices().get(3) : null);
                pstmt.setString(7, question.getResponse()); // Pour le champ response
                pstmt.setString(8, question.getTheme()); // Pour le champ theme
    
                pstmt.addBatch(); // Ajoute la requête à la batch pour exécution ultérieure
            }
    
            pstmt.executeBatch(); // Exécute toutes les requêtes en batch
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

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
            pstmt.setString(6, question.getChoices().size() >= 3? question.getChoices().get(2) : null);
            pstmt.setString(7, question.getChoices().size() >= 4? question.getChoices().get(3) : null);
            pstmt.setString(8, question.getResponse()); // Pour le champ response
            pstmt.setString(9, question.getTheme()); // Pour le champ theme
    
            // Exécuter la requête
            pstmt.executeUpdate();
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

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
