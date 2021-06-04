package projektplanung;

import projektplanung.Persistence.DatabaseHandler;

public class Main {
    public static void main( String[] args )
    {
        DatabaseHandler db_handler = new DatabaseHandler("jdbc:sqlite:database/release.sqlite");
        db_handler.setupJDBCConnection();

        Menu menu = new Menu(db_handler);
        menu.mainMenu();

        db_handler.closeJDBCConnection();
    }
}
