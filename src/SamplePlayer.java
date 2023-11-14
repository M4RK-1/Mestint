import game.racetrack.Direction;
import game.racetrack.RaceTrackGame;
import game.racetrack.RaceTrackPlayer;
import game.racetrack.utils.Cell;
import game.racetrack.utils.Coin;
import game.racetrack.utils.PlayerState;

import java.util.*;

import static java.lang.Math.abs;

public class SamplePlayer extends RaceTrackPlayer {

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

    public SamplePlayer(PlayerState state, Random random, int[][] track, Coin[] coins, int color) {
        super(state, random, track, coins, color);

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

        int midIndex = (permutations.size() + 1) / 2;


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

        //System.out.println("Path: " + bestPath);System.out.println("Total Distance: " + shortestDistance);

    }


    public int[][] myTrack = track;

    public int[][] distanceMatrix = new int[12][12];

    List<Integer> bestPath = null;

    List<Cell> destinationList = new ArrayList<>();

    ArrayList<PositionWithParent[][]> stepPaths = new ArrayList<>();

    ArrayList<PositionWithParent> combinedRouteCellsList = new ArrayList<>();

    public boolean findRoute = true;

    public ArrayList<Integer> moveList = new ArrayList<>();
    public int moveListCounter = -1;


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
            //PrintPathMapWOWalls(findMiddleLine);


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

            //||(i==state.i&&state.j==j)
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

            //PrintPathMap(findPath);

            ArrayList<PositionWithParent[][]> stepPaths = new ArrayList<>();

            for (int i = 1; i < destinationList.size(); i++) {
                PositionWithParent[][] path = FindPathBetweenTwoPoint(findPath, destinationList.get(i - 1), destinationList.get(i));
                PositionWithParent[][] pathCopy = copyPath(path);
                stepPaths.add(pathCopy);

                //PrintPathMapWOWalls(stepPaths.get(0));
                /*System.out.println(i+". ut");
                for (int j = 0; j < pathCopy.length; j++) {
                    for (int k = 0; k < pathCopy[0].length; k++) {
                        switch (pathCopy[j][k].value) {
                            case 0 -> System.out.print(" ");
                            case -1 -> System.out.print(" ");
                            default -> System.out.print(pathCopy[j][k].value + "");
                        }
                    }
                    System.out.println();
                }

                 */
            }




            for (int i = 0; i < stepPaths.size(); i++) {
                ArrayList<PositionWithParent> routeCellsList = new ArrayList<>();
                PositionWithParent currentRouteCell = stepPaths.get(i)[destinationList.get(i+1).i][destinationList.get(i+1).j];
                //PrintPathMapWOWalls(stepPaths.get(i));
                while (currentRouteCell.parent_value!=-1){
                    routeCellsList.add(currentRouteCell);
                    currentRouteCell=stepPaths.get(i)[currentRouteCell.parent_i][currentRouteCell.parent_j];
                }
                if (i == 0) routeCellsList.add(stepPaths.get(0)[destinationList.get(0).i][destinationList.get(0).j]);
                Collections.reverse(routeCellsList);
                combinedRouteCellsList.addAll(routeCellsList);
            }




            /*PositionWithParent currentRouteCell = stepPaths.get(0)[destinationList.get(1).i][destinationList.get(1).j];
            while (currentRouteCell.parent_value!=-1){
                routeCellsList.add(currentRouteCell);
                currentRouteCell=stepPaths.get(0)[currentRouteCell.parent_i][currentRouteCell.parent_j];
            }
            Collections.reverse(routeCellsList);
            combinedRouteCellsList.addAll(routeCellsList);

            routeCellsList = new ArrayList<>();
            currentRouteCell = stepPaths.get(1)[destinationList.get(2).i][destinationList.get(2).j];
            while (currentRouteCell.parent_value!=-1){
                routeCellsList.add(currentRouteCell);
                currentRouteCell=stepPaths.get(1)[currentRouteCell.parent_i][currentRouteCell.parent_j];
            }
            Collections.reverse(routeCellsList);
            combinedRouteCellsList.addAll(routeCellsList);*/

