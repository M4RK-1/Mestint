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
            findPath[coins[j].i][coins[j].j]=((j+2)*-1);
        }

        /*for (int i = 0; i < findPath.length; i++) {
            for (int j = 0; j < findPath[i].length; j++) {
                switch (findPath[i][j]) {
                    case 0 -> System.out.print(" ");
                    case -100 -> System.out.print("X");
                    default -> System.out.print(Math.abs(findPath[i][j] % 10) + "");
                }
            }
            System.out.println();
        }*/

        for (int actualNumber = -1; actualNumber > -13; actualNumber--) {
            GetAllDistances(findPath,actualNumber);
        }
        /*int numVertices = distanceMatrix.length;
        int startVertex = 0; // Replace with your fixed starting vertex
        int endVertex = 11; // Replace with your fixed ending vertex
        int numRemainingVertices = numVertices - 1;

        List<Integer> bestPath = null;
        int shortestDistance = Integer.MAX_VALUE;

        // Generate all possible permutations of 10 remaining vertices (excluding start and end)
        int[] permutation = new int[10];
        for (int i1 = 1; i1 < numRemainingVertices; i1++) {
            permutation[0] = i1;
            for (int i2 = 1; i2 < numRemainingVertices; i2++) {
                permutation[1] = i2;
                for (int i3 =  1; i3 < numRemainingVertices; i3++) {
                    permutation[2] = i3;
                    for (int i4 =  1; i4 < numRemainingVertices; i4++) {
                        permutation[3] = i4;
                        for (int i5 =  1; i5 < numRemainingVertices; i5++) {
                            permutation[4] = i5;
                            for (int i6 =  1; i6 < numRemainingVertices; i6++) {
                                permutation[5] = i6;
                                for (int i7 =  1; i7 < numRemainingVertices; i7++) {
                                    permutation[6] = i7;
                                    for (int i8 =  1; i8 < numRemainingVertices; i8++) {
                                        permutation[7] = i8;
                                        for (int i9 =  1; i9 < numRemainingVertices; i9++) {
                                            permutation[8] = i9;
                                            for (int i10 =  1; i10 < numRemainingVertices; i10++) {
                                                permutation[9] = i10;

                                                // Create a path by adding the fixed start and end vertices
                                                List<Integer> currentPath = new ArrayList<>();
                                                currentPath.add(startVertex);
                                                for (int vertex : permutation) {
                                                    currentPath.add(vertex);
                                                }
                                                currentPath.add(endVertex);

                                                int currentDistance = calculateTotalDistance(currentPath, distanceMatrix);

                                                if (currentDistance < shortestDistance) {
                                                    shortestDistance = currentDistance;
                                                    bestPath = currentPath;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }




        */

        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        List<int[]> permutations = new ArrayList<>();
        generatePermutations(nums, 0, permutations);

        int midIndex = (permutations.size() + 1) / 2;


        int shortestDistance = Integer.MAX_VALUE;

        for (int[] permutation : permutations) {
            // Create a path by adding the fixed start and end vertices
            List<Integer> currentPath = new ArrayList<>();
            currentPath.add(0);
            for (int num: permutation) {
                currentPath.add(num);
            }
            currentPath.add(11);

            int currentDistance = calculateTotalDistance(currentPath, distanceMatrix);

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                bestPath = currentPath;
            }
        }

        //System.out.println("Path: " + bestPath);System.out.println("Total Distance: " + shortestDistance);

    }




    public int[][] myTrack = track;

    public int[][] distanceMatrix = new int[12][12];

    List<Integer> bestPath = null;

    public boolean findRoute=true;

    public ArrayList<Integer> moveList = new ArrayList<>();
    public int moveListCounter = -1;




    public game.racetrack.utils.Cell lastPosition = toCell(state);


    int[][] neighbors = {
            {0, -1}, {-1, -1}, {-1, 0}, {-1, 1},
            {0, 1}, {1, 1}, {1, 0}, {1, -1}
    };


    @Override
    public Direction getDirection(long remainingTime) {


        if (lastPosition != toCell(state)) {
            java.util.List<game.racetrack.utils.Cell> felvevoMezok = game.racetrack.RaceTrackGame.lineCrossing(lastPosition, toCell(state));
            for (game.racetrack.utils.Cell c : felvevoMezok) {
                myTrack[c.i][c.j] = 1;
            }
        }


        /*
        //randomlep
        if (random.nextInt(100) < 3) {
            lastPosition = toCell(state);
            return RaceTrackGame.DIRECTIONS[random.nextInt(RaceTrackGame.DIRECTIONS.length)];
        }*/

        if (findRoute){

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

            /*
            //region kozeli lepes
            for (int i = 0; i < neighbors.length; i++) {
                int newRow = this.state.i + neighbors[i][0];
                int newCol = this.state.j + neighbors[i][1];
                try {
                    if (findPath[newRow][newCol].value == -2 || findPath[newRow][newCol].value == -3) {
                        return RaceTrackGame.DIRECTIONS[i + 1];
                    }
                } catch (Exception ignored) {
                }
            }
            //endregion
            */



            //region middle line generate

            int[][] TRDLneighbors = {
                    {0, -1}, {-1, 0}, {0, 1}, {1, 0}
            };


            PositionWithParent[][] findMiddleLine = new PositionWithParent[myTrack.length][myTrack[0].length];
            for (int i = 0; i < myTrack.length; i++) {
                for (int j = 0; j < myTrack[i].length; j++) {
                    if (myTrack[i][j] == 1) {
                        findMiddleLine[i][j] = new PositionWithParent(0, i, j);
                    } else if (myTrack[i][j] == 2) {
                        findMiddleLine[i][j] = new PositionWithParent(-1, i, j);
                    } else if (myTrack[i][j] == 5) {
                        if (i<2){
                            findMiddleLine[i][j] = new PositionWithParent(-2, i, j);
                        }else {
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
                    if (findPath[i][j].value == -2 && i<2) findMiddleLine[i][j].value = -2;
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

            PositionWithParent kovetkezoCel = new PositionWithParent(0, 0, 0);


            int c = 1;
            outerLoop:
            for (c = 1; c < 1000; c++) {
                for (int i = 0; i < findPath.length; i++) {
                    for (int j = 0; j < findPath[0].length; j++) {
                        if (findPath[i][j].value == c) {
                            for (int[] neighbor : neighbors) {
                                int newRow = i + neighbor[0];
                                int newCol = j + neighbor[1];
                                try {
                                    if ((findPath[newRow][newCol].value == 0 || findPath[newRow][newCol].value > c + 1)
                                            || (findPath[newRow][newCol].value == -3 || findPath[newRow][newCol].value == -2)) {
                                        findPath[newRow][newCol].parent_value = findPath[i][j].value;
                                        findPath[newRow][newCol].parent_i = findPath[i][j].i;
                                        findPath[newRow][newCol].parent_j = findPath[i][j].j;
                                    }

                                    if (findPath[newRow][newCol].value == 0 || findPath[newRow][newCol].value > c + 1)
                                        findPath[newRow][newCol].value = c + 1;
                                    else if (findPath[newRow][newCol].value == -2
                                        //|| findPath[newRow][newCol].value == -3
                                    ) {
                                        kovetkezoCel = findPath[newRow][newCol];
                                        break outerLoop;
                                    }
                                } catch (Exception ignored) {
                                }


                            }
                        }
                    }
                }
            }

            for (int i = 0; i < findPath.length; i++) {
                for (int j = 0; j < findPath[0].length; j++) {
                    if (findPath[i][j].value == c + 1) {
                        findPath[i][j].value = 0;
                    }
                }
            }


            for (int x = 0; x < 1000; x++) {
                PositionWithParent currentCell = findPath[kovetkezoCel.parent_i][kovetkezoCel.parent_j];
                for (int i = 0; i < findPath.length; i++) {
                    for (int j = 0; j < findPath[0].length; j++) {
                        if (findPath[i][j].value == currentCell.value) {
                            if (!(i == currentCell.i && j == currentCell.j)) {
                                findPath[i][j].value = 0;
                            }
                        }
                    }
                }
                kovetkezoCel = currentCell;
                if (kovetkezoCel.value == 2) {
                    break;
                }
            }
            //PrintPathMap(findPath);

            //endregion


            //region vector fitting


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
                    {0, -1},{0, 1}
            };


            for (int i = 1; i < vectorParsing.length-1; i++) {
                for (int j = 1; j < vectorParsing[0].length-1; j++) {
                    int vertcrossCount=0;
                    for (int[] neighbor:vertCrossNeighbors) {
                        int newRow = i + neighbor[0];
                        int newCol = j + neighbor[1];
                        if (vectorParsing[newRow][newCol].value==1)vertcrossCount++;
                    }
                    if (vertcrossCount==2){
                        vectorParsing[i][j]=new VectorFittingData(1, i, j);
                        vectorParsing[i+horiCrossNeighbors[0][0]][j+horiCrossNeighbors[0][1]]=new VectorFittingData(-1, i, j);
                        vectorParsing[i+horiCrossNeighbors[1][0]][j+horiCrossNeighbors[1][1]]=new VectorFittingData(-1, i, j);
                    }

                    int horicrossCount=0;
                    for (int[] neighbor:horiCrossNeighbors) {
                        int newRow = i + neighbor[0];
                        int newCol = j + neighbor[1];
                        if (vectorParsing[newRow][newCol].value==1)horicrossCount++;
                    }
                    if (horicrossCount==2){
                        vectorParsing[i][j]=new VectorFittingData(1, i, j);
                        vectorParsing[i+vertCrossNeighbors[0][0]][j+vertCrossNeighbors[0][1]]=new VectorFittingData(-1, i, j);
                        vectorParsing[i+vertCrossNeighbors[1][0]][j+vertCrossNeighbors[1][1]]=new VectorFittingData(-1, i, j);
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
                    } catch (Exception ignored){}

                }

                validCells.sort(customComparator);
                Collections.reverse(validCells);

                //endregion

                if (talalt) {
                    AddNewCell(moveList, validCells, vectorParsing, touchedCellsValidSteps, lepesDarab, touchedCells, touchedCellsSpeedVectors, speedVector, vectorNeighbors, touchedCellsValidStepCells);

                } else {
                /*debug("Nem talalt");
                PrintVectorMap(vectorParsing);
                for (int i = 0; i < touchedCells.size(); i++) {
                    System.out.println("Aktualis mezo: " + touchedCells.get(i) + " | Speed vector: " + touchedCellsSpeedVectors.get(i)[0] + ":" + touchedCellsSpeedVectors.get(i)[1] + " | Valid steps: " + touchedCellsValidSteps.get(i));
                }*/

                    //region delete old
                    DeleteLastCell(vectorParsing, touchedCells, touchedCellsSpeedVectors, touchedCellsValidSteps, moveList, touchedCellsValidStepCells);
                    //endregion


                    //break vectorFittingWhile;
                }
            }
            //endregion

        }
        else {
            lastPosition = toCell(state);
            moveListCounter++;
            return RaceTrackGame.DIRECTIONS[moveList.get(moveListCounter)];
        }


        lastPosition = toCell(state);
        return RaceTrackGame.DIRECTIONS[0];
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
                    case 1 -> System.out.print("P");
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

    void createCircle(int[][] array, int centerX, int centerY, int radius) {

        int[][] korbe = {
                {0, -1}, {-1, -1}, {-1, 0}, {-1, 1},
                {0, 1}, {1, 1}, {1, 0}, {1, -1}
        };

        array[centerX][centerY] = -5;


        for (int x = 0; x < radius; x++) {
            for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < array[0].length; j++) {
                    if (array[i][j] == (array[centerX][centerY] - x)) {
                        for (int[] neighbor : korbe) {
                            int newRow = i + neighbor[0];
                            int newCol = j + neighbor[1];
                            try {
                                if (array[newRow][newCol] == 0 || array[newRow][newCol] == -4) {
                                    array[newRow][newCol] = (array[centerX][centerY] - 1 - x);
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (array[i][j] < -4) array[i][j] = -4;
            }
        }

        array[centerX][centerY] = -3;

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

    private void GetAllDistances(int[][] findPath,int actualNumber) {

        int[][] distanceMap = new int[myTrack.length][myTrack[0].length];
        for (int i = 0; i < findPath.length; i++) {
            for (int j = 0; j < findPath[i].length; j++) {
                distanceMap[i][j] = findPath[i][j];
            }
        }

        ArrayList<Integer> erintettCelok=new ArrayList<>();
        erintettCelok.add(actualNumber);
        distanceMatrix[abs(actualNumber)-1][abs(actualNumber)-1]=0;

        int c = 1;
        outerLoop:
        for (c = 1; c < 1000; c++) {


            for (int i = 0; i < distanceMap.length; i++) {
                for (int j = 0; j < distanceMap[0].length; j++) {
                    if (distanceMap[i][j] == actualNumber || (distanceMap[i][j]==c-1&&distanceMap[i][j] != actualNumber&&distanceMap[i][j] !=0)) {
                        for (int[] neighbor : neighbors) {
                            int newRow = i + neighbor[0];
                            int newCol = j + neighbor[1];
                            try {
                                if (distanceMap[newRow][newCol] == 0)
                                    distanceMap[newRow][newCol] = c;
                                else if (distanceMap[newRow][newCol] <0 && distanceMap[newRow][newCol]!=-100 && !erintettCelok.contains(distanceMap[newRow][newCol])) {
                                    erintettCelok.add(distanceMap[newRow][newCol]);
                                    distanceMatrix[abs(actualNumber)-1][abs(distanceMap[newRow][newCol])-1]=c;
                                    /*
                                    //kiir
                                    for (int[] ints : distanceMap) {
                                        for (int anInt : ints) {
                                            switch (anInt) {
                                                case 0 -> System.out.print(" ");
                                                case -100 -> System.out.print(" ");
                                                default -> System.out.print(abs(anInt % 10) + "");
                                            }
                                        }
                                        System.out.println();
                                    }
                                    */
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