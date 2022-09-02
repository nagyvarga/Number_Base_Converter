package converter;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class Main {
    static final int DECIMAL = 10;

    public static void main(String[] args) {
        System.out.print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ");
        String line = readLine();
        while (!line.equals(Commands.exit.commandName)) {
            List<Integer> bases = processBasesLine(line);
            if (bases != null) {
                int sourceBase = bases.get(0);
                int targetBase = bases.get(1);
                convertNumbers(sourceBase, targetBase);
            }
            System.out.print("\nEnter two numbers in format: {source base} {target base} (To quit type /exit) ");
            line = readLine();
        }
    }

    private static void convertNumbers(int sourceBase, int targetBase) {
        System.out.printf("Enter number in base %d to convert to base %d (To go back type /back) ", sourceBase, targetBase);
        String line = readLine();
        String result;
        String resultFraction;
        while (!line.equals(Commands.back.commandName)) {

            if (verifyNumber(sourceBase, line)) {
                String[] seperatedLine = line.split("\\.");
                String integerPart = seperatedLine[0];
                String fractionalPart = null;
                if (seperatedLine.length > 1) {
                    fractionalPart = seperatedLine[1];
                }

                if (sourceBase == DECIMAL) {
                    result = decimalToOtherSystem(integerPart, targetBase);
                    if (fractionalPart != null) {
                        resultFraction = decimalToOtherSystemFraction(fractionalPart, targetBase) + "0".repeat(5);
                        result = result + resultFraction.substring(0,6);
                    }
                } else {
                    String decNum = otherSystemToDecimal(integerPart, sourceBase);
                    result = decimalToOtherSystem(decNum, targetBase);
                    String decNumFraction;
                    if (fractionalPart != null) {
                        decNumFraction = otherSystemToDecimalFraction(fractionalPart, sourceBase);
                        resultFraction = decimalToOtherSystemFraction(decNumFraction.substring(2), targetBase);
                        result = result + resultFraction.substring(0,6);
                    }
                }
                System.out.printf("Conversion result: %s\n\n", result);
            }

            System.out.printf("Enter number in base %d to convert to base %d (To go back type /back) ", sourceBase, targetBase);
            line = readLine();
        }
    }

    private static boolean verifyNumber(int sourceBase, String line) {
        for (char c : line.toCharArray()) {
            if ((convertSymbolToNum(c) < 0 || convertSymbolToNum(c) > sourceBase - 1) && c != '.') {
                return false;
            }
        }
        return true;
    }

    private static List<Integer> processBasesLine(String line) {
        List<Integer> bases = new ArrayList<>();
        String[] lineParts = line.split(" ");
        if (lineParts.length == 2) {
            try {
                bases.add(Integer.parseInt(lineParts[0]));
                bases.add(Integer.parseInt(lineParts[1]));
                if (bases.get(0) >= 2 && bases.get(0) <= 36 && bases.get(1) >= 2 && bases.get(1) <= 36) {
                    return bases;
                }
            } catch (Exception e) {
                // System.out.print("Error");
                return null;
            }
        }
        return null;
    }

    private static String decimalToOtherSystem(String num, int targetBase) {
        StringBuilder result = new StringBuilder();
        BigInteger target = BigInteger.valueOf(targetBase);
        BigInteger[] divAndRem;
        BigInteger number = new BigInteger(num);
        do {
            divAndRem = number.divideAndRemainder(target);
            result.append(convertNumToSymbol(divAndRem[1]));
            number = divAndRem[0];
        } while (!number.equals(BigInteger.ZERO));
        return result.reverse().toString();
    }

    private static String decimalToOtherSystemFraction(String fraction, int targetBase) {
        BigDecimal fractionNum = new BigDecimal("0." + fraction);
        BigDecimal targetMultiplier = new BigDecimal(targetBase);
        StringBuilder result = new StringBuilder(".");
        BigDecimal remainder = fractionNum;
        fractionNum = remainder.multiply(targetMultiplier);
        int count = 0;
        while (fractionNum.compareTo(BigDecimal.ZERO) != 0 && count < 15) {
            count++;
            remainder = fractionNum.remainder(BigDecimal.ONE);
            result.append(convertNumToSymbol(fractionNum.subtract(remainder).toBigInteger()));
            fractionNum = remainder.multiply(targetMultiplier);
        }
        return result + "0".repeat(5);
    }

    private static String otherSystemToDecimal(String num, int sourceBase) {
        BigInteger result = BigInteger.ZERO;
        BigInteger multiplier = BigInteger.ONE;
        for (int i = num.length() - 1; i >= 0; i--) {
            result = result.add(BigInteger.valueOf(convertSymbolToNum(num.charAt(i))).multiply(multiplier));
            multiplier = multiplier.multiply(BigInteger.valueOf(sourceBase));
        }
        return result.toString();
    }

    private static String otherSystemToDecimalFraction(String num, int sourceBase) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal multiplier = BigDecimal.ONE.divide(BigDecimal.valueOf(sourceBase),30, RoundingMode.DOWN);
        for (char c : num.toCharArray()) {
            if (convertSymbolToNum(c) != 0) {
                result = result.add(BigDecimal.valueOf(convertSymbolToNum(c)).multiply(multiplier));
            }
            multiplier = multiplier.divide(BigDecimal.valueOf(sourceBase),30, RoundingMode.DOWN);
        }
        return result + "0".repeat(7);
    }

    private static char convertNumToSymbol(BigInteger num) {
        if (num.compareTo(BigInteger.valueOf(DECIMAL)) < 0) {
            return (char) ('0' + num.intValue());
        } else {
            return (char) ('a' + num.intValue() - 10);
        }
    }

    private static int convertSymbolToNum(char symbol) {
        if (symbol >= '0' && symbol <= '9') {
            return symbol - '0';
        } else {
            return symbol + 10 - 'a';
        }
    }

    private static String readLine() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
