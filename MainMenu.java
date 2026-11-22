/******************** Importing Essential Libraries ************************/

import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;


/*************************** DATABASE CONNECTION ************************/

class DBConnection
{
    // Reads URL, user and password from db.properties (kept out of GitHub)
    public static Connection getConnection() throws SQLException
    {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties"))
        {
            p.load(in);
        }
        catch (IOException e)
        {
            throw new SQLException("Could not read db.properties: " + e.getMessage());
        }
        return DriverManager.getConnection(
                p.getProperty("db.url"),
                p.getProperty("db.user"),
                p.getProperty("db.password"));
    }
}


/*************************** MENU OF EMS ****************************/

class MainMenu
{
    public void menu()
    {
        System.out.println("\t\t*******************************************");
        System.out.println("\t\t\t  EMPLOYEE MANAGEMENT SYSTEM");
        System.out.println("\t\t*******************************************");
        System.out.println("\t\t\t    --------------------");
        System.out.println("\t\t\t     ~$ Yashwanth Bhukya ");
        System.out.println("\t\t\t    --------------------");
        System.out.println("\n\nPress 1 : To Add an Employee Details");
        System.out.println("Press 2 : To See an Employee Details ");
        System.out.println("Press 3 : To Remove an Employee");
        System.out.println("Press 4 : To Update Employee Details");
        System.out.println("Press 5 : To View All Employees");
        System.out.println("Press 6 : To Search Employees by Name");
        System.out.println("Press 7 : To Exit the EMS Portal");
    }
}


/************************* Input Validation ************************/

class Validator
{
    public static boolean validEmail(String e)  { return e.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$"); }
    public static boolean validContact(String c){ return c.matches("\\d{10}"); }
}


/************************* Taking Employee Details ************************/

class EmployDetail
{
    String name;
    String father_name;
    String email;
    String position;
    String employ_id;
    BigDecimal employ_salary;
    String employ_contact;

    private String ask(Scanner sc, String label)
    {
        while (true)
        {
            System.out.print(label);
            String v = sc.nextLine().trim();
            if (!v.isEmpty()) return v;
            System.out.println("This field cannot be empty. Try again.");
        }
    }

    public void getInfo(Scanner sc)
    {
        name        = ask(sc, "Enter Employee's name --------: ");
        father_name = ask(sc, "Enter Employee's Father name -: ");
        employ_id   = ask(sc, "Enter Employee's ID ----------: ");

        while (true)
        {
            email = ask(sc, "Enter Employee's Email ID ----: ");
            if (Validator.validEmail(email)) break;
            System.out.println("Invalid email (example: name@gmail.com). Try again.");
        }

        position = ask(sc, "Enter Employee's Position ----: ");

        while (true)
        {
            employ_contact = ask(sc, "Enter Employee contact Info --: ");
            if (Validator.validContact(employ_contact)) break;
            System.out.println("Contact must be exactly 10 digits. Try again.");
        }

        while (true)
        {
            System.out.print("Enter Employee's Salary ------: ");
            try
            {
                employ_salary = new BigDecimal(sc.nextLine().trim());
                if (employ_salary.signum() >= 0) break;
                System.out.println("Salary cannot be negative. Try again.");
            }
            catch (NumberFormatException e)
            {
                System.out.println("Salary must be a number. Try again.");
            }
        }
    }
}


/************************ To add details of Employee *********************/

class Employee_Add
{
    public void addEmployee(Scanner sc)
    {
        EmployDetail emp = new EmployDetail();
        emp.getInfo(sc);

        String sql = "INSERT INTO employees " +
                "(employee_id, name, father_name, contact, email, position, salary) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1, emp.employ_id);
            ps.setString(2, emp.name);
            ps.setString(3, emp.father_name);
            ps.setString(4, emp.employ_contact);
            ps.setString(5, emp.email);
            ps.setString(6, emp.position);
            ps.setBigDecimal(7, emp.employ_salary);
            ps.executeUpdate();
            System.out.println("\nEmployee has been Added :)\n");
        }
        catch (SQLIntegrityConstraintViolationException e)
        {
            System.out.println("\nEmployee already exists :(");
        }
        catch (SQLException e)
        {
            System.out.println("\nDatabase error: " + e.getMessage());
        }

