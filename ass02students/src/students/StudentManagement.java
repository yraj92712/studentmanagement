package students;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

public class StudentManagement {
    private static final String URL = "jdbc:mysql://localhost:3306/student";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "@Yash7417";

    private static final String GET_ALL_STUDENTS = "SELECT * FROM Student";
    private static final String GET_STUDENT_BY_ROLLNO = "SELECT * FROM Student WHERE rollNo = ?";
    private static final String INSERT_STUDENT = "INSERT INTO Student (rollNo, name, branch, totMarks, percentage, grade) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_STUDENT_MARKS = "UPDATE Student SET totMarks = ?, percentage = ?, grade = ? WHERE rollNo = ?";
    private static final String DELETE_STUDENT = "DELETE FROM Student WHERE rollNo = ?";

    public static void main(String[] args) throws Exception {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.println("\n\n1. Add Student");
                System.out.println("2. View All Students");
                System.out.println("3. View Student By Roll Number");
                System.out.println("4. Update Student Marks");
                System.out.println("5. Delete Student");
                System.out.println("6. Exit\n\n");
                System.out.println("Enter Your Choice: ");
                String opt = br.readLine();

                switch (opt) {
                    case "1": {
                        addStudent(connection, br);
                        break;
                    }
                    case "2": {
                        viewAllStudents(connection);
                        break;
                    }
                    case "3": {
                        viewStudentByRollNo(connection, br);
                        break;
                    }
                    case "4": {
                        updateStudentMarks(connection, br);
                        break;
                    }
                    case "5": {
                        deleteStudent(connection, br);
                        break;
                    }
                    case "6": {
                        System.out.println("Good Bye");
                        System.exit(0);
                    }
                    default: {
                        System.err.println("Invalid Option Selected");
                    }
                }
            }
        }
    }

    private static void addStudent(Connection connection, BufferedReader br) throws Exception {
        System.out.println("Enter Student Roll Number: ");
        int rollNo = Integer.parseInt(br.readLine());
        System.out.println("Enter Student Name: ");
        String name = br.readLine();
        System.out.println("Enter Student Branch: ");
        String branch = br.readLine();

        System.out.println("Enter Marks for 6 Subjects: ");
        int s1 = Integer.parseInt(br.readLine());
        int s2 = Integer.parseInt(br.readLine());
        int s3 = Integer.parseInt(br.readLine());
        int s4 = Integer.parseInt(br.readLine());
        int s5 = Integer.parseInt(br.readLine());
        int s6 = Integer.parseInt(br.readLine());

        int totMarks = s1 + s2 + s3 + s4 + s5 + s6;
        double percentage = (double) totMarks / 6.0;
        String grade = calculateGrade(percentage);

        PreparedStatement ps = connection.prepareStatement(INSERT_STUDENT);
        ps.setInt(1, rollNo);
        ps.setString(2, name);
        ps.setString(3, branch);
        ps.setInt(4, totMarks);
        ps.setDouble(5, percentage);
        ps.setString(6, grade);

        int k = ps.executeUpdate();
        if (k > 0) {
            System.out.println("Student Added Successfully");
        } else {
            System.err.println("Failed to Add Student");
        }
    }

    private static void viewAllStudents(Connection connection) throws Exception {
        PreparedStatement ps = connection.prepareStatement(GET_ALL_STUDENTS);
        ResultSet rs = ps.executeQuery();
        System.out.printf("%-10s %-20s %-20s %-10s %-10s %-10s%n", "RollNo", "Name", "Branch", "TotMarks", "Percentage", "Grade");
        while (rs.next()) {
            System.out.printf("%-10d %-20s %-20s %-10d %-10.2f %-10s%n",
                    rs.getInt("rollNo"), rs.getString("name"), rs.getString("branch"), rs.getInt("totMarks"), rs.getDouble("percentage"), rs.getString("grade"));
        }
    }

    private static void viewStudentByRollNo(Connection connection, BufferedReader br) throws Exception {
        System.out.println("Enter Student Roll Number: ");
        int rollNo = Integer.parseInt(br.readLine());

        PreparedStatement ps = connection.prepareStatement(GET_STUDENT_BY_ROLLNO);
        ps.setInt(1, rollNo);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            System.out.printf("%-10d %-20s %-20s %-10d %-10.2f %-10s%n",
                    rs.getInt("rollNo"), rs.getString("name"), rs.getString("branch"), rs.getInt("totMarks"), rs.getDouble("percentage"), rs.getString("grade"));
        } else {
            System.err.println("Record Not Found");
        }
    }

    private static void updateStudentMarks(Connection connection, BufferedReader br) throws Exception {
        System.out.println("Enter Student Roll Number: ");
        int rollNo = Integer.parseInt(br.readLine());

        System.out.println("Enter New Marks for 6 Subjects: ");
        int s1 = Integer.parseInt(br.readLine());
        int s2 = Integer.parseInt(br.readLine());
        int s3 = Integer.parseInt(br.readLine());
        int s4 = Integer.parseInt(br.readLine());
        int s5 = Integer.parseInt(br.readLine());
        int s6 = Integer.parseInt(br.readLine());

        int totMarks = s1 + s2 + s3 + s4 + s5 + s6;
        double percentage = (double) totMarks / 600;
        String grade = calculateGrade(percentage);

        PreparedStatement ps = connection.prepareStatement(UPDATE_STUDENT_MARKS);
        ps.setInt(1, totMarks);
        ps.setDouble(2, percentage);
        ps.setString(3, grade);
        ps.setInt(4, rollNo);

        int k = ps.executeUpdate();
        if (k > 0) {
            System.out.println("Student Marks Updated Successfully");
        } else {
            System.err.println("Failed to Update Student Marks");
        }
    }

    private static void deleteStudent(Connection connection, BufferedReader br) throws Exception {
        System.out.println("Enter Student Roll Number: ");
        int rollNo = Integer.parseInt(br.readLine());

        PreparedStatement ps = connection.prepareStatement(GET_STUDENT_BY_ROLLNO);
        ps.setInt(1, rollNo);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            ps = connection.prepareStatement(DELETE_STUDENT);
            ps.setInt(1, rollNo);
            int k = ps.executeUpdate();
            if (k > 0) {
                System.out.println("Student Deleted Successfully");
            } else {
                System.err.println("Failed to Delete Student");
            }
        } else {
            System.err.println("Record Not Found");
        }
    }

    private static String calculateGrade(double percentage) {
        if (percentage >= 90) return "A";
        else if (percentage >= 80) return "B";
        else if (percentage >= 70) return "C";
        else if (percentage >= 60) return "D";
        else return "F";
    }
}
