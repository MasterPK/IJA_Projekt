package app.models;

import com.opencsv.CSVReader;
import com.sun.media.sound.InvalidFormatException;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface CSVLoader {
    static List<String[]> load(String filename, String[] firstLine) {
        try {
            Reader reader = new FileReader(filename);
            List<String[]> list = new ArrayList<>();
            CSVReader csvReader = new CSVReader(reader);
            String[] line;
            line = csvReader.readNext();
            if (!Arrays.equals(line, firstLine)) {
                throw new InvalidFormatException();
            }

            while ((line = csvReader.readNext()) != null) {
                if(line.length!=firstLine.length)
                {
                    throw new InvalidFormatException();
                }
                list.add(line);
            }
            reader.close();
            csvReader.close();
            return list;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }

    }
}
