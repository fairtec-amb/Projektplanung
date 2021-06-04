package projektplanung.Persistence;

import projektplanung.DateHelper;
import projektplanung.Persistence.Entities.*;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    Connection conn = null;
    boolean connected = false;
    String url;

    public DatabaseHandler(String url) {
        this.url = url;
    }


    public void setupJDBCConnection() {
        try {
            conn = DriverManager.getConnection(url);
            connected = true;

            System.out.println("DatabaseHandler: Connection established");
        } catch (SQLException e) {
            connected = false;
            throw new Error("Problem", e);
        }
    }

    public void closeJDBCConnection() {
        try {
            if (conn != null) {
                conn.close();
                connected = false;
            }
        } catch (SQLException ex) {
            connected = true;
            System.out.println(ex.getMessage());
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean checkIfEmployeeIdExists(int employeeId) {
        ResultSet rs;
        String sqlString =
                "SELECT COUNT(*) AS 'MatchesFound'\n" +
                        "FROM Mitarbeiter\n" +
                        "WHERE M_ID = " + employeeId + "\n" +
                        ";";

        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString);

            return rs.getInt("MatchesFound") == 1;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public Mitarbeiter getEmployeeById(int employeeId) {
        Mitarbeiter employee = null;
        String sqlString =
                "SELECT COUNT(*) AS 'MatchesFound', *\n" +
                        "FROM Mitarbeiter\n" +
                        "WHERE M_ID = " + employeeId + "\n" +
                        ";";
        ResultSet rs;


        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString);

            if (rs.getInt("MatchesFound") == 0) {
                throw new Exception("Mitarbeiter nicht gefunden!");
            } else if (rs.getInt("MatchesFound") > 1) {
                throw new Exception("Interner Fehler: uneindeutige MitarbeiterId");
            }

            employee = new Mitarbeiter();
            employee.setID(rs.getInt("M_ID"));
            employee.setName(rs.getString("M_Name"));
            employee.setVorname(rs.getString("M_Vorname"));
            employee.setStrasse(rs.getString("M_Strasse"));
            employee.setOrt(rs.getString("M_Ort"));
            employee.setPlz(rs.getString("M_PLZ"));
            employee.setMail(rs.getString("M_Mail"));
            employee.setTelefon(rs.getString("M_Telefon"));
        } catch (Exception e) {
            System.out.println("There was a problem while loading the Employeedata!");
        }

        return employee;
    }

    public List<MitarbeiterProjekte> getAllProjectsByEmployee(Mitarbeiter employee) {
        List<MitarbeiterProjekte> list = new ArrayList<MitarbeiterProjekte>();
        ResultSet rs;

        String sqlString =
                "SELECT\n" +
                        "   Projekte.*,\n" +
                        "   MitarbeiterProjekte.*,\n" +
                        "   MitarbeiterRolle.*\n" +
                        "FROM\n" +
                        "   Projekte\n" +
                        "INNER JOIN\n" +
                        "   MitarbeiterProjekte\n" +
                        "ON MitarbeiterProjekte.MP_ProjektID = Projekte.P_ID\n" +
                        "INNER JOIN\n" +
                        "   MitarbeiterRolle\n" +
                        "ON MitarbeiterRolle.MR_ID = MitarbeiterProjekte.MP_MitarbeiterRolle\n" +
                        "WHERE\n" +
                        "   MitarbeiterRolle.MR_MitarbeiterID = " + employee.getID() + "\n" +
                        ";";

        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString);
            while (rs.next()) {
                MitarbeiterProjekte p = new MitarbeiterProjekte(
                        rs.getInt("P_ID"),
                        rs.getString("P_Projektbezeichnung"),
                        rs.getString("P_StartDatum"),
                        rs.getString("P_EndeDatum"),
                        rs.getInt("P_Stunden"),
                        rs.getInt("MP_ID"),
                        rs.getInt("MP_StundenGeleistet")
                );
                list.add(p);
            }
        } catch (SQLException ex) {
            System.out.println("printEx");
            ex.printStackTrace();
        }
        return list;
    }

    public List<MitarbeiterInfo> getAllEmployeeForProject(int project_id) {

        List<MitarbeiterInfo> list = new ArrayList<MitarbeiterInfo>();
        MitarbeiterInfo employeeInfo = null;
        String sqlString =
                "SELECT Mitarbeiter.*, MitarbeiterProjekte.MP_StundenGeleistet \n" +
                        "FROM Mitarbeiter INNER JOIN MitarbeiterRolle \n" +
                        "ON Mitarbeiter.M_ID = MitarbeiterRolle.MR_MitarbeiterID \n" +
                        "INNER JOIN MitarbeiterProjekte \n" +
                        "ON MitarbeiterRolle.MR_ID = MitarbeiterProjekte.MP_MitarbeiterRolle \n" +
                        "WHERE MitarbeiterProjekte.MP_ProjektID = " + project_id + ";";
        ResultSet rs;

        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString);

            while (rs.next()) {

                employeeInfo = new MitarbeiterInfo();
                employeeInfo.setID(rs.getInt("M_ID"));
                employeeInfo.setName(rs.getString("M_Name"));
                employeeInfo.setVorname(rs.getString("M_Vorname"));
                employeeInfo.setStrasse(rs.getString("M_Strasse"));
                employeeInfo.setOrt(rs.getString("M_Ort"));
                employeeInfo.setPlz(rs.getString("M_PLZ"));
                employeeInfo.setMail(rs.getString("M_Mail"));
                employeeInfo.setTelefon(rs.getString("M_Telefon"));
                employeeInfo.setStunden(rs.getInt("MP_StundenGeleistet"));

                list.add(employeeInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem while loading the Employeedata!");
        }
        return list;
    }

    public List<Projekt> getAllProjects() {

        List<Projekt> list = new ArrayList<Projekt>();
        String sqlString =
                "SELECT Projekte.* FROM Projekte;";
        ResultSet rs;

        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString);

            while (rs.next()) {
                Projekt project = new Projekt(rs.getInt("P_ID"),
                        rs.getString("P_Projektbezeichnung"),
                        DateHelper.stringToDate(rs.getString("P_StartDatum")),
                        DateHelper.stringToDate(rs.getString("P_EndeDatum")),
                        rs.getInt("P_Stunden"));

                list.add(project);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem while loading the projects!");
        }
        return list;
    }

    public List<Rolle> getAllRoles() {

        List<Rolle> list = new ArrayList<Rolle>();

        String sqlString =
                "SELECT Rolle.* FROM Rolle;";
        ResultSet rs;

        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString);

            while (rs.next()) {
                Rolle role = new Rolle(rs.getInt("R_ID"),
                        rs.getString("R_Bezeichnung"));

                list.add(role);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem while loading the Roles!");
        }

        return list;
    }

    public void addEmployeeHoursToProject(int mp_id, int eingabe) {
        String selectEmployeeIdSql = "UPDATE MitarbeiterProjekte " +
                "SET MP_StundenGeleistet = " + eingabe + " " +
                "WHERE MitarbeiterProjekte.MP_ID = " + mp_id + ";";
        ResultSet resultSet;

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(selectEmployeeIdSql);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem while updating your time!");
        }
    }

    public boolean createProject(Projekt project) {

        String selectEmployeeIdSql = "INSERT INTO Projekte (P_ProjektBezeichnung, P_StartDatum, P_EndeDatum, P_Stunden) " +
                "VALUES ('" + project.getBezeichnung() + "', '" + project.getStartDatum() + "', '" +
                project.getEndDatum() + "', '" + project.getStunden() + "');";
        ResultSet resultSet;

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(selectEmployeeIdSql);

            return true;

        } catch (Exception e) {
            System.out.println("There was a problem while loading the projects!");
            return false;
        }


    }

    public List<Mitarbeiter> getAllEmployeesWithRoleNotInThisProject(int project_id, int role_id) {

        List<Mitarbeiter> list = new ArrayList<Mitarbeiter>();
        Mitarbeiter employee;
        String sqlString =
                "SELECT\n" +
                        "    Mitarbeiter.*\n" +
                        "FROM\n" +
                        "    Mitarbeiter\n" +
                        "WHERE\n" +
                        "    M_ID NOT IN (\n" +
                        "        SELECT\n" +
                        "            MR_MitarbeiterID\n" +
                        "        FROM\n" +
                        "            MitarbeiterRolle\n" +
                        "        WHERE\n" +
                        "            MR_ID IN (\n" +
                        "                SELECT\n" +
                        "                    MP_MitarbeiterRolle\n" +
                        "                FROM\n" +
                        "                    MitarbeiterProjekte\n" +
                        "                WHERE\n" +
                        "                    MP_ProjektID = " + project_id + "\n" +
                        "            )\n" +
                        "    )\n" +
                        "    AND EXISTS (\n" +
                        "        SELECT\n" +
                        "            1\n" +
                        "        FROM\n" +
                        "            MitarbeiterRolle\n" +
                        "        WHERE\n" +
                        "            MitarbeiterRolle.MR_MitarbeiterID = Mitarbeiter.M_ID\n" +
                        "            AND MitarbeiterRolle.MR_RolleID = " + role_id + "\n" +
                        "    )\n" +
                        ";";
        ResultSet rs;

        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString);

            while (rs.next()) {
                employee = new Mitarbeiter();
                employee.setID(rs.getInt("M_ID"));
                employee.setName(rs.getString("M_Name"));
                employee.setVorname(rs.getString("M_Vorname"));
                employee.setStrasse(rs.getString("M_Strasse"));
                employee.setOrt(rs.getString("M_Ort"));
                employee.setPlz(rs.getString("M_PLZ"));
                employee.setMail(rs.getString("M_Mail"));
                employee.setTelefon(rs.getString("M_Telefon"));

                list.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem while loading the EmployeeList!");
        }
        return list;
    }

    public List<Projekt> getAllProjectsExceptGivenProject(Projekt project) {

        List<Projekt> list = new ArrayList<Projekt>();

        String sqlString =
                "SELECT * FROM Projekte WHERE Projekte.P_ID != " + project.getID() + ";";

        ResultSet rs;

        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString);

            while (rs.next()) {
                project = new Projekt(rs.getInt("P_ID"),
                        rs.getString("P_Projektbezeichnung"),
                        DateHelper.stringToDate(rs.getString("P_StartDatum")),
                        DateHelper.stringToDate(rs.getString("P_EndeDatum")),
                        rs.getInt("P_Stunden"));
                list.add(project);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem while loading the projectlist!");
            return null;
        }

        return list;

    }

    public Projekt getProjectById(int project_id) {

        Projekt project = null;
        String sqlString =
                "SELECT Projekte.* FROM Projekte WHERE Projekte.P_ID = " + project_id + ";";
        ResultSet rs;

        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString);

            project = new Projekt(rs.getInt("P_ID"),
                    rs.getString("P_Projektbezeichnung"),
                    DateHelper.stringToDate(rs.getString("P_StartDatum")),
                    DateHelper.stringToDate(rs.getString("P_EndeDatum")),
                    rs.getInt("P_Stunden"));

        } catch (Exception e) {
            System.out.println("There was a problem while loading the project!");
        }
        return project;
    }

    public List<Mitarbeiter> getAllEmployeeForProjectList(List<Projekt> all_projects_except_given) {
        StringBuilder preList = new StringBuilder();

        for (Projekt p : all_projects_except_given) {
            preList.append(p.getID());
            preList.append(", ");
        }
        preList.append(0);
        String project_list = preList.toString();
        List<Mitarbeiter> list = new ArrayList<Mitarbeiter>();
        Mitarbeiter employee;

        String sqlString =
                "SELECT DISTINCT Mitarbeiter.* FROM Mitarbeiter INNER JOIN MitarbeiterRolle " +
                        "ON Mitarbeiter.M_ID = MitarbeiterRolle.MR_MitarbeiterID " +
                        "INNER JOIN MitarbeiterProjekte " +
                        "ON MitarbeiterProjekte.MP_MitarbeiterRolle = MitarbeiterRolle.MR_MitarbeiterID " +
                        "INNER JOIN Projekte " +
                        "ON MitarbeiterProjekte.MP_ProjektID = Projekte.P_ID " +
                        "WHERE MitarbeiterProjekte.MP_ProjektID IN (" + project_list + ");";
        ResultSet rs;

        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString);

            while (rs.next()) {
                employee = new Mitarbeiter();
                employee.setID(rs.getInt("M_ID"));
                employee.setName(rs.getString("M_Name"));
                employee.setVorname(rs.getString("M_Vorname"));
                employee.setStrasse(rs.getString("M_Strasse"));
                employee.setOrt(rs.getString("M_Ort"));
                employee.setPlz(rs.getString("M_PLZ"));
                employee.setMail(rs.getString("M_Mail"));
                employee.setTelefon(rs.getString("M_Telefon"));

                list.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem while loading the EmployeeList!");
        }

        return list;
    }

    public boolean assignEmployeeWithRoleToProject(int r_id, int employee_id, int project_id, int hours) {

        String sqlSelectString =
                "SELECT MitarbeiterRolle.MR_ID FROM MitarbeiterRolle " +
                        "WHERE MitarbeiterRolle.MR_MitarbeiterID = " + employee_id +
                        " AND MitarbeiterRolle.MR_RolleID = " + r_id + ";";

        int employee_role_id = 0;

        ResultSet rs_employee_role;


        try {
            Statement stmt_select = conn.createStatement();
            rs_employee_role = stmt_select.executeQuery(sqlSelectString);

            employee_role_id = rs_employee_role.getInt("MR_ID");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem while loading the EmployeeRole!");
            return false;
        }

        String sqlInsertString =
                "INSERT INTO MitarbeiterProjekte (MP_ProjektID, MP_MitarbeiterRolle, MP_StundenPlanung) " +
                        "VALUES (" + project_id + ", " + employee_role_id + ", " + hours + ");";

        try {
            Statement stmt_insert = conn.createStatement();
            stmt_insert.executeUpdate(sqlInsertString);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem while inserting the Employee to the Project!");
            return false;
        }
    }

    public boolean addNewEmployee(Mitarbeiter employee) {

        String sqlInsertString =
                "INSERT INTO Mitarbeiter (M_Name, M_Vorname, M_Strasse, M_Ort, M_PLZ, M_Mail, M_Telefon) " +
                        "VALUES ('" + employee.getName() + "', '" + employee.getVorname() + "', '" + employee.getStrasse() +
                        "', '" + employee.getOrt() + "', '" + employee.getPlz() + "', '" + employee.getMail() + "', '" +
                        employee.getTelefon() + "');";

        try {
            Statement stmt_insert = conn.createStatement();
            stmt_insert.executeUpdate(sqlInsertString);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem while inserting the new Employee!");
            return false;
        }
    }

    public boolean addNewRole(Rolle role) {

        String sqlInsertString =
                "INSERT INTO Rolle (R_Bezeichnung) VALUES ('" + role.getRollenbezeichnung() + "');";

        try {
            Statement stmt_insert = conn.createStatement();
            stmt_insert.executeUpdate(sqlInsertString);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem while inserting the new Role!");
            return false;
        }
    }

    public boolean assignRoleToEmployee(int role_id, int employee_id) {

        String sqlInsertString =
                "INSERT INTO MitarbeiterRolle (MR_MitarbeiterID, MR_RolleID) VALUES (" +
                        employee_id + ", " + role_id + ");";

        try {
            Statement stmt_insert = conn.createStatement();
            stmt_insert.executeUpdate(sqlInsertString);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem while assigning the Role to the Employee!");
            return false;
        }

    }

    public List<Mitarbeiter> getAllEmployees() {

        List<Mitarbeiter> list = new ArrayList<Mitarbeiter>();
        Mitarbeiter employee;
        String sqlString =
                "SELECT * FROM Mitarbeiter;";
        ResultSet rs;

        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString);

            while (rs.next()) {
                employee = new Mitarbeiter();
                employee.setID(rs.getInt("M_ID"));
                employee.setName(rs.getString("M_Name"));
                employee.setVorname(rs.getString("M_Vorname"));
                employee.setStrasse(rs.getString("M_Strasse"));
                employee.setOrt(rs.getString("M_Ort"));
                employee.setPlz(rs.getString("M_PLZ"));
                employee.setMail(rs.getString("M_Mail"));
                employee.setTelefon(rs.getString("M_Telefon"));

                list.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem while loading the EmployeeList!");
        }
        return list;
    }

    public List<Rolle> getAllRolesNotAssigned(int employee_id) {

        List<Rolle> list = new ArrayList<Rolle>();

        Rolle role;
        String sqlString =
                "SELECT * FROM Rolle WHERE R_ID NOT IN (SELECT MR_RolleID FROM MitarbeiterRolle " +
                        "WHERE MR_MitarbeiterID = " + employee_id + ");";
        ResultSet rs;

        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString);

            while (rs.next()) {
                role = new Rolle(rs.getInt("R_ID"),
                        rs.getString("R_Bezeichnung"));
                list.add(role);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem while loading the not assigned Roles!");
        }
        return list;
    }

    public boolean updateEmployeeData(@NotNull Mitarbeiter employee)
    {
        String sqlString =
                "UPDATE Mitarbeiter\n" +
                "SET\n" +
                "   M_Name = '" + employee.getName() + "',\n" +
                "   M_Vorname = '" + employee.getVorname() + "',\n" +
                "   M_Strasse = '" + employee.getStrasse() + "',\n" +
                "   M_Ort = '" + employee.getOrt() + "',\n" +
                "   M_PLZ = '" + employee.getPlz() + "',\n" +
                "   M_Mail = '" + employee.getMail() + "',\n" +
                "   M_Telefon = '" + employee.getTelefon() + "'\n" +
                "WHERE Mitarbeiter.M_ID = " + employee.getID() + ";";

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sqlString);
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("There was a problem while updating the employee's information!");
            return false;
        }
        return true;
    }

    public Connection getConn() {
        return conn;
    }
}
