package com.jacudibu.ubiWrap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import ubitrack.*;

/**
 * Created by Stefan on 15.07.2017.
 */
public class UbiManager {
    public static final boolean enableLogging = false;
    public static final String ubitrackPath = "C:\\Ubitrack\\bin\\ubitrack";
    public static final String dfgPath = "C:\\Ubitrack\\wolfBA_DFG.dfg";

    private static SimpleFacade facade;
    private static Array<PoseReceiver> receivers = new Array<PoseReceiver>();

    public static void init() {
        if (enableLogging) {
            ubitrack.initLogging();
        }

        facade = new SimpleFacade(ubitrackPath);

        if (!hasError()) {
            loadDataflow(dfgPath);
        }
    }

    public static void update() throws Exception {
        if (hasError()) {
            throw new Exception("Ubitrack has errors!");
        }

        for (int i = 0; i < receivers.size; i++) {
            receivers.get(i).update();
        }
    }

    private static void loadDataflow(String path) {
        facade.loadDataflow(path);
        facade.startDataflow();

        // TODO: parse dfg properly
        receivePose("272pose", "5cc5");
    }

    private static void receivePose(String id) {
        receivePose(id, "ffff");
    }

    private static void receivePose(String id, String qr) {
        PoseReceiver receiver = new PoseReceiver(qr);
        facade.setPoseCallback(id, receiver);

        receivers.add(receiver);
    }

    private static boolean hasError() {
        if (facade.getLastError() != null) {
            Gdx.app.log("Ubitrack",facade.getLastError());
            return true;
        }

        return false;
    }

}
