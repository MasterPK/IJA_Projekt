package app.models;


import app.models.maps.Coordinate;
import app.models.maps.MyStop;
import app.models.maps.Stop;
import app.models.maps.Street;
import com.opencsv.CSVReader;
import com.sun.media.sound.InvalidFormatException;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface CSVLoader {
    static List<String[]> load(String filename, String[] firstLine) throws Exception {
        Reader reader = new FileReader(filename);
        List<String[]> list = new ArrayList<>();
        CSVReader csvReader = new CSVReader(reader);
        String[] line;
        line = csvReader.readNext();
        if (!Arrays.equals(line, firstLine)) {
            throw new InvalidFormatException();
        }

        while ((line = csvReader.readNext()) != null) {
            if (line.length != firstLine.length) {
                throw new InvalidFormatException();
            }
            list.add(line);
        }
        reader.close();
        csvReader.close();
        return list;
    }

}
