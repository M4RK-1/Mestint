///>.<",vizi.mark@stud.u-szeged.hu
import game.racetrack.Direction;
import game.racetrack.RaceTrackGame;
import game.racetrack.RaceTrackPlayer;
import game.racetrack.utils.Cell;
import game.racetrack.utils.Coin;
import game.racetrack.utils.PlayerState;

import java.util.*;

import static java.lang.Math.abs;

public class Agent extends RaceTrackPlayer {

    public static class VectorFittingData {
        int value;
        int i;
        int j;
        int parent_i;
        int parent_j;

        int maxSpeed = 3;
        boolean inUse;

        public VectorFittingData(int value, int i, int j, int parent_i, int parent_j, boolean inUse) {
            this.value = value;
            this.i = i;
            this.j = j;
            this.parent_i = parent_i;
            this.parent_j = parent_j;
            this.inUse = inUse;
        }

        public VectorFittingData(int value, int i, int j) {
            this.value = value;
            this.i = i;
            this.j = j;
            this.parent_i = -1;
            this.parent_j = -1;
            this.inUse = false;
        }
    }

    public static class PositionWithParent {
        int value;
        int i;
        int j;
        int parent_value;
        int parent_i;
        int parent_j;


        public PositionWithParent(int value, int i, int j, int parent_value, int parent_i, int parent_j) {
            this.value = value;
            this.i = i;
            this.j = j;
            this.parent_value = parent_value;
            this.parent_i = parent_i;
            this.parent_j = parent_j;
        }

        public PositionWithParent(int value, int i, int j) {
            this.value = value;
            this.i = i;
            this.j = j;
            this.parent_value = -1;
            this.parent_i = -1;
            this.parent_j = -1;
        }

        public PositionWithParent(PositionWithParent other) {
            this.value = other.value;
            this.i = other.i;
            this.j = other.j;
            this.parent_i = other.parent_i;
            this.parent_j = other.parent_j;
            this.parent_value = other.parent_value;
        }

    }

    public Agent(PlayerState state, Random random, int[][] track, Coin[] coins, int color) {
        super(state, random, track, coins, color);


        ArrayList<ArrayList<Integer>> tmpSwitchPlace = new ArrayList<>();
        tmpSwitchPlace.add(new ArrayList<>(){});
        tmpSwitchPlace.add(new ArrayList<>(){{add(0);}});
        tmpSwitchPlace.add(new ArrayList<>(){{add(0);add(0);}});
        tmpSwitchPlace.add(new ArrayList<>(){{add(1);add(2);add(1);}});


        for (int i = 5; i < 136; i++) {

            ArrayList<Integer> tempList = new ArrayList<>();

            int actualNum = -1;
            int numCounter = 0;
            boolean nemTalat = true;
            ArrayList<Integer> lastList = tmpSwitchPlace.get(tmpSwitchPlace.size()-1);
            int max = 0;

            for (int num : lastList) if (num > max) max = num;


            for (int j = lastList.size() - 1; j >= 0; j--) {
                if (numCounter==2 && actualNum!=max){
                    for (int k = 0; k < j+1; k++) {
                        tempList.add(lastList.get(k));
                    }
                    tempList.add(actualNum+1);
                    for (int k = j+2; k < lastList.size(); k++) {
                        tempList.add(lastList.get(k));
                    }
                    nemTalat=false;


                }else if (numCounter==3&&actualNum==max){
                    for (int k = 0; k < j+2; k++) {
                        tempList.add(lastList.get(k));
                    }
                    tempList.add(actualNum+1);
                    for (int k = j+3; k < lastList.size(); k++) {
                        tempList.add(lastList.get(k));
                    }
                    nemTalat=false;


                }

                if (lastList.get(j)==actualNum){
                    numCounter++;
                }else {
                    actualNum=lastList.get(j);
                    numCounter = 1;
                }

            }
            if (nemTalat){
                tempList.addAll(lastList);
                tempList.add(1);
            }

            tmpSwitchPlace.add(tempList);
        }


        for (int i = 0; i < tmpSwitchPlace.size(); i++) {
            ArrayList<Integer> differencesList = new ArrayList<>();
            ArrayList <Integer> currSwitchPlace = tmpSwitchPlace.get(i);
            for (int j = 0; j < currSwitchPlace.size()-1; j++) {
                int diff = currSwitchPlace.get(j + 1) - currSwitchPlace.get(j);
                differencesList.add(diff);
            }
            switchPlace.add(differencesList);
        }

        int[][] findPath = new int[myTrack.length][myTrack[0].length];
        for (int i = 0; i < myTrack.length; i++) {
            for (int j = 0; j < myTrack[i].length; j++) {
                if (myTrack[i][j] == 1) {
                    findPath[i][j] = 0;
                } else if (myTrack[i][j] == 2) {
                    findPath[i][j] = -100;
                } else if (myTrack[i][j] == 5) {
                    findPath[i][j] = -12;
                } else if (myTrack[i][j] == 33) {
                    findPath[i][j] = 0;
                } else {
                    findPath[i][j] = -100;
                }
                findPath[state.i][state.j] = -1;
            }
        }
        for (int j = 0; j < coins.length; j++) {
            findPath[coins[j].i][coins[j].j] = ((j + 2) * -1);
        }


        for (int actualNumber = -1; actualNumber > -13; actualNumber--) {
            GetAllDistances(findPath, actualNumber);
        }


        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        List<int[]> permutations = new ArrayList<>();
        generatePermutations(nums, 0, permutations);




        int shortestDistance = Integer.MAX_VALUE;

        for (int[] permutation : permutations) {
            List<Integer> currentPath = new ArrayList<>();
            currentPath.add(0);
            for (int num : permutation) {
                currentPath.add(num);
            }
            currentPath.add(11);

            int currentDistance = calculateTotalDistance(currentPath, distanceMatrix);

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                bestPath = currentPath;
            }
        }


