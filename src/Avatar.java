import java.util.UUID;

/**
 * La classe Avatar représente un avatar dans un système.
 */
public class Avatar {
    private String id; // Identifiant unique de l'avatar
    private String name; // Nom de l'avatar
    private String mdp; // Mot de passe de l'avatar
    private int pv; // Points de vie de l'avatar
    private DBProcess dbProcess = new DBProcess();

    /**
     * Constructeur pour créer un nouvel avatar avec un nom et un mot de passe.
     * @param name Le nom de l'avatar.
     * @param mdp Le mot de passe de l'avatar.
     */
    public Avatar(String name, String mdp ) {
        this.id  = UUID.randomUUID().toString(); // Génère un identifiant unique pour l'avatar.
        while (dbProcess.isUser(this.id)) this.id = UUID.randomUUID().toString();
        
            
        this.name = name; // Initialise le nom de l'avatar avec la valeur passée en paramètre.
        this.mdp = mdp; // Initialise le mot de passe de l'avatar avec la valeur passée en paramètre.
        this.pv = 0; // Initialise les points de vie de l'avatar à 0.
    }

    /**
     * Constructeur pour créer un nouvel avatar avec tous les attributs spécifiés.
     * @param id L'identifiant unique de l'avatar.
     * @param name Le nom de l'avatar.
     * @param mdp Le mot de passe de l'avatar.
     * @param pv Les points de vie de l'avatar.
     */
    public Avatar(String id, String name, String mdp, int pv) {
        this.id  = id; // Initialise l'identifiant de l'avatar avec la valeur passée en paramètre.
        this.name = name; // Initialise le nom de l'avatar avec la valeur passée en paramètre.
        this.mdp = mdp; // Initialise le mot de passe de l'avatar avec la valeur passée en paramètre.
        this.pv = pv; // Initialise les points de vie de l'avatar avec la valeur passée en paramètre.
    }

    /**
     * Constructeur par défaut pour créer un nouvel avatar avec des valeurs par défaut.
     */
    public Avatar() {}

    /**
     * Obtient l'identifiant unique de l'avatar.
     * @return L'identifiant unique de l'avatar.
     */
    public String getId() {
        return id;
    }

    /**
     * Obtient les points de vie de l'avatar.
     * @return Les points de vie de l'avatar.
     */
    public int getPV() {
        return pv;
    }

    /**
     * Obtient le nom de l'avatar.
     * @return Le nom de l'avatar.
     */
    public String getName() {
        return name;
    }

    /**
     * Obtient le mot de passe de l'avatar.
     * @return Le mot de passe de l'avatar.
     */
    public String getMdp() {
        return mdp;
    }
}
