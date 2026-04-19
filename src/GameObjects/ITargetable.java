package GameObjects;

import Faction.Faction;

import java.awt.geom.Rectangle2D;

public interface ITargetable {
    float getX();
    float getY();
    Rectangle2D.Float getHitbox();
    boolean isDestroyed();
    Faction getOwnerFaction();
    void removeHealth(int damage);
}
