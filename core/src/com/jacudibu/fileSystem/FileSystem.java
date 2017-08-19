package com.jacudibu.fileSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Created by Stefan Wolf (Jacudibu) on 19.08.2017.
 */
public class FileSystem {
    protected static FileHandle getFileHandle(String path, PathType pathType) {
        switch (pathType) {
            case INTERNAL:
                return Gdx.files.internal(path);
            case EXTERNAL:
                return Gdx.files.external(path);
            case ABSOLUTE:
                return Gdx.files.absolute(path);
            case CLASSPATH:
                return Gdx.files.classpath(path);
            default:
                return null;
        }
    }

}
