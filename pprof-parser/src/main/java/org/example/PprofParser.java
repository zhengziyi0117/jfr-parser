package org.example;

import com.google.perftools.profiles.ProfileProto;
import entity.FrameTree;
import entity.FrameTreeBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class PprofParser {
    public static FrameTree parsePprofFile(String filePath, String endpoint) {
        try (InputStream fileStream = new FileInputStream(filePath); InputStream gzipStream = new GZIPInputStream(fileStream)) {
            ProfileProto.Profile profile = ProfileProto.Profile.parseFrom(gzipStream);
            return new FrameTreeBuilder(profile, endpoint).build();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
