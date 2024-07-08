package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        JFRAnalyzer jfrAnalyzer = new JFRAnalyzer("test.jfr");
        JFRTreeNode jfrTreeNode = jfrAnalyzer.parseRecords();
        Gson gson = new Gson();
        System.out.println(gson.toJson(jfrTreeNode));
        Map<String, Integer> signature2index = jfrAnalyzer.getSignature2index();
        System.out.println(gson.toJson(signature2index));
    }
}