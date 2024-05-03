import java.sql.*;


public class DBProcess extends Jeu {


    public void inscrire(Avatar avatar) { 
        

        if (!findUserById(avatar.getName())) {
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
            gestion.wait(2000);
        }     
    }

    public boolean findUserById(String user_id) {
        try {
            Connection findUserById_conn = DriverManager.getConnection(db_url);
            Statement findUserById_stmt = findUserById_conn.createStatement();

            if (findUserById_stmt.executeQuery(String.format("SELECT name FROM avatar WHERE user_id = '%s'", user_id)).next()) {
                return true; // Return true if the user was found
            } else {
                return false; // Return false if the user was not found
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
