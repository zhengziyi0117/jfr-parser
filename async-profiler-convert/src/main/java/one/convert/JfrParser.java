package one.convert;

import one.jfr.JfrReader;

import java.io.IOException;

public class JfrParser {

    public static FrameTree dumpTree(String fileName, Arguments args) throws IOException {
        try (JfrReader jfr = new JfrReader(fileName)) {
            JfrToFlame converter = new JfrToFlame(jfr, args);
            converter.convert();
            return converter.dumpTree();
        }
    }

}
