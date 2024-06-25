package ru.job4j.grabber;

import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class PsqlStore implements Store {

    private Connection connection;

    public PsqlStore(Properties config) {
        try {
            Class.forName(config.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            connection = DriverManager.getConnection(
                    config.getProperty("jdbc.url"),
                    config.getProperty("jdbc.username"),
                    config.getProperty("jdbc.password"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Post createPost(ResultSet res) throws SQLException {
        return new Post(
                res.getInt("id"),
                res.getString("name"),
                res.getString("link"),
                res.getString("text"),
                res.getTimestamp("created").toLocalDateTime());
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO post (name, text, link, created) VALUES (?, ?, ?, ?) ON CONFLICT(link) DO NOTHING")) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Post> getAll() {
        List<Post> result = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM post")) {
            try (ResultSet date = statement.executeQuery()) {
                while (date.next()) {
                    result.add(createPost(date));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM post WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet date = statement.executeQuery()) {
                if (date == null) {
                    post = null;
                } else {
                    while (date.next()) {
                        post = createPost(date);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) throws Exception {
        HabrCareerDateTimeParser timeParser = new HabrCareerDateTimeParser();
        HabrCareerParse parse = new HabrCareerParse(timeParser);
        Properties properties = new Properties();
        try (InputStream input = PsqlStore.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PsqlStore psqlStore = new PsqlStore(properties)) {
            List<Post> result = parse.list("https://career.habr.com/vacancies?page=1&q=Java%20developer&type=all");
            for (Post post : result) {
                psqlStore.save(post);
            }
            System.out.println(psqlStore.findById(15));
            System.out.println(psqlStore.getAll());
        }
    }

}
