package Persistance;

import Business.Entities.Config;
import com.google.gson.Gson;
import java.io.FileReader;

public class ConfigDAO {
    private static final String FILE_PATH = "config.json";

    public Config getConfig() throws Exception {
        Gson gson = new Gson();
        return gson.fromJson(new FileReader(FILE_PATH), Config.class);
    }
}