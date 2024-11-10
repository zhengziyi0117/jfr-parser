package one.jfr;

import lombok.Data;

import java.util.regex.Pattern;

@Data
public class Arguments {
    private String output;
    private String state;
    private Pattern include;
    private Pattern exclude;
    private int skip;
    private boolean reverse;
    private boolean cpu;
    private boolean wall;
    private boolean alloc;
    private boolean live;
    private boolean lock;
    private boolean threads;
    private boolean classify;
    private boolean total;
}
