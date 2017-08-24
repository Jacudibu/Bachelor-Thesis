package com.jacudibu.ubiWrap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.jacudibu.Core;
import ubitrack.*;

/**
 * Created by Stefan on 15.07.2017.
 */
public class UbiManager {
    private static boolean isInit;
    public static final boolean enableLogging = false;
    public static final String ubitrackPath = "C:\\Ubitrack\\bin\\ubitrack";
    public static final String dfgPath = "C:\\Ubitrack\\wolfBA_DFG.dfg";

    private static SimpleFacade facade;
    private static Array<PoseReceiver> receivers = new Array<PoseReceiver>();

    public static void init() {
        if (isInit) {
            Gdx.app.log("Ubitrack", "init called twice!");
            return;
        }

        System.loadLibrary("ubitrack_java");
        isInit = true;
        Core.usingUbitrack = true;

        if (enableLogging) {
            ubitrack.initLogging();
        }

        facade = new SimpleFacade(ubitrackPath);

        if (!hasError()) {
            loadDataflow(dfgPath);
        }
    }

    public static void update() throws Exception {
        if (!isInit) {
            return;
        }

        if (hasError()) {
            throw new Exception("Ubitrack has errors!");
        }

        for (int i = 0; i < receivers.size; i++) {
            if (receivers.get(i).requestUpdate) {
                receivers.get(i).update();
            }
        }
    }

    public static void loadDataflow(String path) {
        if (!isInit) {
            Gdx.app.log("Ubitrack", "load Dataflow called without being initialized!");
            return;
        }

        facade.loadDataflow(path);
        facade.startDataflow();
    }

    private static void receivePose(String id) {
        receivePose(id, "ffff");
    }

    protected static void receivePose(String id, String qr) {
        if (!isInit) {
            Gdx.app.log("Ubitrack", "receivePose called without being initialized!");
            return;
        }

        PoseReceiver receiver = new PoseReceiver(id, qr);
        facade.setPoseCallback(id, receiver);

        receivers.add(receiver);
    }

    private static boolean hasError() {
        if (!isInit) {
            return false;
        }

        if (facade.getLastError() != null) {
            Gdx.app.log("Ubitrack",facade.getLastError());
            return true;
        }

        return false;
    }

}
