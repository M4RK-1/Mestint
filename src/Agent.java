///>.<",vizi.mark@stud.u-szeged.hu

import game.racetrack.Direction;
import game.racetrack.RaceTrackGame;
import game.racetrack.RaceTrackPlayer;
import game.racetrack.utils.Cell;
import game.racetrack.utils.Coin;
import game.racetrack.utils.PlayerState;

import java.util.*;

import static game.racetrack.RaceTrackGame.*;

public class Agent extends RaceTrackPlayer {


    public static class TreeLayer {
        ArrayList<UltimateCell> stepLayer = new ArrayList<>();
        ArrayList<int[]> speedLayer = new ArrayList<>();
        ArrayList<Integer> directionLayer = new ArrayList<>();
        ArrayList<Integer> layerConnectionLayer = new ArrayList<>();

        public TreeLayer(ArrayList<UltimateCell> stepLayer, ArrayList<int[]> speedLayer, ArrayList<Integer> directionLayer, ArrayList<Integer> layerConnectionLayer) {
            this.stepLayer = stepLayer;
            this.speedLayer = speedLayer;
            this.directionLayer = directionLayer;
            this.layerConnectionLayer = layerConnectionLayer;
        }

        public TreeLayer() {
        }


    }

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

        public UltimateCell(int i, int j) {
            this.faceValue = 0;
            this.hiddenValue = 0;
            this.i = i;
            this.j = j;
        }

