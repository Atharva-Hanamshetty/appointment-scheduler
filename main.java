import java.sql.*;
import java.util.Scanner;

public class Main {

    static final String URL =
        "jdbc:mysql://localhost:3306/appointmentdb";
    static final String USER = "root";
    static final String PASSWORD = "your_password";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                URL, USER, PASSWORD);

            while (true) {
                System.out.println("\n--- APPOINTMENT SCHEDULING ---");
                System.out.println("1. Add Available Slot");
                System.out.println("2. View Available Slots");
                System.out.println("3. Book Appointment");
                System.out.println("4. Cancel Appointment");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1: {
                        System.out.print("Enter doctor/faculty name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter date (YYYY-MM-DD): ");
                        String date = sc.nextLine();

                        System.out.print("Enter time (HH:MM:SS): ");
                        String time = sc.nextLine();

                        PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO appointments " +
                            "(advisor_name, appointment_date, " +
                            "appointment_time) VALUES (?, ?, ?)");

                        ps.setString(1, name);
                        ps.setDate(2, Date.valueOf(date));
                        ps.setTime(3, Time.valueOf(time));

                        try {
                            ps.executeUpdate();
                            System.out.println("Slot added successfully!");
                        } catch (SQLIntegrityConstraintViolationException e) {
                            System.out.println(
                                "This slot already exists!");
                        }
                        break;
                    }

                    case 2: {
                        PreparedStatement ps = con.prepareStatement(
                            "SELECT * FROM appointments " +
                            "WHERE status = 'AVAILABLE' " +
                            "ORDER BY appointment_date, appointment_time");

                        ResultSet rs = ps.executeQuery();

                        System.out.println("\nAvailable Slots:");

                        while (rs.next()) {
                            System.out.println(
                                "ID: " + rs.getInt("id") +
                                " | Advisor: " + rs.getString("advisor_name") +
                                " | Date: " + rs.getDate("appointment_date") +
                                " | Time: " + rs.getTime("appointment_time"));
                        }
                        break;
                    }

                    case 3: {
                        System.out.print("Enter slot ID: ");
                        int id = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter your name: ");
                        String student = sc.nextLine();

                        PreparedStatement ps = con.prepareStatement(
                            "UPDATE appointments " +
                            "SET student_name = ?, status = 'BOOKED' " +
                            "WHERE id = ? AND status = 'AVAILABLE'");

                        ps.setString(1, student);
                        ps.setInt(2, id);

                        if (ps.executeUpdate() > 0)
                            System.out.println("Appointment booked!");
                        else
                            System.out.println(
                                "Slot unavailable or already booked.");
                        break;
                    }

                    case 4: {
                        System.out.print("Enter appointment ID: ");
                        int id = sc.nextInt();

                        PreparedStatement ps = con.prepareStatement(
                            "UPDATE appointments " +
                            "SET student_name = NULL, status = 'AVAILABLE' " +
                            "WHERE id = ? AND status = 'BOOKED'");

                        ps.setInt(1, id);

                        if (ps.executeUpdate() > 0)
                            System.out.println("Appointment cancelled!");
                        else
                            System.out.println(
                                "Invalid ID or appointment not booked.");
                        break;
                    }

                    case 5:
                        con.close();
                        sc.close();
                        System.out.println("Exiting application.");
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}