/* 
 * Copyright 2018 Patrik Karlström.
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
package org.nbgames.yaya.scorecard;

import java.util.Collections;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import org.nbgames.yaya.gamedef.GameRow;

/**
 *
 * @author Patrik Karlström
 */
public class FormulaParser {

    private static final LinkedList<Integer> sArgList = new LinkedList<>();
    private static LinkedList<Integer> sDiceList;
    private static GameRow sGameRow;

    public static int parseFormula(String formulaString, LinkedList<Integer> values, GameRow gameRow) {
        sDiceList = values;
        sGameRow = gameRow;
        int result = -1;
        String[] parseString = formulaString.split(" ");
        String command = parseString[0];
        sArgList.clear();

        for (int i = 1; i < parseString.length; i++) {
            sArgList.add(Integer.valueOf(parseString[i]));
        }

        try {
            Formula formula = Formula.valueOf(command.toUpperCase());
            result = processFormula(formula);
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown command: " + command);
        }

        return result;
    }

    private static int getDuplicates(int numOfDuplicates, int face) {
        int freq = Collections.frequency(sDiceList, face);
        int result = 0;
        if (freq >= numOfDuplicates) {
            result = numOfDuplicates * face;
        }

        return result;
    }

    private static int getDuplicates(int numOfDuplicates) {
        int result = 0;
        int cnt;

        for (int i = 6; i > 0; i--) {
            cnt = Collections.frequency(sDiceList, i);
            if (cnt >= numOfDuplicates) {
                result = numOfDuplicates * i;
                if (sGameRow.getMax() == sGameRow.getLim()) {
                    result = sGameRow.getMax();
                }
                break;
            }
        }

        return result;
    }

    private static int getHouse(int majorPart, int minorPart) {
        int result = 0;

        LinkedList<Integer> majorList = new LinkedList<>();
        LinkedList<Integer> minorList = new LinkedList<>();

        for (int i = 6; i > 0; i--) {
            int sum = getSumOf(i);
            int freq = sum / i;

            if (freq >= majorPart) {
                if (majorList.size() > 0) {
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

        if (majorList.size() > 0 && minorList.size() > 0) {
            result = majorPart * Collections.max(majorList) + minorPart * Collections.max(minorList);
        }

        return result;
    }

    private static int getPair(int numOfPairs) {
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

    private static int getStraight(int sizeOfStraight) {
        int result = 0;
        SortedSet<Integer> sortedSet = new TreeSet<>();

        for (Integer integer : sDiceList) {
            sortedSet.add(integer);
        }

        if (sortedSet.size() >= sizeOfStraight) {
            int setSum = 0;

            for (Integer integer : sortedSet) {
                setSum += integer;
            }

            if (sortedSet.size() > sizeOfStraight) {
                if (setSum >= sGameRow.getLim()) {
                    result = sGameRow.getMax();
                }
            } else {
                if (setSum == sGameRow.getLim()) {
                    result = sGameRow.getMax();
                }
            }
        }

        return result;
    }

    private static int getSum() {
        int result = 0;
        for (int face : sDiceList) {
            result += face;
        }
        return result;
    }

    private static int getSumOf(int face) {
        return face * Collections.frequency(sDiceList, face);
    }

    private static int processFormula(Formula formula) {
        int result = -1;

        switch (formula) {
            case DUPLICATES:
                result = getDuplicates(sArgList.get(0));
                break;

            case HOUSE:
                result = getHouse(sArgList.get(0), sArgList.get(1));
                break;

            case PAIR:
                result = getPair(sArgList.get(0));
                break;

            case STRAIGHT:
                result = getStraight(sArgList.get(0));
                break;

            case SUM:
                if (sArgList.size() == 0) {
                    result = getSum();
                } else {
                    result = getSumOf(sArgList.get(0));
                }
                break;
        }

        return result;
    }

    private FormulaParser() {
    }

    public enum Formula {

        DUPLICATES, HOUSE,
        PAIR,
        STRAIGHT, SUM,
    }
}
