package edu.sustech.hpc.util;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class YamlObj {
    Yaml yaml;
    public Object object;
    String filePath;

    public YamlObj(Yaml yaml, Object object, String filePath) {
        this.yaml = yaml;
        this.object = object;
        this.filePath = filePath;
    }

    public YamlObj(Yaml yaml, Object object) {
        this(yaml, object, null);
    }

    public void writeYaml(String filePath) throws FileNotFoundException {
        PrintWriter writer = null;
        writer = new PrintWriter(filePath);
        yaml.dump(object, writer);
        writer.close();
    }

    public void writeYaml() throws FileNotFoundException {
        writeYaml(filePath);
    }
}
