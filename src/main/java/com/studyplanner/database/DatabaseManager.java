package com.studyplanner.database;

import com.studyplanner.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:study_planner.db";
    private Connection connection;

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            System.out.println("Database initialized successfully!");
        } catch (SQLException e) {
            System.err.println(
                "Error initializing database: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        String createCoursesTable = """
                CREATE TABLE IF NOT EXISTS courses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    code TEXT NOT NULL UNIQUE,
                    description TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;

        String createTopicsTable = """
                CREATE TABLE IF NOT EXISTS topics (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    course_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    description TEXT,
                    priority INTEGER DEFAULT 3,
                    difficulty_level INTEGER DEFAULT 3,
                    first_study_date DATE,
                    last_review_date DATE,
                    review_count INTEGER DEFAULT 0,
                    easiness_factor REAL DEFAULT 2.5,
                    interval INTEGER DEFAULT 1,
                    mastered BOOLEAN DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
                )
            """;

        String createExamSchedulesTable = """
                CREATE TABLE IF NOT EXISTS exam_schedules (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    course_id INTEGER NOT NULL,
                    exam_type TEXT NOT NULL,
                    title TEXT NOT NULL,
                    exam_date DATE NOT NULL,
                    exam_time TIME,
                    location TEXT,
                    notes TEXT,
                    completed BOOLEAN DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
                )
            """;

        String createStudySessionsTable = """
                CREATE TABLE IF NOT EXISTS study_sessions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    topic_id INTEGER NOT NULL,
                    course_id INTEGER NOT NULL,
                    scheduled_date DATE NOT NULL,
                    session_type TEXT NOT NULL,
                    completed BOOLEAN DEFAULT 0,
                    completed_at TIMESTAMP,
                    performance_rating INTEGER,
                    notes TEXT,
                    duration_minutes INTEGER DEFAULT 30,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE,
                    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
                )
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createCoursesTable);
            stmt.execute(createTopicsTable);
            stmt.execute(createExamSchedulesTable);
            stmt.execute(createStudySessionsTable);
        }
    }

    // ==================== COURSE OPERATIONS ====================

    public int addCourse(Course course) throws SQLException {
        String sql =
            "INSERT INTO courses (name, code, description) VALUES (?, ?, ?)";
        try (
            PreparedStatement pstmt = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            pstmt.setString(1, course.getName());
            pstmt.setString(2, course.getCode());
            pstmt.setString(3, course.getDescription());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void updateCourse(Course course) throws SQLException {
        String sql =
            "UPDATE courses SET name = ?, code = ?, description = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, course.getName());
            pstmt.setString(2, course.getCode());
            pstmt.setString(3, course.getDescription());
            pstmt.setInt(4, course.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.executeUpdate();
        }
    }

    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY code";

        try (
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setCode(rs.getString("code"));
                course.setDescription(rs.getString("description"));
                courses.add(course);
            }
        }
        return courses;
    }

    public Course getCourseById(int id) throws SQLException {
        String sql = "SELECT * FROM courses WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setCode(rs.getString("code"));
                course.setDescription(rs.getString("description"));
                return course;
            }
        }
        return null;
    }

    // ==================== TOPIC OPERATIONS ====================

    public int addTopic(Topic topic) throws SQLException {
        String sql = """
                INSERT INTO topics (course_id, name, description, priority, difficulty_level,
                                   first_study_date, last_review_date, review_count,
                                   easiness_factor, interval, mastered)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (
            PreparedStatement pstmt = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            pstmt.setInt(1, topic.getCourseId());
            pstmt.setString(2, topic.getName());
            pstmt.setString(3, topic.getDescription());
            pstmt.setInt(4, topic.getPriority());
            pstmt.setInt(5, topic.getDifficultyLevel());
            pstmt.setObject(6, topic.getFirstStudyDate());
            pstmt.setObject(7, topic.getLastReviewDate());
            pstmt.setInt(8, topic.getReviewCount());
            pstmt.setDouble(9, topic.getEasinessFactor());
            pstmt.setInt(10, topic.getInterval());
            pstmt.setBoolean(11, topic.isMastered());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void updateTopic(Topic topic) throws SQLException {
        String sql = """
                UPDATE topics SET course_id = ?, name = ?, description = ?,
                                priority = ?, difficulty_level = ?,
                                first_study_date = ?, last_review_date = ?,
                                review_count = ?, easiness_factor = ?,
                                interval = ?, mastered = ?
                WHERE id = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, topic.getCourseId());
            pstmt.setString(2, topic.getName());
            pstmt.setString(3, topic.getDescription());
            pstmt.setInt(4, topic.getPriority());
            pstmt.setInt(5, topic.getDifficultyLevel());
            pstmt.setObject(6, topic.getFirstStudyDate());
            pstmt.setObject(7, topic.getLastReviewDate());
            pstmt.setInt(8, topic.getReviewCount());
            pstmt.setDouble(9, topic.getEasinessFactor());
            pstmt.setInt(10, topic.getInterval());
            pstmt.setBoolean(11, topic.isMastered());
            pstmt.setInt(12, topic.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteTopic(int topicId) throws SQLException {
        String sql = "DELETE FROM topics WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, topicId);
            pstmt.executeUpdate();
        }
    }

    public List<Topic> getTopicsByCourse(int courseId) throws SQLException {
        List<Topic> topics = new ArrayList<>();
        String sql =
            "SELECT * FROM topics WHERE course_id = ? ORDER BY priority DESC, name";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                topics.add(extractTopicFromResultSet(rs));
            }
        }
        return topics;
    }

    public Topic getTopicById(int id) throws SQLException {
        String sql = "SELECT * FROM topics WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractTopicFromResultSet(rs);
            }
        }
        return null;
    }

    public List<Topic> getAllTopics() throws SQLException {
        List<Topic> topics = new ArrayList<>();
        String sql = "SELECT * FROM topics ORDER BY priority DESC, name";

        try (
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                topics.add(extractTopicFromResultSet(rs));
            }
        }
        return topics;
    }

    private Topic extractTopicFromResultSet(ResultSet rs) throws SQLException {
        Topic topic = new Topic();
        topic.setId(rs.getInt("id"));
        topic.setCourseId(rs.getInt("course_id"));
        topic.setName(rs.getString("name"));
        topic.setDescription(rs.getString("description"));
        topic.setPriority(rs.getInt("priority"));
        topic.setDifficultyLevel(rs.getInt("difficulty_level"));

        String firstStudyDate = rs.getString("first_study_date");
        if (firstStudyDate != null) {
            topic.setFirstStudyDate(LocalDate.parse(firstStudyDate));
        }

        String lastReviewDate = rs.getString("last_review_date");
        if (lastReviewDate != null) {
            topic.setLastReviewDate(LocalDate.parse(lastReviewDate));
        }

        topic.setReviewCount(rs.getInt("review_count"));
        topic.setEasinessFactor(rs.getDouble("easiness_factor"));
        topic.setInterval(rs.getInt("interval"));
        topic.setMastered(rs.getBoolean("mastered"));

        return topic;
    }

    // ==================== EXAM SCHEDULE OPERATIONS ====================

    public int addExamSchedule(ExamSchedule exam) throws SQLException {
        String sql = """
                INSERT INTO exam_schedules (course_id, exam_type, title, exam_date,
                                           exam_time, location, notes, completed)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (
            PreparedStatement pstmt = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            pstmt.setInt(1, exam.getCourseId());
            pstmt.setString(2, exam.getExamType());
            pstmt.setString(3, exam.getTitle());
            pstmt.setObject(4, exam.getExamDate());
            pstmt.setObject(5, exam.getExamTime());
            pstmt.setString(6, exam.getLocation());
            pstmt.setString(7, exam.getNotes());
            pstmt.setBoolean(8, exam.isCompleted());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void updateExamSchedule(ExamSchedule exam) throws SQLException {
        String sql = """
                UPDATE exam_schedules SET course_id = ?, exam_type = ?, title = ?,
                                         exam_date = ?, exam_time = ?, location = ?,
                                         notes = ?, completed = ?
                WHERE id = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, exam.getCourseId());
            pstmt.setString(2, exam.getExamType());
            pstmt.setString(3, exam.getTitle());
            pstmt.setObject(4, exam.getExamDate());
            pstmt.setObject(5, exam.getExamTime());
            pstmt.setString(6, exam.getLocation());
            pstmt.setString(7, exam.getNotes());
            pstmt.setBoolean(8, exam.isCompleted());
            pstmt.setInt(9, exam.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteExamSchedule(int examId) throws SQLException {
        String sql = "DELETE FROM exam_schedules WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            pstmt.executeUpdate();
        }
    }

    public List<ExamSchedule> getUpcomingExams() throws SQLException {
        List<ExamSchedule> exams = new ArrayList<>();
        String sql = """
                SELECT * FROM exam_schedules
                WHERE exam_date >= date('now') AND completed = 0
                ORDER BY exam_date ASC
            """;

        try (
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                exams.add(extractExamFromResultSet(rs));
            }
        }
        return exams;
    }

    public List<ExamSchedule> getExamsByCourse(int courseId)
        throws SQLException {
        List<ExamSchedule> exams = new ArrayList<>();
        String sql =
            "SELECT * FROM exam_schedules WHERE course_id = ? ORDER BY exam_date";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                exams.add(extractExamFromResultSet(rs));
            }
        }
        return exams;
    }

    private ExamSchedule extractExamFromResultSet(ResultSet rs)
        throws SQLException {
        ExamSchedule exam = new ExamSchedule();
        exam.setId(rs.getInt("id"));
        exam.setCourseId(rs.getInt("course_id"));
        exam.setExamType(rs.getString("exam_type"));
        exam.setTitle(rs.getString("title"));

        String examDate = rs.getString("exam_date");
        if (examDate != null) {
            exam.setExamDate(LocalDate.parse(examDate));
        }

        String examTime = rs.getString("exam_time");
        if (examTime != null) {
            exam.setExamTime(LocalTime.parse(examTime));
        }

        exam.setLocation(rs.getString("location"));
        exam.setNotes(rs.getString("notes"));
        exam.setCompleted(rs.getBoolean("completed"));

        return exam;
    }

    // ==================== STUDY SESSION OPERATIONS ====================

    public int addStudySession(StudySession session) throws SQLException {
        String sql = """
                INSERT INTO study_sessions (topic_id, course_id, scheduled_date,
                                           session_type, completed, completed_at,
                                           performance_rating, notes, duration_minutes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (
            PreparedStatement pstmt = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            pstmt.setInt(1, session.getTopicId());
            pstmt.setInt(2, session.getCourseId());
            pstmt.setObject(3, session.getScheduledDate());
            pstmt.setString(4, session.getSessionType());
            pstmt.setBoolean(5, session.isCompleted());
            pstmt.setObject(6, session.getCompletedAt());
            pstmt.setObject(
                7,
                session.getPerformanceRating() > 0
                    ? session.getPerformanceRating()
                    : null
            );
            pstmt.setString(8, session.getNotes());
            pstmt.setInt(9, session.getDurationMinutes());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void updateStudySession(StudySession session) throws SQLException {
        String sql = """
                UPDATE study_sessions SET topic_id = ?, course_id = ?, scheduled_date = ?,
                                         session_type = ?, completed = ?, completed_at = ?,
                                         performance_rating = ?, notes = ?, duration_minutes = ?
                WHERE id = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, session.getTopicId());
            pstmt.setInt(2, session.getCourseId());
            pstmt.setObject(3, session.getScheduledDate());
            pstmt.setString(4, session.getSessionType());
            pstmt.setBoolean(5, session.isCompleted());
            pstmt.setObject(6, session.getCompletedAt());
            pstmt.setObject(
                7,
                session.getPerformanceRating() > 0
                    ? session.getPerformanceRating()
                    : null
            );
            pstmt.setString(8, session.getNotes());
            pstmt.setInt(9, session.getDurationMinutes());
            pstmt.setInt(10, session.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteStudySession(int sessionId) throws SQLException {
        String sql = "DELETE FROM study_sessions WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, sessionId);
            pstmt.executeUpdate();
        }
    }

    public List<StudySession> getSessionsByDate(LocalDate date)
        throws SQLException {
        List<StudySession> sessions = new ArrayList<>();
        String sql = """
                SELECT s.*, t.name as topic_name, c.name as course_name, c.code as course_code
                FROM study_sessions s
                JOIN topics t ON s.topic_id = t.id
                JOIN courses c ON s.course_id = c.id
                WHERE s.scheduled_date = ?
                ORDER BY s.session_type, c.code
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, date);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                StudySession session = extractSessionFromResultSet(rs);
                session.setTopicName(rs.getString("topic_name"));
                session.setCourseName(
                    rs.getString("course_code") +
                        " - " +
                        rs.getString("course_name")
                );
                sessions.add(session);
            }
        }
        return sessions;
    }

    public List<StudySession> getTodaySessions() throws SQLException {
        return getSessionsByDate(LocalDate.now());
    }

    public List<StudySession> getUpcomingSessions(int limit)
        throws SQLException {
        List<StudySession> sessions = new ArrayList<>();
        String sql = """
                SELECT s.*, t.name as topic_name, c.name as course_name, c.code as course_code
                FROM study_sessions s
                JOIN topics t ON s.topic_id = t.id
                JOIN courses c ON s.course_id = c.id
                WHERE s.scheduled_date > DATE('now')
                AND s.completed = 0
                ORDER BY s.scheduled_date ASC, s.session_type
                LIMIT ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                StudySession session = extractSessionFromResultSet(rs);
                session.setTopicName(rs.getString("topic_name"));
                session.setCourseName(
                    rs.getString("course_code") +
                        " - " +
                        rs.getString("course_name")
                );
                sessions.add(session);
            }
        }

        return sessions;
    }

    private StudySession extractSessionFromResultSet(ResultSet rs)
        throws SQLException {
        StudySession session = new StudySession();
        session.setId(rs.getInt("id"));
        session.setTopicId(rs.getInt("topic_id"));
        session.setCourseId(rs.getInt("course_id"));

        String scheduledDate = rs.getString("scheduled_date");
        if (scheduledDate != null) {
            session.setScheduledDate(LocalDate.parse(scheduledDate));
        }

        session.setSessionType(rs.getString("session_type"));
        session.setCompleted(rs.getBoolean("completed"));

        String completedAt = rs.getString("completed_at");
        if (completedAt != null) {
            session.setCompletedAt(LocalDateTime.parse(completedAt));
        }

        int rating = rs.getInt("performance_rating");
        if (!rs.wasNull()) {
            session.setPerformanceRating(rating);
        }

        session.setNotes(rs.getString("notes"));
        session.setDurationMinutes(rs.getInt("duration_minutes"));

        return session;
    }

    public int getStudyStreak() throws SQLException {
        String sql = """
                WITH RECURSIVE dates AS (
                    SELECT DATE('now') as check_date
                    UNION ALL
                    SELECT DATE(check_date, '-1 day')
                    FROM dates
                    WHERE check_date > DATE('now', '-30 days')
                )
                SELECT COUNT(*) as streak
                FROM (
                    SELECT d.check_date
                    FROM dates d
                    WHERE EXISTS (
                        SELECT 1 FROM study_sessions s
                        WHERE DATE(s.scheduled_date) = d.check_date
                        AND s.completed = 1
                    )
                    AND d.check_date <= DATE('now')
                    ORDER BY d.check_date DESC
                    LIMIT (
                        SELECT COUNT(*)
                        FROM dates d2
                        WHERE d2.check_date <= DATE('now')
                        AND NOT EXISTS (
                            SELECT 1
                            FROM dates d3
                            WHERE d3.check_date > d2.check_date
                            AND d3.check_date <= DATE('now')
                            AND NOT EXISTS (
                                SELECT 1 FROM study_sessions s
                                WHERE DATE(s.scheduled_date) = d3.check_date
                                AND s.completed = 1
                            )
                        )
                    )
                )
            """;

        try (
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt("streak");
            }
        }
        return 0;
    }

    public int getTodayStudyTime() throws SQLException {
        String sql = """
                SELECT COALESCE(SUM(duration_minutes), 0) as total_minutes
                FROM study_sessions
                WHERE scheduled_date = DATE('now')
                AND completed = 1
            """;

        try (
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt("total_minutes");
            }
        }
        return 0;
    }

    public int getYesterdayStudyTime() throws SQLException {
        String sql = """
                SELECT COALESCE(SUM(duration_minutes), 0) as total_minutes
                FROM study_sessions
                WHERE scheduled_date = DATE('now', '-1 day')
                AND completed = 1
            """;

        try (
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt("total_minutes");
            }
        }
        return 0;
    }

    public List<Topic> getNextReviewTopics(int limit) throws SQLException {
        List<Topic> topics = new ArrayList<>();
        String sql = """
                SELECT t.*, c.name as course_name, c.code as course_code,
                       DATE(t.last_review_date, '+' || t.interval || ' days') as next_review_date
                FROM topics t
                JOIN courses c ON t.course_id = c.id
                WHERE t.last_review_date IS NOT NULL
                AND t.mastered = 0
                AND DATE(t.last_review_date, '+' || t.interval || ' days') <= DATE('now', '+7 days')
                ORDER BY next_review_date ASC, t.priority DESC
                LIMIT ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Topic topic = extractTopicFromResultSet(rs);
                topics.add(topic);
            }
        }
        return topics;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println(
                "Error closing database connection: " + e.getMessage()
            );
        }
    }
}
