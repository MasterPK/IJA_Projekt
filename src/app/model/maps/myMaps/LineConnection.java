package app.model.maps.myMaps;

import java.util.Date;

public class LineConnection {
    private int id;
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;

    }

    public LineConnection(int id, Date date)
    {
        this.id=id;
        this.date=date;
    }


}
