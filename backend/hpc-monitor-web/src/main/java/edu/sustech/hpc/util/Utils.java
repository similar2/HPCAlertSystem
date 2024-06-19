package edu.sustech.hpc.util;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {
    public static YamlObj getYaml(String filePath) throws IOException {
        Yaml yaml = new Yaml();
        InputStream inputStream = null;
        inputStream = Files.newInputStream(Paths.get(filePath));
        Object obj = yaml.load(inputStream);
        inputStream.close();
        return new YamlObj(yaml, obj, filePath);
    }
}
