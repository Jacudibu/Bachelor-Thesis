package com.jacudibu.Utility;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Stefan Wolf (Jacudibu) on 08.07.2017.
 */
public class QRGenerator {
    private static final int size = 64;
    private static final int borderSize = 2;
    private static final int cellSize = (size - borderSize * 2) / 4;
    private static final Color markedColor = Color.BLACK;
    private static final Color freeColor = Color.WHITE;

    public static boolean isValidCode(String code) {
        if (code.isEmpty() || code.length() > 4) {
            return false;
        }

        int value = Integer.parseInt(code, 16);
        if (value < 0 || value > 65535) {
            return false;
        }
        return true;
    }

    public static Texture generate(String code) {
        if (isValidCode(code)) {
            return new Texture(generatePixmap(code));
        }
        else {
            return null;
        }
    }

    private static Pixmap generatePixmap(String code) {
        Pixmap map = new Pixmap(size, size, Pixmap.Format.RGBA8888 );

        for (int row = 0; row < 4; row++) {
            drawRow(map, code.charAt(row), row);
        }

        return map;
    }

    private static void drawRow(Pixmap map, char c, int row) {
        int value = Integer.parseInt(c + "", 16);
        for (int cell = 0; cell < 4; cell++) {
            drawCell(map, value, row, cell);
        }
    }

    private static void drawCell(Pixmap map, int value, int row, int cell) {
        setMapColor(map, value, cell);
        map.fillRectangle(borderSize + row * cellSize, borderSize + (3 - cell) * cellSize, cellSize, cellSize);
    }

    private static void setMapColor(Pixmap map, int value, int cell) {
        map.setColor(getCellColor(value, cell));
    }

    private static Color getCellColor(int value, int cell) {
        if (cell == 0) {
            // _000
            if (value > 7) {
                return markedColor;
            }
            else {
                return freeColor;
            }
        }
        else if (cell == 1) {
            // 0_00
            switch (value) {
                case 4:
                case 5:
                case 6:
                case 7:
                case 12:
                case 13:
                case 14:
                case 15:
                    return markedColor;
                default:
                    return freeColor;
            }
        }
        else if (cell == 2) {
            // 00_0
            switch (value) {
                case 2:
                case 3:
                case 6:
                case 7:
                case 10:
                case 11:
                case 14:
                case 15:
                    return markedColor;
                default:
                    return freeColor;
            }
        }
        else if (cell == 3) {
            // 000_
            if (value % 2 == 1) {
                return markedColor;
            }
            else return freeColor;
        }
        else {
            return null;
        }
    }
}
