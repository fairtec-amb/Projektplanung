package projektplanung.Persistence.Entities;

import projektplanung.DateHelper;

import java.util.Date;

public class Projekt {

    private int ID;
    private String bezeichnung;
    private Date startDatum;
    private Date endDatum;
    private int Stunden;

    public Projekt(int id, String bez, Date sdat, Date edat, int std) {
        this.ID = id;
        this.bezeichnung = bez;
        this.startDatum = sdat;
        this.endDatum = edat;
        this.Stunden = std;

    }

    public Projekt(String bez, Date sdat, Date edat, int std) {
        this.bezeichnung = bez;
        this.startDatum = sdat;
        this.endDatum = edat;
        this.Stunden = std;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getStartDatum() {

        return DateHelper.dateToString(startDatum);
    }

    public String getEndDatum() {

        return DateHelper.dateToString(endDatum);
    }

    public int getStunden() {
        return Stunden;
    }

    public void setStunden(int stunden) {
        Stunden = stunden;
    }
}
