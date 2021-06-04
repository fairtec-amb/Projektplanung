package projektplanung.Persistence;

import projektplanung.DateHelper;
import projektplanung.Persistence.Entities.Mitarbeiter;
import projektplanung.Persistence.Entities.Projekt;
import projektplanung.Persistence.Entities.Rolle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class DatabaseHandlerTest {
    // final String prodDbUrl = "jdbc:sqlite:database/release.sqlite";
    final String testDbUrl = "jdbc:sqlite:database/test.sqlite";

    private DatabaseHandler createDbHandler(String dbUrl) {
        DatabaseHandler dbh = new DatabaseHandler(dbUrl);
        dbh.setupJDBCConnection();

        return dbh;
    }

    private void addEmployeeToDatabase(DatabaseHandler dbh) {
        String sqlString = "INSERT INTO Mitarbeiter VALUES ( 1, 'Test', 'TestV', 'Strasse1'," +
                " 'TestOrt', '1234', 'test@test.de', '56789');";

        executeDMLDatabaseStatement(dbh, sqlString);
    }

    private void addProjectToDatabase(DatabaseHandler dbh) {
        String sqlString = "INSERT INTO Projekte VALUES ( 1, 'TestProjekt', '2021-01-01', '2021-01-09'," +
                " 100);";

        executeDMLDatabaseStatement(dbh, sqlString);
    }

    private void addRoleToDatabase(DatabaseHandler dbh) {
        String sqlString = "INSERT INTO Rolle VALUES ( 1, 'TestRolle');";

        executeDMLDatabaseStatement(dbh, sqlString);
    }

    private void addEmployeeRoleToDatabase(DatabaseHandler dbh) {
        String sqlString = "INSERT INTO MitarbeiterRolle VALUES ( 1, 1, 1);";

        executeDMLDatabaseStatement(dbh, sqlString);
    }

    private void addEmployeeProjectToDatabase(DatabaseHandler dbh) {
        String sqlString = "INSERT INTO MitarbeiterProjekte VALUES ( 1, 1, 1, 10, 5);";

        executeDMLDatabaseStatement(dbh, sqlString);
    }

    private void executeDMLDatabaseStatement(DatabaseHandler dbh, String sqlString) {
        Connection conn = dbh.getConn();

        try {
            Statement stmnt = conn.createStatement();
            stmnt.executeUpdate(sqlString);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ResultSet executeSQLDatabaseStatement(DatabaseHandler dbh, String sqlString) {
        Connection conn = dbh.getConn();
        ResultSet rs = null;
        try {
            Statement stmnt = conn.createStatement();
            rs = stmnt.executeQuery(sqlString);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    private void setupDefaultTestDatabase(DatabaseHandler dbh) {
        addEmployeeToDatabase(dbh);
        addRoleToDatabase(dbh);
        addProjectToDatabase(dbh);
        addEmployeeRoleToDatabase(dbh);
        addEmployeeProjectToDatabase(dbh);
    }

    @BeforeEach
    public void clearTestDb() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);
        Connection conn = dbh.getConn();

        String selectSql = "DELETE FROM Mitarbeiter;" +
                "DELETE FROM MitarbeiterRolle;" +
                "DELETE FROM MitarbeiterProjekte;" +
                "DELETE FROM Projekte;" +
                "DELETE FROM Rolle;";
        ResultSet resultSet;

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(selectSql);

        } catch (Exception e) {
            e.printStackTrace();
        }

        dbh.closeJDBCConnection();
    }

    @Test
    public void CheckIfDatabaseConnectionIsTrue() {
        DatabaseHandler databaseHandler = new DatabaseHandler(testDbUrl);
        databaseHandler.setupJDBCConnection();

        assertEquals(true, databaseHandler.isConnected());
        databaseHandler.closeJDBCConnection();

    }

    @Test
    public void CheckIfDatabaseConnectionIsFalse() {
        Error error = assertThrows(Error.class, () -> {
            DatabaseHandler databaseHandler = new DatabaseHandler("123");
            databaseHandler.setupJDBCConnection();
        });

        assertEquals("Problem", error.getMessage());
    }

    @Test
    public void CheckIfDatabaseConnectionDisconnects() {
        DatabaseHandler databaseHandler = new DatabaseHandler(testDbUrl);
        databaseHandler.setupJDBCConnection();
        databaseHandler.closeJDBCConnection();

        assertEquals(false, databaseHandler.isConnected());
    }

    @Test
    public void TestCheckIfEmployeeIdExistsTrue() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);
        addEmployeeToDatabase(dbh);

        assertTrue(dbh.checkIfEmployeeIdExists(1));

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestCheckIfEmployeeIdExistsFalse() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        assertFalse(dbh.checkIfEmployeeIdExists(0));

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetEmployeeByIdSuccess() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        addEmployeeToDatabase(dbh);

        Mitarbeiter m = dbh.getEmployeeById(1);
        assertEquals(1, m.getID());

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetEmployeeByIdFail() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        assertNull(dbh.getEmployeeById(0));

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetAllProjectsByEmployeeWithProjects() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        setupDefaultTestDatabase(dbh);

        Mitarbeiter m = dbh.getEmployeeById(1);
        List list = dbh.getAllProjectsByEmployee(m);
        System.out.println("TestGetAllProjectsByEmployee: size: " + list.size());

        assertEquals(1, list.size());

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetAllProjectsByEmployeeWithoutAProject() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        setupDefaultTestDatabase(dbh);

        executeDMLDatabaseStatement(dbh, "INSERT INTO Mitarbeiter (M_ID, M_Name) VALUES (2, 'Employee2');");

        Mitarbeiter m = dbh.getEmployeeById(2);
        List list = dbh.getAllProjectsByEmployee(m);
        System.out.println("TestGetAllProjectsByEmployee: size: " + list.size());

        assertEquals(0, list.size());

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestgetAllEmployeeForProjectWithAssignedEmployee() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        setupDefaultTestDatabase(dbh);

        List list = dbh.getAllEmployeeForProject(1);

        assertEquals(1, list.size());

        dbh.closeJDBCConnection();

    }

    @Test
    public void TestgetAllEmployeeForProjectWithNoExistingProject() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        List list = dbh.getAllEmployeeForProject(0);

        assertEquals(0, list.size());

        dbh.closeJDBCConnection();

    }

    @Test
    public void TestgetAllEmployeeForProjectWithNoAssignedEmployees() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        addProjectToDatabase(dbh);

        List list = dbh.getAllEmployeeForProject(1);

        assertEquals(0, list.size());

        dbh.closeJDBCConnection();

    }


    @Test
    public void TestGetAllProjectsWithExistingProject() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        setupDefaultTestDatabase(dbh);

        List<Projekt> list = dbh.getAllProjects();

        assertEquals(1, list.size());
        assertEquals(1, list.get(0).getID());

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetAllProjectsWithNoExistingProject() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        List<Projekt> list = dbh.getAllProjects();

        assertEquals(0, list.size());

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetAllRolesWithExistingRoles() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);
        setupDefaultTestDatabase(dbh);

        List<Rolle> list = dbh.getAllRoles();

        assertEquals(1, list.size());
        assertEquals(1, list.get(0).getR_ID());

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetAllRolesWithNoExistingRoles() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);
        List<Rolle> list = dbh.getAllRoles();

        assertEquals(0, list.size());

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestAddEmployeeHoursToProject() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);
        setupDefaultTestDatabase(dbh);
        int m_id = 0;
        int hours = 0;

        dbh.addEmployeeHoursToProject(1, 15);

        String sqlString = "SELECT MitarbeiterProjekte.MP_StundenGeleistet, " +
                "MitarbeiterRolle.MR_MitarbeiterID " +
                "FROM MitarbeiterProjekte INNER JOIN MitarbeiterRolle " +
                "ON MitarbeiterProjekte.MP_MitarbeiterRolle = MitarbeiterRolle.MR_ID " +
                "WHERE MitarbeiterRolle.MR_MitarbeiterID = 1;";

        ResultSet rs = executeSQLDatabaseStatement(dbh, sqlString);

        try {
            m_id = rs.getInt("MR_MitarbeiterID");
            hours = rs.getInt("MP_StundenGeleistet");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        assertEquals(1, m_id);
        assertEquals(15, hours);

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestAddNewRole() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);
        addRoleToDatabase(dbh);

        String sqlString = "SELECT * FROM Rolle;";

        ResultSet rs = executeSQLDatabaseStatement(dbh, sqlString);

        int r_id = 0;
        String r_bez = "";


        try {
            r_id = rs.getInt("R_ID");
            r_bez = rs.getString("R_Bezeichnung");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        assertEquals(1, r_id);
        assertEquals("TestRolle", r_bez);

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetProjectByIdThatExists() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);
        setupDefaultTestDatabase(dbh);

        Projekt project = dbh.getProjectById(1);

        assertEquals(1, project.getID());
        assertEquals("TestProjekt", project.getBezeichnung());
        assertEquals("2021-01-01", project.getStartDatum());
        assertEquals("2021-01-09", project.getEndDatum());
        assertEquals(100, project.getStunden());

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetProjectByIdThatDoesntExist() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);
        Projekt project = dbh.getProjectById(1);

        assertNull(project);

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestAddNewEmployee() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);
        Mitarbeiter employee = new Mitarbeiter();

        employee.setID(1);
        employee.setName("Nachname");
        employee.setVorname("Vorname");
        employee.setStrasse("Test123");
        employee.setPlz("1234");
        employee.setOrt("Ort");
        employee.setMail("Mail");
        employee.setTelefon("Telefon");

        dbh.addNewEmployee(employee);

        String sqlString = "SELECT *, COUNT(*) AS 'MatchesFound' FROM Mitarbeiter;";

        List<Mitarbeiter> list = new ArrayList<Mitarbeiter>();

        ResultSet rs = executeSQLDatabaseStatement(dbh, sqlString);

        int matches = 0;
        String m_name = null;
        String m_vname = null;
        String m_strasse = null;
        String m_plz = null;
        String m_ort = null;
        String m_mail = null;
        String m_tel = null;


        try {
            m_name = rs.getString("M_Name");
            m_vname = rs.getString("M_Vorname");
            m_strasse = rs.getString("M_Strasse");
            m_plz = rs.getString("M_PLZ");
            m_ort = rs.getString("M_Ort");
            m_mail = rs.getString("M_Mail");
            m_tel = rs.getString("M_Telefon");
            matches = rs.getInt("MatchesFound");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        assertEquals(employee.getName(), m_name);
        assertEquals(employee.getVorname(), m_vname);
        assertEquals(employee.getStrasse(), m_strasse);
        assertEquals(employee.getPlz(), m_plz);
        assertEquals(employee.getOrt(), m_ort);
        assertEquals(employee.getMail(), m_mail);
        assertEquals(employee.getTelefon(), m_tel);
        assertEquals(1, matches);

        dbh.closeJDBCConnection();

    }

    @Test
    public void TestGetAllEmployees() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);
        List<Mitarbeiter> list = dbh.getAllEmployees();

        assertEquals(0, list.size());
        setupDefaultTestDatabase(dbh);

        list = dbh.getAllEmployees();
        assertEquals(1, list.size());

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestAssignRoleToEmployee() {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        addRoleToDatabase(dbh);
        addEmployeeToDatabase(dbh);

        dbh.assignRoleToEmployee(1, 1);

        String sqlString = "SELECT * FROM MitarbeiterRolle;";

        ResultSet rs = executeSQLDatabaseStatement(dbh, sqlString);

        int m_id = 0;
        int r_id = 0;

        try {
            m_id = rs.getInt("MR_MitarbeiterID");
            r_id = rs.getInt("MR_RolleID");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        assertEquals(1, m_id);
        assertEquals(1, r_id);

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestCreateProject()
    {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        Projekt p = null;

        p = new Projekt("TestBezeichnung",
                DateHelper.stringToDate("2021-01-01"),
                DateHelper.stringToDate("2021-01-03"),
                10);

        boolean check = dbh.createProject(p);
        assertEquals(check, true);


        String sqlString = "SELECT * FROM Projekte;";

        ResultSet rs = executeSQLDatabaseStatement(dbh, sqlString);

        String p_bez = null;
        String p_start = null;
        String p_end = null;
        int p_std = 0;

        try {
            p_bez = rs.getString("P_Projektbezeichnung");
            p_start = rs.getString("P_StartDatum");
            p_end = rs.getString("P_EndeDatum");
            p_std = rs.getInt("P_Stunden");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        assertEquals(p.getBezeichnung(), p_bez);
        assertEquals(p.getStartDatum(), p_start);
        assertEquals(p.getEndDatum(), p_end);
        assertEquals(p.getStunden(), p_std);

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetAllEmployeesWithRoleNotInThisProject()
    {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        setupDefaultTestDatabase(dbh);
        String sqlStringEmployee = "INSERT INTO Mitarbeiter VALUES ( 2, 'No', 'Role', 'Strasse1'," +
                " 'TestOrt', '1234', 'test@test.de', '56789');";

        String sqlStringEmployeeRole = "INSERT INTO MitarbeiterRolle VALUES ( 2, 2, 1);";

        executeDMLDatabaseStatement(dbh, sqlStringEmployee);
        executeDMLDatabaseStatement(dbh, sqlStringEmployeeRole);

        List<Mitarbeiter> list = new ArrayList<Mitarbeiter>();
        list = dbh.getAllEmployeesWithRoleNotInThisProject(1, 1);

        assertEquals(2, list.get(0).getID());

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetAllProjectsExceptGivenProject()
    {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        addProjectToDatabase(dbh);

        String sqlString = "INSERT INTO Projekte VALUES ( 2, 'TestProjekt2', '2021-01-01', '2021-01-09'," +
                " 100);";

        executeDMLDatabaseStatement(dbh, sqlString);

        Projekt projekt = new Projekt(1, "TestProjekt",
                DateHelper.stringToDate("2021-01-01"),
                DateHelper.stringToDate("2021-01-09"), 100);

        List<Projekt> list = dbh.getAllProjectsExceptGivenProject(projekt);

        assertEquals(2, list.get(0).getID());

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetAllEmployeeForProjectListForProjectWithNoEmployees()
    {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        addProjectToDatabase(dbh);

        List<Projekt> list = new ArrayList<Projekt>();
        Projekt p = new Projekt(1, "TestProjekt",
                DateHelper.stringToDate("2021-01-01"),
                DateHelper.stringToDate("2021-01-09"), 100);
        list.add(p);

        List<Mitarbeiter> employees = dbh.getAllEmployeeForProjectList(list);

        assertEquals(0, employees.size());

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetAllEmployeeForProjectListForProjectWithEmployees()
    {
        DatabaseHandler dbh = createDbHandler(testDbUrl);
        setupDefaultTestDatabase(dbh);

        List<Projekt> list = new ArrayList<Projekt>();
        Projekt p = new Projekt(1, "TestProjekt",
                DateHelper.stringToDate("2021-01-01"),
                DateHelper.stringToDate("2021-01-09"), 100);
        list.add(p);

        List<Mitarbeiter> employees = dbh.getAllEmployeeForProjectList(list);

        assertEquals(1, employees.size());

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetAllEmployeeForProjectListForProjectWithNoIntersectingEmployees()
    {
        DatabaseHandler dbh = createDbHandler(testDbUrl);
        setupDefaultTestDatabase(dbh);

        List<Projekt> list = new ArrayList<Projekt>();
        Projekt p1 = new Projekt(1, "TestProjekt",
                DateHelper.stringToDate("2021-01-01"),
                DateHelper.stringToDate("2021-01-09"), 100);
        Projekt p2 = new Projekt(2, "TestProjekt",
                DateHelper.stringToDate("2021-01-01"),
                DateHelper.stringToDate("2021-01-09"), 100);

        String sqlStringProject = "INSERT INTO Projekte VALUES ( 2, 'TestProjekt', '2021-01-01', '2021-01-09'," +
                " 100);";
        executeDMLDatabaseStatement(dbh, sqlStringProject);

        String sqlStringEmployee = "INSERT INTO Mitarbeiter VALUES ( 2, 'Test2', 'TestV2', 'Strasse1'," +
                " 'TestOrt', '1234', 'test@test.de', '56789');";
        executeDMLDatabaseStatement(dbh, sqlStringEmployee);

        String sqlStringEmployeeProject = "INSERT INTO MitarbeiterProjekte VALUES ( 2, 2, 2, 10, 5);";
        executeDMLDatabaseStatement(dbh, sqlStringEmployeeProject);

        String sqlStringEmployeeRole = "INSERT INTO MitarbeiterRolle VALUES ( 2, 2, 1);";
        executeDMLDatabaseStatement(dbh, sqlStringEmployeeRole);

        list.add(p1);
        list.add(p2);

        List<Mitarbeiter> employees = dbh.getAllEmployeeForProjectList(list);

        assertEquals(2, employees.size());
        assertNotEquals(employees.get(0).getID(), employees.get(1).getID());

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetAllEmployeeForProjectListForProjectWithIntersectingEmployees()
    {
        DatabaseHandler dbh = createDbHandler(testDbUrl);
        setupDefaultTestDatabase(dbh);

        List<Projekt> list = new ArrayList<Projekt>();
        Projekt p1 = new Projekt(1, "TestProjekt",
                DateHelper.stringToDate("2021-01-01"),
                DateHelper.stringToDate("2021-01-09"), 100);
        Projekt p2 = new Projekt(2, "TestProjekt",
                DateHelper.stringToDate("2021-01-01"),
                DateHelper.stringToDate("2021-01-09"), 100);

        String sqlStringProject = "INSERT INTO Projekte VALUES ( 2, 'TestProjekt', '2021-01-01', '2021-01-09'," +
                " 100);";
        executeDMLDatabaseStatement(dbh, sqlStringProject);

        String sqlStringEmployee = "INSERT INTO Mitarbeiter VALUES ( 2, 'Test2', 'TestV2', 'Strasse1'," +
                " 'TestOrt', '1234', 'test@test.de', '56789');";
        executeDMLDatabaseStatement(dbh, sqlStringEmployee);

        String sqlStringEmployeeProject = "INSERT INTO MitarbeiterProjekte VALUES ( 2, 2, 2, 10, 5);";
        executeDMLDatabaseStatement(dbh, sqlStringEmployeeProject);

        String sqlStringEmployeeProject2 = "INSERT INTO MitarbeiterProjekte VALUES ( 3, 2, 1, 10, 5);";
        executeDMLDatabaseStatement(dbh, sqlStringEmployeeProject2);

        String sqlStringEmployeeProject3 = "INSERT INTO MitarbeiterProjekte VALUES ( 4, 1, 2, 10, 5);";
        executeDMLDatabaseStatement(dbh, sqlStringEmployeeProject3);

        String sqlStringEmployeeRole = "INSERT INTO MitarbeiterRolle VALUES ( 2, 2, 1);";
        executeDMLDatabaseStatement(dbh, sqlStringEmployeeRole);

        list.add(p1);
        list.add(p2);

        List<Mitarbeiter> employees = dbh.getAllEmployeeForProjectList(list);

        assertEquals(2, employees.size());
        assertNotEquals(employees.get(0).getID(), employees.get(1).getID());

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestAssignEmployeeWithRoleToProject()
    {
        DatabaseHandler dbh = createDbHandler(testDbUrl);

        addRoleToDatabase(dbh);
        addProjectToDatabase(dbh);
        addEmployeeToDatabase(dbh);
        addEmployeeRoleToDatabase(dbh);

        boolean check = dbh.assignEmployeeWithRoleToProject(1, 1, 1, 10);

        assertEquals(true, check);

        String sqlString = "SELECT MitarbeiterProjekte.MP_ProjektID, MitarbeiterProjekte.MP_StundenPlanung, " +
                "MitarbeiterRolle.MR_RolleID, MitarbeiterRolle.MR_MitarbeiterID " +
                "FROM MitarbeiterProjekte INNER JOIN MitarbeiterRolle ON " +
                "MitarbeiterProjekte.MP_MitarbeiterRolle = MitarbeiterRolle.MR_ID";
        ResultSet rs = executeSQLDatabaseStatement(dbh, sqlString);

        int r_id = 0;
        int m_id = 0;
        int p_id = 0;
        int hours = 0;

        try {
            p_id = rs.getInt("MP_ProjektID");
            hours = rs.getInt("MP_StundenPlanung");
            m_id = rs.getInt("MR_MitarbeiterID");
            r_id = rs.getInt("MR_RolleID");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        assertEquals(1, r_id);
        assertEquals(1, m_id);
        assertEquals(1, p_id);
        assertEquals(10, hours);

        dbh.closeJDBCConnection();
    }

    @Test
    public void TestGetAllRolesNotAssigned()
    {
        DatabaseHandler dbh = createDbHandler(testDbUrl);
        setupDefaultTestDatabase(dbh);

        Rolle role = new Rolle("NeueRolle");
        dbh.addNewRole(role);

        List<Rolle> list = dbh.getAllRolesNotAssigned(1);

        assertEquals(1, list.size());
        assertEquals("NeueRolle", list.get(0).getRollenbezeichnung());
    }

    @Test
    public void testUpdateEmployeeData()
    {
        DatabaseHandler dbh = createDbHandler(testDbUrl);
        addEmployeeToDatabase(dbh);

        Mitarbeiter employee = dbh.getEmployeeById(1);

        String name = "changedName";
        String vorname = "changedVorame";
        String strasse = "changedStrasse";
        String ort = "changedOrt";
        String plz = "changedPlz";
        String mail = "changedMail";
        String Tel = "changedTel";

        employee.setName(name);
        employee.setVorname(vorname);
        employee.setStrasse(strasse);
        employee.setOrt(ort);
        employee.setPlz(plz);
        employee.setMail(mail);
        employee.setTelefon(Tel);


        dbh.updateEmployeeData(employee);
        Mitarbeiter employeeChanged = dbh.getEmployeeById(1);


        assertEquals(name, employeeChanged.getName());
        assertEquals(vorname, employeeChanged.getVorname());
        assertEquals(strasse, employeeChanged.getStrasse());
        assertEquals(ort, employeeChanged.getOrt());
        assertEquals(plz, employeeChanged.getPlz());
        assertEquals(mail, employeeChanged.getMail());
        assertEquals(Tel, employeeChanged.getTelefon());

    }

}