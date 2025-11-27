import java.sql.*;
import java.util.Scanner;


// --- 1. ABSTRACTION & ENCAPSULATION ---
// Abstract base class representing a generic Account
abstract class Account {
    // Encapsulation: Private fields with protected/public accessors
    protected int accountNumber;
    protected String holderName;
    protected double balance;

    public Account(int accountNumber, String holderName, double balance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
    }

    // Abstract method enforcing Polymorphism (subclasses must implement this)
    public abstract void deposit(double amount);
    public abstract boolean withdraw(double amount);

    public double getBalance() { return balance; }
    public int getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
}

// --- 2. INHERITANCE ---
// Concrete class extending the abstract Account class
class SavingsAccount extends Account {

    public SavingsAccount(int accountNumber, String holderName, double balance) {
        super(accountNumber, holderName, balance);
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            System.out.println("Success: Deposited $" + amount);
        } else {
            System.out.println("Error: Invalid deposit amount.");
        }
    }

    // Polymorphism in action: Specific withdrawal logic for Savings
    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            System.out.println("Success: Withdrawn $" + amount);
            return true;
        } else {
            System.out.println("Error: Insufficient funds or invalid amount.");
            return false;
        }
    }
}

// --- 3. DATABASE CONNECTIVITY (JDBC) ---
class DatabaseHandler {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/atm_db";
    private static final String USER = "USERNAM"; // CHANGE THIS
    private static final String PASS = "PASSWORD"; // CHANGE THIS

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // Create Operation
    public static int createAccount(String name, String pin) {
        String sql = "INSERT INTO accounts (holder_name, pin, balance) VALUES (?, ?, 0.0)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, pin);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    // Read Operation (Authentication)
    public static SavingsAccount login(int accNum, String pin) {
        String sql = "SELECT * FROM accounts WHERE account_number = ? AND pin = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accNum);
            pstmt.setString(2, pin);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new SavingsAccount(
                    rs.getInt("account_number"),
                    rs.getString("holder_name"),
                    rs.getDouble("balance")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // Update Operation (Balance)
    public static void updateBalance(int accNum, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setInt(2, accNum);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Log Transaction (Part of Create/Update)
    public static void logTransaction(int accNum, String type, double amount) {
        String sql = "INSERT INTO transactions (account_number, type, amount) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accNum);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Read History
    public static void printTransactionHistory(int accNum) {
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY timestamp DESC LIMIT 5";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accNum);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\n--- Last 5 Transactions ---");
            while (rs.next()) {
                System.out.printf("%s | %s | $%.2f%n", 
                    rs.getTimestamp("timestamp"), rs.getString("type"), rs.getDouble("amount"));
            }
            System.out.println("---------------------------");
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    // Delete Operation
    public static void deleteAccount(int accNum) {
         String sql = "DELETE FROM accounts WHERE account_number = ?";
         try (Connection conn = getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setInt(1, accNum);
             pstmt.executeUpdate();
             System.out.println("Account deleted successfully.");
         } catch (SQLException e) { e.printStackTrace(); }
    }
}

// --- 4. MAIN INTERFACE ---
public class ATMSystem {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== JAVA ATM INTERFACE ===");
            System.out.println("1. Login");
            System.out.println("2. Create New Account");
            System.out.println("3. Exit");
            System.out.print("Select Option: ");
            
            int choice = scanner.nextInt();
            switch (choice) {
                case 1: loginScreen(); break;
                case 2: createAccountScreen(); break;
                case 3: 
                    System.out.println("Exiting System. Goodbye!");
                    System.exit(0);
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void createAccountScreen() {
        System.out.print("Enter Name: ");
        scanner.nextLine(); // Consume newline
        String name = scanner.nextLine();
        System.out.print("Set 4-Digit PIN: ");
        String pin = scanner.next();
        
        int accNum = DatabaseHandler.createAccount(name, pin);
        if (accNum != -1) {
            System.out.println("Account Created! Your Account Number is: " + accNum);
        } else {
            System.out.println("Error creating account.");
        }
    }

    private static void loginScreen() {
        System.out.print("Enter Account Number: ");
        int accNum = scanner.nextInt();
        System.out.print("Enter PIN: ");
        String pin = scanner.next();

        // Fetch generic Account object from DB
        SavingsAccount userAccount = DatabaseHandler.login(accNum, pin);

        if (userAccount != null) {
            System.out.println("Welcome, " + userAccount.getHolderName());
            userMenu(userAccount);
        } else {
            System.out.println("Invalid Credentials.");
        }
    }

    private static void userMenu(SavingsAccount account) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transaction History");
            System.out.println("5. Delete Account");
            System.out.println("6. Logout");
            System.out.print("Choose: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Current Balance: $" + account.getBalance());
                    break;
                case 2:
                    System.out.print("Enter Amount to Deposit: ");
                    double depAmt = scanner.nextDouble();
                    account.deposit(depAmt); // Modifies object state
                    DatabaseHandler.updateBalance(account.getAccountNumber(), account.getBalance()); // Sync DB
                    DatabaseHandler.logTransaction(account.getAccountNumber(), "DEPOSIT", depAmt);
                    break;
                case 3:
                    System.out.print("Enter Amount to Withdraw: ");
                    double withAmt = scanner.nextDouble();
                    if (account.withdraw(withAmt)) {
                        DatabaseHandler.updateBalance(account.getAccountNumber(), account.getBalance());
                        DatabaseHandler.logTransaction(account.getAccountNumber(), "WITHDRAW", withAmt);
                    }
                    break;
                case 4:
                    DatabaseHandler.printTransactionHistory(account.getAccountNumber());
                    break;
                case 5:
                    System.out.print("Are you sure? (yes/no): ");
                    String confirm = scanner.next();
                    if(confirm.equalsIgnoreCase("yes")) {
                        DatabaseHandler.deleteAccount(account.getAccountNumber());
                        loggedIn = false;
                    }
                    break;
                case 6:
                    loggedIn = false;
                    System.out.println("Logged out.");
                    break;
            }
        }
    }
}