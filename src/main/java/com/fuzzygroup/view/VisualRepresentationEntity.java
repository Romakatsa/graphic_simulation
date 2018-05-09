package com.fuzzygroup.view;

import com.fuzzygroup.exceptions.DisplayException;
import com.fuzzygroup.view.enums.ColorEnum;
import com.ngeneration.graphic.engine.ThreeVector;

public abstract class VisualRepresentationEntity extends VisualRepresentationState {

    private   final StateHistory history;
    protected transient String displayQualifier;
    private transient Display display;
    private transient boolean enableHistoryLogging = true;
    private transient String qualifier;
    private transient long mask;

    public VisualRepresentationEntity(ThreeVector position, ThreeVector speed, ThreeVector acceleration, ThreeVector size,
                                      Shape shapes, ColorEnum colors, boolean visible) {
        this(position, speed, acceleration, size, shapes, colors, 0, 0, visible, new StateHistory());
    }

    public VisualRepresentationEntity(ThreeVector position, ThreeVector speed, ThreeVector acceleration, ThreeVector size,
                                      Shape shapes, ColorEnum colors, double rotation, double opacity,
                                      boolean visible, StateHistory history) {
        super(position, speed, acceleration, size, shapes, colors, rotation, opacity, visible);
        this.history = history;
//        this.display = getBoundDisplay();
        if (display != null) {
//            this.enableHistoryLogging = display.isHistoryEnable();
//            registration(); // todo?
        }
    }

    public void addAction(Runnable r, int type, int... triggerKey) {
//        display.addAction(new KeyboardEvent(r, type, triggerKey));
    }

    public void bindToDisplay(String displayQualifier) {
        try {
            display = Context.SINGLETON.getByName(displayQualifier);
//            display.register(this);
            saveStateToHistory();
//            if (display != null) {
//                display.register(this);
//            } else {
//                throw new DisplayException("No display defined");
//            }
        } catch (DisplayException e) {
            e.printStackTrace();
        }
    }

//    public void stopVisualise() {
//        display.remove(this);
//    }

    public void calculateNextInstant() {
        //todo
        recomputeState();
        saveStateToHistory();
    }

    public abstract void recomputeState();

    private void saveStateToHistory() {
        if (!enableHistoryLogging) {
            history.clearAndSave(Context.uptimeMillis(), cloneState());
        } else {
            history.addStateAt(Context.uptimeMillis(), cloneState());
        }
    }

    public void moveTo(double x, double y, double z) {
        // todo is it ok?
        position = new ThreeVector(x, y, z);
    }

    public void shiftOn(double deltaX, double deltaY, double deltaZ) {
        // todo is it ok?
        moveTo(position.getX() + deltaX, position.getY() + deltaY, position.getZ() + deltaZ);
    }

    public VisualRepresentationState getFloorStateAt(int timestamp) {
        return history.getFloorStateAt(timestamp);
    }

    public VisualRepresentationState getLastState() {
        return history.getLastState();
    }

    public Shape getCompositeShape() {
        return shapes;
    }


    //todo make display private?
    public Display getDisplay() {
        return display;
    }

    public boolean isEnableHistoryLogging() {
        return enableHistoryLogging;
    }

    public void enableHistoryLogging() {
        this.enableHistoryLogging = true;
    }

    public void disableHistoryLogging() {
        this.enableHistoryLogging = false;
    }

    public StateHistory getHistory() {
        return history;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public void addToMask(long plusValue) {
        this.mask = this.mask | plusValue;
    }
    public void removeFromMask(long plusValue) {
        this.mask = this.mask & ~plusValue;
    }

    public boolean matchMask(long mask) {
        return (this.mask & mask) != 0;
    }

    public static class DefaultMasks {
        public static final int     OBSERVABLE =    0b00000001;
        public static final int     ACCOUNTABLE =   0b00000010;
        public static final int     SELECTED =      0b00000011;
    }

}
