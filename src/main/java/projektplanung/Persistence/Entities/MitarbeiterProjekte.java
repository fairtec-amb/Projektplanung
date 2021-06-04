package projektplanung.Persistence.Entities;

import projektplanung.DateHelper;

public class MitarbeiterProjekte extends Projekt {

    private int MP_Id;
    private int MP_geleistet;

    public MitarbeiterProjekte(int id, String bez, String sdat, String edat, int std, int mp_id, int mp_geleistet) {
        super(id, bez, DateHelper.stringToDate(sdat), DateHelper.stringToDate(edat), std);
        this.MP_Id = mp_id;
        this.MP_geleistet = mp_geleistet;
    }

    public int getMP_Id() {
        return MP_Id;
    }

    public void setMP_Id(int MP_Id) {
        this.MP_Id = MP_Id;
    }

    public int getMP_geleistet() {
        return MP_geleistet;
    }

    public void setMP_geleistet(int MP_geleistet) {
        this.MP_geleistet = MP_geleistet;
    }
}
