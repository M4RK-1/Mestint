///>.<",vizi.mark@stud.u-szeged.hu

import game.racetrack.Direction;
import game.racetrack.RaceTrackGame;
import game.racetrack.RaceTrackPlayer;
import game.racetrack.utils.Cell;
import game.racetrack.utils.Coin;
import game.racetrack.utils.PlayerState;

import java.util.*;

import static game.racetrack.RaceTrackGame.isNotWall;
import static game.racetrack.RaceTrackGame.line8connect;
import static java.lang.Math.abs;

public class Agent extends RaceTrackPlayer {


    public static class PositionWithParent {
        int value;
        int i;
        int j;
        int parent_value;
        int parent_i;
        int parent_j;


        public PositionWithParent(int value, int i, int j) {
            this.value = value;
            this.i = i;
            this.j = j;
            this.parent_value = -1;
            this.parent_i = -1;
            this.parent_j = -1;
        }

    }

    public static class UltimateCell {
        int faceValue;
        int hiddenValue;
        int i;
        int j;

        ArrayList<int[]> speedVectors = new ArrayList<>();

        public UltimateCell(int faceValue, int hiddenValue, int i, int j) {
            this.faceValue = faceValue;
            this.hiddenValue = hiddenValue;
            this.i = i;
            this.j = j;
        }

        @Override
        public String toString() {
            return faceValue + "";
        }
    }

    public int[][] myTrack = track;

    public int[][] distanceMatrix = new int[12][12];

    List<Integer> bestPath = null;

    List<Cell> destinationList = new ArrayList<>();

    ArrayList<int[]> destinationCordinates = new ArrayList<>();

    public ArrayList<Integer> moveList = new ArrayList<>();
    public int moveListCounter = -1;

    int[][] neighbors = {{0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}};

