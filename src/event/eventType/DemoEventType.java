package event.eventType;

import bitzero.server.core.IBZEventType;

/**
 * Created by hieupt on 11/8/18.
 */
public enum DemoEventType implements IBZEventType {
    CHANGE_NAME,
    LOGIN_SUCCESS,
    CHANGE_POSITION,
    //
    UPDATE_RESOURCE;
    //
    private DemoEventType() {
    }
}
