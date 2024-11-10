
import com.google.gson.Gson;
import one.convert.Arguments;
import one.convert.FrameTree;
import one.convert.JfrParser;
import one.jfr.event.JfrEventType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class Main {
    private static final Gson GSON = new Gson();

    public static void main(String[] args) throws IOException {
        Arguments arguments = new Arguments();
        arguments.state = "runnable,default,sleeping";
        Map<JfrEventType, FrameTree> map = JfrParser.dumpTree("/Users/ziyizheng/IdeaProjects/sb-demo/test.jfr", arguments);
        map.forEach((eventType, tree) -> {
            try (FileOutputStream fileOutputStream = new FileOutputStream(eventType.name() + ".json");) {
                String json = GSON.toJson(tree);
                fileOutputStream.write(json.getBytes());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}