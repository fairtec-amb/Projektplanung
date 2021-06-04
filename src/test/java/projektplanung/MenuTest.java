package projektplanung;

import projektplanung.Persistence.Entities.*;
import projektplanung.enums.UserType;
import projektplanung.Persistence.DatabaseHandler;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class MenuTest {

    Menu menu;

    private String testDbUrl = "jdbc:sqlite:database/test.sqlite";

    private void setupMenu()
    {
        DatabaseHandler db_handler = new DatabaseHandler(testDbUrl);
        menu = new Menu(db_handler);
        MockitoAnnotations.initMocks(this);
    }

    private void setupMenu(String str)
    {
        stringToSystemIn(str);
        DatabaseHandler db_handler = new DatabaseHandler(testDbUrl);
        menu = new Menu(db_handler);
        MockitoAnnotations.initMocks(this);
    }

    private void setupMenuMockDbh(DatabaseHandler dbh)
    {
        menu = new Menu(dbh);
        MockitoAnnotations.initMocks(this);
    }

    private void setupMenuMockDbh(DatabaseHandler dbh, String str)
    {
        stringToSystemIn(str);
        menu = new Menu(dbh);
        MockitoAnnotations.initMocks(this);
    }

    private void stringToSystemIn(String str)
    {
        System.setIn(new ByteArrayInputStream(str.getBytes()));
    }

    @Test
    public void mockUserInput()
    {
        Scanner scanner = new Scanner(System.in);
        String input = "1";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertEquals("1", new Scanner(System.in).nextLine());

        String input2 = "2";
        InputStream in2 = new ByteArrayInputStream(input2.getBytes());
        System.setIn(in2);

        assertEquals("2",  new Scanner(System.in).nextLine() );
        System.out.println("It works!");

    }


    @Test
    public void testGetPositiveIntFromUserInput()
    {
        setupMenu("1\n0\n-1\n\n");
        Integer res;

        // 1 => 1
        res = menu.getPositiveIntFromUserInput();
        assertEquals(1, res);

        // 0 => null
        res = menu.getPositiveIntFromUserInput();
        assertNull(res);

        // -1 => null
        res = menu.getPositiveIntFromUserInput();
        assertNull(res);

        // "" => null
        res = menu.getPositiveIntFromUserInput();
        assertNull(res);
    }

    @Test
    public void testGetUserInput()
    {
        String input = "1\n1";
        setupMenu(input);
        assertEquals("1", menu.getUserInput());
        assertEquals("1", menu.getUserInput());
    }

    @Test
    public void testStandardMenuNavigationWithOneOption()
    {
        String input = "1\n0\n2";
        setupMenu(input);

        String[] options = {"FirstOption"};
        Integer res;

        // 1 => 1
        res = menu.standardMenuNavigation(options);
        assertEquals(1, res);

        // 0 => null
        res = menu.standardMenuNavigation(options);
        assertNull(res);

        // 2 => null
        res = menu.standardMenuNavigation(options);
        assertNull(res);
    }

    @Test
    public void testStandardMenuNavigationWithMultipleOptions()
    {
        String input = "1\n0\n3";
        setupMenu(input);

        String[] options = {"FirstOption", "SecondOption"};
        Integer res;

        // 1 => 1
        res = menu.standardMenuNavigation(options);
        assertEquals(1, res);

        // 0 => null
        res = menu.standardMenuNavigation(options);
        assertNull(res);

        // 3 => null
        res = menu.standardMenuNavigation(options);
        assertNull(res);
    }

    @Test
    public void testStandardMenuNavigationWithNoOptions()
    {
        String input = "1\n0";
        setupMenu(input);

        String[] options = {};
        Integer res;

        // 0 => null
        res = menu.standardMenuNavigation(options);
        assertNull(res);

        // 0 => null
        res = menu.standardMenuNavigation(options);
        assertNull(res);
    }

    @Test
    public void testPrintInvalidSelection()
    {
        setupMenu();
        menu.printInvalidSelection();
    }

    @Test
    public void testStandardMenuNavigationWrapperWithOneOption()
    {
        String input = "1\n0\n2";
        setupMenu(input);

        String[] options = {"FirstOption"};
        Integer res;

        // 1 => 1
        res = menu.standardMenuNavigationWrapper(options);
        assertEquals(1, res);

        // 0 => null
        res = menu.standardMenuNavigationWrapper(options);
        assertNull(res);

        // 2 => null
        res = menu.standardMenuNavigationWrapper(options);
        assertNull(res);
    }

    @Test
    public void testStandardMenuNavigationWrapperWithMultipleOptions()
    {
        String input = "1\n0\n3";
        setupMenu(input);

        String[] options = {"FirstOption", "SecondOption"};
        Integer res;

        // 1 => 1
        res = menu.standardMenuNavigationWrapper(options);
        assertEquals(1, res);

        // 0 => null
        res = menu.standardMenuNavigationWrapper(options);
        assertNull(res);

        // 3 => null
        res = menu.standardMenuNavigationWrapper(options);
        assertNull(res);
    }

    @Test
    public void testStandardMenuNavigationWrapperWithNoOptions()
    {
        String input = "1\n0";
        setupMenu(input);

        String[] options = {};
        Integer res;

        // 0 => null
        res = menu.standardMenuNavigationWrapper(options);
        assertNull(res);

        // 0 => null
        res = menu.standardMenuNavigationWrapper(options);
        assertNull(res);
    }

    @Test
    public void testUserTypeSelection()
    {
        String input = "1\n2\n0";
        setupMenu(input);

        assert(menu.userTypeSelection());
        assertEquals(UserType.Admin, menu.getUserType());
        assert(menu.userTypeSelection());
        assertEquals(UserType.Employee, menu.getUserType());
        assert(!menu.userTypeSelection());
        assertEquals(UserType.Employee, menu.getUserType());
    }

    @Test
    public void testGreetEmployee()
    {
        setupMenu();
        Mitarbeiter mockEmployee = mock(Mitarbeiter.class);

        when(mockEmployee.getName()).thenReturn("Testlastname");
        when(mockEmployee.getVorname()).thenReturn("Testfirstname");

        menu.greetEmployee(mockEmployee);
    }

    @Test
    public void testShowAllProjectsByEmployee()
    {
        DatabaseHandler mockDbh = mock(DatabaseHandler.class);
        setupMenuMockDbh(mockDbh);

        Mitarbeiter mockMitarbeiter = mock(Mitarbeiter.class);
        List<MitarbeiterProjekte> mockListMAP = new ArrayList<MitarbeiterProjekte>();

        // empty list
        when(mockDbh.getAllProjectsByEmployee(mockMitarbeiter)).thenReturn(mockListMAP);
        menu.showAllProjectsByEmployee(mockMitarbeiter);

        // non-empty list
        MitarbeiterProjekte mockMP = mock(MitarbeiterProjekte.class);
        when(mockMP.getID()).thenReturn(1);
        when(mockMP.getBezeichnung()).thenReturn("Test Project");
        when(mockMP.getMP_geleistet()).thenReturn(0);
        mockListMAP.add(mockMP);

        menu.showAllProjectsByEmployee(mockMitarbeiter);
    }

    @Test
    public void testCreateProjectMenu()
    {
        String str;
        DatabaseHandler mockDbh = mock(DatabaseHandler.class);
        when(mockDbh.createProject(any(Projekt.class))).thenReturn(true);
        NoSuchElementException error;


        // fails on empty name
        str = "\n";
        setupMenuMockDbh(mockDbh, str);

        error = assertThrows(NoSuchElementException.class, () -> {
            assert(!menu.createProjectMenu());
            menu.getUserInput();
        });

        // fails on bad input for start date
        str = "Test Project\n\n";
        setupMenuMockDbh(mockDbh, str);

        error = assertThrows(NoSuchElementException.class, () -> {
            assert(!menu.createProjectMenu());
            menu.getUserInput();
        });


        // fails on bad input for end date
        str = "Test Project\n2020-01-01\n";
        setupMenuMockDbh(mockDbh, str);

        error = assertThrows(NoSuchElementException.class, () -> {
            assert(!menu.createProjectMenu());
            menu.getUserInput();
        });

        // fails on end date before start date
        str = "Test Project\n2020-01-01\n2000-01-01";
        setupMenuMockDbh(mockDbh, str);

        error = assertThrows(NoSuchElementException.class, () -> {
            assert(!menu.createProjectMenu());
            menu.getUserInput();
        });

        // fails on invalid planned hours
        str = "Test Project\n2020-01-01\n2020-01-01\n0";
        setupMenuMockDbh(mockDbh, str);

        error = assertThrows(NoSuchElementException.class, () -> {
            assert(!menu.createProjectMenu());
            menu.getUserInput();
        });

        // success
        str = "Test Project\n2020-01-01\n2020-01-01\n10";
        setupMenuMockDbh(mockDbh, str);
        assert(menu.createProjectMenu());
    }

    @Test
    public void testTimeRegistrationMenu()
    {
        String str;
        DatabaseHandler mockDbh = mock(DatabaseHandler.class);
        NoSuchElementException error;

        List<MitarbeiterProjekte> list = new ArrayList<>();
        MitarbeiterProjekte mockMP = mock(MitarbeiterProjekte.class);
        when(mockMP.getBezeichnung()).thenReturn("Test Project");
        when(mockMP.getID()).thenReturn(1);
        list.add(mockMP);
        when(mockDbh.getAllProjectsByEmployee(any(Mitarbeiter.class))).thenReturn(list);

        Mitarbeiter mockEmployee = mock(Mitarbeiter.class);


        // fails on invalid selection for project
        str = "0\n";
        setupMenuMockDbh(mockDbh, str);

        error = assertThrows(NoSuchElementException.class, () -> {
            assert(!menu.timeRegistrationMenu(mockEmployee));
            menu.getUserInput();
        });

        // fails on invalid hour count
        str = "1\n0\n";
        setupMenuMockDbh(mockDbh, str);

        error = assertThrows(NoSuchElementException.class, () -> {
            assert(!menu.timeRegistrationMenu(mockEmployee));
            menu.getUserInput();
        });

        // success
        str = "1\n1\n";
        setupMenuMockDbh(mockDbh, str);

        assert(menu.timeRegistrationMenu(mockEmployee));
    }

    @Test
    public void testGetUserInputForProjectID()
    {
        String str;
        DatabaseHandler mockDbh = mock(DatabaseHandler.class);

        List<Projekt> list = new ArrayList<>();
        Projekt mockProject = mock(Projekt.class);
        when(mockProject.getBezeichnung()).thenReturn("Test Project");
        when(mockProject.getID()).thenReturn(8);
        list.add(mockProject);

        when(mockDbh.getAllProjects()).thenReturn(list);

        str = "1";
        setupMenuMockDbh(mockDbh, str);
        assertEquals(8, menu.getUserInputForProjectID());
    }

    @Test
    public void testShowAllEmployeesByProject()
    {
        String str;
        DatabaseHandler mockDbh = mock(DatabaseHandler.class);

        List<MitarbeiterInfo> list = new ArrayList<>();

        when(mockDbh.getAllEmployeeForProject(anyInt())).thenReturn(list);

        // success: project has no employees
        str = "1";
        setupMenuMockDbh(mockDbh, str);
        assert(menu.showAllEmployeesByProject(1));

        // success: project has employees
        MitarbeiterInfo mockMitarbeiter = mock(MitarbeiterInfo.class);
        list.add(mockMitarbeiter);

        str = "1";
        setupMenuMockDbh(mockDbh, str);
        assert(menu.showAllEmployeesByProject(1));
    }

    @Test
    public void testGetEmployeeIdFromUserInput()
    {
        String str;
        DatabaseHandler mockDbh = mock(DatabaseHandler.class);

        List<Mitarbeiter> list = new ArrayList<>();

        Mitarbeiter mockMitarbeiter = mock(Mitarbeiter.class);
        when(mockMitarbeiter.getName()).thenReturn("Testlastname");
        when(mockMitarbeiter.getVorname()).thenReturn("Testfirstname");
        when(mockMitarbeiter.getID()).thenReturn(6);

        // fail: list is empty
        str = "1";
        setupMenuMockDbh(mockDbh, str);
        assertNull(menu.getEmployeeIdFromUserInput(list));

        // fail: invalid selection
        list.add(mockMitarbeiter);

        str = "0\n1";
        setupMenuMockDbh(mockDbh, str);
        assertNull(menu.getEmployeeIdFromUserInput(list));

        // success: valid selection
        assertEquals(6, menu.getEmployeeIdFromUserInput(list));
    }

    @Test
    public void testEmployeeIdMenu()
    {
        String str;
        DatabaseHandler mockDbh = mock(DatabaseHandler.class);

        when(mockDbh.checkIfEmployeeIdExists(1)).thenReturn(true);
        when(mockDbh.checkIfEmployeeIdExists(2)).thenReturn(false);

        str = "1\n2";
        setupMenuMockDbh(mockDbh, str);
        // success
        assertEquals(1, menu.employeeIdMenu());
        // fail: unknown employee
        assertNull(menu.employeeIdMenu());
    }

    @Test
    public void testCreateEmployeeMenu()
    {
        String str;
        DatabaseHandler mockDbh = mock(DatabaseHandler.class);

        when(mockDbh.addNewEmployee(any(Mitarbeiter.class))).thenReturn(true);

        str = "Lastname\nFirstname\n123 Fake St\nABC012\nTown\ntest@example.com\n+00";
        setupMenuMockDbh(mockDbh, str);

        // success
        assert(menu.createEmployeeMenu());
    }

    @Test
    public void testCreateRoleMenu()
    {
        String str;
        DatabaseHandler mockDbh = mock(DatabaseHandler.class);

        when(mockDbh.addNewRole(any(Rolle.class))).thenReturn(false);

        // fail: empty role description
        str = "\n";
        setupMenuMockDbh(mockDbh, str);

        assert(!menu.createRoleMenu());

        // fail: dbh fails to create role
        str = "TestRole";
        setupMenuMockDbh(mockDbh, str);

        assert(!menu.createRoleMenu());

        // success
        str = "TestRole";
        when(mockDbh.addNewRole(any(Rolle.class))).thenReturn(true);
        setupMenuMockDbh(mockDbh, str);

        assert(menu.createRoleMenu());
    }

    @Test
    public void testGetRoleIdFromUserInput()
    {
        String str;
        DatabaseHandler dbhMock = mock(DatabaseHandler.class);

        List<Rolle> list = new ArrayList<>();

        Rolle mockRole = mock(Rolle.class);
        when(mockRole.getR_ID()).thenReturn(10);
        when(mockRole.getRollenbezeichnung()).thenReturn("Testrole");

        // fail: no roles
        str = "0";
        setupMenuMockDbh(dbhMock, str);

        assertNull(menu.getRoleIdFromUserInput(list));

        // fail: invalid selection
        list.add(mockRole);

        str = "0";
        setupMenuMockDbh(dbhMock, str);

        // success
        str = "1";
        setupMenuMockDbh(dbhMock, str);
    }

    @Test
    public void testListAllEmployees()
    {
        DatabaseHandler mockDbh = mock(DatabaseHandler.class);
        List<Mitarbeiter> list = new ArrayList<>();

        Mitarbeiter mockMA = mock(Mitarbeiter.class);
        when(mockMA.getName()).thenReturn("Lastname");
        when(mockMA.getVorname()).thenReturn("Firstname");
        when(mockMA.getID()).thenReturn(13);

        when(mockDbh.getAllEmployees()).thenReturn(list);

        // empty employee list
        setupMenuMockDbh(mockDbh);
        menu.listAllEmployees();

        // non-empty employee list
        list.add(mockMA);
        menu.listAllEmployees();

    }

    @Test
    public void testGetOverlappingProjects() throws ParseException {
        List<Projekt> list = new ArrayList<>();

        Projekt mockP_1 = mock(Projekt.class);
        when(mockP_1.getStartDatum()).thenReturn("2020-01-01");
        when(mockP_1.getEndDatum()).thenReturn("2020-01-01");

        Projekt mockP_2 = mock(Projekt.class);
        when(mockP_2.getStartDatum()).thenReturn("2025-01-01");
        when(mockP_2.getEndDatum()).thenReturn("2025-01-01");

        setupMenu();

        assertEquals(0, menu.getOverlappingProjects(mockP_1, list).size());

        list.add(mockP_2);
        assertEquals(0, menu.getOverlappingProjects(mockP_1, list).size());

        list.add(mockP_1);
        assertEquals(1, menu.getOverlappingProjects(mockP_1, list).size());
    }

    @Test
    public void testSubtractFromEmployeeList()
    {
        Mitarbeiter mockMA_1 = mock(Mitarbeiter.class);
        when(mockMA_1.getID()).thenReturn(1);

        Mitarbeiter mockMA_2 = mock(Mitarbeiter.class);
        when(mockMA_2.getID()).thenReturn(2);

        List<Mitarbeiter> list_1 = new ArrayList<>();
        List<Mitarbeiter> list_2;

        list_1.add(mockMA_1);

        setupMenu();

        list_2 = new ArrayList<>();
        assertEquals(list_1, menu.subtractFromEmployeeList(list_1, list_2));

        list_2.add(mockMA_2);
        assertEquals(list_1, menu.subtractFromEmployeeList(list_1, list_2));

        list_2.add(mockMA_1);
        assertEquals(new ArrayList<Mitarbeiter>(), menu.subtractFromEmployeeList(list_1, list_2));
    }

    /*
    @TODO: tests for:
      //mainMenu
      //employeeMenu
      //employeeMenuOptions
      //userMenu
      updateEmployeeData
      //assignRoleToEmployee
      //planningMenu
     */





    /*
    @Test
    public void CheckIfInputForUserCreationCreatesAUser()
    {
        menu.setInput("1");
        boolean check = menu.startMenu();

        assertEquals(Benutzer.class, menu.person.getClass());
    }

    @Test
    public void CheckIfInputForWorkerCreationCreatesAWorker()
    {
        menu.setInput("2");
        boolean check = menu.startMenu();

        assertEquals(Mitarbeiter.class, menu.person.getClass());
    }

    @Test
    public void CheckIfWrongInputBreaksEverything()
    {
        menu.setInput("Aber ich will weder Mitarbeiter noch Benutzer sein!");
        boolean check = menu.startMenu();

        assertEquals(false, check);
    }
*/
}