        @Override
        public String toString() {
            return faceValue + "";
        }
    }

    public int[][] myTrack = track;

    PositionWithParent[][] findPath;

    ArrayList<Cell> finishCells = new ArrayList<>();

    public int[][] distanceMatrix = new int[12][12];


    List<Integer> bestPath = null;

    List<Cell> destinationList = new ArrayList<>();

    ArrayList<int[]> destinationCordinates = new ArrayList<>();


    public ArrayList<Integer> moveList = new ArrayList<>();

    public ArrayList<TreeLayer> debugMoveList = new ArrayList<>();
    public int moveListCounter = -1;


    int[][] vectorNeighbors = {{0, 0}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}};

    public Agent(PlayerState state, Random random, int[][] track, Coin[] coins, int color) {
        super(state, random, track, coins, color);

        //region copymap
        findPath = mainMapCopy();
        //endregion


        //region coins order
        calculateBestOrderBetweenPoints(coins);
        //endregion


        //region cel kordinatak kinyerese
        destinationCordinatesGenerate(coins);
        //endregion


        //region map letrehozas
        UltimateCell[][] UltimatePath = new UltimateCell[myTrack.length][myTrack[0].length];
        for (int i = 0; i < UltimatePath.length; i++) {
            for (int j = 0; j < UltimatePath[i].length; j++) {
                UltimatePath[i][j] = new UltimateCell(-1, -1, i, j);
            }
        }
        //endregion

        //region map feltoltese
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

        //region fa struktura letrehozasa
        ArrayList<TreeLayer> tree = new ArrayList<>();
        //endregion


        //region fa elso layer
        UltimateCell tmpUCell = UltimatePath[2][7];
        tmpUCell.speedVectors.add(new int[]{0, 0});
        TreeLayer elsoLayer = new TreeLayer(new ArrayList<>() {{
            add(tmpUCell);
        }},
                new ArrayList<>() {{
                    add(new int[]{0, 0});
                }},
                new ArrayList<>() {{
                    add(0);
                }},
                new ArrayList<>() {{
                    add(0);
                }}
        );

        tree.add(elsoLayer);
        //endregion


        for (int destNum = 0; destNum < destinationCordinates.size() - 1; destNum++) {
            int chokeTreeCounter = 0;
            int[] to = new int[]{destinationCordinates.get(destNum + 1)[0], destinationCordinates.get(destNum + 1)[1]};

            //region Map Generate
            UltimatePath = new UltimateCell[myTrack.length][myTrack[0].length];
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


            //region lepes szamolas
            outerfor:
            while (true) {
                //region tree aktualis layer
                ArrayList<UltimateCell> actualTreeLayer = tree.get(tree.size() - 1).stepLayer;
                ArrayList<int[]> actualSpeedLayer = tree.get(tree.size() - 1).speedLayer;
                //endregion

                //region tree kovetkezo layer
                ArrayList<UltimateCell> nextTreeLayer = new ArrayList<>();
                ArrayList<int[]> nextSpeedLayer = new ArrayList<>();
                ArrayList<Integer> nextDirectionLayer = new ArrayList<>();
                ArrayList<Integer> nextLayerParents = new ArrayList<>();
                //endregion

                //region tree kapcsolati layer
                ArrayList<UltimateCell> lastTreeLayer = new ArrayList<>();
                ArrayList<int[]> lastSpeedLayer = new ArrayList<>();
                ArrayList<Integer> lastDirectionLayer = new ArrayList<>();
                ArrayList<Integer> lastLayerParents = new ArrayList<>();
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

                        //region vizsgalt cell adatok
                        UltimateCell inspectedUltimateCell = new UltimateCell(newRow, newCol);
                        int[] newSpeed = new int[]{actualSpeed[0] + neighbor[0], actualSpeed[1] + neighbor[1]};
                        //endregion

                        //region falba utkozes check
                        Cell lastCell = new Cell(actalUltimateCell.i, actalUltimateCell.j);
                        Cell newCell = new Cell(inspectedUltimateCell.i, inspectedUltimateCell.j);
                        for (Cell utCell : line8connect(lastCell, newCell)) {
                            if (isNotWall(utCell, track)) {
                                inspectedUltimateCell = UltimatePath[utCell.i][utCell.j];
                                newSpeed[0] = inspectedUltimateCell.i - actalUltimateCell.i;
                                newSpeed[1] = inspectedUltimateCell.j - actalUltimateCell.j;
                            } else {
                                break;
                            }
                        }
                        newCell = new Cell(inspectedUltimateCell.i, inspectedUltimateCell.j);
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

                        //region valid lepes check
                        if (!(vanEzASpeed)) {
                            //region layer node add
                            UltimatePath[inspectedUltimateCell.i][inspectedUltimateCell.j].hiddenValue = -2;
                            UltimatePath[inspectedUltimateCell.i][inspectedUltimateCell.j].speedVectors.add(new int[]{newSpeed[0], newSpeed[1]});
                            nextTreeLayer.add(UltimatePath[inspectedUltimateCell.i][inspectedUltimateCell.j]);
                            nextSpeedLayer.add(new int[]{newSpeed[0], newSpeed[1]});
                            nextDirectionLayer.add(j);
                            nextLayerParents.add(i);
                            //endregion

                            //region cel check
                            if (destNum < 10) { //coin check
                                for (Cell utCell : lineCrossing(lastCell, newCell)) {
                                    if (utCell.i == to[0] && utCell.j == to[1]) {
                                        lastTreeLayer.add(UltimatePath[inspectedUltimateCell.i][inspectedUltimateCell.j]);
                                        lastSpeedLayer.add(new int[]{newSpeed[0], newSpeed[1]});
                                        lastDirectionLayer.add(j);
                                        lastLayerParents.add(i);
                                        chokeTreeCounter++;
                                        if (chokeTreeCounter==2){
                                            tree.add(new TreeLayer(lastTreeLayer, lastSpeedLayer, lastDirectionLayer, lastLayerParents));
                                            break outerfor;
                                        }
                                    }
                                }
                            } else { //finish check
                                for (Cell finCell : finishCells) {
                                    if (inspectedUltimateCell.i == finCell.i && inspectedUltimateCell.j == finCell.j) {
                                        tree.add(new TreeLayer(nextTreeLayer, nextSpeedLayer, nextDirectionLayer, nextLayerParents));
                                        break outerfor;
                                    }
                                }
                            }
                            //endregion
                        }
                    }
                    //endregion
                }

                //region kapcsolat layer hozzaadasa a fahoz
                if (chokeTreeCounter != 0) {
                    tree.add(new TreeLayer(lastTreeLayer, lastSpeedLayer, lastDirectionLayer, lastLayerParents));
                    break;
                }
                //endregion
                //region layer hozzaadasa a fahoz
                tree.add(new TreeLayer(nextTreeLayer, nextSpeedLayer, nextDirectionLayer, nextLayerParents));
                //endregion


            }
        }

        //region Path Calculate
        ArrayList<Integer> pathDirections = new ArrayList<>();
        ArrayList<TreeLayer> finalPathInTheTree = new ArrayList<>();

        //region get last layers
        int outerListSize = tree.size();
        ArrayList<UltimateCell> lastStepList = tree.get(outerListSize - 1).stepLayer;
        ArrayList<int[]> lastSpeedList = tree.get(outerListSize - 1).speedLayer;
        ArrayList<Integer> lastDirectionList = tree.get(outerListSize - 1).directionLayer;
        ArrayList<Integer> lastConnectionList = tree.get(outerListSize - 1).layerConnectionLayer;

        int innerListSize = lastStepList.size();
        //endregion

        //region get destination values
        UltimateCell actualUltimateCell = lastStepList.get(innerListSize - 1);
        int[] actualSpeed = lastSpeedList.get(innerListSize - 1);
        int actualDirection = lastDirectionList.get(innerListSize - 1);
        int actualConnection = lastConnectionList.get(innerListSize - 1);
        //endregion

        //region save destination values
        pathDirections.add(actualDirection);
        finalPathInTheTree.add(new TreeLayer());
        finalPathInTheTree.get(0).stepLayer.add(actualUltimateCell);
        finalPathInTheTree.get(0).speedLayer.add(actualSpeed);
        finalPathInTheTree.get(0).directionLayer.add(actualDirection);
        finalPathInTheTree.get(0).layerConnectionLayer.add(actualConnection);
        //endregion

        //region get layers
        for (int i = outerListSize - 2; i >= 1; i--) {
            //region get layer
            lastStepList = tree.get(i).stepLayer;
            lastSpeedList = tree.get(i).speedLayer;
            lastDirectionList = tree.get(i).directionLayer;
            lastConnectionList = tree.get(i).layerConnectionLayer;
            //endregion

            //region destination get values
            actualUltimateCell = lastStepList.get(actualConnection);
            actualSpeed = lastSpeedList.get(actualConnection);
            actualDirection = lastDirectionList.get(actualConnection);
            actualConnection = lastConnectionList.get(actualConnection);
            //endregion

            //region save destination values
            pathDirections.add(actualDirection);
            finalPathInTheTree.add(new TreeLayer());
            finalPathInTheTree.get(finalPathInTheTree.size() - 1).stepLayer.add(actualUltimateCell);
            finalPathInTheTree.get(finalPathInTheTree.size() - 1).speedLayer.add(actualSpeed);
            finalPathInTheTree.get(finalPathInTheTree.size() - 1).directionLayer.add(actualDirection);
            finalPathInTheTree.get(finalPathInTheTree.size() - 1).layerConnectionLayer.add(actualConnection);
            //endregion
        }
        //endregion

        //region order correction
        Collections.reverse(pathDirections);
        Collections.reverse(finalPathInTheTree);
        //endregion
        //endregion


        moveList.addAll(pathDirections);
        debugMoveList.addAll(finalPathInTheTree);
        //endregion
    }


    @Override
    public Direction getDirection(long remainingTime) {
        moveListCounter++;
        return RaceTrackGame.DIRECTIONS[moveList.get(moveListCounter)];
    }

    /**
     * Kiszamolja a tavolsagot ket Cell kozott
     *
     * @param start  Start cella
     * @param finish Cel cella
     * @return a ket pont kozotti tavolsag a mappon
     */
    private int getDistanceBetweenTwoPoints(Cell start, Cell finish) {

        int[] from = new int[]{start.i, start.j};
        int[] to = new int[]{finish.i, finish.j};


        //region Map Generate
        UltimateCell[][] UltimatePath = new UltimateCell[myTrack.length][myTrack[0].length];

        for (int i = 0; i < UltimatePath.length; i++) {
            for (int j = 0; j < UltimatePath[i].length; j++) {
                UltimatePath[i][j] = new UltimateCell(-1, -1, i, j);
            }
        }

        for (int i = 0; i < UltimatePath.length; i++) {
            for (int j = 0; j < UltimatePath[i].length; j++) {
                if (findPath[i][j].value != -1 && i >= 3) {
                    UltimatePath[i][j].faceValue = 0;
                    UltimatePath[i][j].hiddenValue = 0;
                }
            }
        }
        //endregion

        //region fa struktura letrehozasa
        ArrayList<TreeLayer> tree = new ArrayList<>();
        //endregion


        TreeLayer elsoLayer = new TreeLayer(new ArrayList<>() {{
            add(UltimatePath[from[0]][from[1]]);
        }},
                new ArrayList<>() {{
                    add(new int[]{0, 0});
                }},
                new ArrayList<>() {{
                    add(0);
                }},
                new ArrayList<>() {{
                    add(0);
                }}
        );

        tree.add(elsoLayer);
        //endregion


        outerfor:
        for (int c = 0; ; c++) {
            //region tree aktualis layer
            ArrayList<UltimateCell> actualTreeLayer = tree.get(c).stepLayer;
            ArrayList<int[]> actualSpeedLayer = tree.get(c).speedLayer;
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

                    //region vizsgalt cell adatok
                    UltimateCell inspectedUltimateCell = new UltimateCell(newRow, newCol);
                    int[] newSpeed = new int[]{actualSpeed[0] + neighbor[0], actualSpeed[1] + neighbor[1]};
                    //endregion

                    //region falba utkozes check
                    Cell lastCell = new Cell(actalUltimateCell.i, actalUltimateCell.j);
                    Cell newCell = new Cell(inspectedUltimateCell.i, inspectedUltimateCell.j);
                    for (Cell utCell : line8connect(lastCell, newCell)) {
                        if (isNotWall(utCell, track)) {
                            inspectedUltimateCell = UltimatePath[utCell.i][utCell.j];
                            newSpeed[0] = inspectedUltimateCell.i - actalUltimateCell.i;
                            newSpeed[1] = inspectedUltimateCell.j - actalUltimateCell.j;
                        } else {
                            break;
                        }
                    }
                    newCell = new Cell(inspectedUltimateCell.i, inspectedUltimateCell.j);
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

                    //region valid lepes check
                    if (!(vanEzASpeed)) {
                        //region layer node add
                        UltimatePath[inspectedUltimateCell.i][inspectedUltimateCell.j].hiddenValue = -2;
                        UltimatePath[inspectedUltimateCell.i][inspectedUltimateCell.j].speedVectors.add(new int[]{newSpeed[0], newSpeed[1]});
                        nextTreeLayer.add(UltimatePath[inspectedUltimateCell.i][inspectedUltimateCell.j]);
                        nextSpeedLayer.add(new int[]{newSpeed[0], newSpeed[1]});
                        nextDirectionLayer.add(j);
                        nextLayerParents.add(i);
                        //endregion

                        //region cel check

                        for (Cell utCell : lineCrossing(lastCell, newCell)) {
                            if (utCell.i == to[0] && utCell.j == to[1]) {
                                tree.add(new TreeLayer(nextTreeLayer, nextSpeedLayer, nextDirectionLayer, nextLayerParents));
                                break outerfor;
                            }
                        }
                        //endregion
                    }
                }
                //endregion
            }

            //region layerek hozzaadasa a fahoz
            tree.add(new TreeLayer(nextTreeLayer, nextSpeedLayer, nextDirectionLayer, nextLayerParents));
            //endregion


        }

        return tree.size();

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
                    finishCells.add(new Cell(i, j));
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
     *
     * @param coins 'Coin' objektumok tombje, amely a track-en levo coin-ok at tartalmazza
     */
    private void calculateBestOrderBetweenPoints(Coin[] coins) {
        getAllDistances2();

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
     * Kiszamiolja az osszes cel-tol a tobbi celig a tavolsagat
     * Ezt elment a 'distanceMatrix' tomb-be
     */
    private void getAllDistances2() {
        ArrayList<Cell> cordinates = new ArrayList<>();
        cordinates.add(new Cell(2, 7));
        for (Coin coin : coins) {
            cordinates.add(new Cell(coin.i, coin.j));
        }
        cordinates.add(new Cell(3, 127));

        for (int i = 0; i < cordinates.size(); i++) {
            for (int j = i; j < cordinates.size(); j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                }

                int tmp = getDistanceBetweenTwoPoints(cordinates.get(i), cordinates.get(j));
                distanceMatrix[i][j] = tmp;
                distanceMatrix[j][i] = tmp;


            }
        }
    }
}