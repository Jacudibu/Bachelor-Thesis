package com.jacudibu.UI;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Created by Stefan Wolf (Jacudibu) on 14.05.2017.
 * A simple Input filter to allow characters relevant for floating point number input.
 */
public class FloatFilter implements TextField.TextFieldFilter {
    private char[] accepted = new char [] {'0', '1', '2', '3' , '4', '5', '6', '7', '8', '9', '.', ',', '-'};

    @Override
    public boolean acceptChar(TextField textField, char c) {
        for(int i = 0; i < accepted.length; i++) {
            if (c == accepted[i]) {
                return true;
            }
        }

        return false;
    }
}
