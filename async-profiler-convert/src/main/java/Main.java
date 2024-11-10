import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import one.convert.Arguments;
import one.convert.FrameTree;
import one.convert.JfrParser;
import one.jfr.event.JfrEventType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;

public class Main {
    public static final Gson GSON = new Gson();
    public static void main(String[] args) throws IOException {

        Arguments arguments = new Arguments();
        arguments.state = "default,runnable,sleeping";
        arguments.lines = true;
        try (InputStream inputStream = Files.newInputStream(Path.of("/home/zhengziyi/IdeaProjects/spring-demo/all.jfr"))){
            byte[] bytes = inputStream.readAllBytes();
            Map<JfrEventType, FrameTree> map = JfrParser.dumpTree(bytes, arguments);
            map.forEach((k, v) -> {
                String json = GSON.toJson(v);
                try {
                    Path path = Files.createFile(Path.of(k.name()+".json"));
                    Files.write(path, json.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
