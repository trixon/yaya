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
import java.util.Collections;
import java.util.TreeSet;
import se.trixon.yaya.rules.GameCell;

/**
 *
 * @author Patrik Karlström
 */
public class FormulaParser {

    private final ArrayList<Integer> mArgList = new ArrayList<>();
    private ArrayList<Integer> mDiceList;
    private GameCell mGameCell;

    public FormulaParser() {
    }

    public int parseFormula(ArrayList<Integer> values, GameCell gameCell) {
        mDiceList = values;
        mGameCell = gameCell;

        var formulaString = mGameCell.getFormula();
        var parseString = formulaString.split(" ");
        var command = parseString[0];
        mArgList.clear();

        for (int i = 1; i < parseString.length; i++) {
            mArgList.add(Integer.valueOf(parseString[i]));
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

    private int getDuplicates(int numOfDuplicates, int face) {
        int freq = Collections.frequency(mDiceList, face);
        int result = 0;
        if (freq >= numOfDuplicates) {
            result = numOfDuplicates * face;
        }

        return result;
    }

    private int getDuplicates(int numOfDuplicates) {
        int result = 0;
        int cnt;

        for (int i = 6; i > 0; i--) {
            cnt = Collections.frequency(mDiceList, i);
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

    private int getHouse(int majorPart, int minorPart) {
        int result = 0;

        var majorList = new ArrayList<Integer>();
        var minorList = new ArrayList<Integer>();

        for (int i = 6; i > 0; i--) {
            int sum = getSumOf(i);
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
            result = majorPart * Collections.max(majorList) + minorPart * Collections.max(minorList);
        }

        return result;
    }

    private int getPair(int numOfPairs) {
        int result = 0;
        int pairCounter = 0;
        int startFace = 6;

        for (int i = 0; i < numOfPairs; i++) {
            for (int j = startFace; j > 0; j--) {
                int pairSum = getDuplicates(2, j);
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

    private int getStraight(int sizeOfStraight) {
        int result = 0;
        var sortedSet = new TreeSet<Integer>();

        for (var integer : mDiceList) {
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

    private int getSum() {
        int result = 0;
        for (int face : mDiceList) {
            result += face;
        }
        return result;
    }

    private int getSumOf(int face) {
        return face * Collections.frequency(mDiceList, face);
    }

    private int processFormula(Formula formula) {
        int result = -1;

        switch (formula) {
            case DUPLICATES ->
                result = getDuplicates(mArgList.get(0));

            case HOUSE ->
                result = getHouse(mArgList.get(0), mArgList.get(1));

            case PAIR ->
                result = getPair(mArgList.get(0));

            case STRAIGHT ->
                result = getStraight(mArgList.get(0));

            case SUM -> {
                if (mArgList.isEmpty()) {
                    result = getSum();
                } else {
                    result = getSumOf(mArgList.get(0));
                }
            }
        }

        return result;
    }

    public enum Formula {

        DUPLICATES,
        HOUSE,
        PAIR,
        STRAIGHT,
        SUM,
    }
}
