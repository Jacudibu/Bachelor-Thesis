package com.jacudibu.UI;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Created by Stefan Wolf (Jacudibu) on 08.07.2017.
 */
public class HexFilter implements TextField.TextFieldFilter {
    private char[] accepted = new char [] {'0', '1', '2', '3' , '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

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
