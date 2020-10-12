package edu.cmu.ds.project4;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/******************************************************************************
 * Model of Web Server.
 * Communicate with MongoDB Atlas to insert, retrieve and analyze log.
 * Use Singleton Pattern to ensure only one MongoDB connection at a time
 *
 * @author Qiuchen Z.
 * @date 4/3/20
 ******************************************************************************/
public class MongoConn {
    private MongoCollection collection;
    private JSONArray allLogs;
    private int totalLatency;
    private static MongoConn singleInstance;

    /**
     * Private constructor
     */
    private MongoConn() {
        // connect to mongodb
        MongoClientURI uri = new MongoClientURI("");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("ds");
        collection = database.getCollection("nasa_log");
        // disable logger
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.OFF);
        allLogs = new JSONArray();
    }

    /**
     * Get the singleton object
     *
     * @return singleton NASAModel object
     */
    public static MongoConn getInstance() {
        if (singleInstance == null) {
            singleInstance = new MongoConn();
        }
        return singleInstance;
    }

    /**
     * Insert a log
     *
     * @param logJson log to insert in json format
     */
    public void insertLog(JSONObject logJson) {
        Document document = Document.parse(logJson.toString());
        collection.insertOne(document);
        System.out.printf("Write to Document %s: %s\n", collection.getNamespace(), logJson.toString());
    }

    /**
     * Rerieve all logs from mongodb server
     */
    public void retrieveLog() {
        totalLatency = 0;
        // sort by timestamp in descending order, and drop the _id
        BasicDBObject sort = new BasicDBObject("timestamp", -1);
        BasicDBObject projection = new BasicDBObject("_id", 0);

        // go through all logs
        FindIterable<Document> cursor = collection.find().sort(sort).projection(projection);
        try (MongoCursor<Document> iter = cursor.iterator()) {
            allLogs = new JSONArray();
            while (iter.hasNext()) {
                // deal with timestamp long type issue
                Document doc = iter.next();
                JSONObject j = new JSONObject(doc.toJson());
                Date date = new Date(doc.getLong("timestamp"));
                j.put("time", date.toString());
                totalLatency += j.getInt("latency");
                allLogs.put(j);
            }
        }
    }

    /**
     * Get all logs. Should be called after retriveLog()
     *
     * @return all logs stored in mongoDB
     */
    public JSONArray getLogs() {
        return allLogs;
    }

    /**
     * Count the frequency for each value of a certain key in log
     * and sort the result
     *
     * @param fieldKey log key to count
     * @return a sorted list of value and counts in descending order of counts
     */
    public List<List<String>> filedCount(String fieldKey) {
        Map<String, Integer> countMap = new HashMap<>();

        // count frequency
        for (Object logObj : allLogs) {
            JSONObject log = (JSONObject) logObj;
            if (log.has(fieldKey)) {
                String value = log.getString(fieldKey);
                countMap.put(value, countMap.getOrDefault(value, 0) + 1);
            }
        }

        // add to list sort the result
        List<List<String>> ret = new ArrayList<>();
        for (String fieldValue : countMap.keySet()) {
            List<String> l = new ArrayList<>();
            l.add(fieldValue);
            l.add("" + countMap.get(fieldValue));
            ret.add(l);
        }
        ret.sort((r1, r2) -> Integer.parseInt(r2.get(1)) - Integer.parseInt(r1.get(1)));
        return ret;
    }

    /**
     * @return average latency
     */
    public int averageLatency(){
        return allLogs.length() == 0? 0: totalLatency / allLogs.length();
    }

    /**
     * Test driver
     *
     * @param args not used
     */
    public static void main(String[] args) {
        MongoConn mongoDBConn = MongoConn.getInstance();
        mongoDBConn.retrieveLog();
        mongoDBConn.getLogs();
    }
}