            //System.out.println("XD");


            /*PositionWithParent[][] mergedPath = new PositionWithParent[myTrack.length][myTrack[0].length];

            for (int i = 0; i < mergedPath.length; i++) {
                for (int j = 0; j < mergedPath[i].length; j++) {
                    mergedPath[i][j]=new PositionWithParent(0,i,j);
                }
            }

            for (PositionWithParent[][] stepPath: stepPaths) {
                //System.out.println("==============================================================================================");PrintPathMapWOWalls(stepPath);System.out.println("============");
                for (int i = 0; i < stepPath.length; i++) {
                    for (int j = 0; j < stepPath[i].length; j++) {
                        if (stepPath[i][j].value==1){
                            mergedPath[i][j].value+=1;
                        }
                    }
                }
                //PrintPathMapWOWalls(mergedPath);System.out.println("==============================================================================================");
            }
            PrintPathMap(mergedPath);
            */


            //endregion


            //region vector fitting
            /*

            //region map copy

            VectorFittingData[][] vectorParsing = new VectorFittingData[myTrack.length][myTrack[0].length];

            for (int i = 0; i < findPath.length; i++) {
                for (int j = 0; j < findPath[0].length; j++) {
                    if (findPath[i][j].value > 1) {
                        vectorParsing[i][j] = new VectorFittingData(1, i, j);
                    } else {
                        if (findPath[i][j].value == -2) vectorParsing[i][j] = new VectorFittingData(3, i, j);
                        else vectorParsing[i][j] = new VectorFittingData(-1, i, j);
                    }
                }
            }

            int[] vectorStartingPosition = {state.i, state.j};
            vectorParsing[vectorStartingPosition[0]][vectorStartingPosition[1]].value = 2;

            //endregion

            //region elek kerekitese
            int[][] vertCrossNeighbors = {
                    {-1, 0}, {1, 0}
            };
            int[][] horiCrossNeighbors = {
                    {0, -1}, {0, 1}
            };


            for (int i = 1; i < vectorParsing.length - 1; i++) {
                for (int j = 1; j < vectorParsing[0].length - 1; j++) {
                    int vertcrossCount = 0;
                    for (int[] neighbor : vertCrossNeighbors) {
                        int newRow = i + neighbor[0];
                        int newCol = j + neighbor[1];
                        if (vectorParsing[newRow][newCol].value == 1) vertcrossCount++;
                    }
                    if (vertcrossCount == 2) {
                        vectorParsing[i][j] = new VectorFittingData(1, i, j);
                        vectorParsing[i + horiCrossNeighbors[0][0]][j + horiCrossNeighbors[0][1]] = new VectorFittingData(-1, i, j);
                        vectorParsing[i + horiCrossNeighbors[1][0]][j + horiCrossNeighbors[1][1]] = new VectorFittingData(-1, i, j);
                    }

                    int horicrossCount = 0;
                    for (int[] neighbor : horiCrossNeighbors) {
                        int newRow = i + neighbor[0];
                        int newCol = j + neighbor[1];
                        if (vectorParsing[newRow][newCol].value == 1) horicrossCount++;
                    }
                    if (horicrossCount == 2) {
                        vectorParsing[i][j] = new VectorFittingData(1, i, j);
                        vectorParsing[i + vertCrossNeighbors[0][0]][j + vertCrossNeighbors[0][1]] = new VectorFittingData(-1, i, j);
                        vectorParsing[i + vertCrossNeighbors[1][0]][j + vertCrossNeighbors[1][1]] = new VectorFittingData(-1, i, j);
                    }


                }
            }
            //endregion


            Comparator<int[]> customComparator = new Comparator<int[]>() {
                @Override
                public int compare(int[] array1, int[] array2) {
                    int lastElement1 = array1[array1.length - 1];
                    int lastElement2 = array2[array2.length - 1];
                    return Integer.compare(lastElement1, lastElement2);
                }
            };

            int[] speedVector = {0, 0};
            int[] vectorPointingPosition = new int[]{state.i, state.j};

            vectorStartingPosition = new int[]{state.i, state.j};

            ArrayList<Cell> touchedCells = new java.util.ArrayList<>();
            ArrayList<int[]> touchedCellsSpeedVectors = new java.util.ArrayList<>();
            ArrayList<ArrayList<int[]>> touchedCellsValidStepCells = new java.util.ArrayList<>();
            ArrayList<Integer> touchedCellsValidSteps = new java.util.ArrayList<>();

            touchedCells.add(new Cell(vectorStartingPosition[0], vectorStartingPosition[1]));
            touchedCellsSpeedVectors.add(new int[]{speedVector[0], speedVector[1]});
            touchedCellsValidSteps.add(1);


            boolean vectorListDone = true;

            int[][] vectorNeighbors = {
                    {0, 0}, {0, -1}, {-1, -1}, {-1, 0},
                    {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}
            };

            int vectorCounter = 0;
            vectorFittingWhile:
            while (vectorCounter < 1000) {
                vectorCounter++;
                //System.out.println("Vector counter: "+vectorCounter);
                //region alap ertekek bealitasa
                vectorStartingPosition[0] = touchedCells.get(touchedCells.size() - 1).i;
                vectorStartingPosition[1] = touchedCells.get(touchedCells.size() - 1).j;
                speedVector = touchedCellsSpeedVectors.get(touchedCellsSpeedVectors.size() - 1);
                //endregion

                //region uj ertekek bealitasa
                Cell currentCell = new Cell(vectorStartingPosition[0], vectorStartingPosition[1]);
                vectorPointingPosition[0] = vectorStartingPosition[0] + speedVector[0];
                vectorPointingPosition[1] = vectorStartingPosition[1] + speedVector[1];
                //System.out.println("======================================================================================");System.out.println("A mezo amire a vektor mutat:(" + vectorPointingPosition[0] + "," + vectorPointingPosition[1] + ")");System.out.println("A mezo amin allunk:(" + currentCell.i + "," + currentCell.j + ")");
                //endregion
                //PrintVectorMap(vectorParsing);

                boolean talalt = false;
                int lepesDarab = 0;
                ArrayList<int[]> validCells = new ArrayList<>();

                //region search neighbours

                for (int i = 0; i < vectorNeighbors.length; i++) {
                    //System.out.println("\nA lepes sorszame:" + i);

                    int newRow = vectorPointingPosition[0] + vectorNeighbors[i][0];
                    int newCol = vectorPointingPosition[1] + vectorNeighbors[i][1];
                    Cell newCell = new Cell(newRow, newCol);

                    try {
                        if (vectorParsing[newRow][newCol].value == 3) {
                            //System.out.println("WIIIIIIINNNN");
                            findRoute = false;

                            break vectorFittingWhile;

                        }
                        if (vectorParsing[newRow][newCol].value == 1) {
                            //System.out.println("A " + vectorCounter + ". lepes kordinatai: (" + newRow + "," + newCol + ")");System.out.println("Az aktualis mezo" + currentCell);System.out.println("A sebbeseg vektor:(" + speedVector[0] + "," + speedVector[1] + ")");System.out.println("A lista merete:" + moveList.size() + " Az iteraciok szama:" + vectorCounter);System.out.println("A lepes iranya:(" + vectorNeighbors[i][0] + "," + vectorNeighbors[i][1] + ")");System.out.println("A mezo amit eppen vizsgalunk:(" + newRow + "," + newCol + ")");System.out.println();
                            talalt = true;
                            validCells.add(new int[]{i, newRow, newCol, (int) (RaceTrackGame.euclideanDistance(currentCell, newCell) * 10)});
                            lepesDarab++;
                        }
                    } catch (Exception ignored) {
                    }

                }

                validCells.sort(customComparator);
                Collections.reverse(validCells);

                //endregion

                if (talalt) {
                    AddNewCell(moveList, validCells, vectorParsing, touchedCellsValidSteps, lepesDarab, touchedCells, touchedCellsSpeedVectors, speedVector, vectorNeighbors, touchedCellsValidStepCells);

                } else {


                    //region delete old
                    DeleteLastCell(vectorParsing, touchedCells, touchedCellsSpeedVectors, touchedCellsValidSteps, moveList, touchedCellsValidStepCells);
                    //endregion


                    //break vectorFittingWhile;
                }
            }

             */
            //endregion

