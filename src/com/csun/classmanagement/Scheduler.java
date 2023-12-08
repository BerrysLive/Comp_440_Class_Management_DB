package com.csun.classmanagement;

import java.sql.*;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scheduler {
    private Map<Integer, Classroom> classrooms = new HashMap<>();
    private Connection connection; // Assume you have a database connection here

    public Scheduler(Connection connection) {
        this.connection = connection;
    }

    public void initializeClassrooms() {
        this.classrooms = fetchClassroomData();
    }

    public void processBookingRequests(List<Request> requests) {
        Collections.sort(requests); // Assuming Request implements Comparable and sorts by startTime

        for (Request request : requests) {
            Classroom classroom = classrooms.get(request.getClassroomId());
            if (classroom == null) {
                System.out.println("Classroom not found: " + request.getClassroomId());
                continue;
            }

            if (isAvailable(classroom, request)) {
                bookClassroom(classroom, request);
                System.out.println("Booking successful: " + request.getClassroomId());
            } else {
                System.out.println("Booking conflict: " + request.getClassroomId());
            }
        }
    }

    private boolean isAvailable(Classroom classroom, Request request) {
        // Convert java.sql.Time to java.time.LocalTime before comparison
        LocalTime blackoutStart = classroom.getBlackoutStart() != null ? classroom.getBlackoutStart().toLocalTime() : null;
        LocalTime blackoutEnd = classroom.getBlackoutEnd() != null ? classroom.getBlackoutEnd().toLocalTime() : null;
        LocalTime requestStartTime = request.getStartTime().toLocalTime(); // Adjust this line to match your actual Request class
        LocalTime requestEndTime = request.getEndTime().toLocalTime(); // Adjust this line to match your actual Request class

        return (blackoutStart == null || requestEndTime.isBefore(blackoutStart)) &&
                (blackoutEnd == null || requestStartTime.isAfter(blackoutEnd));
    }


    private void bookClassroom(Classroom classroom, Request request) {
        classroom.setBlackoutStart(request.getStartTime());
        classroom.setBlackoutEnd(request.getEndTime());
        updateClassroomInDatabase(classroom);
    }

    private Map<Integer, Classroom> fetchClassroomData() {
        Map<Integer, Classroom> classrooms = new HashMap<>();
        String sql = "SELECT * FROM classroom"; // Adjust SQL as necessary
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("classroom_id");
                int buildingId = resultSet.getInt("building_id");
                String roomNum = resultSet.getString("room_num");
                int capacity = resultSet.getInt("capacity");
                String equipment = resultSet.getString("equipment");
                java.sql.Time blackoutStartSql = resultSet.getTime("blackout_start");
                java.sql.Time blackoutEndSql = resultSet.getTime("blackout_end");
                LocalTime blackoutStart = blackoutStartSql != null ? blackoutStartSql.toLocalTime() : null;
                LocalTime blackoutEnd = blackoutEndSql != null ? blackoutEndSql.toLocalTime() : null;

                Classroom classroom = new Classroom(id, buildingId, roomNum, capacity, equipment, blackoutStartSql, blackoutEndSql);
                classrooms.put((int) id, classroom);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classrooms;
    }


    private void updateClassroomInDatabase(Classroom classroom) {
        String sql = "UPDATE classroom SET blackout_start = ?, blackout_end = ? WHERE classroom_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTime(1, Time.valueOf(classroom.getBlackoutStart().toLocalTime()));
            statement.setTime(2, Time.valueOf(classroom.getBlackoutEnd().toLocalTime()));
            statement.setInt(3, classroom.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
