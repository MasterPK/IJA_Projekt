package app.model.maps.myMaps;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LineConnection {
    private int id;
    private List<Date> timetable = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;

    }

    public LineConnection(int id)
    {
        this.id=id;
    }

    public void addStopTime(Date time){
        timetable.add(time);
    }

    public List<Date> getTimetable()
    {
        return timetable;
    }


}
