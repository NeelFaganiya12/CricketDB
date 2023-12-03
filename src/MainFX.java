// SQL Libraries
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
// JavaFX Libraries
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.control.TextField;

public class MainFX extends Application {

    public void dialogBox(String text)
    {
        Dialog<String> dialog = new Dialog<String>();
        //Setting the title
        dialog.setTitle("FreeHit DBMS Message");
        ButtonType type = new ButtonType("Ok", ButtonData.OK_DONE);
        //Setting the content of the dialog
        dialog.setContentText(text);
        //Adding buttons to the dialog pane
        dialog.getDialogPane().getButtonTypes().add(type);
        dialog.show();
    }

    private Connection baseDatabaseConnection()
    {
        Connection connection = null;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            String dbUrl = System.getenv("dbUrl");
            connection = DriverManager.getConnection(dbUrl);
            if (connection != null) {
                return connection;
            }
        }
        catch (ClassNotFoundException ex) {
            dialogBox("Database Connection Error");
        } catch (SQLException ex) {
            dialogBox("Database Connection Error");
        }
        return connection;
    }

    public void updateValueDialogBox()
    {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("Update Value - Freehit DBMS Message");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField = new TextField("current name");
        TextField textField2 = new TextField("new name");
        ObservableList<TableName> options = FXCollections.observableArrayList(TableName.values());
        ComboBox<TableName> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField, textField2, comboBox));
        Platform.runLater(textField::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField.getText(),
                textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            updateValueFromDatabase(results.name, results.Newname, results.table);
        });
    }

    private void deleteValueDialogBox()
    {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("Delete Value - Freehit DBMS Message");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField = new TextField("delete name");
        ObservableList<TableName> options = FXCollections.observableArrayList(TableName.values());
        ComboBox<TableName> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField, comboBox));
        Platform.runLater(textField::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            deleteValueFromDatabase(results.name, results.table);
        });
    }

    private void searchValueDialogBox()
    {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("Search Value - Freehit DBMS Message");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField = new TextField("Search name");
        ObservableList<TableName> options = FXCollections.observableArrayList(TableName.values());
        ComboBox<TableName> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField, comboBox));
        Platform.runLater(textField::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            selectValueFromDatabase(results.name, results.table);
        }); 
    }
    
    private static enum TableName {Player, Umpire, Venue, Team}

    private static class Results {

        String name;
        TableName table;
        String Newname;

        public Results(String name, String Newname, TableName table) {
            this.name = name;
            this.table = table;
            this.Newname = Newname;
        }
        public Results(String name, TableName table) {
            this.name = name;
            this.table = table;
        } 
    }

    public void updateValueFromDatabase(String name, String Newname, TableName table) {
        Connection connection = baseDatabaseConnection();
        String query= "";
        if (table.toString() == "Player") query = "update player set player_name ='"+Newname+"' where player_name='"+name+"'";
        if (table.toString() == "Umpire") query = "update umpire set umpire_name ='"+Newname+"' where umpire_name='"+name+"'";
        if (table.toString() == "Venue") query = "update venue set venue_name ='"+Newname+"' where venue_name='"+name+"'";
        if (table.toString() == "Team") query = "update team set umpire_name ='"+Newname+"' where team_name='"+name+"'";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);        
            dialogBox("Table Updated");
		} 
        catch (SQLException e) 
        {
            dialogBox(e.toString());
        }
    }

    public void deleteValueFromDatabase(String name, TableName table) {
        Connection connection = baseDatabaseConnection();
        String query= "";
        if (table.toString() == "Player") query = "delete from player where player_name='"+name+"'";
        if (table.toString() == "Umpire") query = "delete from umpire where umpire_name='"+name+"'";
        if (table.toString() == "Venue") query = "delete from venue where venue_name='"+name+"'";
        if (table.toString() == "Team") query = "delete from team where team_name='"+name+"'";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);    
            dialogBox("Value Deleted");    
		} 
        catch (SQLException e) {
            dialogBox(e.toString());
		}
    }


    public void selectValueFromDatabase(String name, TableName table) {
        Connection connection = baseDatabaseConnection();
        String query= "";
        if (table.toString() == "Player") query = "select * from player where player_name='"+name+"'";
        if (table.toString() == "Umpire") query = "select * from umpire where umpire_name='"+name+"'";
        if (table.toString() == "Venue") query = "select * from venue where venue_name='"+name+"'";
        if (table.toString() == "Team") query = "select * from team where team_name='"+name+"'";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int flag=0;
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                for (int j = 1; j <= columnsNumber; j++) {
                    if((j == 1) && (flag==0))
                    {
                        for (int k = 1; k <= columnsNumber; k++) {
                                System.out.print(rsmd.getColumnName(k) + "     ");
                        }
                        flag=1;
                        System.out.println("\n----------------------------------------------------------------------------------------");
                    }
                    if (j > 1) System.out.print("   ");
                    String columnValue = rs.getString(j);
                    System.out.print(columnValue + "      ");
                }
                System.out.println("");
                dialogBox("Search success");
            }  
		} 
        catch (SQLException e) {
            dialogBox(e.toString());
		}
    }

    private String[] getDropTablesQueries(){
        return new String[]{"DROP TABLE Umpire CASCADE CONSTRAINTS",
                "DROP TABLE Team CASCADE CONSTRAINTS",
                "DROP TABLE Venue CASCADE CONSTRAINTS",
                "DROP TABLE Player CASCADE CONSTRAINTS",
                "DROP TABLE Player_Age CASCADE CONSTRAINTS",
                "DROP TABLE Match_Info CASCADE CONSTRAINTS",
                "DROP TABLE Match_Team1_Info CASCADE CONSTRAINTS",
                "DROP TABLE Match_Team2_Info CASCADE CONSTRAINTS",
                "DROP TABLE MatchResult CASCADE CONSTRAINTS",
                "COMMIT"};
    }

    private String[] getCreateTablesQueries() {
        return new String[]{"CREATE TABLE Umpire(Umpire_ID Number primary key, Umpire_name VARCHAR(30) NOT NULL, Nationality VARCHAR(20))",
                 "CREATE TABLE Team(Team_ID NUMBER primary key, Team_Name VARCHAR(20) NOT NULL, Team_Logo VARCHAR(30), Team_Points NUMBER)", 
                 "CREATE TABLE Venue(Venue_ID Number primary key, Venue_Name VARCHAR(30) NOT NULL, Pitch_Type VARCHAR(15), Time_Zone VARCHAR(10), Capacity Number)", 
                 "CREATE TABLE Player(Player_ID NUMBER primary key, Player_Name VARCHAR(30) NOT NULL, Player_Role VARCHAR(12) NOT NULL, Player_Image VARCHAR(30), Total_Matches_Played NUMBER, Team_ID REFERENCES Team(Team_ID), CONSTRAINT Player_Role CHECK (Player_Role in ('Batsman', 'Bowler', 'All-Rounder')))", 
                 "CREATE TABLE Player_Age (Player_ID REFERENCES Player(Player_ID), Player_DoB DATE NOT NULL, Player_Age NUMBER as (2023 - (to_number(to_char(Player_DoB,'YYYY')))) virtual)", 
                 "CREATE TABLE Match_Info (Match_ID INT Unique NOT NULL, Team_1 references Team(Team_ID), Team_2 references Team(Team_ID), Toss_won VARCHAR(255), CONSTRAINT Toss_won CHECK (Toss_won in (Team_1, Team_2)), PRIMARY KEY (MATCH_ID, Team_1, Team_2))", 
                 "CREATE TABLE Match_Team1_Info (Match_ID references Match_Info (Match_ID), Team_1 references Team (Team_ID), Team1_runs Number NOT NULL, Team1_overs FLOAT NOT NULL, Team1_wickets NUMBER NOT NULL, CONSTRAINT Team1_wickets CHECK ((Team1_wickets >=0) and (Team1_wickets <= 10)), CONSTRAINT Team1_overs CHECK ((Team1_overs >=0) and (Team1_overs <= 50)), PRIMARY KEY ( Match_ID, Team_1 ))", 
                 "CREATE TABLE Match_Team2_Info (Match_ID references Match_Info (Match_ID), Team_2 references Team (Team_ID), Team2_runs NUMBER NOT NULL, Team2_overs FLOAT NOT NULL, Team2_wickets NUMBER NOT NULL, CONSTRAINT Team2_wickets CHECK ((Team2_wickets >=0) and (Team2_wickets <= 10)), CONSTRAINT Team2_overs CHECK ((Team2_overs >=0) and (Team2_overs <= 50)), PRIMARY KEY ( Match_ID, Team_2 ))", 
                 "CREATE TABLE MatchResult (Match_ID references Match_Info (Match_ID), Team_1 references Team(Team_ID), Team_2 references Team(Team_ID), Team1_runs NUMBER Not null, Team2_runs NUMBER not Null, Winner Number as ( Case When (Team1_runs > Team2_runs) Then Team_1 else Team_2 End), Loser Number as (Case When (Team1_runs < Team2_runs) Then Team_1 else Team_2 End), CONSTRAINT Team_2 CHECK (Team_2 != Team_1), PRIMARY KEY (Match_ID, Team_1, Team_2))",
                 "COMMIT"};
    }

    private String[] getInsertValuesQueries() {
        return new String[]{"insert into Team values(1, 'India', 'india.png', 12)",
            "insert into Team values(86, 'Australia', 'australia.png', 10)",
            "insert into Team values(151, 'New Zealand', 'nz.png', 10)",
            "insert into Team values(721, 'Pakistan', 'pakistan.png', 12)",
            "insert into Team values(216, 'Sri Lanka', 'sl.png', 6)",
            "insert into Team values(427, 'Bangladesh', 'bangladesh.png', 8)",
            "INSERT INTO Player (Player_ID, Player_Name, Player_Role, Player_Image, Total_Matches_Played, Team_ID) VALUES (1, 'Sachin Tendulkar', 'Batsman', 'sachin.jpg', 463, 1)",
            "INSERT INTO Player (Player_ID, Player_Name, Player_Role, Player_Image, Total_Matches_Played, Team_ID) VALUES (2, 'Rahul Dravid', 'Batsman', 'dravid.jpg', 344, 1)",
            "INSERT INTO Player (Player_ID, Player_Name, Player_Role, Player_Image, Total_Matches_Played, Team_ID) VALUES (3, 'Babar Azam', 'Batsman', 'babar.jpg', 75, 721)",
            "INSERT INTO Player (Player_ID, Player_Name, Player_Role, Player_Image, Total_Matches_Played, Team_ID) VALUES (4, 'Shaheen Afridi', 'Bowler', 'shaheen.jpg', 35, 721)",
            "INSERT INTO Player (Player_ID, Player_Name, Player_Role, Player_Image, Total_Matches_Played, Team_ID) VALUES (5, 'Steve Smith', 'Batsman', 'smith.jpg', 118, 86)",
            "INSERT INTO Player (Player_ID, Player_Name, Player_Role, Player_Image, Total_Matches_Played, Team_ID) VALUES (6, 'Pat Cummins', 'Bowler', 'cummins.jpg', 32, 86)",
            "INSERT INTO Player (Player_ID, Player_Name, Player_Role, Player_Image, Total_Matches_Played, Team_ID) VALUES (7, 'Nishil Kapadia', 'Bowler', 'nish.jpg', 32, 1)",
            "INSERT INTO Player_Age(Player_ID, Player_DOB) Values (1, TO_DATE('1973-04-24', 'YYYY-MM-DD'))",
            "INSERT INTO Player_Age(Player_ID, Player_DOB) Values (2, TO_DATE('1973-01-11', 'YYYY-MM-DD'))",
            "INSERT INTO Player_Age(Player_ID, Player_DOB) Values (3, TO_DATE('1987-03-14', 'YYYY-MM-DD'))",
            "INSERT INTO Player_Age(Player_ID, Player_DOB) Values (4, TO_DATE('1998-02-27', 'YYYY-MM-DD'))",
            "INSERT INTO Player_Age(Player_ID, Player_DOB) Values (5, TO_DATE('2000-09-24', 'YYYY-MM-DD'))",
            "INSERT INTO Player_Age(Player_ID, Player_DOB) Values (6, TO_DATE('1999-05-13', 'YYYY-MM-DD'))",
            "INSERT INTO Player_Age(Player_ID, Player_DOB) Values (7, TO_DATE('1992-10-20', 'YYYY-MM-DD'))",
            "INSERT INTO Venue (Venue_ID, Venue_Name, Pitch_Type, Time_Zone, Capacity) VALUES (1, 'Eden Gardens', 'Grass', 'IST', 68000)",
            "INSERT INTO Venue (Venue_ID, Venue_Name, Pitch_Type, Time_Zone, Capacity) VALUES (2, 'Wankhede Stadium', 'Turf', 'IST', 45000)",
            "INSERT INTO Venue (Venue_ID, Venue_Name, Pitch_Type, Time_Zone, Capacity) VALUES (3, 'M. Chinnaswamy Stadium', 'Grass', 'IST', 38000)",
            "INSERT INTO Venue (Venue_ID, Venue_Name, Pitch_Type, Time_Zone, Capacity) VALUES (4, 'Rajiv Gandhi Stadium', 'Turf', 'IST', 55000)",
            "INSERT INTO Venue (Venue_ID, Venue_Name, Pitch_Type, Time_Zone, Capacity) VALUES (5, 'PCA Stadium, Mohali', 'Grass', 'IST', 28000)",
            "INSERT INTO Venue (Venue_ID, Venue_Name, Pitch_Type, Time_Zone, Capacity) VALUES (6, 'Sardar Patel Stadium', 'Turf', 'IST', 54000)",
            "INSERT INTO Venue (Venue_ID, Venue_Name, Pitch_Type, Time_Zone, Capacity) VALUES (7, 'Feroz Shah Kotla Ground', 'Grass', 'IST', 42000)",
            "INSERT INTO Venue (Venue_ID, Venue_Name, Pitch_Type, Time_Zone, Capacity) VALUES (8, 'M. A. Chidambaram Stadium', 'Turf', 'IST', 50000)",
            "INSERT INTO Venue (Venue_ID, Venue_Name, Pitch_Type, Time_Zone, Capacity) VALUES (9, 'Holkar Cricket Stadium', 'Grass', 'IST', 30000)",
            "INSERT INTO Venue (Venue_ID, Venue_Name, Pitch_Type, Time_Zone, Capacity) VALUES (10, 'Sawai Mansingh Stadium', 'Turf', 'IST', 30000)",
            "INSERT INTO Umpire (Umpire_ID, Umpire_name, Nationality) VALUES (1, 'Gary Ortiz', 'New Zealand')",
            "INSERT INTO Umpire (Umpire_ID, Umpire_name, Nationality) VALUES (2, 'Alice Johnson', 'Australia')",
            "INSERT INTO Umpire (Umpire_ID, Umpire_name, Nationality) VALUES (3, 'David Lee', 'India')",
            "INSERT INTO Umpire (Umpire_ID, Umpire_name, Nationality) VALUES (4, 'Linda Wilson', 'South Africa')",
            "INSERT INTO Umpire (Umpire_ID, Umpire_name, Nationality) VALUES (5, 'Michael Brown', 'New Zealand')",
            "INSERT INTO Umpire (Umpire_ID, Umpire_name, Nationality) VALUES (6, 'Sophia Miller', 'Pakistan')",
            "INSERT INTO Umpire (Umpire_ID, Umpire_name, Nationality) VALUES (7, 'William Davis', 'Sri Lanka')",
            "INSERT INTO Umpire (Umpire_ID, Umpire_name, Nationality) VALUES (8, 'Emily Clark', 'West Indies')",
            "INSERT INTO Umpire (Umpire_ID, Umpire_name, Nationality) VALUES (9, 'James Wilson', 'Bangladesh')",
            "INSERT INTO Umpire (Umpire_ID, Umpire_name, Nationality) VALUES (10, 'Olivia Anderson', 'Zimbabwe')",
            "INSERT INTO Match_Info (Match_ID, Team_1, Team_2, Toss_won) VALUES (1, 151, 721, 151)",
            "INSERT INTO Match_Info (Match_ID, Team_1, Team_2, Toss_won) VALUES (2, 86, 216, 86)",
            "INSERT INTO Match_Info (Match_ID, Team_1, Team_2, Toss_won) VALUES (3, 216, 721, 216)",
            "INSERT INTO Match_Info (Match_ID, Team_1, Team_2, Toss_won) VALUES (4, 1, 427, 1)",
            "INSERT INTO Match_Info (Match_ID, Team_1, Team_2, Toss_won) VALUES (5, 86, 1, 1)",
            "INSERT INTO Match_Info (Match_ID, Team_1, Team_2, Toss_won) VALUES (6, 721, 86, 86)",
            "INSERT INTO Match_Info (Match_ID, Team_1, Team_2, Toss_won) VALUES (7, 86, 151, 151)",
            "INSERT INTO Match_Team1_Info (Match_ID, Team_1, Team1_runs, Team1_overs, Team1_wickets) VALUES (1, 151, 220, 45.3, 8)",
            "INSERT INTO Match_Team1_Info (Match_ID, Team_1, Team1_runs, Team1_overs, Team1_wickets) VALUES (2, 86, 310, 49.5, 5)",
            "INSERT INTO Match_Team1_Info (Match_ID, Team_1, Team1_runs, Team1_overs, Team1_wickets) VALUES (3, 216, 180, 40.2, 9)",
            "INSERT INTO Match_Team1_Info (Match_ID, Team_1, Team1_runs, Team1_overs, Team1_wickets) VALUES (4, 1, 260, 48.0, 6)",
            "INSERT INTO Match_Team1_Info (Match_ID, Team_1, Team1_runs, Team1_overs, Team1_wickets) VALUES (5, 86, 290, 49.0, 7)",
            "INSERT INTO Match_Team1_Info (Match_ID, Team_1, Team1_runs, Team1_overs, Team1_wickets) VALUES (6, 721, 290, 49.0, 7)",
            "INSERT INTO Match_Team1_Info (Match_ID, Team_1, Team1_runs, Team1_overs, Team1_wickets) VALUES (7, 86, 290, 49.0, 7)",
            "INSERT INTO Match_Team2_Info (Match_ID, Team_2, Team2_runs, Team2_overs, Team2_wickets) VALUES (1, 721, 200, 42.0, 7)",
            "INSERT INTO Match_Team2_Info (Match_ID, Team_2, Team2_runs, Team2_overs, Team2_wickets) VALUES (2, 216, 280, 48.2, 4)",
            "INSERT INTO Match_Team2_Info (Match_ID, Team_2, Team2_runs, Team2_overs, Team2_wickets) VALUES (3, 721, 150, 35.1, 8)",
            "INSERT INTO Match_Team2_Info (Match_ID, Team_2, Team2_runs, Team2_overs, Team2_wickets) VALUES (4, 427, 240, 47.3, 5)",
            "INSERT INTO Match_Team2_Info (Match_ID, Team_2, Team2_runs, Team2_overs, Team2_wickets) VALUES (5, 1, 270, 49.5, 6)",
            "INSERT INTO Match_Team2_Info (Match_ID, Team_2, Team2_runs, Team2_overs, Team2_wickets) VALUES (6, 86, 270, 49.5, 6)",
            "INSERT INTO Match_Team2_Info (Match_ID, Team_2, Team2_runs, Team2_overs, Team2_wickets) VALUES (7, 151, 270, 49.5, 6)", "COMMIT"};
    }

    private String[] getSelectQueries() {
        return new String[]{"Select Player_ID, Player_Name FROM Player",
                "Select * FROM Player where Player_Role='Batsman'",
                "Select Umpire_name FROM Umpire",
                "Select Venue_Name, Capacity from Venue",
                "Select * from Player_Age where Player_Age>28",
                "Select * from Player where Team_ID=1",
                "Select * from Match_Info where (Team_1=86 or Team_2=86)",
                "Select * from Umpire where Nationality='India'",
                "Select * from Venue where Pitch_Type='Grass'",
                "COMMIT"};
    }

    public void connectDatabase(String command) {
        Connection connection = baseDatabaseConnection();
        //CREATE TABLE CODE
        if(command.equalsIgnoreCase("Create"))
        {
            String[] query = getCreateTablesQueries();
            try (Statement stmt = connection.createStatement()) {
                for(int i = 0; i<query.length; i++)
                {
                    ResultSet rs = stmt.executeQuery(query[i]);
                }
                dialogBox("Tables Created");
			} 
            catch (SQLException e) {
                dialogBox(e.getMessage());
			}
        }

        //DROP TABLE CODE
        if(command.equalsIgnoreCase("Drop"))
        {
            String[] query = getDropTablesQueries();
            try (Statement stmt = connection.createStatement()) {
                for(int i = 0; i<query.length; i++)
                {
                    ResultSet rs = stmt.executeQuery(query[i]);
                }
                dialogBox("Tables Dropped");   
			} 
            catch (SQLException e) {
                dialogBox(e.toString());
			}
        }
            
        //POPULATE TABLE CODE
        if(command.equalsIgnoreCase("Populate"))
        {
			String[] query = getInsertValuesQueries();
			try (Statement stmt = connection.createStatement()) {
			    for (int i = 0; i <query.length; i++) 
                {
                    ResultSet rs = stmt.executeQuery(query[i]);
                }
                dialogBox("Tables Populated");
			} 
            catch (SQLException e) {
                dialogBox(e.toString());
			}
        }


        //QUERY TABLE
        if(command.equalsIgnoreCase("Query"))
        {
                String[] query = getSelectQueries();
                try (Statement stmt = connection.createStatement()) {
                    
                for(int i = 0; i<query.length; i++)
                {
                    System.out.println();
                    System.out.println();
                    int flag=0;
                    ResultSet rs = stmt.executeQuery(query[i]);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnsNumber = rsmd.getColumnCount();
                    while (rs.next()) {
                        for (int j = 1; j <= columnsNumber; j++) {
                            if((j == 1) && (flag==0))
                            {
                                 for (int k = 1; k <= columnsNumber; k++) {
                                    System.out.print(rsmd.getColumnName(k) + "     ");
                                 }
                                 flag=1;
                                 System.out.println("\n----------------------------------------------------------------------------------------");
                            }
                            if (j > 1) System.out.print("   ");
                            String columnValue = rs.getString(j);
                            System.out.print(columnValue + "      ");
                        }
                    System.out.println("");
                    }
                }
                dialogBox("Success! - Queries printed");
			} catch (SQLException e) {
                dialogBox(e.toString());
			}
        }
        try
        {
            connection.close();
        }
        catch(SQLException e)
        { 
            dialogBox("Error while closing connection:"+e.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cricket Database");

        
        Image backgroundImage = new Image("/Images/cricketbg.jpg");

        //creates the background image
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );

        // Create a layout container (StackPane) and set the background
        StackPane root = new StackPane();
        root.setBackground(new Background(background));

        // Create content (your existing code)
        Label title = new Label("Cricket DB");
        title.setTextFill(Color.web("#FF0000"));
        title.setFont(new Font(50));
        title.setUnderline(true);

        Button btn = createStyledButton("Drop Tables", "-fx-background-color: linear-gradient(#B5FFF3, #80ffff); -fx-text-fill: black;");
        Button btn1 = createStyledButton("Create Tables", "-fx-background-color: linear-gradient(#B5FFF3, #80ffff); -fx-text-fill: black;");
        Button btn2 = createStyledButton("Populate Tables", "-fx-background-color: linear-gradient(#B5FFF3,#80ffff ); -fx-text-fill: black;");
        Button btn3 = createStyledButton("Query Tables", "-fx-background-color: linear-gradient(#B5FFF3, #80ffff); -fx-text-fill: black;");
        Button btn4 = createStyledButton("Exit", "-fx-background-color: linear-gradient(#B5FFF3, #80ffff); -fx-text-fill: black;");
        Button btn5 = createStyledButton("Update Tables", "-fx-background-color: linear-gradient(#B5FFF3, #80ffff); -fx-text-fill: black;");
        Button btn6 = createStyledButton("Delete Tables", "-fx-background-color: linear-gradient(#B5FFF3, #80ffff); -fx-text-fill: black;");
        Button btn7 = createStyledButton("Search Tables", "-fx-background-color: linear-gradient(#B5FFF3, #80ffff); -fx-text-fill: black;");

        // Set actions for buttons
        btn.setOnAction(event -> connectDatabase("Drop"));
        btn1.setOnAction(event -> connectDatabase("Create"));
        btn2.setOnAction(event -> connectDatabase("Populate"));
        btn3.setOnAction(event -> connectDatabase("Query"));
        btn5.setOnAction(event -> updateValueDialogBox());
        btn6.setOnAction(event -> deleteValueDialogBox());
        btn7.setOnAction(event -> searchValueDialogBox());
        btn4.setOnAction(event -> System.exit(0));

        // Create a GridPane for layout
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);

        // ColumnConstraints to ensure the title spans the entire width
        ColumnConstraints colConstraints = new ColumnConstraints();
        colConstraints.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(colConstraints, colConstraints);

        // Add components to the grid
        gridPane.add(title, 1, 0, 1, 1);

        // HBox for the first row of buttons (Drop Tables and Create Tables)
        HBox firstRowButtons = new HBox(10);
        firstRowButtons.setAlignment(Pos.CENTER);
        firstRowButtons.getChildren().addAll(btn, btn1);

        // HBox for the second row of buttons (Populate Tables and Query Tables)
        HBox secondRowButtons = new HBox(10);
        secondRowButtons.setAlignment(Pos.CENTER);
        secondRowButtons.getChildren().addAll(btn2, btn3);

        HBox thirdRowButtons = new HBox(10);
        thirdRowButtons.setAlignment(Pos.CENTER);
        thirdRowButtons.getChildren().addAll(btn5, btn6);

        // VBox for buttons, centered and spaced out
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(firstRowButtons, secondRowButtons,thirdRowButtons, btn7, btn4);

        // Add VBox to the grid
        gridPane.add(buttonBox, 0, 1, 2, 1);

        // Enable grid lines (for visualization during development) change this when viewing final
        gridPane.setGridLinesVisible(false);

        // Add grid to the StackPane
        root.getChildren().add(gridPane);

        // Create a scene with the StackPane
        Scene scene = new Scene(root, 800, 500);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createStyledButton(String text, String style) {
        Button button = new Button(text);
        button.setStyle(style);
        return button;
    }
    public static void main(String[] args) {
        launch(args);
    }
}