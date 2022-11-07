/*
 * Copyright 2022 Patrik Karlström <patrik@trixon.se>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.trixon.yaya.scorecard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.TreeSet;
import se.trixon.yaya.rules.GameCell;

/**
 *
 * @author Patrik Karlström
 */
public class FormulaParser {

    private final ArrayList<String> mArgList = new ArrayList<>();
    private ArrayList<Integer> mDiceValues;
    private GameCell mGameCell;

    public FormulaParser() {
    }

    public int parseFormula(ArrayList<Integer> diceValues, GameCell gameCell) {
        mDiceValues = diceValues;
        mGameCell = gameCell;

        var formulaString = mGameCell.getFormula();
        var parseString = formulaString.split(" ");
        var command = parseString[0];
        mArgList.clear();

        for (int i = 1; i < parseString.length; i++) {
            mArgList.add(parseString[i]);
        }

        int result = -1;
        try {
            var formula = Formula.valueOf(command.toUpperCase());
            result = processFormula(formula);
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown command: " + command);
        }

        return result;
    }

    private int calcCustomCrag(int score) {
        if (calcPair(1) > 0 && calcSumTotal(mDiceValues) == 13) {
            return score;
        } else {
            return 0;
        }
    }

    private int calcDuplicates(int numOfDuplicates, int face) {
        int freq = Collections.frequency(mDiceValues, face);
        int result = 0;
        if (freq >= numOfDuplicates) {
            result = numOfDuplicates * face;
        }

        return result;
    }

    private int calcEquals() {
        var diceValues = new ArrayList<>(mDiceValues);
        Collections.sort(diceValues);
        var values = new ArrayList<>(Arrays.stream(mArgList.get(0).split(",")).map(Integer::parseInt).sorted().toList());

        for (int i = 0; i < values.size(); i++) {
            if (!Objects.equals(values.get(i), diceValues.get(i))) {
                return 0;
            }
        }

        return Integer.parseInt(mArgList.get(1));
    }

    private int calcHouse(int majorPart, int minorPart) {
        int result = 0;

        var majorList = new ArrayList<Integer>();
        var minorList = new ArrayList<Integer>();

        for (int i = 6; i > 0; i--) {
            int sum = calcSumOf(i);
            int freq = sum / i;

            if (freq >= majorPart) {
                if (!majorList.isEmpty()) {
                    if (i > Collections.max(majorList)) {
                        majorList.add(i);
                    }
                } else {
                    majorList.add(i);
                }
            }

            if ((freq >= minorPart) && (majorList.indexOf(i) == -1)) {
                minorList.add(i);
            }
        }

        if (!majorList.isEmpty() && !minorList.isEmpty()) {
            if (mGameCell.getLim() == mGameCell.getMax()) {
                result = mGameCell.getMax();
            } else {
                result = majorPart * Collections.max(majorList) + minorPart * Collections.max(minorList);
            }
        }

        return result;
    }

    private int calcPair(int numOfPairs) {
        int result = 0;
        int pairCounter = 0;
        int startFace = 6;

        for (int i = 0; i < numOfPairs; i++) {
            for (int j = startFace; j > 0; j--) {
                int pairSum = calcDuplicates(2, j);
                if (pairSum > 0) {
                    result += pairSum;
                    pairCounter++;
                    startFace = j - 1;
                    break;
                }
            }
        }

        if (pairCounter < numOfPairs) {
            result = 0;
        }

        return result;
    }

