package com.acuo.algo;

import org.apache.commons.io.IOUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class Renjin {

    static {
        NativeUtils.load("lpsolve55", "lpsolve55j");
    }

    private final ScriptEngine engine;

    public Renjin() {
        // create a script engine manager:
        ScriptEngineManager manager = new ScriptEngineManager();
        // create a Renjin engine:
        engine = manager.getEngineByName("Renjin");
        // check if the engine has loaded correctly:
        if (engine == null) {
            throw new RuntimeException(
                    "Renjin Script Engine not found on the classpath.");
        }
    }

    public void put(String key, Object value) throws ScriptException {
        engine.put(key, value);
    }

    public Object eval(final String instruction) throws ScriptException {
        return engine.eval(instruction);
    }

    public Object execute(String scriptName) {
        try {
            String script = readFile(scriptName);
            return engine.eval(script);
        } catch (IOException | URISyntaxException | ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getDataLink(String dataImportLink) {
        if (dataImportLink.startsWith("file://") || dataImportLink.startsWith("http://") || dataImportLink.startsWith("https://"))
            return dataImportLink;
        return "file://" + Renjin.class.getResource(dataImportLink).getFile();
    }

    private static String readFile(String filePath) throws IOException, URISyntaxException {
        String path = getDataLink(filePath);
        return IOUtils.toString(new URI(path), StandardCharsets.UTF_8);
    }
}
