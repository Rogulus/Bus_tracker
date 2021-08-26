package model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

/**
 * Třída, která má na starosti načtení dat ze souboru.
 * @author Marek Šťastný
 */

public class LinesLoader {

    /**
     * Metoda, která načte linky ze souboru
     * @param filePath cesta k souboru
     * @param parentMap mapa
     * @param dpmb dpmb
     */

     void loadLines(String filePath, MyMap parentMap, DPMB dpmb)
    {
        JSONParser jsonParser = new JSONParser();

        try {
//            InputStream in = getClass().getResourceAsStream(filePath);
//            JSONArray lines = (JSONArray)jsonParser.parse(
//                    new InputStreamReader(in, "UTF-8"));

            FileReader reader = new FileReader(filePath);
            Object obj = jsonParser.parse(reader);

            JSONArray lines = (JSONArray) obj;

            for (Object line: lines) {
                JSONObject l = (JSONObject) line;
                String name = (String) l.get("name");
                String color = (String) l.get("color");

                Line newLine = new Line(name, color, parentMap, dpmb);
                dpmb.addLine(newLine);

                JSONArray lineStreets = (JSONArray) l.get("streets");
                for(Object streetName:  lineStreets){
                    String stringName = (String)streetName;
                    newLine.addStreet(stringName);
                }

                JSONArray lineStops = (JSONArray) l.get("stops");
                for(Object stopName:  lineStops){
                    String stringName = (String)stopName;
                    newLine.addStop(stringName);
                }

                JSONArray lineConnections = (JSONArray) l.get("connections");
                for(Object connectionStart: lineConnections){
                    String stringStart = (String)connectionStart;
                    LocalTime time = LocalTime.parse(stringStart, DateTimeFormatter.ofPattern("HH:mm"));
                    int hour = time.get(ChronoField.CLOCK_HOUR_OF_DAY);
                    if(hour == 24){hour = 0;}
                    int minute = time.get(ChronoField.MINUTE_OF_HOUR);
                    newLine.addConnection(hour, minute);
                }

               //System.out.println(newLine);
            }
        }
        catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }
}