    private int calcSequence(int sizeOfSequence) {
        var diceValues = new ArrayList<>(new TreeSet<>(mDiceValues));
        var setOfLists = new TreeSet<ArrayList<Integer>>((o1, o2) -> {
            return o2.size() - o1.size();
        });

        var list = new ArrayList<Integer>();

        for (int i = 0; i < diceValues.size() - 1; i++) {
            if (diceValues.get(i) == diceValues.get(i + 1) - 1) {
                list.add(diceValues.get(i));
            } else {
                if (!list.isEmpty()) {
                    setOfLists.add(list);
                    list = new ArrayList<>();
                }
            }
        }

        if (!list.isEmpty()) {
            int lastDiceValue = diceValues.get(diceValues.size() - 1);
            int lastListValue = list.get(list.size() - 1);
            if (lastDiceValue == lastListValue + 1) {
                list.add(lastDiceValue);
            }
            setOfLists.add(list);
        }

        int score = 0;

        if (!setOfLists.isEmpty()) {
            var largestSequence = setOfLists.first();
            if (largestSequence.size() >= sizeOfSequence) {
                if (mArgList.size() > 3) {
                    score = Integer.parseInt(mArgList.get(3));
                } else {
                    score = calcSumTotal(largestSequence);
                }
            }
        }

        return score;
    }

    private int calcSum(int arg0, int arg1) {
        int sum = calcSumTotal(mDiceValues);
        int size = mArgList.size();

        if (size == 0) {
            return sum;
        } else if (size == 1) {
            return calcSumOf(arg0);
        } else if (size == 2 && arg0 == sum) {
            return arg1;
        }

        return 0;
    }

    private int calcSumN(int numOfDuplicates) {
        int freq = 0;
        for (int i = 1; i < 7; i++) {
            freq = Math.max(freq, Collections.frequency(mDiceValues, i));
        }

        if (freq >= numOfDuplicates) {
            return calcSumTotal(mDiceValues);
        } else {
            return 0;
        }
    }

    private int calcSumOf(int face) {
        return face * Collections.frequency(mDiceValues, face);
    }

    private int calcSumTotal(ArrayList<Integer> values) {
        return values.stream().mapToInt(Integer::intValue).sum();
    }

    private int getDuplicates(int numOfDuplicates) {
        int result = 0;
        int cnt;

        for (int i = 6; i > 0; i--) {
            cnt = Collections.frequency(mDiceValues, i);
            if (cnt >= numOfDuplicates) {
                result = numOfDuplicates * i;
                if (mGameCell.getMax() == mGameCell.getLim()) {
                    result = mGameCell.getMax();
                }
                break;
            }
        }

        return result;
    }

    private int getStraight(int sizeOfStraight) {
        int result = 0;
        var sortedSet = new TreeSet<Integer>();

        for (var integer : mDiceValues) {
            sortedSet.add(integer);
        }

        if (sortedSet.size() >= sizeOfStraight) {
            int setSum = 0;

            for (var integer : sortedSet) {
                setSum += integer;
            }

            if (sortedSet.size() > sizeOfStraight) {
                if (setSum >= mGameCell.getLim()) {
                    result = mGameCell.getMax();
                }
            } else {
                if (setSum == mGameCell.getLim()) {
                    result = mGameCell.getMax();
                }
            }
        }

        return result;
    }

    private int processFormula(Formula formula) {
        int result = -1;
        int arg0 = -1;
        int arg1 = -1;

        if (!mArgList.isEmpty()) {
            try {
                arg0 = Integer.parseInt(mArgList.get(0));
            } catch (IllegalArgumentException e) {
                //nvm it's ok
            }
            if (mArgList.size() > 1) {
                arg1 = Integer.parseInt(mArgList.get(1));
            }
        }

        switch (formula) {
            case CUSTOM_CRAG ->
                result = calcCustomCrag(arg0);

            case DUPLICATES ->
                result = getDuplicates(arg0);

            case EQUALS ->
                result = calcEquals();

            case HOUSE ->
                result = calcHouse(arg0, arg1);

            case PAIR ->
                result = calcPair(arg0);

            case SEQUENCE ->
                result = calcSequence(arg0);

            case STRAIGHT ->
                result = getStraight(arg0);

            case SUM ->
                result = calcSum(arg0, arg1);

            case SUM_N ->
                result = calcSumN(arg0);
        }

        return result;
    }

    public enum Formula {
        CUSTOM_CRAG,
        DUPLICATES,
        EQUALS,
        HOUSE,
        PAIR,
        SEQUENCE,
        STRAIGHT,
        SUM,
        SUM_N,
    }
}
