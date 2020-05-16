package app.models;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Library can load JSON file into object.
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
 */
public abstract class JSONLoader {

    /**
     * Load file into JSONObject
     * @param filePath Path to file.
     * @return Object with JSON data.
     * @throws Exception when some error.
     */
    public static JSONObject load(String filePath) throws Exception {
        return parseFile(openFile(filePath));
    }

    /**
     * Parser FileReader as JSON
     * @param fileReader File reader to parse
     * @return Final JSON object.
     * @throws Exception when bad file format.
     */
    private static JSONObject parseFile(FileReader fileReader) throws Exception {
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(fileReader);
    }

    /**
     * Open file and create reader
     * @param filePath File path.
     * @return File reader.
     * @throws Exception when I/O errors.
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
