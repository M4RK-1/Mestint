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

public class SpeedTest extends RaceTrackPlayer {


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

        public PositionWithParent(PositionWithParent other) {
            this.value = other.value;
            this.i = other.i;
            this.j = other.j;
            this.parent_i = other.parent_i;
            this.parent_j = other.parent_j;
            this.parent_value = other.parent_value;
        }

    }

    public static class UltimateCell{
        int faceValue;
        int hiddenValue;
        int i;
        int j;

        ArrayList<int[]> speedVectors=new ArrayList<>();

        ArrayList<Integer>   numberOfSteps=new ArrayList<>();

        ArrayList<int[]> parentPosition=new ArrayList<>();
        public UltimateCell(int faceValue, int hiddenValue, int i, int j) {
            this.faceValue = faceValue;
            this.hiddenValue = hiddenValue;
            this.i = i;
            this.j = j;
        }

        public UltimateCell(int faceValue, int i, int j) {
            this.faceValue = faceValue;
            this.hiddenValue = 0;
            this.i = i;
            this.j = j;
        }

        @Override
        public String toString() {
            return faceValue+"";
        }
    }

    final boolean FULLMAPSEARCH =true;

    public int[][] myTrack = track;

    public int[][] distanceMatrix = new int[12][12];

    List<Integer> bestPath = null;

    List<Cell> destinationList = new ArrayList<>();


    ArrayList<PositionWithParent> combinedRouteCellsList = new ArrayList<>();

    ArrayList<PositionWithParent[][]> stepPaths = new ArrayList<>();

    ArrayList<int[]> speedVectors = new ArrayList<>();


    public ArrayList<Integer> moveList = new ArrayList<>();
    public int moveListCounter = -1;

    ArrayList<ArrayList<Integer>> switchPlace = new ArrayList<>();
    int[][] neighbors = {
            {0, -1}, {-1, -1}, {-1, 0}, {-1, 1},
            {0, 1}, {1, 1}, {1, 0}, {1, -1}
    };

    int[][] vectorNeighbors = {
            {0, 0}, {0, -1}, {-1, -1}, {-1, 0},
            {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}
    };

    public SpeedTest(PlayerState state, Random random, int[][] track, Coin[] coins, int color) {
        super(state, random, track, coins, color);


        //region coins order
        calculateBestOrderBetweenPoints(state, coins);
        //endregion

        //region copymap
        PositionWithParent[][] findPath = mainMapCopy(state);
        //endregion


        //region map general
        for (int i = 1; i < destinationList.size(); i++) {
            PositionWithParent[][] path = findPathBetweenTwoPoint(findPath, destinationList.get(i - 1), destinationList.get(i));
            PositionWithParent[][] pathCopy = copyPath(path);
            stepPaths.add(pathCopy);
        }
        //endregion

        //region cel kordinatak kinyerese
        ArrayList<int[]> destinationCordinates = new ArrayList<>();
        destinationCordinates.add(new int[]{2, 7});
        for (int i = 1; i < bestPath.size()-1; i++) {
            Coin tmpCoin= new Coin(coins[bestPath.get(i)-1]);
            destinationCordinates.add(new int[]{tmpCoin.i, tmpCoin.j});
        }
        destinationCordinates.add(new int[]{3, 127});
        //endregion

        

        for (int mapNumber = 0; mapNumber < stepPaths.size(); mapNumber++) {
            PositionWithParent[][] mapPositionWithParent = stepPaths.get(mapNumber);

            UltimateCell[][] UltimatePath = new UltimateCell[myTrack.length][myTrack[0].length];
            for (int i = 0; i < UltimatePath.length; i++) {
                for (int j = 0; j < UltimatePath[i].length; j++) {
                    UltimatePath[i][j] = new UltimateCell(-1, -1, i, j);
                }
            }


            for (int i = 0; i < mapPositionWithParent.length; i++) {
                for (int j = 0; j < mapPositionWithParent[i].length; j++) {
                    if (mapPositionWithParent[i][j].value == 1) {
                        for (int[] neighbor : vectorNeighbors) {
                            int newRow = i + neighbor[0];
                            int newCol = j + neighbor[1];
                            try {
                                if (findPath[newRow][newCol].value!=-1){
                                    UltimatePath[newRow][newCol].faceValue = 0;
                                    UltimatePath[newRow][newCol].hiddenValue = 0;
                                }

                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }



            for (int i = 0; i < UltimatePath.length; i++) {
                for (int j = 0; j < UltimatePath[i].length; j++) {
                    if (FULLMAPSEARCH){
                        if (findPath[i][j].value!=-1){
                            UltimatePath[i][j].faceValue = 0;
                            UltimatePath[i][j].hiddenValue = 0;
                        }
                    }

                    if (i < 3) {
                        UltimatePath[i][j].faceValue = -1;
                        UltimatePath[i][j].hiddenValue = -1;
                    }
                }
            }
            //printFinalMapFaceValues(UltimatePath);


            int[] from = new int[]{
                    destinationCordinates.get(mapNumber)[0],destinationCordinates.get(mapNumber)[1]
            };
            int[] to = new int[]{
                    destinationCordinates.get(mapNumber+1)[0],destinationCordinates.get(mapNumber+1)[1]
            };

            UltimatePath[from[0]][from[1]].speedVectors.add(new int[]{0, 0});
            UltimatePath[from[0]][from[1]].numberOfSteps.add(0);


            ArrayList<ArrayList<UltimateCell>> stepTree = new ArrayList<>();
            ArrayList<ArrayList<int[]>> speedTree = new ArrayList<>();
            ArrayList<ArrayList<Integer>> directionTree = new ArrayList<>();
            ArrayList<ArrayList<Integer>> treeLayerParents = new ArrayList<>();

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


            outerfor:
            for (int c = 0; c < 1000; c++) {
                //printFinalMapHiddenValues(UltimatePath);

                //clear last steps
                for (PositionWithParent[][] stepPath : stepPaths) {
                    for (int i = 0; i < stepPath.length; i++) {
                        for (int j = 0; j < stepPath[i].length; j++) {
                            if (stepPath[i][j].value == 1) {
                                for (int[] neighbor : vectorNeighbors) {
                                    int newRow = i + neighbor[0];
                                    int newCol = j + neighbor[1];
                                    try {
                                        if (UltimatePath[newRow][newCol].hiddenValue == -2) {
                                            UltimatePath[newRow][newCol].hiddenValue = 0;
                                        }

                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                        }
                    }
                }

                //System.out.println();

                ArrayList<UltimateCell> actualTreeLayer = stepTree.get(c);
                ArrayList<int[]> actualSpeedLayer = speedTree.get(c);


                ArrayList<UltimateCell> nextTreeLayer = new ArrayList<>();
                ArrayList<int[]> nextSpeedLayer = new ArrayList<>();
                ArrayList<Integer> nextDirectionLayer = new ArrayList<>();
                ArrayList<Integer> nextLayerParents = new ArrayList<>();


                for (int i = 0; i < actualTreeLayer.size(); i++) {

                    UltimateCell actalUltimateCell = actualTreeLayer.get(i);
                    int[] actualSpeed = actualSpeedLayer.get(i);


                    for (int j = 0; j < vectorNeighbors.length; j++) {
                        int[] neighbor = vectorNeighbors[j];
                        int newRow = actalUltimateCell.i + actualSpeed[0] + neighbor[0];
                        int newCol = actalUltimateCell.j + actualSpeed[1] + neighbor[1];
                        try {
                            UltimateCell inspectedUltimateCell = UltimatePath[newRow][newCol];
                            if (inspectedUltimateCell.faceValue == 0) {
                                int[] newSpeed = new int[]{actualSpeed[0] + neighbor[0], actualSpeed[1] + neighbor[1]};

                                //mar meglevo speed check
                                boolean vanEzASpeed = false;
                                for (int[] speed : inspectedUltimateCell.speedVectors) {
                                    if (speed[0] == newSpeed[0] && speed[1] == newSpeed[1]) {
                                        vanEzASpeed = true;
                                        break;
                                    }
                                }
                                //falba utkozes check
                                boolean vanFal = false;
                                Cell lastCell = new Cell(actalUltimateCell.i, actalUltimateCell.j);
                                Cell newCell = new Cell(inspectedUltimateCell.i, inspectedUltimateCell.j);
                                for (Cell utCell : line8connect(lastCell, newCell)) {
                                    if (!isNotWall(utCell, track)) {
                                        vanFal = true;
                                        break;
                                    }
                                }

                                if (!(vanEzASpeed || vanFal)) {
                                    UltimatePath[newRow][newCol].hiddenValue = -2;
                                    UltimatePath[newRow][newCol].speedVectors.add(newSpeed);
                                    UltimatePath[newRow][newCol].numberOfSteps.add(c + 1);
                                    UltimatePath[newRow][newCol].parentPosition.add(new int[]{actalUltimateCell.i, actalUltimateCell.j});
                                    nextTreeLayer.add(UltimatePath[newRow][newCol]);
                                    nextSpeedLayer.add(newSpeed);
                                    nextDirectionLayer.add(j);
                                    nextLayerParents.add(i);

                                    if (newRow == to[0] && newCol == to[1]
                                            && newSpeed[0] == 0 && newSpeed[1] == 0) {
                                        stepTree.add(nextTreeLayer);
                                        speedTree.add(nextSpeedLayer);
                                        directionTree.add(nextDirectionLayer);
                                        treeLayerParents.add(nextLayerParents);
                                        break outerfor;
                                    }

                                }

                            }

                        } catch (Exception ignored) {
                        }
                    }
                }


                //region purge

                //System.out.println(c+".interation");
                //System.out.println("before purge:"+nextTreeLayer.size());


                //nextTreeLayer.removeIf(n -> squareDistance(2,7 ,n.i,n.j) < finalC-1 );

            /*if (c>3){

                int furthestIndex=0;
                double maxDistance=0;

                for (int x = 0; x < stepTree.get(c-3).size(); x++) {
                    if (absoluteDistance(stepTree.get(c-3).get(x).i,stepTree.get(c-3).get(x).j ,
                            actualDestinationCordinates[0],actualDestinationCordinates[1])>maxDistance)
                    {
                    furthestIndex=x;
                    maxDistance=absoluteDistance(stepTree.get(c-3).get(x).i,stepTree.get(c-3).get(x).j ,
                            actualDestinationCordinates[0],actualDestinationCordinates[1]);
                    }
                }


                ArrayList<Double> tavolsagok =new ArrayList<>();
                for (int x = 0; x < nextTreeLayer.size(); x++) {
                    tavolsagok.add(absoluteDistance(2,7 ,nextTreeLayer.get(x).i,nextTreeLayer.get(x).j));
                }
                Set<Double> duplicateCheck = new HashSet<>();

                Iterator<Double> iterator = tavolsagok.iterator();
                while (iterator.hasNext()) {
                    if (!duplicateCheck.add(iterator.next())) {
                        iterator.remove();
                    }
                }

                Collections.sort(tavolsagok, Collections.reverseOrder());
                if (tavolsagok.size() > 25) {
                    tavolsagok.subList(25, tavolsagok.size()).clear();
                }


                for (int x = 0; x < nextTreeLayer.size(); x++) {
                    if (!tavolsagok.contains(absoluteDistance(2,7 ,nextTreeLayer.get(x).i,nextTreeLayer.get(x).j))){
                        UltimateCell tmpCell = nextTreeLayer.get(x);
                        UltimatePath[tmpCell.i][tmpCell.j].hiddenValue=0;
                        nextTreeLayer.remove(x);
                        nextSpeedLayer.remove(x);
                        nextDirectionLayer.remove(x);
                        x--;

                    }
                }
            }



            while (nextTreeLayer.size() > 1000) {
                int randomIndex = random.nextInt(nextTreeLayer.size());
                    nextTreeLayer.remove(randomIndex);
                    nextSpeedLayer.remove(randomIndex);
                    nextDirectionLayer.remove(randomIndex);
            }
            System.out.println("after purge:"+nextTreeLayer.size());
            System.out.println();*/

                //endregion


                stepTree.add(nextTreeLayer);
                speedTree.add(nextSpeedLayer);
                directionTree.add(nextDirectionLayer);
                treeLayerParents.add(nextLayerParents);

            }

            ArrayList<UltimateCell> path = new ArrayList<>();
            ArrayList<Integer> pathDirections = new ArrayList<>();
            int outerListSize = stepTree.size();
            ArrayList<UltimateCell> lastInnerList = stepTree.get(outerListSize - 1);
            ArrayList<Integer> lastParentList = treeLayerParents.get(outerListSize - 1);
            ArrayList<Integer> lastDirectionList = directionTree.get(outerListSize - 1);
            int innerListSize = lastInnerList.size();

            UltimateCell actualUltimateCell = lastInnerList.get(innerListSize - 1);
            int actualDirection = lastDirectionList.get(innerListSize - 1);
            int actualParent = lastParentList.get(innerListSize - 1);
            pathDirections.add(actualDirection);
            path.add(actualUltimateCell);

            for (int i = outerListSize - 2; i >= 1; i--) {
                lastInnerList = stepTree.get(i);
                lastParentList = treeLayerParents.get(i);
                lastDirectionList = directionTree.get(i);


                actualDirection = lastDirectionList.get(actualParent);
                actualUltimateCell = lastInnerList.get(actualParent);
                actualParent = lastParentList.get(actualParent);


                path.add(actualUltimateCell);
                pathDirections.add(actualDirection);
            }
            //System.out.println("GECI");

            Collections.reverse(path);
            Collections.reverse(pathDirections);

            moveList.addAll(pathDirections);

        }
    }

    public int squareDistance(int ai,int aj,int bi, int bj) {
        return Math.abs(ai - bi) + Math.abs(aj - bj);
    }

    public double absoluteDistance(int ai,int aj,int bi, int bj) {
        return Math.sqrt((ai - bi) * (ai - bi) + (aj - bj) * (aj - bj));
    }

    private static void printFinalMapHiddenValues(UltimateCell[][] mergedPath) {
        for (int i = 0; i < mergedPath.length; i++) {
            for (int j = 0; j < mergedPath[i].length; j++) {
                if (mergedPath[i][j].hiddenValue==-1){
                    System.out.print("X ");
                }else if (mergedPath[i][j].hiddenValue==0){
                    System.out.print("  ");
                }else {
                    System.out.print(abs(mergedPath[i][j].hiddenValue)+" ");
                }
            }
            System.out.println();
        }
    }

    private static void printFinalMapFaceValues(UltimateCell[][] mergedPath) {
        for (int i = 0; i < mergedPath.length; i++) {
            for (int j = 0; j < mergedPath[i].length; j++) {
                if (mergedPath[i][j].faceValue==-1){
                    System.out.print("X ");
                }else if (mergedPath[i][j].faceValue==0){
                    System.out.print("  ");
                }else {
                    System.out.print(mergedPath[i][j].faceValue+" ");
                }
            }
            System.out.println();
        }
    }

    @Override
    public Direction getDirection(long remainingTime) {
        moveListCounter++;
        return RaceTrackGame.DIRECTIONS[moveList.get(moveListCounter)];
    }


    private void speedupOnTheStraights() {
        int numCounter = 0;

        moveList.remove(moveList.size() - 1);
        for (int i = 0; i < 40; i++) {
            moveList.add(0);
        }
        moveList.add(4);


        int c = -1;
        for (int i = 0; i < moveList.size(); i++) {
            c++;
            Integer integer = moveList.get(i);
            if (integer == 0) {
                numCounter++;
            } else if (numCounter > 2) {
                int startIndex = i - numCounter;
                int endIndex = i;

                List<Integer> cutPortion = moveList.subList(startIndex, endIndex);

                ArrayList<Integer> switchVector = new ArrayList<>();
                ArrayList<Integer> actualSwitchPlace = switchPlace.get(numCounter);

                int lol = directionToInt(new Direction(speedVectors.get(c - numCounter)[0], speedVectors.get(c - numCounter)[1]));


                for (Integer value : actualSwitchPlace) {
                    if (value == 1) {
                        switchVector.add(lol);
                    } else if (value == -1) {
                        switchVector.add(revesreDirectionNum(lol));
                    } else {
                        switchVector.add(0);
                    }
                }


                cutPortion.clear();
                cutPortion.addAll(switchVector);
                numCounter = 0;
                i = actualSwitchPlace.size() + startIndex;
            } else {
                numCounter = 0;
            }

        }
    }

    private void singleStepIntListCalculate() {
        int[] currentSpeedVector = new int[]{0, 0};
        speedVectors.add(new int[]{currentSpeedVector[0], currentSpeedVector[1]});
        for (int i = 0; i < combinedRouteCellsList.size() - 1; i++) {

            PositionWithParent currentCell = combinedRouteCellsList.get(i);
            PositionWithParent nextCell = combinedRouteCellsList.get(i + 1);


            int[] tempSpeedVector = new int[2];
            tempSpeedVector[0] = currentSpeedVector[0] - (nextCell.i - currentCell.i);
            tempSpeedVector[1] = currentSpeedVector[1] - (nextCell.j - currentCell.j);

            try {
                for (int j = 0; j < vectorNeighbors.length + 1; j++) {
                    int[] neighbor = vectorNeighbors[j];
                    if (neighbor[0] == tempSpeedVector[0] * -1 && neighbor[1] == tempSpeedVector[1] * -1) {
                        moveList.add(j);
                        break;
                    }
                }
                currentSpeedVector[0] -= tempSpeedVector[0];
                currentSpeedVector[1] -= tempSpeedVector[1];
                speedVectors.add(new int[]{currentSpeedVector[0], currentSpeedVector[1]});
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
                speedVectors.add(new int[]{currentSpeedVector[0], currentSpeedVector[1]});
                i--;
            }
        }
    }

    private PositionWithParent[][] mainMapCopy(PlayerState state) {
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
        return findPath;
    }

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

    private void straightLineSpeedupValuesCalculate() {
        ArrayList<ArrayList<Integer>> tmpSwitchPlace = new ArrayList<>();
        tmpSwitchPlace.add(new ArrayList<>() {
        });
        tmpSwitchPlace.add(new ArrayList<>() {{
            add(0);
        }});
        tmpSwitchPlace.add(new ArrayList<>() {{
            add(0);
            add(0);
        }});
        tmpSwitchPlace.add(new ArrayList<>() {{
            add(1);
            add(2);
            add(1);
        }});


        for (int i = 5; i < 136; i++) {


            ArrayList<Integer> tempList = new ArrayList<>();

            int actualNum = -1;
            int numCounter = 0;
            boolean nemTalat = true;
            ArrayList<Integer> lastList = tmpSwitchPlace.get(tmpSwitchPlace.size() - 1);
            int max = 0;

            for (int num : lastList) if (num > max) max = num;


            for (int j = lastList.size() - 1; j >= 0; j--) {
                if (numCounter == 2 && actualNum != max) {
                    for (int k = 0; k < j + 1; k++) {
                        tempList.add(lastList.get(k));
                    }
                    tempList.add(actualNum + 1);
                    for (int k = j + 2; k < lastList.size(); k++) {
                        tempList.add(lastList.get(k));
                    }
                    nemTalat = false;


                } else if (numCounter == 3 && actualNum == max) {
                    for (int k = 0; k < j + 2; k++) {
                        tempList.add(lastList.get(k));
                    }
                    tempList.add(actualNum + 1);
                    for (int k = j + 3; k < lastList.size(); k++) {
                        tempList.add(lastList.get(k));
                    }
                    nemTalat = false;


                }

                if (lastList.get(j) == actualNum) {
                    numCounter++;
                } else {
                    actualNum = lastList.get(j);
                    numCounter = 1;
                }

            }
            if (nemTalat) {
                tempList.addAll(lastList);
                tempList.add(1);
            }

            tmpSwitchPlace.add(tempList);
        }


        for (ArrayList<Integer> integers : tmpSwitchPlace) {
            ArrayList<Integer> differencesList = new ArrayList<>();
            for (int j = 0; j < integers.size() - 1; j++) {
                int diff = integers.get(j + 1) - integers.get(j);
                differencesList.add(diff);
            }
            switchPlace.add(differencesList);
        }
    }


    private int revesreDirectionNum(int num) {
        Direction tmpDir = intToDirection(num);
        Direction forditottIranyDir = new Direction(tmpDir.i * -1, tmpDir.j * -1);
        return directionToInt(forditottIranyDir);
    }

    private Direction intToDirection(int num) {
        return RaceTrackGame.DIRECTIONS[num];
    }

    private int directionToInt(Direction dir) {
        for (int i = 0; i < RaceTrackGame.DIRECTIONS.length; i++) {
            Direction aktVizsgaltDirection = RaceTrackGame.DIRECTIONS[i];
            if (aktVizsgaltDirection.i == dir.i && aktVizsgaltDirection.j == dir.j) {
                return i;
            }
        }
        return -1;
    }

    private PositionWithParent[][] findPathBetweenTwoPoint(PositionWithParent[][] findPath, Cell from, Cell to) {

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


        for (PositionWithParent[] positionWithParents : baseMap) {
            for (PositionWithParent positionWithParent : positionWithParents) {
                if (positionWithParent.value > 1) {
                    positionWithParent.value = 1;
                }
            }
        }

        boolean javitoUt = false;
        int[] tmpCell = new int[]{-1, -1};
        for (int i = 0; i < baseMap.length; i++) {
            for (int j = 0; j < baseMap[i].length; j++) {
                if (baseMap[i][j].value == 1 && javitoUt && (baseMap[i][j + 1].value != 1 || baseMap[i][j - 1].value != 1)) {
                    for (int k = tmpCell[1]; k < j; k++) {
                        baseMap[i][k].value = 1;
                        if (baseMap[i - 1][k].value == 1) {
                            baseMap[i - 1][k].value = 0;
                        }
                    }
                    javitoUt = false;
                    tmpCell[0] = -1;
                    tmpCell[1] = -1;

                }
                if (baseMap[i][j].value == 1 && !javitoUt) {
                    javitoUt = true;
                    tmpCell[0] = i;
                    tmpCell[1] = j;
                } else if (baseMap[i][j].value == -1 || baseMap[i][j].value == 1) {
                    javitoUt = false;
                    tmpCell[0] = -1;
                    tmpCell[1] = -1;
                }
            }
        }

        javitoUt = false;
        tmpCell = new int[]{-1, -1};
        for (int j = 0; j < baseMap[0].length; j++) {
            for (int i = 0; i < baseMap.length; i++) {
                if (baseMap[i][j].value == 1 && javitoUt && (baseMap[i + 1][j].value != 1 || baseMap[i - 1][j].value != 1)) {
                    for (int k = tmpCell[0]; k < i; k++) {
                        baseMap[k][j].value = 1;
                        if (baseMap[k][j - 1].value == 1) baseMap[k][j - 1].value = 0;
                    }
                    javitoUt = false;
                    tmpCell[0] = -1;
                    tmpCell[1] = -1;

                }
                if (baseMap[i][j].value == 1 && !javitoUt) {
                    javitoUt = true;
                    tmpCell[0] = i;
                    tmpCell[1] = j;
                } else if (baseMap[i][j].value == -1 || baseMap[i][j].value == 1) {
                    javitoUt = false;
                    tmpCell[0] = -1;
                    tmpCell[1] = -1;
                }
            }
        }

        return baseMap;

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

    //Debug
    private void printPathMap(PositionWithParent[][] findPath) {
        for (PositionWithParent[] positionWithParents : findPath) {
            for (int j = 0; j < findPath[0].length; j++) {
                switch (positionWithParents[j].value) {
                    case 0 -> System.out.print(" ");
                    case -1 -> System.out.print("X");
                    case -2 -> System.out.print("F");
                    case -3 -> System.out.print("C");
                    case 1 -> System.out.print("1");
                    default -> System.out.print(positionWithParents[j].value % 10 + "");
                }
            }
            System.out.println();
        }

    }

    private void printPathMapWOWalls(PositionWithParent[][] findPath) {
        for (PositionWithParent[] positionWithParents : findPath) {
            for (int j = 0; j < findPath[0].length; j++) {
                switch (positionWithParents[j].value) {
                    case 0, -1 -> System.out.print(" ");
                    case -2 -> System.out.print("F");
                    case -3 -> System.out.print("C");
                    case 1 -> System.out.print("P");
                    default -> System.out.print(positionWithParents[j].value % 10 + "");
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
}


//java -jar game_engine.jar 0 game.racetrack.RaceTrackGame 11 27 5 0.1 10 1234567890 1000 SamplePlayer

//java -jar game_engine.jar 100 game.racetrack.RaceTrackGame 11 27 5 0.1 10 $((Get-Random -Minimum 0 -Maximum 999999999)) 1000 SamplePlayer