import javafx.application.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.event.*;
import javafx.geometry.*;
import java.io.*;
import java.util.Random;
import java.util.Scanner;
import javafx.scene.image.*;

@SuppressWarnings("restriction")
public class LetsMakeADeal extends Application {
	private int switch_wins, num_wins, num_switches, total_games;
	private int playerDoor, carDoor, goatDoor, mysteryDoor;
	private static Random intGenerator;
	private final String statFileName = "MontyHallStats.txt";
	private HBox optionsBox;
	private String selectText, carText, goatText;
	private Label gameText;
	private Button switchButton, stayButton;
	private Image doorImg, goatImg, carImg;
	private ImageView doorA, doorB, doorC;
	private boolean canOpenDoor;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void init() throws Exception {
		loadStats();
		intGenerator = new Random();
		canOpenDoor = false;
		File doorFile = new File("src/montyDoor.jpg");
		doorImg = new Image(doorFile.toURI().toString(), 100, 200, true, false);
		File goatFile = new File("src/montyGoat.png");
		goatImg = new Image(goatFile.toURI().toString(), 100, 200, true, false);
		File carFile = new File("src/montyCar.png");
		carImg = new Image(carFile.toURI().toString(), 100, 200, true, false);
	}

	@Override
	public void start(Stage stage0) throws Exception {
		stage0.setTitle("Let's Make A Deal");

		Button gameButton = new Button("New Game");
		gameButton.setOnAction(new NewGameHandler());
		Button statButton = new Button("Show Stats");
		statButton.setOnAction(new DisplayStats());
		optionsBox = new HBox(10, gameButton, statButton);
		optionsBox.setAlignment(Pos.CENTER);
		
		doorA = new ImageView(doorImg);
		doorB = new ImageView(doorImg);
		doorC = new ImageView(doorImg);
		doorA.setOnMouseClicked(new DoorHandler(0));
		doorB.setOnMouseClicked(new DoorHandler(1));
		doorC.setOnMouseClicked(new DoorHandler(2));
		Label doorALabel = new Label("A");
		Label doorBLabel = new Label("B");
		Label doorCLabel = new Label("C");
		VBox doorABox = new VBox(10, doorA, doorALabel);
		doorABox.setAlignment(Pos.CENTER);
		VBox doorBBox = new VBox(10, doorB, doorBLabel);
		doorBBox.setAlignment(Pos.CENTER);
		VBox doorCBox = new VBox(10, doorC, doorCLabel);
		doorCBox.setAlignment(Pos.CENTER);
		HBox doors = new HBox(10, doorABox, doorBBox, doorCBox);
		doors.setAlignment(Pos.CENTER);
		
		
		switchButton = new Button("Switch");
		switchButton.setOnAction(new WinHandler(true));
		stayButton = new Button("Don't Switch");
		stayButton.setOnAction(new WinHandler(false));
		switchButton.setDisable(true);
		stayButton.setDisable(true);
		HBox switchBox = new HBox(10, switchButton, stayButton);
		switchBox.setAlignment(Pos.CENTER);
		
		gameText = new Label();
		VBox buttonBox = new VBox(25, optionsBox, doors, switchBox, gameText);
		buttonBox.setAlignment(Pos.CENTER);
		Scene game = new Scene(buttonBox, 350, 450);
		stage0.setScene(game);
		stage0.show();
	}

	@Override
	public void stop() throws Exception {
		saveStats();		
	}

	public void loadStats()
	{
		File statFile = new File(statFileName);

		total_games = 0;
		num_wins = 0;
		num_switches = 0;
		switch_wins = 0;
		
		if (statFile.exists())
		{
			try {
				Scanner statScanner = new Scanner(statFile);
				try
				{
					total_games = statScanner.nextInt();					
					num_wins = statScanner.nextInt();
					num_switches = statScanner.nextInt();
					switch_wins = statScanner.nextInt();
				} catch (Exception ex) {
					System.out.println("Data for " + statFileName + " has been corrupted");
					ex.printStackTrace();
				}
				statScanner.close();
			} catch (FileNotFoundException e) {
				System.out.println(statFileName + " cannot be opened");
				e.printStackTrace();
			}
		}
	}
	
	public void saveStats()
	{
		try {
			PrintWriter saveFile = new PrintWriter(statFileName);
			saveFile.println(total_games);					
			saveFile.println(num_wins);
			saveFile.println(num_switches);
			saveFile.println(switch_wins);
			saveFile.close();
		} catch (FileNotFoundException e) {
			System.out.println(statFileName + " cannot be saved");
			e.printStackTrace();
		}
	}
		
