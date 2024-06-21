package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    static Properties configTime(File file) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(file));
        return properties;
    }

    private Connection initConnection(Properties properties) throws ClassNotFoundException, SQLException {
        Class.forName(properties.getProperty("jdbc.driver"));
        return DriverManager.getConnection(
                properties.getProperty("jdbc.url"),
                properties.getProperty("jdbc.username"),
                properties.getProperty("jdbc.password")
        );
    }

    public static void main(String[] args) throws IOException, InterruptedException, SQLException, ClassNotFoundException {
        File file = new File("src/main/resources/rabbit.properties");
        Properties properties = configTime(file);
        AlertRabbit rabbit = new AlertRabbit();
        try (Connection connection = rabbit.initConnection(properties)) {
            int rabbitInterval = Integer.parseInt((String) properties.get("rabbit.interval"));
            try {
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
                JobDataMap data = new JobDataMap();
                data.put("data", connection);
                JobDetail job = newJob(Rabbit.class)
                        .usingJobData(data)
                        .build();
                SimpleScheduleBuilder times = simpleSchedule()
                        .withIntervalInSeconds(rabbitInterval)
                        .repeatForever();
                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(times)
                        .build();
                scheduler.scheduleJob(job, trigger);
                Thread.sleep(10000);
                scheduler.shutdown();
            } catch (SchedulerException se) {
                se.printStackTrace();
            }
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("data");
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO rabbit(created_date) VALUES (?)"
            )) {
                statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
