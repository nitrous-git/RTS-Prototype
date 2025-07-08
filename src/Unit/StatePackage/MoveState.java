package Unit.StatePackage;

import Unit.WorkerUnit;
import Util.Camera;

public class MoveState implements IUnitState {
    private float x;
    private float y;
    private final Camera camera;

    public MoveState(float x, float y, Camera cam) {
        this.x = x;
        this.y = y;
        this.camera = cam;
    }

    @Override
    public void onEnter(WorkerUnit unit) {
        unit.moveTo(x, y, camera);
    }

    @Override
    public void update(WorkerUnit unit) {
        unit.updateMoveToLocation();
    }

    @Override
    public void onExit(WorkerUnit unit) { }
}
