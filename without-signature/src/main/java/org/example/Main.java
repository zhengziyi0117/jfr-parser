package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        JFRAnalyzer jfrAnalyzer = new JFRAnalyzer("/Users/bytedance/IdeaProjects/learn/5e29897138cade370c22d279f71b049e-1676454220481.jfr");
        Map<JFREventType, JFRTreeNode> jfrEventTypeJFRTreeNodeMap = jfrAnalyzer.parseRecords();
        jfrEventTypeJFRTreeNodeMap.forEach((k, v) -> {
            String json = gson.toJson(v);
            try (OutputStream outputStream = Files.newOutputStream(Path.of(k.getName()));){
                outputStream.write(json.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Map<String, Integer> signature2index = jfrAnalyzer.getSignature2index();
        System.out.println(gson.toJson(signature2index));
    }
}