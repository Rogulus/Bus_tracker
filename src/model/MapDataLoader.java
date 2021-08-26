package model;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;


import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * Třída, která načte ze souboru data
 * @author Marek Šťastný
 */

 public class MapDataLoader {

    /**
     * Metoda, která načte ze souboru data mapy
     * @param filePath cesta k souboru
     * @param parentMap mapa
     */

     void  loadMapData(String filePath, MyMap parentMap) {

        JSONParser jsonParser = new JSONParser();

        try {
            FileReader reader = new FileReader(filePath);
            Object obj = jsonParser.parse(reader);

            JSONObject jsonObj = (JSONObject) obj;


            JSONObject map = (JSONObject) jsonObj.get("map");
            JSONArray crossroads = (JSONArray) map.get("crossroads");
            JSONArray streets = (JSONArray) map.get("streets");
            JSONArray stops = (JSONArray) map.get("stops");
            int width = Integer.parseInt((String) map.get("width"));
            int hight =  Integer.parseInt((String) map.get("hight"));
            parentMap.setWidth(width);
            parentMap.setHight(hight);


            for (Object crossroad : crossroads) {
                JSONObject c = (JSONObject) crossroad;
                String name = (String) c.get("name");
                int x = Integer.parseInt((String) c.get("x"));
                int y = Integer.parseInt((String) c.get("y"));
                new Crossroad(name, x, y, parentMap);
            }

            //parentMap.printCrossroads();

            for (Object street : streets) {
                JSONObject s = (JSONObject) street;
                String name = (String) s.get("name");
                String startCrossroadName = (String) s.get("start");
                String endCrossroadName = (String) s.get("end");
                new Street(name, startCrossroadName, endCrossroadName, parentMap);
            }

            //parentMap.printStreets();

            for (Object stop : stops) {
                JSONObject s = (JSONObject) stop;
                String stopName = (String) s.get("name");
                String streetName = (String) s.get("street");
                int percentDistance = Integer.parseInt((String) s.get("percentDistance"));
                new BusStop(stopName, streetName, percentDistance, parentMap);
            }

            //parentMap.printStops();
        } catch (NullPointerException | ParseException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
     }
}