import java.nio.channels.ScatteringByteChannel;
import java.sql.*;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);

    private static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:C:/Users/Danie/OneDrive/Desktop/SQL Java24/JDBCSQLite/SQLiteJava24.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void printMenu() {
        System.out.println("\nVälj\n");
        System.out.println(
                "0  - Avsluta programmet\n" +
                        "1  - Visa alla tränare\n" +
                        "2  - Visa alla pass\n" +
                        "3  - Visa pass med tränare\n" +
                        "4  - Sök efter pass\n" +
                        "5  - Visa favoriter\n" +
                        "6  - Lägg till tränare\n" +
                        "7  - Uppdatera tränare\n" +
                        "8  - Ta bort tränare\n" +
                        "9  - Lägg till klass\n" +
                        "10 - Markera favoritpass");
    }

    public static void main(String[] args) {

        boolean quit = false;
        printMenu();
        while (!quit) {
            System.out.println("\nAnge ett val:");
            int action = scanner.nextInt();
            scanner.nextLine();

            switch (action) {
                case 0:
                    System.out.println("\nProgrammet avslutas...");
                    quit = true;
                    break;

                case 1:
                    getAllTrainers();
                    break;

                case 2:
                    getAllClasses();
                    break;

                case 3:
                    viewClassesWithTrainers();
                    break;

                case 4:
                    searchClasses();
                    break;

                case 5:
                    favoriteStatistic();
                    break;

                case 6:
                    inputAddTrainer();

                    break;

                case 7:
                    inputUpdateTrainer();

                    break;

                case 8:
                    deleteTrainer();
                    break;

                case 9:
                    inputAddClass();
                    break;

                case 10:
                    markClassAsFavorite();
                    break;

                default:
                    System.out.println("Ogiltigt val försök igen!");
                    break;
            }
        }

    }


    private static void getAllTrainers() {
        String sql = "SELECT * FROM Trainers";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("TrainerId | Namn | Specialisering | Email | Erfarenhet");
            while (rs.next()) {
                System.out.println(rs.getInt("trainerId") + " | " +
                        rs.getString("trainerName") + " | " +
                        rs.getString("trainerSpecialization") + " | " +
                        rs.getString("trainerEmail") + " | " +
                        rs.getDouble("trainerYearsOfExpertice"));
            }
        } catch (SQLException e) {
            System.out.println("Fel vid hämtning av tränare: " + e.getMessage());
        }
    }

    private static void inputAddTrainer() {
        System.out.print("Ange tränarens namn: ");
        String name = scanner.nextLine();

        System.out.println("Ange tränarens specialisering:");
        String specialization = scanner.nextLine();

        System.out.println("Ange tränarens e-postadress:");
        String email = scanner.nextLine();

        System.out.println("Ange tränarens antal års erfarenhet:");
        double experience = Double.parseDouble(scanner.nextLine());

        addTrainer(name, specialization, email, experience);
    }

    private static void addTrainer(String name, String specialization, String email, double experience) {
        String sql = "INSERT INTO Trainers(trainerName, trainerSpecialization, trainerEmail, trainerYearsOfExpertice) VALUES(?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, specialization);
            pstmt.setString(3, email);
            pstmt.setDouble(4, experience);
            pstmt.executeUpdate();
            System.out.println("Ny tränare tillagd.");
        } catch (SQLException e) {
            System.out.println("Fel vid tilläg av tränare" + e.getMessage());
        }

    }

    public static void inputUpdateTrainer(){
            System.out.println("Ange id för tränaren som ska uppdateras");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.println("Ange nytt namn:");
            String name = scanner.nextLine();

            System.out.println("Ange ny specialisering:");
            String specialization = scanner.nextLine();

            System.out.println("Ange ny e-postadress:");
            String email = scanner.nextLine();

            System.out.println("Ange antal års erfarenhet:");
            double experience = Double.parseDouble(scanner.nextLine());

            updateTrainer(name, specialization, email, experience, id);
        }


    public static void updateTrainer(String name, String specialization, String email, double experience, int id) {
        String sql = "UPDATE Trainers SET trainerName = ?, trainerSpecialization = ?, trainerEmail = ?, trainerYearsOfExpertice = ? WHERE trainerId = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, specialization);
            pstmt.setString(3, email);
            pstmt.setDouble(4, experience);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
            System.out.println("Tränare uppdaterad.");
        } catch (SQLException e) {
            System.out.println("Fel vid uppdatering av tränare: " + e.getMessage());
        }
    }

    private static void deleteTrainer() {
        System.out.println("Ange id för tränare som ska tas bort");
        int id = Integer.parseInt(scanner.nextLine());

        String sql = "DELETE FROM Trainers WHERE trainerId = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Tränare borttagen.");
        } catch (SQLException e) {
            System.out.println("Fel vid borttagning av tränare: " + e.getMessage());
        }
    }


    private static void viewClassesWithTrainers() {
        String sql = "SELECT Classes.classId, Classes.className, Classes.classSchedule, Trainers.trainerName " +
                "FROM Classes " +
                "INNER JOIN Trainers ON Classes.classesTrainerId = Trainers.trainerId";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("Klass ID: " + rs.getInt("classId") +
                        ", Namn: " + rs.getString("className") +
                        ", Schema: " + rs.getString("classSchedule") +
                        ", Tränare: " + rs.getString("trainerName"));
            }
        } catch (SQLException e) {
            System.out.println("Fel vid hämtning av klasser: " + e.getMessage());
        }
    }

    private static void inputAddClass() {
        System.out.println("Ange klassens namn");
        String className = scanner.nextLine();

        System.out.println("Ange klassens schema");
        String classSchedule = scanner.nextLine();

        System.out.println("Ange tränarens ID som ska tilldelas klassen");
        int trainerId = Integer.parseInt(scanner.nextLine());

        System.out.println("Ange klassens kapacitet");
        int classCapacity = Integer.parseInt(scanner.nextLine());

        addClass(className, classSchedule, trainerId, classCapacity);
    }

    private static void addClass(String className, String classSchedule, int trainerId, int classCapacity) {
        String sql = "INSERT INTO Classes(className, classSchedule, classesTrainerId, classCapacity) VALUES(?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, className);
            pstmt.setString(2, classSchedule);
            pstmt.setInt(3, trainerId);
            pstmt.setInt(4, classCapacity);

            pstmt.executeUpdate();
            System.out.println("Ny klass tillagd.");
        } catch (SQLException e) {
            System.out.println("Fel vid tillägg av klass: " + e.getMessage());
        }
    }

    private static void getAllClasses() {
        String sql = "SELECT * FROM Classes";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("KlassId | KlassNamn | KlassSchema | KlassKapacitet | Favorit");
            while (rs.next()) {
                int isFavorite = rs.getInt("isFavorite");
                String favoriteStatus = (isFavorite == 1) ? "Ja" : "Nej";

                System.out.println(rs.getInt("ClassId") + " | " +
                        rs.getString("ClassName") + " | " +
                        rs.getString("classSchedule") + " | " +
                        rs.getInt("classCapacity") + " | " +
                        favoriteStatus);
            }
        } catch (SQLException e) {
            System.out.println("Fel vid hämtning av Klasser: " + e.getMessage());
        }
    }

    private static void searchClasses() {
        System.out.println("Ange passet du vill söka efter:");
        String className = scanner.nextLine().toUpperCase();

        String sql = "SELECT * FROM Classes WHERE UPPER(className) LIKE ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + className + "%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                do {
                    System.out.println("Klass ID: " + rs.getInt("classId") +
                            ", Namn: " + rs.getString("className") +
                            ", Schema: " + rs.getString("classSchedule") +
                            ", Kapacitet: " + rs.getInt("classCapacity"));
                } while (rs.next());
            } else {
                System.out.println("Inga pass hittades med det namnet.");
            }
        } catch (SQLException e) {
            System.out.println("Fel vid sökning: " + e.getMessage());
        }
    }

    private static void markClassAsFavorite() {
        System.out.println("Ange ID för det pass som ska markeras som favorit:");
        int classId = Integer.parseInt(scanner.nextLine());

        String sql = "UPDATE Classes SET isFavorite = 1 WHERE classId = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, classId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Passet har markerats som favorit!");
            } else {
                System.out.println("Inget pass hittades med angivna ID.");
            }
        } catch (SQLException e) {
            System.out.println("Fel vid uppdatering " + e.getMessage());
        }

    }

    private static void favoriteStatistic(){
        String sql = "SELECT COUNT(*) AS favoriteCount FROM Classes WHERE isFavorite = 1";

        try(Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int favoriteCount = rs.getInt("favoriteCount");
                System.out.println("Antal favoritpass: " + favoriteCount);
            }
        }catch (SQLException e) {
            System.out.println("Fel vid hämtning av statistik: " + e.getMessage());
        }
    }
}

