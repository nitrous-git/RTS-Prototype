package EnemyAI;

public abstract class AIState {

    public float timestamp;
    protected String name;
    protected AIManager ai;

    public AIState(AIManager ai, String name) {
        this.ai = ai;
        this.name = name;
    }

    public abstract void onEnter();
    public abstract void update();
    public abstract void onExit();
    public abstract boolean isComplete();
}