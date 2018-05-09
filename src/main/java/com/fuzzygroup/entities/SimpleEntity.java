package com.fuzzygroup.entities;

import com.fuzzygroup.view.Shape;
import com.fuzzygroup.view.*;
import com.fuzzygroup.view.enums.ColorEnum;
import com.ngeneration.graphic.engine.ThreeVector;

public class SimpleEntity extends VisualRepresentationEntity {



    public SimpleEntity() {
        super(new ThreeVector(0, 0, 0), new ThreeVector(0, 0, 0), new ThreeVector(0, 0, 0),
                new ThreeVector(3, 3, 3),
                Shape.TRIANGLE, ColorEnum.BLUE, true);
    }

//    public SimpleEntity(String displayQualifier) {
//        super(new ThreeVector(0, 0, 0), new ThreeVector(0, 0, 0), new ThreeVector(0, 0, 0),
//                new ThreeVector(3, 3, 3),
//                new HashMap<>(), new HashMap<>(), true);
//        this.displayQualifier = displayQualifier;
//    }

    public SimpleEntity(ThreeVector position, ThreeVector speed, ThreeVector acceleration, ThreeVector size,
                        Shape shapes, ColorEnum colors, boolean visible) {
        super(position, speed, acceleration, size, shapes, colors, visible);
    }

//    @Override
//    protected Display getBoundDisplay() {
//        try {
//            if (displayQualifier != null && !"".equals(displayQualifier)) {
//                return Context.SINGLETON.getByName(displayQualifier);
//            } else {
//                return Context.SINGLETON.getByName(DISPLAY_QUALIFIER_NAME);
//            }
//        } catch (DisplayException e) {
//            e.printStackTrace();
//        }
//        // todo ugly
//        return null;
//    }

    @Override
    public void recomputeState() {
        setSpeed(getSpeed().plus(getAcceleration().multiple(getDisplay().getDeltaTime())));
//        speed = speed.plus(acceleration.multiple(display.getDeltaTime()));
        setPosition(getPosition().plus(getSpeed().multiple(getDisplay().getDeltaTime())));
//        position = position.plus(speed.multiple(display.getDeltaTime()));
    }

}