        for (int index : bestPath) {
            if (index == 0) destinationList.add(new Cell(2, 7));
            else if (index == 11) destinationList.add(new Cell(3, 127));
            else destinationList.add(new Cell(coins[index - 1].i, coins[index - 1].j));
        }



    }


    public int[][] myTrack = track;

    public int[][] distanceMatrix = new int[12][12];

    List<Integer> bestPath = null;

    List<Cell> destinationList = new ArrayList<>();



    ArrayList<PositionWithParent> combinedRouteCellsList = new ArrayList<>();

    public boolean findRoute = true;

    public ArrayList<Integer> moveList = new ArrayList<>();
    public int moveListCounter = -1;

    ArrayList<ArrayList<Integer>> switchPlace = new ArrayList<>();


    public game.racetrack.utils.Cell lastPosition = toCell(state);


    int[][] neighbors = {
            {0, -1}, {-1, -1}, {-1, 0}, {-1, 1},
            {0, 1}, {1, 1}, {1, 0}, {1, -1}
    };


    @Override
    public Direction getDirection(long remainingTime) {


        if (findRoute) {

            //region mapcopy
            PositionWithParent[][] findPath = new PositionWithParent[myTrack.length][myTrack[0].length];
            for (int i = 0; i < myTrack.length; i++) {
                for (int j = 0; j < myTrack[i].length; j++) {
                    if (myTrack[i][j] == 1) {
                        findPath[i][j] = new PositionWithParent(0, i, j);
                    } else if (myTrack[i][j] == 2) {
                        findPath[i][j] = new PositionWithParent(-1, i, j);
                    } else if (myTrack[i][j] == 5) {
                        findPath[i][j] = new PositionWithParent(-2, i, j);
                    } else if (myTrack[i][j] == 17) {
                        findPath[i][j] = new PositionWithParent(-3, i, j);
                    } else if (myTrack[i][j] == 33) {
                        findPath[i][j] = new PositionWithParent(0, i, j);
                    } else {
                        findPath[i][j] = new PositionWithParent(-100, i, j);
                    }
                    findPath[state.i][state.j] = new PositionWithParent(1, i, j);

                }
            }
            //endregion


            //region middle line generate

            int[][] TRDLneighbors = {
                    {0, -1}, {-1, 0}, {0, 1}, {1, 0}
            };


            //copy
            PositionWithParent[][] findMiddleLine = new PositionWithParent[myTrack.length][myTrack[0].length];
            for (int i = 0; i < myTrack.length; i++) {
                for (int j = 0; j < myTrack[i].length; j++) {
                    if (myTrack[i][j] == 1) {
                        findMiddleLine[i][j] = new PositionWithParent(0, i, j);
                    } else if (myTrack[i][j] == 2) {
                        findMiddleLine[i][j] = new PositionWithParent(-1, i, j);
                    } else if (myTrack[i][j] == 5) {
                        if (i < 2) {
                            findMiddleLine[i][j] = new PositionWithParent(-2, i, j);
                        } else {
                            findMiddleLine[i][j] = new PositionWithParent(0, i, j);
                        }
                    } else if (myTrack[i][j] == 17) {
                        findMiddleLine[i][j] = new PositionWithParent(0, i, j);
                    } else if (myTrack[i][j] == 33) {
                        findMiddleLine[i][j] = new PositionWithParent(0, i, j);
                    } else {
                        findMiddleLine[i][j] = new PositionWithParent(-100, i, j);
                    }

                }
            }


            boolean xd = true;
            while (xd) {
                xd = false;
                for (int c = 0; c < 8; c++) {
                    for (int i = 0; i < findMiddleLine.length; i++) {
                        for (int j = 0; j < findMiddleLine[0].length; j++) {
                            if (c == 0) {
                                if (findMiddleLine[i][j].value == -1 + c) {
                                    for (int[] TRDLneighbor : TRDLneighbors) {
                                        int newRow = i + TRDLneighbor[0];
                                        int newCol = j + TRDLneighbor[1];
                                        try {
                                            if (findMiddleLine[newRow][newCol].value == 0) {
                                                findMiddleLine[newRow][newCol].value = c + 1;
                                                xd = true;
                                            }
                                        } catch (Exception ignored) {
                                        }


                                    }
                                }
                            } else {
                                if (findMiddleLine[i][j].value == c) {
                                    for (int[] TRDLneighbor : TRDLneighbors) {
                                        int newRow = i + TRDLneighbor[0];
                                        int newCol = j + TRDLneighbor[1];
                                        try {
                                            if (findMiddleLine[newRow][newCol].value == 0) {
                                                findMiddleLine[newRow][newCol].value = c + 1;
                                                xd = true;
                                            }
                                        } catch (Exception ignored) {
                                        }


                                    }
                                }
                            }


                        }
                    }
                }
            }



            /*
            //Restrict the map by one cell
            for (int i = 0; i < findMiddleLine.length; i++) {
                for (int j = 0; j < findMiddleLine[0].length; j++) {
                    if (findMiddleLine[i][j].value == 1) {
                        findMiddleLine[i][j].value = -1;
                    } else if (findMiddleLine[i][j].value >= 2) {
                        findMiddleLine[i][j].value = 0;
                    } else {
                        findMiddleLine[i][j].value = -1;
                    }
                }
            }
             */

            for (int i = 0; i < findMiddleLine.length; i++) {
                for (int j = 0; j < findMiddleLine[0].length; j++) {
                    if (findMiddleLine[i][j].value == 0) {
                        findMiddleLine[i][j].value = -1;
                    } else if (findMiddleLine[i][j].value >= 1) {
                        findMiddleLine[i][j].value = 0;
                    } else {
                        findMiddleLine[i][j].value = -1;
                    }
                }
            }
            for (int i = 0; i < findMiddleLine.length; i++) {
                for (int j = 0; j < findMiddleLine[0].length; j++) {
                    if (findPath[i][j].value == -2 && i < 2) findMiddleLine[i][j].value = -2;
                    if (findPath[i][j].value == -3) {
                        for (int[] neighbor : neighbors) {
                            int newRow = i + neighbor[0];
                            int newCol = j + neighbor[1];
                            try {
                                if (findPath[newRow][newCol].value == 0) {
                                    findMiddleLine[newRow][newCol].value = 0;
                                }

                            } catch (Exception ignored) {
                            }
                        }
                        if (findPath[i][j].value == -3) findMiddleLine[i][j].value = -3;

                    }
                }
            }


            //coinok korul kitakarit
            for (int[] neighbor : TRDLneighbors) {
                int newRow = state.i + neighbor[0];
                int newCol = state.j + neighbor[1];
                try {
                    if (findPath[newRow][newCol].value == 0) {
                        findMiddleLine[newRow][newCol].value = 0;
                    }

                } catch (Exception ignored) {
                }
            }
            findMiddleLine[state.i][state.j].value = 1;


            //endregion

            //region find way

            findPath = findMiddleLine;



            ArrayList<PositionWithParent[][]> stepPaths = new ArrayList<>();

            for (int i = 1; i < destinationList.size(); i++) {
                PositionWithParent[][] path = FindPathBetweenTwoPoint(findPath, destinationList.get(i - 1), destinationList.get(i));
                PositionWithParent[][] pathCopy = copyPath(path);
                stepPaths.add(pathCopy);


            }


            for (int i = 0; i < stepPaths.size(); i++) {
                ArrayList<PositionWithParent> routeCellsList = new ArrayList<>();
                PositionWithParent currentRouteCell = stepPaths.get(i)[destinationList.get(i + 1).i][destinationList.get(i + 1).j];

                while (currentRouteCell.parent_value != -1) {
                    routeCellsList.add(currentRouteCell);
                    currentRouteCell = stepPaths.get(i)[currentRouteCell.parent_i][currentRouteCell.parent_j];
                }
                if (i == 0) routeCellsList.add(stepPaths.get(0)[destinationList.get(0).i][destinationList.get(0).j]);
                Collections.reverse(routeCellsList);
                combinedRouteCellsList.addAll(routeCellsList);
            }










            //endregion




            //region lepeskinyeres



            int[][] vectorNeighbors = {
                    {0, 0}, {0, -1}, {-1, -1}, {-1, 0},
                    {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}
            };

            ArrayList<int[]> speedVectors = new ArrayList<>();
            int[] currentSpeedVector = new int[]{0, 0};
            speedVectors.add(new int[]{currentSpeedVector[0],currentSpeedVector[1]});
            for (int i = 0; i < combinedRouteCellsList.size() - 1; i++) {
                ArrayList<int[]> neededSpeedVectors = new ArrayList<>();

                PositionWithParent currentCell = combinedRouteCellsList.get(i);

                int maxDistance = abs(Math.max(currentSpeedVector[0],currentSpeedVector[1]));

                //lehetseges celok kiszamitasa
                for (int j = 1; j < maxDistance+2; j++) {
                    try {
                        PositionWithParent nextCell = combinedRouteCellsList.get(i + j);
                        int[] tempSpeedVector = new int[2];
                        tempSpeedVector[0] = nextCell.i - currentCell.i;
                        tempSpeedVector[1] = nextCell.j - currentCell.j;
                        neededSpeedVectors.add(tempSpeedVector);
                    }catch (Exception ignored){}
                }


                outerFor:
                for (int c = 0; c < neededSpeedVectors.size(); c++) {
                    int[] tempSpeedVector = new int[2];
                    tempSpeedVector[0] = currentSpeedVector[0] - neededSpeedVectors.get(c)[0];
                    tempSpeedVector[1] = currentSpeedVector[1] - neededSpeedVectors.get(c)[1];

                    try {
                        for (int j = 0; j < vectorNeighbors.length + 1; j++) {
                            int[] neighbor = vectorNeighbors[j];
                            if (neighbor[0] == tempSpeedVector[0] * -1 && neighbor[1] == tempSpeedVector[1] * -1) {
                                moveList.add(j);
                                //System.out.println(toString(RaceTrackGame.DIRECTIONS[j]));
                                break;
                            }
                        }
                        currentSpeedVector[0] -= tempSpeedVector[0];
                        currentSpeedVector[1] -= tempSpeedVector[1];
                        speedVectors.add(new int[]{currentSpeedVector[0],currentSpeedVector[1]});
                        break outerFor;
                    } catch (ArrayIndexOutOfBoundsException ignored) {

                        for (int j = 0; j < vectorNeighbors.length + 1; j++) {
                            int[] neighbor = vectorNeighbors[j];
                            if (neighbor[0] == currentSpeedVector[0] * -1 && neighbor[1] == currentSpeedVector[1] * -1) {
                                moveList.add(j);
                                break;
                            }
                        }
                        currentSpeedVector[0] -= currentSpeedVector[0];
                        currentSpeedVector[1] -= currentSpeedVector[1];
                        speedVectors.add(new int[]{currentSpeedVector[0],currentSpeedVector[1]});
                        i--;
                        break outerFor;

                    }


                }



            }
            //endregion

            //region step more

            int numCounter = 0;


            int c = -1;
            for (int i = 0; i < moveList.size(); i++) {
                c++;
                Integer integer = moveList.get(i);
                if (integer == 0) {
                    numCounter++;
                } else if ( numCounter>2) {
                    int startIndex = i-numCounter;
                    int endIndex = i;

                    List<Integer> cutPortion = moveList.subList(startIndex, endIndex);

                    ArrayList<Integer> switchVector = new ArrayList<>();
                    ArrayList<Integer> actualSwitchPlace = switchPlace.get(numCounter);

                    int lol = DirectionToInt(new Direction(speedVectors.get(c-numCounter)[0],speedVectors.get(c-numCounter)[1]));


                    for (int j = 0; j < actualSwitchPlace.size(); j++) {
                        if (actualSwitchPlace.get(j)==1){
                            switchVector.add(lol);
                        }else if (actualSwitchPlace.get(j)==-1){
                            switchVector.add(RevesreDirectionNum(lol));
                        }else {
                            switchVector.add(0);
                        }
                    }


                    cutPortion.clear();
                    cutPortion.addAll(switchVector);
                    numCounter = 0;
                    i=actualSwitchPlace.size()+startIndex;
                }else {
                    numCounter = 0;
                }

            }




            //endregion

            //delete Later
            findRoute = false;


        } else {
            lastPosition = toCell(state);
            try {
                moveListCounter++;
                return RaceTrackGame.DIRECTIONS[moveList.get(moveListCounter)];
            } catch (Exception error) {
                return RaceTrackGame.DIRECTIONS[random.nextInt(RaceTrackGame.DIRECTIONS.length)];
            }

        }

        return RaceTrackGame.DIRECTIONS[0];


    }

    private int ForceBetween(int number){
        if (number <= -1) {
            return  -1;
        } else if (number >= 1) {
            return  1;
        }
        return 0;
    }

    private int RevesreDirectionNum(int num){
        Direction tmpDir = IntToDirection(num);
        Direction forditottIranyDir = new Direction(tmpDir.i*-1,tmpDir.j*-1);
        return DirectionToInt(forditottIranyDir);
    }

    private Direction IntToDirection(int num){
        return RaceTrackGame.DIRECTIONS[num];
    }

    private int DirectionToInt(Direction dir){
        for (int i = 0; i < RaceTrackGame.DIRECTIONS.length; i++) {
            Direction aktVizsgaltDirection = RaceTrackGame.DIRECTIONS[i];
            if (aktVizsgaltDirection.i==dir.i&&aktVizsgaltDirection.j==dir.j){
                return i;
            }
        }
        return -1;
    }

    private PositionWithParent[][] FindPathBetweenTwoPoint(PositionWithParent[][] findPath, Cell from, Cell to) {

        PositionWithParent[][] baseMap = new PositionWithParent[findPath.length][findPath[0].length];

        for (int i = 0; i < findPath.length; i++) {
            for (int j = 0; j < findPath[i].length; j++) {
                if (i < 2) {
                    baseMap[i][j] = new PositionWithParent(-1, i, j);
                } else {
                    if (findPath[i][j].value == 0 || findPath[i][j].value == -1) {
                        baseMap[i][j] = findPath[i][j];
                    } else {
                        baseMap[i][j] = new PositionWithParent(0, i, j);
                    }
                }

            }
        }
        baseMap[from.i][from.j].value = 1;

        for (int[] neighbor : neighbors) {
            int newRow = from.i + neighbor[0];
            int newCol = from.j + neighbor[1];
            try {
                if (newRow == to.i && newCol == to.j) {
                    baseMap[newRow][newCol].parent_value = 1;
                    baseMap[newRow][newCol].parent_i = from.i;
                    baseMap[newRow][newCol].parent_j = from.j;
                    baseMap[newRow][newCol].value = 1;
                    return baseMap;
                }
            } catch (Exception ignored) {
            }
        }


        PositionWithParent kovetkezoCel = new PositionWithParent(0, 0, 0);
        int c;
        outerLoop:
        for (c = 1; c < 1000; c++) {
            for (int i = 0; i < baseMap.length; i++) {
                for (int j = 0; j < baseMap[0].length; j++) {
                    if (baseMap[i][j].value == c) {
                        for (int[] neighbor : neighbors) {
                            int newRow = i + neighbor[0];
                            int newCol = j + neighbor[1];
                            try {
                                if (baseMap[newRow][newCol].value == 0) {
                                    baseMap[newRow][newCol].parent_value = baseMap[i][j].value;
                                    baseMap[newRow][newCol].parent_i = baseMap[i][j].i;
                                    baseMap[newRow][newCol].parent_j = baseMap[i][j].j;
                                    baseMap[newRow][newCol].value = c + 1;
                                }
                                if (newRow == to.i && newCol == to.j) {

                                    kovetkezoCel = baseMap[newRow][newCol];
                                    break outerLoop;
                                }
                            } catch (Exception ignored) {
                            }

                        }
                    }
                }
            }

        }


        for (int x = 0; x < 1000; x++) {
            PositionWithParent currentCell = baseMap[kovetkezoCel.i][kovetkezoCel.j];
            for (int i = 0; i < baseMap.length; i++) {
                for (int j = 0; j < baseMap[0].length; j++) {
                    if (baseMap[i][j].value == currentCell.value) {
                        if (!(i == currentCell.i && j == currentCell.j)) {
                            baseMap[i][j].value = 0;
                        }
                    }
                }
            }
            if (kovetkezoCel.value == 2) {
                break;
            }
            kovetkezoCel = baseMap[kovetkezoCel.parent_i][kovetkezoCel.parent_j];
        }


        for (int i = 0; i < baseMap.length; i++) {
            for (int j = 0; j < baseMap[i].length; j++) {
                if (baseMap[i][j].value > 1) {
                    baseMap[i][j].value = 1;
                }
            }
        }
        //PrintPathMapWOWalls(baseMap);


        return baseMap;

    }

    private static void DeleteLastCell(VectorFittingData[][] vectorParsing, ArrayList<Cell> touchedCells, ArrayList<int[]> touchedCellsSpeedVectors, ArrayList<Integer> touchedCellsValidSteps, ArrayList<Integer> moveList, ArrayList<ArrayList<int[]>> touchedCellsValidStepCells) {

        vectorParsing[touchedCells.get(touchedCells.size() - 1).i]
                [touchedCells.get(touchedCells.size() - 1).j].value = 1;
        vectorParsing[touchedCells.get(touchedCells.size() - 1).i]
                [touchedCells.get(touchedCells.size() - 1).j].inUse = false;
        touchedCells.remove(touchedCells.size() - 1);
        touchedCellsSpeedVectors.remove(touchedCellsSpeedVectors.size() - 1);
        touchedCellsValidSteps.remove(touchedCellsValidSteps.size() - 1);
        moveList.remove(moveList.size() - 1);
        touchedCellsValidStepCells.remove(touchedCellsValidStepCells.size() - 1);
    }


    public static PositionWithParent[][] copyPath(PositionWithParent[][] map) {
        PositionWithParent[][] copy = new PositionWithParent[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                copy[i][j] = new PositionWithParent(map[i][j]);
            }
        }
        return copy;
    }

    private static void AddNewCell(ArrayList<Integer> moveList, ArrayList<int[]> validCells, VectorFittingData[][] vectorParsing, ArrayList<Integer> touchedCellsValidSteps, int lepesDarab, ArrayList<Cell> touchedCells, ArrayList<int[]> touchedCellsSpeedVectors, int[] speedVector, int[][] vectorNeighbors, ArrayList<ArrayList<int[]>> touchedCellsValidStepCells) {
        moveList.add(validCells.get(validCells.size() - 1)[0]);

        vectorParsing[validCells.get(validCells.size() - 1)[1]]
                [validCells.get(validCells.size() - 1)[2]].value = 0;

        vectorParsing[validCells.get(validCells.size() - 1)[1]]
                [validCells.get(validCells.size() - 1)[2]].inUse = true;

        touchedCellsValidSteps.add(lepesDarab);

        touchedCells.add(new Cell(validCells.get(validCells.size() - 1)[1],
                validCells.get(validCells.size() - 1)[2]));

        touchedCellsSpeedVectors.add(new int[]{speedVector[0] + vectorNeighbors[validCells.get(validCells.size() - 1)[0]][0],
                speedVector[1] + vectorNeighbors[validCells.get(validCells.size() - 1)[0]][1]});

        touchedCellsValidStepCells.add(validCells);
    }

    private void PrintVectorMap(VectorFittingData[][] vectorParsing) {
        System.out.println("ParentValues:");
        System.out.print(" ");
        for (int i1 = 0; i1 < vectorParsing[0].length; i1++) {
            System.out.print(i1 % 10);
        }
        for (int i1 = 0; i1 < vectorParsing.length; i1++) {
            System.out.print(i1 % 10);
            for (int j2 = 0; j2 < vectorParsing[0].length; j2++) {
                if (vectorParsing[i1][j2].value == -1) System.out.print(" ");
                else System.out.print(vectorParsing[i1][j2].value);
            }
            System.out.println();
        }
    }

    private void PrintPathMap(PositionWithParent[][] findPath) {
        for (int i = 0; i < findPath.length; i++) {
            for (int j = 0; j < findPath[0].length; j++) {
                switch (findPath[i][j].value) {
                    case 0 -> System.out.print(" ");
                    case -1 -> System.out.print("X");
                    case -2 -> System.out.print("F");
                    case -3 -> System.out.print("C");
                    case 1 -> System.out.print("1");
                    default -> System.out.print(findPath[i][j].value % 10 + "");
                }
            }
            System.out.println();
        }

    }

    private void PrintPathMapWOWalls(PositionWithParent[][] findPath) {
        for (int i = 0; i < findPath.length; i++) {
            for (int j = 0; j < findPath[0].length; j++) {
                switch (findPath[i][j].value) {
                    case 0 -> System.out.print(" ");
                    case -1 -> System.out.print(" ");
                    case -2 -> System.out.print("F");
                    case -3 -> System.out.print("C");
                    case 1 -> System.out.print("P");
                    default -> System.out.print(findPath[i][j].value % 10 + "");
                }
            }
            System.out.println();
        }

    }

    void debug(String a) {
        System.out.println();
        System.out.println("DEBUG " + a + "!");
        System.out.println();
    }

    public Cell toCell(PlayerState ps) {
        return new Cell(ps.i, ps.j);
    }

    public static void generatePermutations(int[] nums, int index, List<int[]> result) {
        if (index == nums.length) {
            result.add(Arrays.copyOf(nums, nums.length));
            return;
        }

        for (int i = index; i < nums.length; i++) {
            int temp = nums[index];
            nums[index] = nums[i];
            nums[i] = temp;

            generatePermutations(nums, index + 1, result);

            temp = nums[index];
            nums[index] = nums[i];
            nums[i] = temp;
        }
    }

    public static int calculateTotalDistance(List<Integer> path, int[][] distanceMatrix) {
        int totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            totalDistance += distanceMatrix[path.get(i)][path.get(i + 1)];
        }
        return totalDistance;
    }

    private void GetAllDistances(int[][] findPath, int actualNumber) {

        int[][] distanceMap = new int[myTrack.length][myTrack[0].length];
        for (int i = 0; i < findPath.length; i++) {
            for (int j = 0; j < findPath[i].length; j++) {
                distanceMap[i][j] = findPath[i][j];
            }
        }

        ArrayList<Integer> erintettCelok = new ArrayList<>();
        erintettCelok.add(actualNumber);
        distanceMatrix[abs(actualNumber) - 1][abs(actualNumber) - 1] = 0;

        int c = 1;
        outerLoop:
        for (c = 1; c < 1000; c++) {


            for (int i = 0; i < distanceMap.length; i++) {
                for (int j = 0; j < distanceMap[0].length; j++) {
                    if (distanceMap[i][j] == actualNumber || (distanceMap[i][j] == c - 1 && distanceMap[i][j] != actualNumber && distanceMap[i][j] != 0)) {
                        for (int[] neighbor : neighbors) {
                            int newRow = i + neighbor[0];
                            int newCol = j + neighbor[1];
                            try {
                                if (distanceMap[newRow][newCol] == 0)
                                    distanceMap[newRow][newCol] = c;
                                else if (distanceMap[newRow][newCol] < 0 && distanceMap[newRow][newCol] != -100 && !erintettCelok.contains(distanceMap[newRow][newCol])) {
                                    erintettCelok.add(distanceMap[newRow][newCol]);
                                    distanceMatrix[abs(actualNumber) - 1][abs(distanceMap[newRow][newCol]) - 1] = c;

                                }
                            } catch (Exception ignored) {
                            }


                        }
                    }
                }
            }
        }
    }


}


//java -jar game_engine.jar 0 game.racetrack.RaceTrackGame 11 27 5 0.1 10 1234567890 1000 SamplePlayer

//java -jar game_engine.jar 0 game.racetrack.RaceTrackGame 11 27 5 0.1 10 1234567890 1000 SamplePlayer

//java -jar game_engine.jar 100 game.racetrack.RaceTrackGame 11 27 5 0.1 10 $((Get-Random -Minimum 0 -Maximum 999999999)) 1000 SamplePlayer