package org.example;

import com.google.gson.Gson;
import one.jfr.Arguments;
import parser.FrameTree;
import parser.JFREventType;
import parser.JFRParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class Main {
    private static final Gson GSON = new Gson();

    public static void main(String[] args) throws IOException {
        Arguments arguments = new Arguments();
        Map<JFREventType, FrameTree> map = JFRParser.dumpTree("/home/zhengziyi/IdeaProjects/spring-demo/test.jfr", arguments);
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