package pandorum.features;

import arc.func.Cons;
import arc.util.Http;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static pandorum.PluginVars.gson;
import static pandorum.PluginVars.translatorLocales;

public class Translator {

    public static void translate(String text, String to, Cons<String> cons) {
        JsonObject json = new JsonObject();
        json.addProperty("to", to);
        json.addProperty("text", text);
        json.addProperty("enableTransliteration", true);

        Http.post("https://api-b2b.backenster.com/b1/api/v3/translate")
                .header("authorization", "Bearer a_25rccaCYcBC9ARqMODx2BV2M0wNZgDCEl3jryYSgYZtF1a702PVi4sxqi2AmZWyCcw4x209VXnCYwesx")
                .header("content-type", "application/json")
                .content(json.toString())
                .error(e -> cons.get(""))
                .submit(response -> cons.get(gson.fromJson(response.getResultAsString(), JsonObject.class).get("result").getAsString()));
    }

    public static void loadLanguages() {
        Http.get("https://api-b2b.backenster.com/b1/api/v3/getLanguages")
                .header("authorization", "Bearer a_25rccaCYcBC9ARqMODx2BV2M0wNZgDCEl3jryYSgYZtF1a702PVi4sxqi2AmZWyCcw4x209VXnCYwesx")
                .header("content-type", "application/json")
                .submit(response -> {
                    JsonArray languages = gson.fromJson(response.getResultAsString(), JsonObject.class).get("result").getAsJsonArray();
                    for (JsonElement element : languages) {
                        JsonObject language = element.getAsJsonObject();
                        translatorLocales.put(language.get("name").getAsString(), language.get("englishName").getAsString());
                    }
                });
    }
}