	public class NewGameHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			optionsBox.setVisible(false);
			canOpenDoor = true;
			doorA.setImage(doorImg);
			doorB.setImage(doorImg);
			doorC.setImage(doorImg);
			carDoor = intGenerator.nextInt(3);
			gameText.setText("Select a door");
		}	
	}

	public class DoorHandler implements EventHandler<MouseEvent> {
		private int door;
		
		public DoorHandler(int doorNum)
		{
			door = doorNum;
		}

		@Override
		public void handle(MouseEvent event) {
			if (!canOpenDoor)
				return;
			
			playerDoor = door;
			showDoorSelection();

			Random goatGen = new Random();
			do {
				goatDoor = goatGen.nextInt(3);
			} while (goatDoor == carDoor || goatDoor == playerDoor);
			mysteryDoor = 3 - goatDoor - playerDoor;
			switch(goatDoor)
			{
				case 0: goatText = "Door A has a goat.";
						doorA.setImage(goatImg);
						break;
				case 1: goatText = "Door B has a goat.";
						doorB.setImage(goatImg);
						break;
				case 2: goatText = "Door C has a goat.";
						doorC.setImage(goatImg);
						break;
			}
			switchButton.setDisable(false);
			stayButton.setDisable(false);
			gameText.setText(selectText + "\n" + goatText + "\nDo you want to switch doors?");
		}
		
	}
	
	public class WinHandler implements EventHandler<ActionEvent> {
		private boolean switchDoor;
		
		public WinHandler(boolean swDoor)
		{
			switchDoor = swDoor;
		}

		@Override
		public void handle(ActionEvent event) {
			if (switchDoor)
			{
				num_switches++;
				playerDoor = mysteryDoor;
				showDoorSelection();
			}
			switch(carDoor)
			{
				case 0: carText = "Door A has a car.";
						doorA.setImage(carImg);
						doorB.setImage(goatImg);
						doorC.setImage(goatImg);
						break;
				case 1: carText = "Door B has a car.";
						doorA.setImage(goatImg);
						doorB.setImage(carImg);
						doorC.setImage(goatImg);
						break;
				case 2: carText = "Door C has a car.";
						doorA.setImage(goatImg);
						doorB.setImage(goatImg);
						doorC.setImage(carImg);
						break;
			}
			if (playerDoor == carDoor)
			{
				if (switchDoor)
				{
					switch_wins++;					
				}
				num_wins++;
				gameText.setText(selectText + "\n" + carText + "\nYou win!");
			}
			else
			{
				gameText.setText(selectText + "\n" + carText + "\nYou lose.");
			}
			total_games++;			
			
			optionsBox.setVisible(true);
			switchButton.setDisable(true);
			stayButton.setDisable(true);
			canOpenDoor = false;
		}		
	}
	
	public void showDoorSelection()
	{
		switch(playerDoor)
		{
			case 0: selectText = "You selected Door A.";
					break;
			case 1: selectText = "You selected Door B.";
					break;
			case 2: selectText = "You selected Door C.";
					break;
		}		
	}
	
	public class DisplayStats implements EventHandler<ActionEvent> {

		private Label switch_win, switch_lose, switch_games,
		stay_win, stay_lose, stay_games,
		total_wins, total_losses, games_played,
		winSwitch, winStay, winStat;
		
		@Override
		public void handle(ActionEvent event) {
			
			GridPane statGrid = getData();
			statGrid.setHgap(10);
			statGrid.setVgap(5);
			statGrid.setAlignment(Pos.CENTER);
			
			GridPane statGrid2 = getStats();
			statGrid2.setHgap(10);
			statGrid2.setVgap(5);
			statGrid2.setAlignment(Pos.CENTER);
			
			Button saveButton = new Button("Save Stats to File");
			saveButton.setOnAction(new SaveStats());
			Button clearButton = new Button("Clear Stats");
			clearButton.setOnAction(new ResetStats());
			HBox statButtons = new HBox(10, saveButton, clearButton);
			statButtons.setAlignment(Pos.CENTER);
			
			VBox statBox = new VBox(10, statGrid, statGrid2, statButtons);
			statBox.setAlignment(Pos.CENTER);
			statBox.setPadding(new Insets(10));
			Scene statList = new Scene(statBox);
			Stage statScreen = new Stage();
			statScreen.setTitle("Game Statistics");
			statScreen.setScene(statList);
			statScreen.show();
		}
		
		public GridPane getData()
		{
			switch_win = new Label(Integer.toString(switch_wins));
			switch_lose = new Label(Integer.toString(num_switches - switch_wins));
			switch_games = new Label(Integer.toString(num_switches));
			stay_win = new Label(Integer.toString(num_wins - switch_wins));
			stay_lose = new Label(Integer.toString(total_games - num_switches - num_wins + switch_wins));
			stay_games = new Label(Integer.toString(total_games - num_switches));
			total_wins = new Label(Integer.toString(num_wins));
			total_losses = new Label(Integer.toString(total_games - num_wins));
			games_played = new Label(Integer.toString(total_games));

			Label switch_winLabel = new Label("Switched & Won");
			Label switch_loseLabel = new Label("Switched & Lost");
			Label switch_gamesLabel = new Label("Games Switched");
			Label stay_winLabel = new Label("Not Switched & Won");
			Label stay_loseLabel = new Label("Not Switched & Lost");
			Label stay_gamesLabel = new Label("Games Not Switched");
			Label total_winsLabel = new Label("Games Won");
			Label total_lossesLabel = new Label("Games Lost");
			Label games_playedLabel = new Label("Games Played");
			
			GridPane grid = new GridPane();
			grid.add(switch_winLabel, 0, 0);
			grid.add(switch_win,  1, 0);
			grid.add(switch_loseLabel, 2, 0);
			grid.add(switch_lose, 3, 0);
			grid.add(switch_gamesLabel, 4, 0);
			grid.add(switch_games, 5, 0);
			grid.add(stay_winLabel, 0, 1);
			grid.add(stay_win, 1, 1);
			grid.add(stay_loseLabel, 2, 1);
			grid.add(stay_lose, 3, 1);
			grid.add(stay_gamesLabel, 4, 1);
			grid.add(stay_games, 5, 1);
			grid.add(total_winsLabel, 0, 2);
			grid.add(total_wins, 1, 2);
			grid.add(total_lossesLabel, 2, 2);
			grid.add(total_losses, 3, 2);
			grid.add(games_playedLabel, 4, 2);
			grid.add(games_played, 5, 2);
			
			return grid;

		}
		
		public GridPane getStats()
		{
			double winPercent = 0.0, winSwitchPercent = 0.0, winStayPercent = 0.0;
			if (total_games > 0)
			{
				if (num_switches > 0)
				{
					winSwitchPercent = (switch_wins * 1.0) / (num_switches * 1.0) * 100.0;					
				}
				if (total_games - num_switches > 0)
				{
					winStayPercent = ((num_wins - switch_wins) * 1.0) / ((total_games - num_switches) * 1.0) * 100.0;
				}
				winPercent = (num_wins * 1.0) / (total_games * 1.0) * 100.0;
			}

			
			winSwitch = new Label(String.format("%.3f", winSwitchPercent) + "%");
			winStay = new Label(String.format("%.3f", winStayPercent) + "%");
			winStat = new Label(String.format("%.3f", winPercent) + "%");

			Label winSwitchLabel = new Label("Win Percentage (Switch)");
			Label winStayLabel = new Label("Win Percentage (Not Switch)");
			Label winStatLabel = new Label("Overall Win Percentage");
			
			GridPane grid = new GridPane();
			grid.add(winSwitchLabel, 0, 0);
			grid.add(winSwitch, 1, 0);
			grid.add(winStayLabel, 0, 1);
			grid.add(winStay, 1, 1);
			grid.add(winStatLabel, 0, 2);
			grid.add(winStat, 1, 2);
			
			return grid;
		}
		
		public class ResetStats implements EventHandler<ActionEvent>{

			@Override
			public void handle(ActionEvent event) {
				total_games = 0;
				num_wins = 0;
				num_switches = 0;
				switch_wins = 0;
				
				switch_win.setText(Integer.toString(switch_wins));
				switch_lose.setText(Integer.toString(num_switches - switch_wins));
				switch_games.setText(Integer.toString(num_switches));
				stay_win.setText(Integer.toString(num_wins - switch_wins));
				stay_lose.setText(Integer.toString(total_games - num_switches - num_wins + switch_wins));
				stay_games.setText(Integer.toString(total_games - num_switches));
				total_wins.setText(Integer.toString(num_wins));
				total_losses.setText(Integer.toString(total_games - num_wins));
				games_played.setText(Integer.toString(total_games));
				
				winSwitch.setText(String.format("%.3f", 0.0) + "%");
				winStay.setText(String.format("%.3f", 0.0) + "%");
				winStat.setText(String.format("%.3f", 0.0) + "%");
			}
			
		}
		
		public class SaveStats implements EventHandler<ActionEvent> {
			private String outFileName;
			private Stage saveStage;
			private TextField fileField;

			@Override
			public void handle(ActionEvent event) {
				Label query = new Label("Name of file to be written:");
				fileField = new TextField();

				Button OKbutton = new Button("OK");
				Button cancelButton = new Button("Cancel");
				OKbutton.setOnAction(new ConfirmSave(true));
				cancelButton.setOnAction(new ConfirmSave(false));
				HBox buttonBox = new HBox(10, OKbutton, cancelButton);
				buttonBox.setAlignment(Pos.CENTER);
				
				VBox saveBox = new VBox(10, query, fileField, buttonBox);
				saveBox.setPadding(new Insets(10));
				
				saveStage = new Stage();
				saveStage.setTitle("Save To File");
				Scene saveScene = new Scene(saveBox, 350, 125);
				saveStage.setScene(saveScene);
				saveStage.show();
			}
			
			public class ConfirmSave implements EventHandler<ActionEvent> {
				private boolean willSave;
				public ConfirmSave(boolean save)
				{
					willSave = save;
				}
				
				@Override
				public void handle(ActionEvent event) {
					if (willSave)
					{
						outFileName = fileField.getText();
						printStats(outFileName);
					}
					saveStage.close();
				}
				
				public void printStats(String fileName)
				{
					try {
						PrintWriter output = new PrintWriter(fileName);
						output.println("Games Played: " + total_games);
						output.println("Games Won: " + num_wins);
						output.println("Games Switched: " + num_switches);
						output.println("Games Won & Switched: " + switch_wins);
						output.close();
					} catch (FileNotFoundException e) {
						System.out.println(fileName + " cannot be opened");
						e.printStackTrace();
					}
				}
			}
			
		}
	}

}
