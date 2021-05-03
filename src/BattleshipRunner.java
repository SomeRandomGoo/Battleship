import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class BattleshipRunner extends Application{
    
    private final int BOARD_SIZE = 10;
    private Square selection;//will use this to save selected square when setting up ships
    
    public static void main(String args[]){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        //Strings used to determing what stage of the game the players are in
        String SETUP = "Set up your ships";
        String DIRECTION = "Pick an orientation";
        String COMPSETUP = "Computer setting up";
        String PLAYING = "Choose a space to shoot at";
        
        //gridPane that holds gridPanes
        GridPane container = new GridPane();
        
        //sets up player grids
        GridPane grid = new GridPane();
        GridPane oppGrid = new GridPane();

        Label messages = new Label(SETUP);//determines game state
        Label errorMessages = new Label("");//any errors are displayed in here

        //players
        HumanPlayer player1 = new HumanPlayer();
        ComputerPlayer player2 = new ComputerPlayer();
        
        Label shipSetup = new Label();//which ship is being set
        
        Label currPlayer = new Label("player1");//determines who's turn it is
        
        //hit/miss indicators for both players
        Label hitIndicator = new Label();
        Label sunkIndicator = new Label();
        Label opphitIndicator = new Label();
        Label oppsunkIndicator = new Label();
        
        //Buttons to finalize board placements
        Button confirm = new Button("yes");
        Button deny = new Button("no");

        //Button behaviors
        confirm.setOnMouseClicked(event->{
            messages.setText(COMPSETUP);
            shipSetup.setText("");
            player2.placeShips(0, 0, 'a'); //dummy arguments do nothing
            messages.setText(PLAYING);
            oppsunkIndicator.setText("");
            for(int k = 0; k < BOARD_SIZE*BOARD_SIZE; k++){
                Square editing = (Square)grid.getChildren().get(k);
                editing.setFill(Color.BLUE);
            }
            container.getChildren().removeAll(confirm,deny);
        });

        deny.setOnMouseClicked(event->{
            if(currPlayer.getText().equals("player1")){
                player1.clearBoard();
                player1.setCurrIndex(0);
                messages.setText(SETUP);
                for(int k = 0; k < BOARD_SIZE*BOARD_SIZE; k++){
                    Square editing = (Square)grid.getChildren().get(k);
                    editing.setFill(Color.BLUE);
                }
                oppsunkIndicator.setText("");
                container.getChildren().removeAll(confirm,deny);
            }
        });
        
        
        //Sets up boards of squares and sets their behaviors when clicked on
        //defaults for base squares is blue fill and white stroke

        //Computer Board
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                Square cell = new Square(0,0,75,i,j);
                cell.setFill(Color.BLUE);
                cell.setStroke(Color.WHITE);
                oppGrid.add(cell,i,j);
            }
        }

        //Player Board
        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                Square cell = new Square(0,0,75,i,j);
                cell.setFill(Color.BLUE);
                cell.setStroke(Color.WHITE);
                grid.add(cell,i,j);
                cell.setOnMouseClicked(event ->{
                    //when messages text is playing the human player will shoot at a square
                    if(messages.getText().equals(PLAYING)){
                        //makes sure it's player1 going
                        if(currPlayer.getText().equals("player1")){
                            //takes row and col from square and sents it to the take turn method
                                String outcome = player1.takeTurn(player2, cell.getRow(), cell.getCol());

                                //if Hit is returned it sets the cell as black and changes turn to player 2
                                if(outcome.equals("Hit")){

                                    hitIndicator.setText("Hit");
                                    cell.setFill(Color.BLACK);
                                    currPlayer.setText("player2");

                                //if miss is returned, sets cell as white and changes turn 
                                }else if(outcome.equals("Miss")){

                                    hitIndicator.setText("Miss");
                                    cell.setFill(Color.WHITE);
                                    currPlayer.setText("player2");
                                
                                //if already shot area selected says so and doesn't change turn
                                }else if(outcome.equals("Area already shot")){
                                    
                                    hitIndicator.setText("You already shot at that area, go again");
                                    
                                //returns a hit phrase of what is sunk, anything other than a ship being sunk should be made into an if statement
                                }else{
                                    
                                    cell.setFill(Color.BLACK);
                                    hitIndicator.setText("Hit");
                                    sunkIndicator.setText("You" + outcome);
                                    if(!player2.checkStillAlive()){
                                        messages.setText("Player1 wins");
                                    }
                                    currPlayer.setText("player2");
                                }
                            }
                            
                            //automatically takes player2's turn if they are a computer
                            if(currPlayer.getText().equals("player2") && player2 instanceof ComputerPlayer){
                                String outcome = "";
                                int r = 0;
                                int c = 0;

                                //ComputerPlayer class has an array of predecided on targets this checks if there is one
                                if(player2.getTarget() != null){
                                    do{
                                        
                                        r = player2.getTarget().getRow();
                                        c = player2.getTarget().getCol();
                                        outcome = player2.takeTurn(player1,r,c);
                                        
                                    }while(outcome.equals("Area already shot") && player2.getTarget() != null);
                                    //while loop to make sure it won't fire on something it already has shot and in that case if there is a target still present
                                }
                                
                                //randomly picks board square until it hits a new spot in the case outcome is not set or do while loop in previous if
                                //resulted in "Area already shot"
                                if(outcome.equals("Area already shot") || outcome.equals("")){

                                    do{
            
                                        r = (int)(Math.random()*10);
                                        c = (int)(Math.random()*10);
                                        outcome = player2.takeTurn(player1, r,c);

                                    }while(outcome.equals("Area already shot"));

                                }



                                //if Hit is returned it sets the cell as Black
                                if(outcome.equals("Hit")){

                                    opphitIndicator.setText("Opponent Hit");
                                    Square editing = (Square)oppGrid.getChildren().get((r*BOARD_SIZE + c));
                                    editing.setFill(Color.BLACK);
                                
                                //if Miss is returned it sets the cell as White
                                }else if(outcome.equals("Miss")){
                                    
                                    opphitIndicator.setText("Opponent Miss");
                                    Square editing = (Square)oppGrid.getChildren().get((r*BOARD_SIZE + c));
                                    editing.setFill(Color.WHITE);
                                
                                //else makes the cell Black and tells what ship has been sunk
                                }else{
                                    
                                    opphitIndicator.setText("Opponent Hit");
                                    Square editing = (Square)oppGrid.getChildren().get((r*BOARD_SIZE + c));
                                    editing.setFill(Color.BLACK);
                                    oppsunkIndicator.setText("Opponent has "+ outcome);

                                    if(!player1.checkStillAlive()){
                                        messages.setText("Player2 wins");
                                    }

                                }
                                    currPlayer.setText("player1");//once a spot has been shot, changes to player1
                                }
                            }

                    //if messages text is SETUP it saves the clicked on cell to selection to be used later if the area is not occupied
                    //otherwise it will say the area is occupied and return back
                    if(messages.getText().equals(SETUP)){
                        if(player1.areaIsFree(cell.getRow(),cell.getCol())){
                            selection = cell;
                            messages.setText(DIRECTION);
                            errorMessages.setText("");
                            selection.setFill(Color.RED);
                        }else{
                            errorMessages.setText("Area is occupied");
                        }
                    }
                    
                    //if messages is equal to DIRECTION it will take selection which was selected in SETUP
                    //and determine the orientation of the ship with the square chosen in this step
                    else if(messages.getText().equals(DIRECTION)){
                        switch(selection.compareTo(cell)){
                            //If the selected cell is the same as the first one it will change the cell back to blue and go back to SETUP
                            case 0:
                                messages.setText(SETUP);
                                selection.setFill(Color.BLUE);
                                break;
                            //cases 1-4 are determined by the compareTo function and coorespond to a direction
                            //n:north, s:south, w:west, e:east
                            //they call the setShipCoor functions to check if it is a valid row if not goes back to setup mode
                            //otherwise the selected spots will turn green on the board
                            case 1:
                                if(!player1.setShipCoor(selection.getRow(), selection.getCol(), 'n')){
                                    errorMessages.setText("Not a valid position");
                                    selection.setFill(Color.BLUE);
                                }else{
                                    for(int k = 0; k < player1.prevShipSize(); k++){
                                        Square editing = (Square)grid.getChildren().get((selection.getRow())*BOARD_SIZE + selection.getCol() - k);
                                        editing.setFill(Color.GREEN);
                                    }
                                }
                                messages.setText(SETUP);
                                break;
                            case 2:
                                if(!player1.setShipCoor(selection.getRow(), selection.getCol(), 's')){
                                    errorMessages.setText("Not a valid position");
                                    selection.setFill(Color.BLUE);
                                }else{
                                    for(int k = 0; k < player1.prevShipSize(); k++){
                                        Square editing = (Square)grid.getChildren().get((selection.getRow())*BOARD_SIZE + selection.getCol() + k);
                                        editing.setFill(Color.GREEN);
                                    }
                                }
                                messages.setText(SETUP);
                                break;
                            case 3:
                               if(!player1.setShipCoor(selection.getRow(), selection.getCol(), 'w')){
                                    errorMessages.setText("Not a valid position");
                                    selection.setFill(Color.BLUE);
                                }else{
                                    for(int k = 0; k < player1.prevShipSize(); k++){
                                        Square editing = (Square)grid.getChildren().get((selection.getRow()-k)*BOARD_SIZE + selection.getCol());
                                        editing.setFill(Color.GREEN);
                                    }
                                }
                                messages.setText(SETUP);
                                break;
                            case 4:
                                if(!player1.setShipCoor(selection.getRow(), selection.getCol(), 'e')){
                                    errorMessages.setText("Not a valid position");
                                    selection.setFill(Color.BLUE);
                                }else{
                                    for(int k = 0; k < player1.prevShipSize(); k++){
                                        Square editing = (Square)grid.getChildren().get((selection.getRow()+k)*BOARD_SIZE + selection.getCol());
                                        editing.setFill(Color.GREEN);
                                    }
                                }
                                messages.setText(SETUP);
                                break;
                            //for cells that are not directly north, south, east, or west of cell in selection
                            case -1:
                                errorMessages.setText("Ship either goes out of bounds, intersects something else,"
                                + "or not a valid direction");
                                messages.setText(SETUP);
                                selection.setFill(Color.BLUE);
                                break;
                        }
                        //checks if all ships are set for a player1 then sets ship for player2 (assuming they are a computer)
                        //otherwise it will display the next ship size to be placed
                        if(player1.allShipsSet()){
                            shipSetup.setText("");
                            messages.setText("");
                            container.add(confirm,1,9);
                            container.add(deny,1,10);
                            oppsunkIndicator.setText("Confirm Placement?");
                        }else{
                            shipSetup.setText("Current ship to set " + player1.currShipSize());
                        }
                    }
                });
            }
        }

        shipSetup.setText("Current ship to set " + player1.currShipSize());//tells shipSetup the first ship to be placed
        //sets up the places of all labels on the screen
        container.add(messages,1,0);
        container.add(errorMessages,1,1);
        container.add(shipSetup,1,6);
        container.add(currPlayer,1,3);
        container.add(hitIndicator,1,4);
        container.add(sunkIndicator,1,5);
        container.add(opphitIndicator,1,7);
        container.add(oppsunkIndicator,1, 8);

        container.add(grid,0,0);
        container.add(oppGrid,2,0);

        container.add(new Label("Player1"),0,1);
        container.add(new Label("Player2"),3,1);
        
        Scene scene = new Scene(container,1725,925);
        stage.setScene(scene);
        stage.show();
    }
}