        System.out.print("\nPress Enter to Continue...");
        sc.nextLine();
    }
}


/************************ To Show details of Employee *********************/

class Employee_Show
{
    public void viewEmployee(String id)
    {
        String sql = "SELECT * FROM employees WHERE employee_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    System.out.println("\nEmployee ID:" + rs.getString("employee_id"));
                    System.out.println("Employee Name     :" + rs.getString("name"));
                    System.out.println("Father's Name     :" + rs.getString("father_name"));
                    System.out.println("Employee Contact  :" + rs.getString("contact"));
                    System.out.println("Email Information :" + rs.getString("email"));
                    System.out.println("Employee position :" + rs.getString("position"));
                    System.out.println("Employee Salary   :" + rs.getBigDecimal("salary"));
                }
                else
                {
                    System.out.println("\nEmployee does not exist :(");
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println("\nDatabase error: " + e.getMessage());
        }
    }
}


/************************ To Show ALL / Search Employees ******************/

class Employee_ShowAll
{
    private static final String LINE = "+----------+----------------------+----------------------+------------+--------------------------+----------------+------------+";
    private static final String ROW  = "| %-8s | %-20s | %-20s | %-10s | %-24s | %-14s | %-10s |%n";

    private void printTable(ResultSet rs) throws SQLException
    {
        System.out.println("\n" + LINE);
        System.out.printf(ROW, "ID", "Name", "Father's Name", "Contact", "Email", "Position", "Salary");
        System.out.println(LINE);

        int count = 0;
        while (rs.next())
        {
            System.out.printf(ROW,
                    rs.getString("employee_id"), rs.getString("name"),
                    rs.getString("father_name"), rs.getString("contact"),
                    rs.getString("email"), rs.getString("position"),
                    rs.getBigDecimal("salary"));
            count++;
        }
        System.out.println(LINE);
        System.out.println("Total employees: " + count);
    }

    public void viewAll()
    {
        String sql = "SELECT * FROM employees ORDER BY employee_id";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql))
        {
            printTable(rs);
        }
        catch (SQLException e)
        {
            System.out.println("\nDatabase error: " + e.getMessage());
        }
    }

    public void searchByName(String keyword)
    {
        String sql = "SELECT * FROM employees WHERE name LIKE ? ORDER BY name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery())
            {
                printTable(rs);
            }
        }
        catch (SQLException e)
        {
            System.out.println("\nDatabase error: " + e.getMessage());
        }
    }
}

/***************************** To Remove Employee *************************/

class Employee_Remove
{
    public void removeEmployee(String id)
    {
        String sql = "DELETE FROM employees WHERE employee_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1, id);
            if (ps.executeUpdate() > 0)
                System.out.println("\nEmployee has been removed Successfully");
            else
                System.out.println("\nEmployee does not exist :( ");
        }
        catch (SQLException e)
        {
            System.out.println("\nDatabase error: " + e.getMessage());
        }
    }
}


/************************ To Update details of Employee ********************/

class Employee_Update
{
    // Returns true if a row was updated
    public boolean updateEmployee(String id, String column, Object newValue)
    {
        // 'column' only ever comes from the fixed list in main(), never from user text
        String sql = "UPDATE employees SET " + column + " = ? WHERE employee_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setObject(1, newValue);
            ps.setString(2, id);
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.out.println("\nDatabase error: " + e.getMessage());
            return false;
        }
    }
}


/************************ To Exit from the EMS Portal *********************/

class CodeExit
{
    public void out()
    {
        System.out.println("\n*****************************************");
        System.out.println("$ cat Thank You For Using my Software :) ");
        System.out.println("*****************************************");
        System.out.println("\t\t\t~ Yashwanth Bhukya\n");
        System.exit(0);
    }
}


/***************************** Main Class *******************************/

