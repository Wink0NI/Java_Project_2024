import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class DBProcess {
    
    private String db_url = "jdbc:sqlite:src/db/db_jeu.db";
    private String nom_admin = "admin";


    public void inscrire(Avatar avatar) { 
        

        if (!isUser(avatar.getName())) {
            //String url = "jdbc:sqlite:BDD/BDD_MonstreDuSavoir.db";
            try {
                Connection inscription_conn = DriverManager.getConnection(db_url);
                Statement inscription_stmt = inscription_conn.createStatement();

                String requete_sql = String.format("INSERT INTO Joueurs (user_id, name, mdp, pv) VALUES ('%s','%s', '%s', 0)", avatar.getId(), avatar.getName(), avatar.getMdp(), avatar.getPV());
                inscription_stmt.executeUpdate(requete_sql);
                inscription_conn.close();
            } 
            catch (SQLException e) {
                e.printStackTrace();
                
            }
        }
        else {
            System.out.println("WARNING: Ce nom d'utilisateur existe déjà !");
        }     
    }

    public boolean connecter(String username, String password) {
        if (isUser(username)) {
            try {
                Connection connexion_conn = DriverManager.getConnection(db_url);
                Statement connexion_stmt = connexion_conn.createStatement();

                ResultSet users = connexion_stmt.executeQuery(String.format("SELECT name, mdp FROM Joueurs WHERE name = '%s'", username));

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
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Avatar getUser(String user_id) {
        try {
            Connection getUser_conn = DriverManager.getConnection(db_url);
            Statement getUser_stmt = getUser_conn.createStatement();

            ResultSet resp = getUser_stmt.executeQuery(String.format("SELECT * FROM Joueurs WHERE name = '%s'", user_id));

            if (resp.next()) {
                Avatar avatar = new Avatar(resp.getString("user_id"), resp.getString("name"), resp.getString("mdp"), resp.getInt("pv"));
                getUser_conn.close();
                return  avatar;// Return true if the user was found
            }
            getUser_conn.close();
            
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return new Avatar();
    }

    

    public int get_nb_questions() {
        int nb_questions = 0;
        try {
            Connection get_nb_questions_conn = DriverManager.getConnection(db_url);
            Statement get_nb_questions_stmt = get_nb_questions_conn.createStatement();
            ResultSet resultat = get_nb_questions_stmt.executeQuery("SELECT COUNT(*) FROM Questions");

            if (resultat.next()) {
                nb_questions = resultat.getInt("COUNT(*)");
            }
            get_nb_questions_conn.close();
            return nb_questions;
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return nb_questions;
    }

    public List<Question> generate_question(int nb_questions_totales) {
        List<Question> questions = new ArrayList<Question>();
  
        try {
            Connection generate_question_conn = DriverManager.getConnection(db_url);
            Statement generate_question_stmt = generate_question_conn.createStatement();
            ResultSet resultat = generate_question_stmt.executeQuery(String.format("SELECT * FROM Questions ORDER BY RANDOM() LIMIT %d;", nb_questions_totales));

            while (resultat.next()) {
                
                int id = resultat.getInt("question_id");
                String texte = resultat.getString("question");
                int val = resultat.getInt("point");
                List<String> choix = new ArrayList<String>();
                choix.add(resultat.getString("choix1"));
                choix.add(resultat.getString("choix2"));
                choix.add(resultat.getString("choix3"));
                choix.add(resultat.getString("choix4"));
                String reponse = resultat.getString("response");

                

                questions.add(new Question(id, texte, choix, reponse, val));
            }
            generate_question_conn.close();
            return questions;
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public void updatePV(String name, int value) {

        try {
            Connection maj_pv_conn = DriverManager.getConnection(db_url);
            Statement maj_pv_stmt = maj_pv_conn.createStatement();
  
            maj_pv_stmt.execute(String.format("UPDATE Joueurs SET pv = %s WHERE name='%s'", value, name));

            maj_pv_conn.close();


        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isAdmin(String name) {
        try {
            Connection isAdmin_conn = DriverManager.getConnection(db_url);
            Statement isAdmin_stmt = isAdmin_conn.createStatement();

            ResultSet resp = isAdmin_stmt.executeQuery(String.format("SELECT * FROM Joueurs j JOIN admin a ON j.user_id = a.user_id WHERE name = '%s'", name));

            if (resp.next()) {
                isAdmin_conn.close();
                return  true;// Return true if the user was found
            }
            isAdmin_conn.close();
            
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }


    public void addAdmin(String name) {
        try {
            Connection add_admin_conn = DriverManager.getConnection(db_url);
            Statement add_admin_stmt = add_admin_conn.createStatement();
            
            Avatar user_Avatar = getUser(name);


            add_admin_stmt.executeUpdate(String.format("INSERT INTO admin VALUES ('%s')", user_Avatar.getId()));

            add_admin_conn.close();


        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeAdmin(String name) {
        try {
            Connection add_admin_conn = DriverManager.getConnection(db_url);
            Statement add_admin_stmt = add_admin_conn.createStatement();


            add_admin_stmt.executeUpdate(String.format("DELETE FROM admin WHERE user_id = (SELECT user_id FROM Joueurs WHERE name = '%s')", name));

            add_admin_conn.close();


        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
