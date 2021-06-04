package projektplanung;

import projektplanung.Persistence.DatabaseHandler;
import projektplanung.Persistence.Entities.*;
import projektplanung.enums.UserType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Menu {
    final Scanner scanner;
    final DatabaseHandler db_handler;

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    UserType userType;

    public Menu(DatabaseHandler db_handler, Scanner scanner) {
        this.db_handler = db_handler;
        this.scanner = scanner;
    }

    public Menu(DatabaseHandler db_handler) {
        this(db_handler, new Scanner(System.in));
    }

    @Nullable
    public Integer standardMenuNavigation(@NotNull String[] menuOptions) {
        System.out.println("Bitte wählen Sie:");
        for (int i = 0; i < menuOptions.length; i++) {
            System.out.println((i + 1) + " " + menuOptions[i]);
        }
        Integer userInput = getPositiveIntFromUserInput();
        if (userInput == null || userInput <= 0 || userInput > menuOptions.length) {
            return null;
        }
        return userInput;
    }

    @Nullable
    public Integer getPositiveIntFromUserInput() {
        try {
            Integer input = Integer.parseInt(scanner.nextLine());
            if (input <= 0) {
                return null;
            }
            return input;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void printInvalidSelection() {
        System.out.println("Ungültige Eingabe! Das Programm wird beendet.");
    }

    @Nullable
    public Integer standardMenuNavigationWrapper(@NotNull String[] menuOptions) {
        Integer input = standardMenuNavigation(menuOptions);
        if (input == null) {
            printInvalidSelection();
        }
        return input;
    }

    public boolean mainMenu() {
        System.out.println("Willkommen im Programm");
        boolean userTypeSuccess = userTypeSelection();
        if (!userTypeSuccess) {
            return false;
        }

        switch (getUserType()) {
            case Admin:
                return userMenu();
            case Employee:
                return employeeMenu();
            default:
                return false;
        }
    }

    public boolean userTypeSelection() {
        String[] options = {"Projektleiter", "Mitarbeiter"};
        Integer input = standardMenuNavigationWrapper(options);
        if (input == null) {
            return false;
        }
        switch (input) {
            case 1:
                setUserType(UserType.Admin);
                break;
            case 2:
                setUserType(UserType.Employee);
                break;
            default:
                return false;
        }
        return true;
    }

    public boolean employeeMenu() {
        Integer employeeId = employeeIdMenu();
        if (employeeId == null) {
            return false;
        }
        Mitarbeiter employee = db_handler.getEmployeeById(employeeId);
        if (employee == null) {
            return false;
        }
        greetEmployee(employee);
        return employeeMenuOptions(employee);
    }

    @Nullable
    public Integer employeeIdMenu() {
        System.out.println("Bitte geben Sie Ihre MitarbeiterId ein:");
        Integer m_id = getPositiveIntFromUserInput();
        if (m_id == null) {
            return null;
        }

        if (!db_handler.checkIfEmployeeIdExists(m_id)) {
            System.out.println("Unbekannte MitarbeiterId!");
            return null;
        }

        return m_id;
    }

    public void greetEmployee(@NotNull Mitarbeiter employee) {
        System.out.println("Willkommen " + employee.getVorname() + " " + employee.getName() + "!");
    }

    public boolean employeeMenuOptions(Mitarbeiter employee) {
        String[] options = {
                "Meine Projekte",
                "Stundenerfassung"
        };
        Integer input = standardMenuNavigationWrapper(options);

        if (input == null) {
            return false;
        }

        switch (input) {
            case 1:
                showAllProjectsByEmployee(employee);
                return true;
            case 2:
                return timeRegistrationMenu(employee);
            default:
                return false;
        }
    }

    public boolean userMenu() {
        String[] options = {
                "Projekt anlegen",
                "Projekte inklusive Mitarbeiter und geleistete Stunden auflisten",
                "Personalplanung",
                "Mitarbeiter anlegen",
                "Rolle anlegen",
                "Mitarbeiter eine Rolle zuweisen",
                "Kontaktdaten eines Mitarbeiters ändern"
        };
        Integer input = standardMenuNavigation(options);
        if (input == null) {
            return false;
        }

        switch (input) {
            case 1:
                return createProjectMenu();
            case 2:
                return menuAllEmployeesByProject();
            case 3:
                return planningMenu();
            case 4:
                return createEmployeeMenu();
            case 5:
                return createRoleMenu();
            case 6:
                return assignRoleToEmployee();
            case 7:
                return updateEmployeeData();
            default:
                return false;
        }
    }

    public boolean updateEmployeeData() {
        List<Mitarbeiter> employee_list = db_handler.getAllEmployees();
        Integer input_int;
        String[] menu_options;

        menu_options = new String[employee_list.size()];

        for (int i = 0; i < employee_list.size(); i++) {
            menu_options[i] = employee_list.get(i).getName() + ", " + employee_list.get(i).getVorname();
        }

        input_int = standardMenuNavigationWrapper(menu_options);
        if (input_int == null) {
            return false;
        }

        Mitarbeiter employee = employee_list.get(input_int-1);

        String name = getUserInput("Geben Sie den neuen Namen des Mitarbeiters ein:");
        if (name == null || name == "")
        {
            printInvalidSelection();
            return false;
        }

        String vorname = getUserInput("Geben Sie den neuen Vornamen des Mitarbeiters ein:");
        if (vorname == null || vorname == "")
        {
            printInvalidSelection();
            return false;
        }

        String strasse = getUserInput("Geben Sie die neue Straße des Mitarbeiters ein:");
        if (strasse == null || strasse == "")
        {
            printInvalidSelection();
            return false;
        }

        String ort = getUserInput("Geben Sie den neuen Ort des Mitarbeiters ein:");
        if (ort == null || ort == "")
        {
            printInvalidSelection();
            return false;
        }

        String plz = getUserInput("Geben Sie die neue PLZ des Mitarbeiters ein:");
        if (plz == null || plz == "")
        {
            printInvalidSelection();
            return false;
        }

        String mail = getUserInput("Geben Sie die neue Mailadresse des Mitarbeiters ein:");
        if (mail == null || mail == "")
        {
            printInvalidSelection();
            return false;
        }

        String tel = getUserInput("Geben Sie die neue Telefonnumer des Mitarbeiters ein:");
        if (tel == null || tel == "")
        {
            printInvalidSelection();
            return false;
        }

        employee.setName(name);
        employee.setVorname(vorname);
        employee.setStrasse(strasse);
        employee.setOrt(ort);
        employee.setPlz(plz);
        employee.setMail(mail);
        employee.setTelefon(tel);

        return db_handler.updateEmployeeData(employee);
    }

    public boolean assignRoleToEmployee() {
        List<Mitarbeiter> employee_list = db_handler.getAllEmployees();
        Integer employee_id = getEmployeeIdFromUserInput(employee_list);
        if (employee_id == null) {
            return false;
        }

        List<Rolle> roles_not_assigned = db_handler.getAllRolesNotAssigned(employee_id);
        if (roles_not_assigned.size() <= 0) {
            System.out.println("Keine Rollen gefunden.");
            return false;
        }

        Integer role_id = getRoleIdFromUserInput(roles_not_assigned);
        if (role_id == null) {
            return false;
        }

        boolean success = db_handler.assignRoleToEmployee(role_id, employee_id);

        if (!success) {
            System.out.println("Dem Mitarbeiter konnte diese Rolle nicht zugewiesen werden.");
        } else {
            System.out.println("Rolle erfolgreich zugewiesen.");
        }

        return success;
    }

    @Nullable
    public Integer getRoleIdFromUserInput(@NotNull List<Rolle> role_list) {
        String[] roles = new String[role_list.size()];
        for (int i = 0; i < role_list.size(); i++) {
            roles[i] = role_list.get(i).getRollenbezeichnung();
        }
        Integer user_input_for_role = standardMenuNavigationWrapper(roles);
        if (user_input_for_role == null) {
            return null;
        }

        int role_id = role_list.get(user_input_for_role - 1).getR_ID();
        return role_id;
    }

    public void listAllEmployees() {

        List<Mitarbeiter> employee_list = db_handler.getAllEmployees();

        if (employee_list.size() == 0)
        {
            System.out.println("Keine Mitarbeiter im System!");
            return;
        }
        for (Mitarbeiter m : employee_list) {
            System.out.println("Mitarbeiter ID: " + m.getID() + " Mitarbeitername: " + m.getName() + ", " + m.getVorname());
        }
    }

    public boolean createRoleMenu() {

        String rolle_bezeichnung = getUserInput("Bitte geben Sie die Rollenbezeichnung ein.");
        if (rolle_bezeichnung == null || rolle_bezeichnung == "")
        {
            System.out.println("Ungültige Eingabe!");
            return false;
        }
        Rolle role = new Rolle(rolle_bezeichnung);

        boolean success = db_handler.addNewRole(role);

        if (!success) {
            System.out.println("Rolle konnte nicht erfolgreich hinzugefügt werden.");
        }
        return success;
    }

    public String getUserInput(String str) {
        System.out.println(str);
        String input = scanner.nextLine();
        return input;
    }

    public boolean createEmployeeMenu() {

        Mitarbeiter employee = new Mitarbeiter();

        String employee_lastname = getUserInput("Bitte geben Sie den Nachnamen des Mitarbeiters ein.");
        String employee_firstname = getUserInput("Bitte geben Sie den Vornamen des Mitarbeiters ein.");
        String employee_street = getUserInput("Bitte geben Sie die Strasse inklusive Hausnummer des Mitarbeiters ein.");
        String employee_plz = getUserInput("Bitte geben Sie die Postleitzahl des Mitarbeiters ein.");
        String employee_city = getUserInput("Bitte geben Sie den Ort des Mitarbeiters ein.");
        String employee_mail = getUserInput("Bitte geben Sie die Mailadresse des Mitarbeiters ein.");
        String employee_phone = getUserInput("Bitte geben Sie die Telefonnummer des Mitarbeiters ein.");

        employee.setName(employee_lastname);
        employee.setVorname(employee_firstname);
        employee.setStrasse(employee_street);
        employee.setPlz(employee_plz);
        employee.setOrt(employee_city);
        employee.setMail(employee_mail);
        employee.setTelefon(employee_phone);

        return db_handler.addNewEmployee(employee);
    }

    public boolean planningMenu() {
        Integer input_int;
        String[] menu_options;

        // get project
        Integer project_id = getUserInputForProjectID();
        if (project_id == null) {
            return false;
        }

        Projekt project = db_handler.getProjectById(project_id);
        if (project == null) {
            return false;
        }

        // get all roles
        List<Rolle> role_list = db_handler.getAllRoles();

        if (role_list == null || role_list.size() <= 0) {
            return false;
        }

        Integer role_id = getRoleIdFromUserInput(role_list);
        if (role_id == null) {
            return false;
        }

        // check overlapping projects
        List<Mitarbeiter> employee_list = db_handler.getAllEmployeesWithRoleNotInThisProject(project_id, role_id);
        List<Projekt> all_projects_except_given = db_handler.getAllProjectsExceptGivenProject(project);
        List<Projekt> overlapping_projects = getOverlappingProjects(project, all_projects_except_given);

        // Get all Employees that are working in any overlapping Project.
        List<Mitarbeiter> not_available_employees = db_handler.getAllEmployeeForProjectList(overlapping_projects);

        // remove employees working on an overlapping project fromm available employees
        List<Mitarbeiter> finalEmployeeList = subtractFromEmployeeList(employee_list, not_available_employees);

        if (finalEmployeeList.size() <= 0) {
            System.out.println("Leider stehen keine Mitarbeiter zur Verteilung zur Verfügung.");
            return false;
        }

        Integer employee_id = getEmployeeIdFromUserInput(finalEmployeeList);
        if (employee_id == null) {
            return false;
        }

        // get planned hours for employee
        System.out.println("Bitte geben Sie die geplanten Stunden für diesen Mitarbeiter an: ");
        Integer hours = getPositiveIntFromUserInput();

        if (hours == null) {
            return false;
        }

        // assign employee to project
        if (db_handler.assignEmployeeWithRoleToProject(role_id, employee_id, project_id, hours)) {
            System.out.println("Die Zuweisung wurde erfolgreich gespeichert.");
        } else {
            return false;
        }

        return true;
    }

    @NotNull
    public List<Projekt> getOverlappingProjects(@NotNull Projekt project, @NotNull List<Projekt> project_list) {
        List<Projekt> overlapping_projects = new ArrayList<>();

        for (Projekt p : project_list) {

            if (DateHelper.checkIfOverlap(p, project)) {
                overlapping_projects.add(p);
            }
        }
        return overlapping_projects;
    }

    public List<Mitarbeiter> subtractFromEmployeeList(@NotNull List<Mitarbeiter> list_1, @NotNull List<Mitarbeiter> list_2) {
        List<Mitarbeiter> result_list;
        if (list_2.size() == 0) {
            result_list = list_1;
        } else {
            result_list = new ArrayList<Mitarbeiter>();
            for (Mitarbeiter av : list_1) {
                boolean found = false;
                for (Mitarbeiter not_av : list_2) {
                    if (av.getID() == not_av.getID()) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                result_list.add(av);
            }
        }
        return result_list;
    }

    @Nullable
    public Integer getEmployeeIdFromUserInput(@NotNull List<Mitarbeiter> employeeList) {
        Integer input_int;
        String[] menu_options;
        System.out.println("Folgende Mitarbeiter sind für die gewählte Rolle und im gegebenen Zeitraum verfügbar:");

        menu_options = new String[employeeList.size()];

        for (int i = 0; i < employeeList.size(); i++) {
            menu_options[i] = employeeList.get(i).getName() + ", " + employeeList.get(i).getVorname();
        }

        input_int = standardMenuNavigationWrapper(menu_options);
        if (input_int == null) {
            return null;
        }

        Integer employee_id = employeeList.get(input_int-1).getID();
        return employee_id;
    }

    @Nullable
    public Integer getUserInputForProjectID() {
        List<Projekt> project_list = db_handler.getAllProjects();
        String[] projects = new String[project_list.size()];
        for (int i = 0; i < project_list.size(); i++) {
            projects[i] = project_list.get(i).getBezeichnung();
        }
        Integer user_input_for_project = standardMenuNavigationWrapper(projects);
        if (user_input_for_project == null) {
            return null;
        }

        int project_id = project_list.get(user_input_for_project-1).getID();
        return project_id;
    }

    public boolean menuAllEmployeesByProject() {
        System.out.println("Geben Sie die ID des gewünschten Projekts ein:");
        Integer project_id = getUserInputForProjectID();
        if (project_id == null)
        {
            return false;
        }
        return showAllEmployeesByProject(project_id);
    }

    public boolean showAllEmployeesByProject(int project_id) {
        List<MitarbeiterInfo> employeeInfo_list = db_handler.getAllEmployeeForProject(project_id);

        if (employeeInfo_list.size() == 0) {
            System.out.println("Keine Mitarbeiter in diesem Projekt.");
            return true;
        }

        for (MitarbeiterInfo m_info : employeeInfo_list) {
            System.out.println(m_info.getName() + ", " + m_info.getVorname() + " hat " + m_info.getStunden() +
                    " geleistet.");
        }
        return true;
    }

    public boolean createProjectMenu() {

        System.out.println("Bitte geben Sie die Projektbezeichnung ein.");
        String pbez = scanner.nextLine();
        if (pbez == "")
        {
            printInvalidSelection();
            return false;
        }

        System.out.println("Bitte geben Sie den Starttermin ein.");
        Date stdat = getDateFromUserInput();
        if (stdat == null)
        {
            printInvalidSelection();
            return false;
        }

        System.out.println("Bitte geben Sie den Endtermin ein.");
        Date enddat = getDateFromUserInput();
        if (enddat == null)
        {
            printInvalidSelection();
            return false;
        } else if (stdat.compareTo(enddat) > 0)
        {
            System.out.println("Ungültige Start- und Endtermine!");
        }

        System.out.println("Bitte geben Sie die geplanten Stunden für das Projekt ein.");
        Integer std = getPositiveIntFromUserInput();
        if (std == null)
        {
            printInvalidSelection();
            return false;
        }

        Projekt project = new Projekt(pbez, stdat, enddat, std);
        return db_handler.createProject(project);
    }

    public Date getDateFromUserInput() {
        return DateHelper.stringToDate(scanner.nextLine());
    }

    public boolean timeRegistrationMenu(Mitarbeiter employee) {
        List<MitarbeiterProjekte> employeeProjects = db_handler.getAllProjectsByEmployee(employee);

        String[] menu_options = new String[employeeProjects.size()];

        for (int i = 0; i < employeeProjects.size(); i++) {
            menu_options[i] = employeeProjects.get(i).getBezeichnung();
        }

        Integer input = standardMenuNavigationWrapper(menu_options);
        if (input == null) {
            return false;
        }

        int projektid = employeeProjects.get(input-1).getID();

        System.out.println("Bitte geben sie die gesamt geleisteten Stunden des Projekts ein.");

        Integer hours = getPositiveIntFromUserInput();
        if (hours == null)
        {
            return false;
        }
        db_handler.addEmployeeHoursToProject(employeeProjects.get(projektid - 1).getMP_Id(), hours);
        System.out.println("Die Stunden wurden für das gewählte Projekt aktualisiert.");

        return true;

    }

    public String getUserInput() {
        return scanner.nextLine();
    }

    public void showAllProjectsByEmployee(Mitarbeiter employee) {
        List<MitarbeiterProjekte> employeeProjects = db_handler.getAllProjectsByEmployee(employee);

        for (MitarbeiterProjekte mp : employeeProjects) {
            System.out.println("Projekt ID: " + mp.getID() + ", Projektbezeichnung: " + mp.getBezeichnung() +
                    ", Ihre geleisteten Stunden: " + mp.getMP_geleistet() + ".");
        }
    }
}
