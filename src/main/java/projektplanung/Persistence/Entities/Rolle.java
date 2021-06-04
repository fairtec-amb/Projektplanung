package projektplanung.Persistence.Entities;

public class Rolle {

    private int R_ID;
    private String rollenbezeichnung;

    public Rolle(int id, String bez)
    {
        this.R_ID = id;
        this.rollenbezeichnung = bez;
    }

    public Rolle(String bez)
    {
        this.rollenbezeichnung = bez;
    }

    public int getR_ID() {
        return R_ID;
    }

    public void setR_ID(int r_ID) {
        R_ID = r_ID;
    }

    public String getRollenbezeichnung() {
        return rollenbezeichnung;
    }

    public void setRollenbezeichnung(String rollenbezeichnung) {
        this.rollenbezeichnung = rollenbezeichnung;
    }
}
