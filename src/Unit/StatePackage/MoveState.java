package Unit.StatePackage;

import Building.AbstractBuilding;
import Unit.AbstractUnit;
import Unit.WorkerUnit;
import Util.Camera;
/*
 * Generalize MoveState over any AbstractUnit
 * */
public class MoveState<U extends AbstractUnit> implements IUnitState<U> {
    private float x;
    private float y;
    private final Camera camera;

    public MoveState(float x, float y, Camera cam) {
        this.x = x;
        this.y = y;
        this.camera = cam;
    }

    @Override
    public void onEnter(U unit) {
        unit.moveTo(x, y, camera);
    }

    @Override
    public void update(U unit) {
        unit.updateMoveToLocation();
    }

    @Override
    public void onExit(U unit) { }
}
