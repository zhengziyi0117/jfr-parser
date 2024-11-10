package one.convert;

import one.jfr.JfrReader;
import one.jfr.event.JfrEventType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public class JfrParser {

    public static Map<JfrEventType, FrameTree> dumpTree(String fileName, Arguments args) throws IOException {
        try (JfrReader jfr = new JfrReader(fileName)) {
            JfrToFrameTree converter = new JfrToFrameTree(jfr, args);
            converter.convert();
            return converter.getFrameTreeMap();
//            JfrToFlame converter = new JfrToFlame(jfr, args);
//            converter.convert();
//            converter.dump(null);
//            return null;
        }
    }

    public static Map<JfrEventType, FrameTree> dumpTree(byte[] bytes, Arguments args) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        try (JfrReader jfr = new JfrReader(buf)) {
            JfrToFrameTree converter = new JfrToFrameTree(jfr, args);
            converter.convert();
            return converter.getFrameTreeMap();
//            JfrToFlame converter = new JfrToFlame(jfr, args);
//            converter.convert();
//            converter.dump(null);
//            return null;
        }
    }

}
