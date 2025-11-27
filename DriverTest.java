import java.io.File;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public class DriverTest {
    public static void main(String[] args) {
        System.out.println("=== DIAGNOSTIC TEST ===");
        
        // 1. Check where we are running
        File currentDir = new File(".");
        System.out.println("Current Working Directory: " + currentDir.getAbsolutePath());
        
        // 2. Check for the Jar file explicitly
        File lib = new File("mysql-connector-j-9.5.0.jar"); // Check exact name
        if(lib.exists()) {
            System.out.println("[PASS] Found JAR file: " + lib.getName());
        } else {
            System.out.println("[FAIL] Cannot find 'mysql-connector-j-9.5.0.jar' in this folder.");
            System.out.println("       -> Please verify the EXACT filename.");
        }

        // 3. Try to load the Driver Class
        System.out.println("\nAttempting to load MySQL Driver Class...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[PASS] Driver Class Loaded Successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("[FAIL] Driver Class NOT Found.");
            System.out.println("       -> The JAR is not in the Classpath.");
        }

        // 4. Check Registered Drivers
        System.out.println("\nChecking registered drivers in DriverManager:");
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        boolean found = false;
        while (drivers.hasMoreElements()) {
            Driver d = drivers.nextElement();
            System.out.println(" - " + d.getClass().getName());
            if(d.getClass().getName().contains("mysql")) found = true;
        }
        
        if(!found) {
            System.out.println("[FAIL] No MySQL driver registered.");
        } else {
            System.out.println("[PASS] MySQL driver is ready to use.");
        }
    }
}