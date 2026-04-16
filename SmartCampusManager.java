import java.util.*;
import java.io.*;

// 1. Custom Exception
class InvalidCampusDataException extends Exception {
    public InvalidCampusDataException(String message) {
        super(message);
    }
}

// 2. Student Class
class CampusStudent implements Serializable {
    private int studentId;
    private String fullName;
    private String contactEmail;

    public CampusStudent(int id, String name, String email) throws InvalidCampusDataException {
        if (!email.contains("@")) {
            throw new InvalidCampusDataException("Invalid email format!");
        }
        this.studentId = id;
        this.fullName = name;
        this.contactEmail = email;
    }

    public int getStudentId() { return studentId; }
    public String getFullName() { return fullName; }

    @Override
    public String toString() {
        return "👤 ID: " + studentId + " | Name: " + fullName + " | Email: " + contactEmail;
    }
}

// 3. Course Class
class CampusCourse implements Serializable {
    private int courseId;
    private String title;
    private double tuitionFee;

    public CampusCourse(int id, String title, double fee) throws InvalidCampusDataException {
        if (fee < 0) throw new InvalidCampusDataException("Fee cannot be negative!");
        this.courseId = id;
        this.title = title;
        this.tuitionFee = fee;
    }

    public int getCourseId() { return courseId; }
    public String getTitle() { return title; }

    @Override
    public String toString() {
        return "📘 Course ID: " + courseId + " | Title: " + title + " | Fee: ₹" + tuitionFee;
    }
}

// 4. Multithreading Task
class AsyncEnrollmentTask implements Runnable {
    private String studentName;
    private String courseName;
    private boolean isPriority;

    public AsyncEnrollmentTask(String studentName, String courseName, boolean isPriority) {
        this.studentName = studentName;
        this.courseName = courseName;
        this.isPriority = isPriority;
    }

    @Override
    public void run() {
        try {
            System.out.println("⏳ " + (isPriority ? "[PRIORITY] " : "") +
                    "Processing enrollment for " + studentName + " → " + courseName);

            Thread.sleep(isPriority ? 500 : 2000);

            System.out.println("✅ Completed: " + studentName + " enrolled in " + courseName);
        } catch (InterruptedException e) {
            System.out.println("❌ Interrupted for " + studentName);
        }
    }
}

// 5. Main System
public class SmartCampusManager {

    private static HashMap<Integer, CampusStudent> students = new HashMap<>();
    private static HashMap<Integer, CampusCourse> courses = new HashMap<>();
    private static HashMap<Integer, List<CampusCourse>> enrollments = new HashMap<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        System.out.println("🎓 Smart Campus Management System");

        while (running) {
            System.out.println("\n1.Add Student  2.Add Course  3.Enroll  4.View Students  5.View Enrollments  6.Process  7.Exit");
            System.out.print("Choose: ");

            try {
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {

                    case 1:
                        System.out.print("ID: ");
                        int sid = Integer.parseInt(sc.nextLine());

                        if (students.containsKey(sid))
                            throw new InvalidCampusDataException("Student ID already exists!");

                        System.out.print("Name: ");
                        String name = sc.nextLine();

                        System.out.print("Email: ");
                        String email = sc.nextLine();

                        students.put(sid, new CampusStudent(sid, name, email));
                        System.out.println("✅ Student added.");
                        break;

                    case 2:
                        System.out.print("Course ID: ");
                        int cid = Integer.parseInt(sc.nextLine());

                        if (courses.containsKey(cid))
                            throw new InvalidCampusDataException("Course ID already exists!");

                        System.out.print("Title: ");
                        String title = sc.nextLine();

                        System.out.print("Fee: ");
                        double fee = Double.parseDouble(sc.nextLine());

                        courses.put(cid, new CampusCourse(cid, title, fee));
                        System.out.println("✅ Course added.");
                        break;

                    case 3:
                        System.out.print("Student ID: ");
                        int s = Integer.parseInt(sc.nextLine());

                        System.out.print("Course ID: ");
                        int c = Integer.parseInt(sc.nextLine());

                        if (!students.containsKey(s))
                            throw new InvalidCampusDataException("Student not found!");

                        if (!courses.containsKey(c))
                            throw new InvalidCampusDataException("Course not found!");

                        enrollments.putIfAbsent(s, new ArrayList<>());
                        enrollments.get(s).add(courses.get(c));

                        System.out.println("📌 Enrollment staged.");
                        break;

                    case 4:
                        students.values().forEach(System.out::println);
                        break;

                    case 5:
                        for (int id : enrollments.keySet()) {
                            System.out.println("👤 " + students.get(id).getFullName());
                            for (CampusCourse course : enrollments.get(id)) {
                                System.out.println("   ➜ " + course.getTitle());
                            }
                        }
                        break;

                    case 6:
                        System.out.print("Student ID: ");
                        int pid = Integer.parseInt(sc.nextLine());

                        System.out.print("Priority (true/false): ");
                        boolean priority = Boolean.parseBoolean(sc.nextLine());

                        if (!enrollments.containsKey(pid)) {
                            System.out.println("No enrollments found.");
                            break;
                        }

                        for (CampusCourse course : enrollments.get(pid)) {
                            new Thread(new AsyncEnrollmentTask(
                                    students.get(pid).getFullName(),
                                    course.getTitle(),
                                    priority)).start();
                        }
                        break;

                    case 7:
                        running = false;
                        System.out.println("👋 Exiting...");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }

            } catch (InvalidCampusDataException e) {
                System.out.println("⚠️ " + e.getMessage());
            } catch (Exception e) {
                System.out.println("⚠️ Invalid input!");
            }
        }
        sc.close();
    }
}