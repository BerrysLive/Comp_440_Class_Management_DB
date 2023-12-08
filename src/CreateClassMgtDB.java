import java.io.IOException;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public class CreateClassMgtDB {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cms";
    private static final Scanner keyBoard = new Scanner(System.in);

    /*private enum MenuOption {
        FACULTY_MENU(1),
        END(2);

        private int value;

        MenuOption(int value) {
            this.value = value;
        }
    }*/

    public static void main(String[] args) {


        Properties connectionProps = new Properties();
        connectionProps.put("user", "root");
        connectionProps.put("password", "berrysroot45!"); //substitue your pw for *
        String[] path = {"Department", "Building", "Room"};

        List data = new ArrayList<>();


        //Department dept = new Department();
        //Building bldg = new Building();
        //Classroom room = new Classroom();


        //look @ path for order of String[]
        for (String tmp: path) {
            for (int i = 0; i < path.length; i++){
                data = loadData(tmp);
                processData(data);
            }
        }


        try (Connection conn = DriverManager.getConnection(DB_URL, connectionProps)) {
            executeUpdate(conn, "DROP DATABASE cms");
            executeUpdate(conn, "CREATE DATABASE cms");
            executeUpdate(conn, "USE cms");

            conn.setAutoCommit(false); // Start transaction

            buildTables(conn);

            createListAllCoursesProcedure(conn);
            createListAllDepartmentsProcedure(conn);
            createCourseProcedure(conn);
            createInstructorProcedure(conn);

            addInstructor(conn,1, "Senhua Yu");


            do {
                int mainMenuChoice = printMainMenu();

                if (mainMenuChoice == 1)
                    handleFacultyMenu(keyBoard, conn);
                else if (mainMenuChoice == 2)
                    break;
                else
                    System.out.println("Invalid choice. Please select a valid option. (1-2)");

            } while (true);

            conn.commit(); // Commit transaction

        }  catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void processData(final List data) {


    }

    private static List<String> loadData(String path) {

        String[] tmpArr = new String[path.length()];

        int count = 0;

        try(Scanner fInp = new Scanner(Paths.get(path))) {
            fInp.useDelimiter(",");

            while (fInp.hasNextLine())
                tmpArr[count++] = fInp.nextLine();

        } catch (NoSuchElementException | IllegalStateException | IOException e) {
            System.err.println("Error processing file. Terminating");
            System.exit(1);
        }

        return Arrays.asList(tmpArr);
    }

    // User Input Functions:
    private static int printMainMenu(){
        System.out.println("""
                Welcome to the School's MySQL Database
                Please Choose one of the following options
                1) Faculty Menu
                2) Exit
                """);
        return keyBoard.nextInt();
    }

    private static int printFacultyMenu(Scanner keyBoard){
        System.out.println(
                """
                        1) Create course
                        2) List all courses
                        3) List all instructors
                        4) List all departments
                        5) Return
                        """);
        return keyBoard.nextInt();
    }

    public static void handleFacultyMenu(Scanner keyBoard, Connection conn)  {
        boolean inFacultyMenu = true;
        while (inFacultyMenu) {
            int facultyMenuChoice = printFacultyMenu(keyBoard);
            int instructor_id;
            String course_description;
            String course_name;

            switch (facultyMenuChoice) {
                case 1:
                    // Implement the logic for "Create course" here
                    System.out.println("Enter Instructor ID: ");
                    instructor_id = keyBoard.nextInt();
                    keyBoard.nextLine();
                    System.out.println("Enter Course Name: ");
                    course_name = keyBoard.nextLine();
                    System.out.println("Enter Course Description: ");
                    course_description = keyBoard.nextLine();
                    createCourse(conn, instructor_id, course_name, course_description);
                    break;

                case 2:
                    listAllCourses(conn);
                    break;
                case 3: // list all instructor's logic
                    listAllInstructors(conn);
                    break;
                case 4: // List all Departments logic
                    listAllDepartments(conn);
                    break;
                case 5:
                    inFacultyMenu = false; // Exit the faculty menu and return to the main menu
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option. (1-6)");
                    break;
            }
        }
    }

    private static void addBuildingTuple(final Connection conn, List<String> data){
        try {
            Statement stmt = conn.createStatement();
            String building_name;

            //Statement
            for (String tmp: data) {
                building_name = tmp;
                String sqlStatement = """
                INSERT INTO building (building_name)
                VALUES (building_name);
                """;
                stmt.executeUpdate(sqlStatement);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    // Using Insertion Procedure

    private static void listAllInstructors(final Connection conn)  {
        try (CallableStatement stmt = conn.prepareCall("{CALL ListAllInstructors()}")) {
            ResultSet rs = stmt.executeQuery();
            System.out.println("All Instructors:");
            while (rs.next()) {
                int instructorId = rs.getInt("Instructor_ID");
                String instructorName = rs.getString("name");
                // Add other fields if necessary
                System.out.println("Instructor ID: " + instructorId + ", Name: " + instructorName);
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    private static void listAllDepartments(final Connection conn)  {
        try (CallableStatement stmt = conn.prepareCall("{CALL ListAllDepartments()}")) {
            ResultSet rs = stmt.executeQuery();
            System.out.println("All Departments:");
            while (rs.next()) {
                int departmentId = rs.getInt("department_id");
                String departmentName = rs.getString("department_name");
                // Add other fields if necessary
                System.out.println("Department ID: " + departmentId + ", Name: " + departmentName);
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    private static void listAllCourses(final Connection conn)  {
        try (CallableStatement stmt = conn.prepareCall("{CALL ListAllCourses()}")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int courseId = rs.getInt("course_id");
                String courseTitle = rs.getString("course_title");
                int instructorId = rs.getInt("Instructor_ID");
                String courseDesc = rs.getString("subject");
                // Add more fields if needed
                System.out.println("Course ID: " + courseId + ", Title: " + courseTitle + ", Instructor ID: " + instructorId + " Course Description: " + courseDesc);
            }
        }
        catch(SQLException e){
            throw new RuntimeException();
        }
    }

    // Create Schedule Algorithm
    // Instructor puts in request to create a course
    // next they choose a room and a time they want the room
    // if the room is available at their requested time that time is now blacked out for other people
    // otherwise show the instructor the next available time for the room
    // if there is no available time left then offer them a different room

    // Polynomial time
    // interval scheduling algorithm

    // Test Edit for github bot


    // Create Insertion Procedure
    private static void addInstructor(final Connection conn, int departmentId, String instructorName) {
        try (CallableStatement stmt = conn.prepareCall("{CALL AddInstructor(?, ?)}")) {
            stmt.setInt(1, departmentId);
            stmt.setString(2, instructorName);
            stmt.execute();
            //System.out.println("Instructor added successfully: " + instructorName + " (Instructor_ID: " + generatedInstructorId + ")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        private static void createCourse(final Connection conn, int instructorId, String courseTitle, String subject)  {
        try (CallableStatement stmt = conn.prepareCall("{CALL CreateCourse(?, ?, ?)}")) {
            stmt.setInt(1, instructorId);
            stmt.setString(2, courseTitle);
            stmt.setString(3, subject);
            stmt.execute();
            //System.out.println("Course created successfully.");
        }catch(SQLException e){
           System.out.println("Error Message:" + e.getMessage());
           e.printStackTrace();
        }
    }

    private static void createListAllInstructorsProcedure(final Connection conn) {
        String createProcedureSQL =
                "CREATE PROCEDURE ListAllInstructors() " +
                        "BEGIN " +
                        "SELECT * FROM instructor; " +
                        "END";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createProcedureSQL);
            //System.out.println("Stored procedure 'ListAllInstructors' created successfully.");
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static void createListAllCoursesProcedure(final Connection conn)  {
        String createListAllCoursesProcedure =
                "CREATE PROCEDURE ListAllCourses() " +
                        "BEGIN " +
                        "SELECT * FROM courses; " +
                        "END";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createListAllCoursesProcedure);
            System.out.println("Stored procedure 'ListAllCourses' created successfully.");
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    private static void createInstructorProcedure(final Connection conn)  {

        String createProcedure =
                "CREATE PROCEDURE AddInstructor(IN department_id INT,IN name VARCHAR(50)) " +
                        "BEGIN " +
                        "INSERT INTO Instructor (name) VALUES (name); " +
                        "END";

            executeUpdate(conn, createProcedure);
            // System.out.println("Add Instructor procedure has been created.");

    }

   /*private static void createBuildingProcedure(final Connection conn)  {
        String createProcedure =
                """
                        CREATE PROCEDURE AddBuilding(IN building_name VARCHAR(50))
                        BEGIN
                        INSERT INTO building (building_name) VALUES (building_name);
                        END""";

        executeUpdate(conn, createProcedure);
        //System.out.println("Add Building procedure has been created.");
    }
*/

    private static void createDepartmentProcedure(final Connection conn)  {
        String createProcedure =
                "CREATE PROCEDURE AddDepartment(IN department_name VARCHAR(50)) " +
                        "BEGIN " +
                        "INSERT INTO department (department_name) VALUES (department_name); " +
                        "END";

        executeUpdate(conn, createProcedure);
        //System.out.println("Add Department procedure has been created.");
    }


    private static void createCourseProcedure(final Connection conn)  {
        String createProcedure =
                "CREATE PROCEDURE CreateCourse(IN instructor_id INT, IN course_title VARCHAR(50), IN subject VARCHAR(50)) " +
                        "BEGIN " +
                        "INSERT INTO courses (Instructor_ID, course_title, subject) VALUES (instructor_id, course_title, subject); " +
                        "END";

        executeUpdate(conn, createProcedure);
        //System.out.println("Create Course Procedure has been created.");
    }







    // Drop All Tables Function

    private static void dropTables(final Connection conn)  {
        String[] tables = { "courses", "instructor", "classroom", "department", "building", "schedules" };
        for (String table : tables) {
            executeUpdate(conn, "DROP TABLE IF EXISTS " + table);
            System.out.println(table + " table dropped.");
        }
    }

    // Build Table Function


    private static void buildTables(final Connection conn)  {
        buildBuildingTable(conn);
        buildDepartmentTable(conn);
        buildInstructorTable(conn);
        buildClassroomTable(conn);
        buildCoursesTable(conn);
        buildSectionTable(conn);
        buildRequestTable(conn);
    }

    private static void buildRequestTable(final Connection conn) {
        String sql_stat = """
                CREATE TABLE request(request_id int AUTO_INCREMENT PRIMARY KEY,
                                      building_id int,
                                      classroom_id int,
                                      start_at TIME,
                                      end_at TIME,
                                      equipment VARCHAR(50),
                                      FOREIGN KEY (building_id) REFERENCES building(building_id),
                                      FOREIGN KEY (classroom_id) REFERENCES classroom(classroom_id)) AUTO_INCREMENT=1000""";
        executeUpdate(conn, sql_stat);
    }


    // Create Table Functions

    private static void buildBuildingTable(final Connection conn)  {
        executeUpdate(conn, "CREATE TABLE building (" +
                "building_id int AUTO_INCREMENT PRIMARY KEY, " +
                "building_name VARCHAR(30)) AUTO_INCREMENT=1000");
        //System.out.println("Building table has been created.");
    }

    private static void buildClassroomTable(final Connection conn)  {
        executeUpdate(conn, "CREATE TABLE Classroom (" +
                "classroom_id int auto_increment primary key, " +
                "building_id int, " +
                "room_num varchar(6) not null, " +
                "capacity int not null, " +
                "equipment varchar(50), " +
                "blackout_start TIME, " +
                "blackout_end TIME, " +
                "FOREIGN KEY (building_id) REFERENCES building(building_id))");
        //System.out.println("Classroom table has been created.");
    }

    private static void buildInstructorTable(final Connection conn)  {
        executeUpdate(conn, "CREATE TABLE instructor (" +
                "Instructor_ID int AUTO_INCREMENT PRIMARY KEY, " +
                "department_id int, " +
                "name VARCHAR(50) not null, " +
                "FOREIGN KEY (department_id) REFERENCES department(department_id))");
        //System.out.println("Instructor table has been created.");
    }

    private static void buildCoursesTable(final Connection conn)  {
        executeUpdate(conn, "CREATE TABLE courses (" +
                "course_id int auto_increment primary key, " +
                "Instructor_ID int not null, " +
                "course_title varchar(50) not null, " +
                "subject varchar(50), " +
                "FOREIGN KEY (Instructor_ID) REFERENCES Instructor(Instructor_ID))");
        //System.out.println("Courses table has been created.");
    }

    private static void buildDepartmentTable(final Connection conn)  {
        executeUpdate(conn, "CREATE TABLE department (" +
                "department_id int AUTO_INCREMENT PRIMARY KEY, " +
                "department_name VARCHAR(50) not null, " +
                "dean varchar(50), " +
                "building_id int, " +
                "FOREIGN KEY (building_id) REFERENCES building(building_id))");
        //System.out.println("Department table has been created.");
    }
    private static void buildSectionTable(final Connection conn)  {
        executeUpdate(conn, "CREATE TABLE schedules (" +
                "schedule_id int AUTO_INCREMENT PRIMARY KEY, " +
                "class_id int, " +
                "Instructor_ID int, " +
                "day_of_week VARCHAR(10), " +
                "start_time TIME, " +
                "end_time TIME, " +
                "FOREIGN KEY (class_id) REFERENCES courses(course_id), " +
                "FOREIGN KEY (Instructor_ID) REFERENCES Instructor(Instructor_ID))");
    }

    private static void createListAllDepartmentsProcedure(final Connection conn)  {
        String createProcedureSQL =
                "CREATE PROCEDURE ListAllDepartments() " +
                        "BEGIN " +
                        "SELECT * FROM department; " +
                        "END";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createProcedureSQL);
            //System.out.println("Stored procedure 'ListAllDepartments' created successfully.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeUpdate(Connection conn, String sql)  {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}


   /*// Manual Test
            createBuilding(conn);
            createDepartment(conn, "Mathematics"); // Create department
            createDepartment(conn,"Computer Science");
            *//*createInstructor(conn, 1, "Otto Octavius"); // Create instructor
              createInstructor(conn,2, "Howard Stark");
              createStudent(conn, "Peter Parker", 1); // Create student
              createStudent(conn, "Tony Stark", 2);*//*
              createCourse(conn,1,"Math 150A", "Calc 1");
              createCourse(conn,1,"Math 150B", "Calc 2");
              createCourse(conn,2,"Comp 110", "Introduction to Algorithms and Programming");
              createCourse(conn,2,"Comp 122", "Computer Architecture and Assembly Language");*/


  /*  private static void createBuilding(final Connection conn)  {
        try (CallableStatement stmt = conn.prepareCall("{CALL AddBuilding(?)}")) {
            stmt.setString(1, "Eucalyptus");
            stmt.execute();
            //System.out.println("Building added successfully: " + buildingName);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
*/

   /* private static void createDepartment(final Connection conn, String departmentName)  {
        try (CallableStatement stmt = conn.prepareCall("{CALL AddDepartment(?)}")) {
            stmt.setString(1, departmentName);
            stmt.execute();
            //System.out.println("Department added successfully: " + departmentName + " (Department_ID: " + departmentId + ")");
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }*/


