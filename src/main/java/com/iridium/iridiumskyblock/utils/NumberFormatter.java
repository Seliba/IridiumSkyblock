package com.iridium.iridiumskyblock.utils;

import com.iridium.iridiumskyblock.IridiumSkyblock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

public class NumberFormatter {
    private static final String FORMAT = "%." + IridiumSkyblock.getInstance().getConfiguration().numberAbbreviationDecimalPlaces + "f";
    private static final long ONE_THOUSAND_LONG = 1000;
    private static final long ONE_MILLION_LONG = 1000000;
    private static final long ONE_BILLION_LONG = 1000000000;

    private static final BigDecimal ONE_THOUSAND = new BigDecimal(1000);
    private static final BigDecimal ONE_MILLION = new BigDecimal(1000000);
    private static final BigDecimal ONE_BILLION = new BigDecimal(1000000000);

    public static String format(double number) {
        if (!IridiumSkyblock.getInstance().getConfiguration().displayNumberAbbreviations) {
            return NumberFormat.getInstance().format(number);
        }
        return IridiumSkyblock.getInstance().getConfiguration().prettierAbbreviations ? formatPrettyNumber(new BigDecimal(number)) : formatNumber(number);
    }

    private static String formatNumber(double number) {
        StringBuilder output = new StringBuilder();

        if (number < 0) {
            output.append("ERROR");
        } else if (number < ONE_THOUSAND_LONG) {
            output.append(String.format(FORMAT, number));
        } else if (number < ONE_MILLION_LONG) {
            output.append(String.format(FORMAT, number / ONE_THOUSAND_LONG)).append(IridiumSkyblock.getInstance().getConfiguration().thousandAbbreviation);
        } else if (number < ONE_BILLION_LONG) {
            output.append(String.format(FORMAT, number / ONE_MILLION_LONG)).append(IridiumSkyblock.getInstance().getConfiguration().millionAbbreviation);
        } else {
            output.append(String.format(FORMAT, number / ONE_BILLION_LONG)).append(IridiumSkyblock.getInstance().getConfiguration().billionAbbreviation);
        }

        return output.toString();
    }

    private static String formatPrettyNumber(BigDecimal bigDecimal) {
        bigDecimal = bigDecimal.setScale(IridiumSkyblock.getInstance().getConfiguration().numberAbbreviationDecimalPlaces, RoundingMode.HALF_DOWN);
        StringBuilder outputStringBuilder = new StringBuilder();

        if (bigDecimal.compareTo(BigDecimal.ZERO) < 0) {
            outputStringBuilder
                    .append("-")
                    .append(formatPrettyNumber(bigDecimal.negate()));
        } else if (bigDecimal.compareTo(ONE_THOUSAND) < 0) {
            outputStringBuilder
                    .append(bigDecimal.stripTrailingZeros().toPlainString());
        } else if (bigDecimal.compareTo(ONE_MILLION) < 0) {
            outputStringBuilder
                    .append(bigDecimal.divide(ONE_THOUSAND, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString())
                    .append(IridiumSkyblock.getInstance().getConfiguration().thousandAbbreviation);
        } else if (bigDecimal.compareTo(ONE_BILLION) < 0) {
            outputStringBuilder
                    .append(bigDecimal.divide(ONE_MILLION, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString())
                    .append(IridiumSkyblock.getInstance().getConfiguration().millionAbbreviation);
        } else {
            outputStringBuilder
                    .append(bigDecimal.divide(ONE_BILLION, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString())
                    .append(IridiumSkyblock.getInstance().getConfiguration().billionAbbreviation);
        }

        return outputStringBuilder.toString();
    }
}
