public class ComputerPlayer extends Player{

    Coordinates[] target;//holds planned targets
    Coordinates prevHit;//holds a successful hit that will be compared to target[0] if it exists

    //calls player constructor
    public ComputerPlayer(){
        super();
        target = new Coordinates[4];
        prevHit = null;
    }

    //cycles targets in target array
    public void cycleTarget(){
        for(int j = 0; j < 3 && target[j] != null; j++){
            target[j] = target[j+1];
        }
        target[3] = null;
    }

    //cycles starting from an index
    public void cycleFromIndex(int k){
        for(int j = k; j < 3 && target[j] != null; j++){
            target[j] = target[j+1];
        }
        if(k < 3) target[3] = null;
    }

    //gets the first element in target
    public Coordinates getTarget(){
        return target[0];
    }

    //checks if the coordinate is valid by making sure it's in bounds and a spot that hasn't already been shot
    public boolean isValid(Coordinates check){
        if(check.getRow() < 0 || check.getRow() >= BOARD_SIZE_ROW ||
         check.getCol() < 0 || check.getCol() >= BOARD_SIZE_COL ||
         shot(check.getRow(), check.getCol())) return false;
        else return true;
    }

    //empties target array and prevHit
    private void clearTargets(){
        for(int j = 0; j < 4 && target[j] != null; j++){
            target[j] = null;
        }
        prevHit = null;
    }

    //Parameters are nothing, only exist because are defined in parent class
    @Override
    public boolean placeShips(int r, int c, char d) {
        //keeps going until all ships set
        while(!allShipsSet()){
            //randomly determines row and column
            r = (int)(Math.random() * BOARD_SIZE_ROW);
            c = (int)(Math.random() * BOARD_SIZE_COL);

            //randomly picks a number between 1 and 4 then assigns a direction based on that number
            int direction = (int)(Math.random() * 4 + 1);
            switch(direction){
                case 1:
                    d = 'n';
                    break;
                case 2:
                    d = 'e';
                    break;
                case 3:
                    d = 's';
                    break;
                case 4:
                    d = 'w';
                    break;
            }
            //calls parent to place ships base on randomly generated values
            setShipCoor(r, c, d);
        }
        //returns true when done
        return true;
    }

    //takes shot during its turn returns result of shot as a string
    //takes other player and two ints
    //player to know who to shoot at and two ints to know where to shoot
    @Override
    public String takeTurn(Player other, int r, int c) {

        int hitItem = other.shoot(r,c);


        //chooses reaction based on hitItem that was determined by shoot function
        //cases 1-5 are the name of ship IDs they have lenghts: 2,3,3,4,5
        //any additional ships will just return their id unless added later
        //any non ships should be specified by a number
        switch(hitItem){
            //in the case of a miss and the first element of target is not null, the array will cycle
            case -1:
                if(target[0] != null) cycleTarget();
                return "Miss";
            //in the case of a hit:
            //first element is checked to exist
            //if does not exist then it will designate this new hit and prev hit and set it's immediate north, south, west, and east as targets in the target array
            //if it does exist then it will compare the hit with the coordinates in prev hit and check if another shot in that direction is valid: if so set that as next target
            //if there is nothing in prev hit however it will set prevHit as what was just struck
            case 0:
                if(target[0] == null){
                    prevHit = new Coordinates(r,c);
                    target[0] = new Coordinates(r, c-1);
                    target[1] = new Coordinates(r, c+1);
                    target[2] = new Coordinates(r-1, c);
                    target[3] = new Coordinates(r+1, c);
                    for(int j = 0; j < 4 && target[j] != null; j++){
                        if(!isValid(target[j])) cycleFromIndex(j);
                    }
                }else{
                    if(prevHit != null) {
                        switch(prevHit.compareTo(target[0])){
                            case 1:
                                target[0].setCol(c-1);
                                while(target[0] != null && !isValid(target[0])){
                                    cycleTarget();
                                }
                                break;
                            case 2:
                                target[0].setCol(c+1);
                                while(target[0] != null && !isValid(target[0])){
                                    cycleTarget();
                                }
                                break;
                            case 3:
                                target[0].setRow(r-1);
                                while(target[0] != null && !isValid(target[0])){
                                    cycleTarget();
                                }
                                break;
                            case 4:
                                target[0].setRow(r+1);
                                while(target[0] != null && !isValid(target[0])){
                                    cycleTarget();
                                }
                                break;
                            default:
                                cycleTarget();
                                System.out.println("-1");
                                break;
                            }
                    }else{
                        prevHit = new Coordinates(r, c);
                    }
                }
                return "Hit";
            //if already shot it will cycle the array
            case -2:
                cycleTarget();
                return "Area already shot";
            //destroying a ship will clear the targets of target array and prevHit, shooting will be random now
            case 1:
                clearTargets();
                return " sunk Destroyer";
            case 2:
                clearTargets();
                return " sunk Submarine";
            case 3:
                clearTargets();
                return " sunk Cruiser";
            case 4:
                clearTargets();
                return " sunk Battleship";
            case 5:
                clearTargets();
                return " sunk Carrier";
            default:
                clearTargets();
                return "You sunk ship number " + hitItem;
        }
    }
}