            //region lepeskinyeres

            //endregion

            int[][] vectorNeighbors = {
                    {0, 0}, {0, -1}, {-1, -1}, {-1, 0},
                    {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}
            };

            int[] testSpeedVector = new int[]{0,0};
            for (int i = 0; i < combinedRouteCellsList.size()-1; i++) {
                int[] neededSpeedVector = new int[2];
                PositionWithParent cell = combinedRouteCellsList.get(i);
                PositionWithParent nextCell = combinedRouteCellsList.get(i+1);
                neededSpeedVector[0]=nextCell.i-cell.i;
                neededSpeedVector[1]=nextCell.j-cell.j;

                int[] tempSpeedVector = new int[2];
                tempSpeedVector[0]=testSpeedVector[0]-neededSpeedVector[0];
                tempSpeedVector[1]=testSpeedVector[1]-neededSpeedVector[1];
                for (int j = 0; j < vectorNeighbors.length; j++) {
                    int[] neighbor = vectorNeighbors[j];
                    if (neighbor[0] == tempSpeedVector[0]*-1 && neighbor[1] == tempSpeedVector[1]*-1) {
                        moveList.add(j);
                        //System.out.println(toString(RaceTrackGame.DIRECTIONS[j]));
                        break;
                    }
                }
                testSpeedVector[0]-=tempSpeedVector[0];
                testSpeedVector[1]-=tempSpeedVector[1];






            }

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
                if (baseMap[i][j].value > 1){
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

    /**************** PRINT / DEBUD ******************/
    static String toString(Direction dir) {
        if(dir == null){
            return " ";
        }
        if (dir.same(new Direction(1, 0))) {
            return "↓";
        } else if (dir.same(new Direction(1, 1))) {
            return "↘";
        } else if (dir.same(new Direction(0, 1))) {
            return "→";
        } else if (dir.same(new Direction(-1, 1))) {
            return "↗";
        } else if (dir.same(new Direction(-1, 0))) {
            return "↑";
        } else if (dir.same(new Direction(-1, -1))) {
            return "↖";
        } else if (dir.same(new Direction(0, -1))) {
            return "←";
        } else if (dir.same(new Direction(1, -1))) {
            return "↙";
        }
        return " ";
    }
    static String toString(int dir) {
        switch (dir) {
            case 0 -> { return "↓"; }
            case 1 -> { return "↘"; }
            case 2 -> { return "→"; }
            case 3 -> { return "↗"; }
            case 4 -> { return "↑"; }
            case 5 -> { return "↖"; }
            case 6 -> { return "←"; }
            case 7 -> { return "↙"; }
        }
        return " ";
    }

}


//java -jar game_engine.jar 0 game.racetrack.RaceTrackGame 11 27 5 0.1 10 1234567890 1000 SamplePlayer

//java -jar game_engine.jar 0 game.racetrack.RaceTrackGame 11 27 5 0.1 10 1234567890 1000 SamplePlayer

//java -jar game_engine.jar 100 game.racetrack.RaceTrackGame 11 27 5 0.1 10 $((Get-Random -Minimum 0 -Maximum 999999999)) 1000 SamplePlayer