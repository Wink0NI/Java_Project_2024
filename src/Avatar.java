import java.util.UUID;

public class Avatar {
    private String id;
    private String name;
    private String mdp;
    private int pv;

    public Avatar(String name, String mdp ) {
        this.id  = UUID.randomUUID().toString();
        this.name = name;
        this.mdp = mdp;
        this.pv = 0;
    }

    public Avatar(String id, String name, String mdp, int pv) {
        this.id  = id;
        this.name = name;
        this.mdp = mdp;
        this.pv = pv;
    }

    public Avatar() {}

    public String getId() {
        return id;
    }

    public int getPV() {
        return pv;
    }

    public String getName() {
        return name;
    }

    public String getMdp() {
        return mdp;
    }
}