class EmployManagementSystem
{
    public static void main(String[] args)
    {
        System.out.print("\033[H\033[2J");

        Scanner sc = new Scanner(System.in);
        Employee_Show epv = new Employee_Show();
        MainMenu obj1 = new MainMenu();
        obj1.menu();

        int i = 0;
        while (i < 8)
        {
            System.out.print("\nPlease Enter choice :");
            try
            {
                i = Integer.parseInt(sc.nextLine().trim());
            }
            catch (NumberFormatException e)
            {
                System.out.println("Please enter a number between 1 and 7.");
                continue;
            }

            switch (i)
            {
                case 1:
                {
                    new Employee_Add().addEmployee(sc);
                    System.out.print("\033[H\033[2J");
                    obj1.menu();
                    break;
                }
                case 2:
                {
                    System.out.print("\nPlease Enter Employee's ID :");
                    String id = sc.nextLine();
                    epv.viewEmployee(id);

                    System.out.print("\nPress Enter to Continue...");
                    sc.nextLine();
                    System.out.print("\033[H\033[2J");
                    obj1.menu();
                    break;
                }
                case 3:
                {
                    System.out.print("\nPlease Enter Employee's ID :");
                    String id = sc.nextLine();
                    new Employee_Remove().removeEmployee(id);

                    System.out.print("\nPress Enter to Continue...");
                    sc.nextLine();
                    System.out.print("\033[H\033[2J");
                    obj1.menu();
                    break;
                }
                case 4:
                {
                    System.out.print("\nPlease Enter Employee's ID :");
                    String id = sc.nextLine();
                    epv.viewEmployee(id);

                    System.out.println("\nWhich detail do you want to update?");
                    System.out.println("1 : Name");
                    System.out.println("2 : Father's Name");
                    System.out.println("3 : Contact");
                    System.out.println("4 : Email");
                    System.out.println("5 : Position");
                    System.out.println("6 : Salary");
                    System.out.print("Enter choice :");

                    String[] columns = {"name", "father_name", "contact", "email", "position", "salary"};
                    int field;
                    try
                    {
                        field = Integer.parseInt(sc.nextLine().trim());
                    }
                    catch (NumberFormatException e)
                    {
                        field = 0;
                    }

                    if (field < 1 || field > 6)
                    {
                        System.out.println("Invalid option.");
                    }
                    else
                    {
                        System.out.print("Please Enter the Updated Info :");
                        String input = sc.nextLine();
                        Object value = input;
                        boolean valid = true;

                        if (field == 3 && !Validator.validContact(input.trim()))
                        {
                            System.out.println("Contact must be exactly 10 digits.");
                            valid = false;
                        }
                        if (field == 4 && !Validator.validEmail(input.trim()))
                        {
                            System.out.println("Invalid email.");
                            valid = false;
                        }
                        if (field == 6)
                        {
                            try
                            {
                                BigDecimal sal = new BigDecimal(input.trim());
                                if (sal.signum() < 0) { System.out.println("Salary cannot be negative."); valid = false; }
                                value = sal;
                            }
                            catch (NumberFormatException e)
                            {
                                System.out.println("Salary must be a number.");
                                valid = false;
                            }
                        }

                        if (valid)
                        {
                            if (new Employee_Update().updateEmployee(id, columns[field - 1], value))
                                System.out.println("\nEmployee updated Successfully :)");
                            else
                                System.out.println("\nUpdate failed (employee may not exist).");
                        }
                    }

                    System.out.print("\nPress Enter to Continue...");
                    sc.nextLine();
                    System.out.print("\033[H\033[2J");
                    obj1.menu();
                    break;
                }
                case 5:
                {
                    new Employee_ShowAll().viewAll();

                    System.out.print("\nPress Enter to Continue...");
                    sc.nextLine();
                    System.out.print("\033[H\033[2J");
                    obj1.menu();
                    break;
                }
                case 6:
                {
                    System.out.print("\nEnter name (or part of a name) to search :");
                    String keyword = sc.nextLine().trim();
                    new Employee_ShowAll().searchByName(keyword);

                    System.out.print("\nPress Enter to Continue...");
                    sc.nextLine();
                    System.out.print("\033[H\033[2J");
                    obj1.menu();
                    break;
                }
                case 7:
                {
                    new CodeExit().out();
                }
            }
        }
    }
}

/****************************** CODED BY YASHWANTH BHUKYA ************************/