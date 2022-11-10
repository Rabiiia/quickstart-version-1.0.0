package externalAPIHandling;

import com.google.gson.Gson;
import utils.HttpUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JokeFetcher {
    Gson gson = new Gson();
    ExecutorService executableService = Executors.newFixedThreadPool(2);
    String url = "https://api.chucknorris.io/jokes/random";

    public JokeFetcher() {

    }



    public <T> Future<T> getFutureJoke(String url, T typeOfDTO){
        return executableService.submit( () -> {
            try {
                return gson.fromJson(HttpUtils.fetchData(url), (Type) typeOfDTO);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