    int[][] vectorNeighbors = {{0, 0}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}};

    public Agent(PlayerState state, Random random, int[][] track, Coin[] coins, int color) {
        super(state, random, track, coins, color);


        //region coins order
        calculateBestOrderBetweenPoints(state, coins);
        //endregion

        //region copymap
        PositionWithParent[][] findPath = mainMapCopy();
        //endregion


        //region cel kordinatak kinyerese
        destinationCordinatesGenerate(coins);
        //endregion


        //region lepesek kiszamit
        for (int destNum = 0; destNum < destinationCordinates.size() - 1; destNum++) {

            int[] from = new int[]{destinationCordinates.get(destNum)[0], destinationCordinates.get(destNum)[1]};
            int[] to = new int[]{destinationCordinates.get(destNum + 1)[0], destinationCordinates.get(destNum + 1)[1]};


            //region Map Generate
            UltimateCell[][] UltimatePath = new UltimateCell[myTrack.length][myTrack[0].length];
            for (int i = 0; i < UltimatePath.length; i++) {
                for (int j = 0; j < UltimatePath[i].length; j++) {
                    UltimatePath[i][j] = new UltimateCell(-1, -1, i, j);
                }
            }
            for (int i = 0; i < UltimatePath.length; i++) {
                for (int j = 0; j < UltimatePath[i].length; j++) {
                    if (findPath[i][j].value != -1) {
                        UltimatePath[i][j].faceValue = 0;
                        UltimatePath[i][j].hiddenValue = 0;
                    }

                    if (i < 3) {
                        UltimatePath[i][j].faceValue = -1;
                        UltimatePath[i][j].hiddenValue = -1;
                    }
                }
            }
            //endregion


            //region kezdo mezo setup
            UltimatePath[from[0]][from[1]].speedVectors.add(new int[]{0, 0});
            //endregion


            //region fa struktura letrehozasa
            ArrayList<ArrayList<UltimateCell>> stepTree = new ArrayList<>();
            ArrayList<ArrayList<int[]>> speedTree = new ArrayList<>();
            ArrayList<ArrayList<Integer>> directionTree = new ArrayList<>();
            ArrayList<ArrayList<Integer>> treeLayerParents = new ArrayList<>(); //layerek kozotti kapcsolatok
            //endregion

            //region fa elso layer inicialazas
            stepTree.add(new ArrayList<>() {{
                add(UltimatePath[from[0]][from[1]]);
            }});
            speedTree.add(new ArrayList<>() {{
                add(new int[]{0, 0});
            }});
            directionTree.add(new ArrayList<>() {{
                add(0);
            }});
            treeLayerParents.add(new ArrayList<>() {{
                add(0);
            }});
            //endregion


            outerfor:
            for (int c = 0; c < 1000; c++) {
                //printFinalMapHiddenValues(UltimatePath);

                //region tree aktualis layer
                ArrayList<UltimateCell> actualTreeLayer = stepTree.get(c);
                ArrayList<int[]> actualSpeedLayer = speedTree.get(c);
                //endregion

                //region tree kovetkezo layer
                ArrayList<UltimateCell> nextTreeLayer = new ArrayList<>();
                ArrayList<int[]> nextSpeedLayer = new ArrayList<>();
                ArrayList<Integer> nextDirectionLayer = new ArrayList<>();
                ArrayList<Integer> nextLayerParents = new ArrayList<>();
                //endregion


                for (int i = 0; i < actualTreeLayer.size(); i++) {

                    //region aktualis cella adatok
                    UltimateCell actalUltimateCell = actualTreeLayer.get(i);
                    int[] actualSpeed = actualSpeedLayer.get(i);
                    //endregion


                    //region lehetseges lepesek check
                    for (int j = 0; j < vectorNeighbors.length; j++) {
                        int[] neighbor = vectorNeighbors[j];
                        int newRow = actalUltimateCell.i + actualSpeed[0] + neighbor[0];
                        int newCol = actalUltimateCell.j + actualSpeed[1] + neighbor[1];
                        try {
                            //region vizsgalt cell adatok
                            UltimateCell inspectedUltimateCell = UltimatePath[newRow][newCol];
                            int[] newSpeed = new int[]{actualSpeed[0] + neighbor[0], actualSpeed[1] + neighbor[1]};
                            //endregion

                            //region cellan mar meglevo speed check
                            boolean vanEzASpeed = false;
                            for (int[] speed : inspectedUltimateCell.speedVectors) {
                                if (speed[0] == newSpeed[0] && speed[1] == newSpeed[1]) {
                                    vanEzASpeed = true;
                                    break;
                                }
                            }
                            //endregion

                            //region falba utkozes check
                            boolean vanFal = false;
                            Cell lastCell = new Cell(actalUltimateCell.i, actalUltimateCell.j);
                            Cell newCell = new Cell(inspectedUltimateCell.i, inspectedUltimateCell.j);
                            for (Cell utCell : line8connect(lastCell, newCell)) {
                                if (!isNotWall(utCell, track)) {
                                    vanFal = true;
                                    break;
                                }
                            }
                            //endregion

                            //region valid lepes check
                            if (!(vanEzASpeed || vanFal)) {
                                UltimatePath[newRow][newCol].hiddenValue = -2;
                                UltimatePath[newRow][newCol].speedVectors.add(newSpeed);
                                nextTreeLayer.add(UltimatePath[newRow][newCol]);
                                nextSpeedLayer.add(newSpeed);
                                nextDirectionLayer.add(j);
                                nextLayerParents.add(i);

                                //region cel check
                                if (newRow == to[0] && newCol == to[1] && newSpeed[0] == 0 && newSpeed[1] == 0) {
                                    stepTree.add(nextTreeLayer);
                                    speedTree.add(nextSpeedLayer);
                                    directionTree.add(nextDirectionLayer);
                                    treeLayerParents.add(nextLayerParents);
                                    break outerfor;
                                }
                                //endregion

                            }
                            //endregion


                        } catch (Exception ignored) {
                        }
                    }
                    //endregion
                }

                //region layerek hozzaadasa a fahoz
                stepTree.add(nextTreeLayer);
                speedTree.add(nextSpeedLayer);
                directionTree.add(nextDirectionLayer);
                treeLayerParents.add(nextLayerParents);
                //endregion

            }


            ArrayList<UltimateCell> path = new ArrayList<>();
            ArrayList<Integer> pathDirections = new ArrayList<>();

            //region get last layers
            int outerListSize = stepTree.size();
            ArrayList<UltimateCell> lastInnerList = stepTree.get(outerListSize - 1);
            ArrayList<Integer> lastParentList = treeLayerParents.get(outerListSize - 1);
            ArrayList<Integer> lastDirectionList = directionTree.get(outerListSize - 1);
            int innerListSize = lastInnerList.size();
            //endregion

            //regionget destination values
            UltimateCell actualUltimateCell = lastInnerList.get(innerListSize - 1);
            int actualDirection = lastDirectionList.get(innerListSize - 1);
            int actualParent = lastParentList.get(innerListSize - 1);
            //endregion

            pathDirections.add(actualDirection);
            path.add(actualUltimateCell);


            for (int i = outerListSize - 2; i >= 1; i--) {
                //region get layer
                lastInnerList = stepTree.get(i);
                lastParentList = treeLayerParents.get(i);
                lastDirectionList = directionTree.get(i);
                //endregion

                //region get values
                actualDirection = lastDirectionList.get(actualParent);
                actualUltimateCell = lastInnerList.get(actualParent);
                actualParent = lastParentList.get(actualParent);
                //endregion

                path.add(actualUltimateCell);
                pathDirections.add(actualDirection);
            }

            Collections.reverse(path);
            Collections.reverse(pathDirections);

            moveList.addAll(pathDirections);

        }
        //endregion
    }

    @Override
    public Direction getDirection(long remainingTime) {
        moveListCounter++;
        return RaceTrackGame.DIRECTIONS[moveList.get(moveListCounter)];
    }

    /**
     * General es tarol celkoordinatakat a legjobb utvonal alapjan
     * A metodus a 'destinationCoordinates' listat tolti fel koordinatakkal
     * Az elso koordinata [2, 7] ami a start cella
     * Az utolso koordinata [3, 127] ami az altalam kijelolt cel
     * A kozepso koordinatak a 'bestPath' ermeinek pozicioi szerint teszem bele
     *
     * @param coins 'Coin' objektumok tombje
     */
    private void destinationCordinatesGenerate(Coin[] coins) {
        destinationCordinates.add(new int[]{2, 7});
        for (int i = 1; i < bestPath.size() - 1; i++) {
            Coin tmpCoin = new Coin(coins[bestPath.get(i) - 1]);
            destinationCordinates.add(new int[]{tmpCoin.i, tmpCoin.j});
        }
        destinationCordinates.add(new int[]{3, 127});
    }

    /**
     * Letrehoz egy masolatot a 'myTrack'-rol.
     * A vegigmegyek a 'myTrack' tomb minden cellajan, es a cella szerint a 'myTrack'-be bleteszem a megfelelo elemet
     * Ertek 1, akkor a PositionWithParent (0, i, j)
     * Ertek 2, akkor a PositionWithParent (-1, i, j)
     * Ertek 5, akkor a PositionWithParent (-2, i, j)
     * Ertek 17, akkor a PositionWithParent (-3, i, j)
     * Ertek 33, akkor a PositionWithParent (0, i, j)
     * Ha valami hiba van az eredeti 'myTrack'-ben akkor a PositionWithParent (-100, i, j) lesz
     *
     * @return Egy ketdimenzios PositionWithParent tomb ami tartalmazza a map atalakitott masolatat
     */
    private PositionWithParent[][] mainMapCopy() {
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

            }
        }
        return findPath;
    }

    /**
     * Kiszamolja a legrovidebb utvonalat a celok kozott fix elso es utolso pontal
     * A tavolsagot a pontok kozott egy bfs-futtatasaval hatarozom meg
     *
     * @param state A jatekos jelenlegi allapota, ebbol csak a poziccioja kell
     * @param coins 'Coin' objektumok tombje, amely a track-en levo coin-ok at tartalmazza
     */
    private void calculateBestOrderBetweenPoints(PlayerState state, Coin[] coins) {
        int[][] findPath2 = new int[myTrack.length][myTrack[0].length];
        for (int i = 0; i < myTrack.length; i++) {
            for (int j = 0; j < myTrack[i].length; j++) {
                if (myTrack[i][j] == 1) {
                    findPath2[i][j] = 0;
                } else if (myTrack[i][j] == 2) {
                    findPath2[i][j] = -100;
                } else if (myTrack[i][j] == 5) {
                    findPath2[i][j] = -12;
                } else if (myTrack[i][j] == 33) {
                    findPath2[i][j] = 0;
                } else {
                    findPath2[i][j] = -100;
                }
                findPath2[state.i][state.j] = -1;
            }
        }
        for (int j = 0; j < coins.length; j++) {
            findPath2[coins[j].i][coins[j].j] = ((j + 2) * -1);
        }


        for (int actualNumber = -1; actualNumber > -13; actualNumber--) {
            getAllDistances(findPath2, actualNumber);
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

    /**
     * Minden leheto permutaciot legenerál, es hozzaadja azokat egy eredmeny listahoz
     * A tomben szerepelnek a szamok amiket akarunk hasznalni a permutacio-hoz
     *
     * @param nums   Az az egesz szamok tombje, amelynek permutacioit generalni kell
     * @param index  Az aktualis index a tombben
     * @param result Az a lista, ahol minden generalt permutaciot tarolnak
     */
    private static void generatePermutations(int[] nums, int index, List<int[]> result) {
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

    /**
     * Kiszamitja egy adott utvonal hosszat.
     *
     * @param path           Egy Integer lista, amelyben az adott utvonal pontjainak sorrendje van benne
     * @param distanceMatrix Egy ketdimenzios tomb, amely minden pontok kozotti tavolsagokat tartalmazza
     * @return Az utvonal hossza
     */
    private static int calculateTotalDistance(List<Integer> path, int[][] distanceMatrix) {
        int totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            totalDistance += distanceMatrix[path.get(i)][path.get(i + 1)];
        }
        return totalDistance;
    }

    /**
     * Kiszamiolja egy adott cel-tol a tobbi cel tavolsagat
     * Ezt elment a 'distanceMatrix' tombbe
     *
     * @param findPath     A kezdeti map
     * @param actualNumber A kiindulasi pont indexe, innen szamoljuk a tavolsagokat
     */
    private void getAllDistances(int[][] findPath, int actualNumber) {

        int[][] distanceMap = new int[myTrack.length][myTrack[0].length];
        for (int i = 0; i < findPath.length; i++) {
            System.arraycopy(findPath[i], 0, distanceMap[i], 0, findPath[i].length);
        }

        ArrayList<Integer> erintettCelok = new ArrayList<>();
        erintettCelok.add(actualNumber);
        distanceMatrix[abs(actualNumber) - 1][abs(actualNumber) - 1] = 0;

        int c;
        for (c = 1; c < 1000; c++) {
            for (int i = 0; i < distanceMap.length; i++) {
                for (int j = 0; j < distanceMap[0].length; j++) {
                    if (distanceMap[i][j] == actualNumber || (distanceMap[i][j] == c - 1 && distanceMap[i][j] != actualNumber && distanceMap[i][j] != 0)) {
                        for (int[] neighbor : neighbors) {
                            int newRow = i + neighbor[0];
                            int newCol = j + neighbor[1];
                            try {
                                if (distanceMap[newRow][newCol] == 0) distanceMap[newRow][newCol] = c;
                                else if (distanceMap[newRow][newCol] < 0 && distanceMap[newRow][newCol] != -100 && !erintettCelok.contains(distanceMap[newRow][newCol])) {
                                    erintettCelok.add(distanceMap[newRow][newCol]);
                                    distanceMatrix[abs(actualNumber) - 1][abs(distanceMap[newRow][newCol]) - 1] = c;
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }
        }
    }
}

//java -jar game_engine.jar 0 game.racetrack.RaceTrackGame 11 27 5 0.1 10 1234567890 1000 SamplePlayer

//java -jar game_engine.jar 100 game.racetrack.RaceTrackGame 11 27 5 0.1 10 $((Get-Random -Minimum 0 -Maximum 999999999)) 1000 SamplePlayer