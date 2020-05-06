package app.models;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

/**
 * Library can load JSON file into object.
 */
public abstract class JSONLoader {

    /**
     * Load file into JSONObject
     * @param filePath
     * @return
     * @throws Exception
     */
    public static JSONObject load(String filePath) throws Exception {
        return parseFile(openFile(filePath));
    }

    /**
     * Parser FileReader as JSON
     * @param fileReader
     * @return
     * @throws Exception
     */
    private static JSONObject parseFile(FileReader fileReader) throws Exception {
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(fileReader);
    }

    /**
     * Open file and create reader
     * @param filePath
     * @return
     * @throws Exception
     */
    private static FileReader openFile(String filePath) throws Exception {

        File file1 = new File(filePath);
        FileReader reader = null;
        try {
            reader = new FileReader(file1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new Exception("File not found.");
        }
        return reader;
    }

}
