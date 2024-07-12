package ru.job4j.cache;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class DirFileCache extends AbstractCache<String, String> {

    private final String cachingDir;

    public DirFileCache(String cachingDir) {
        this.cachingDir = cachingDir;
    }

    @Override
    protected String load(String key) {
        StringBuilder result = new StringBuilder();
        StringBuilder path = new StringBuilder();
        path.append(cachingDir);
        path.append("/");
        path.append(key);
        try (BufferedReader input = new BufferedReader(new FileReader(path.toString()))) {
            String line;
            while ((line = input.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        put(key, result.toString());
        return key;
    }
}