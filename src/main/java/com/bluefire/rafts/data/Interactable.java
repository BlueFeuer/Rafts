package com.bluefire.rafts.data;

public class Interactable {

    public float offsetX = 0f;
    public float offsetY = 0f;
    public float offsetZ = 0f;
    public float width = 0f;
    public float height = 0f;
    /**
     * Valid values:
     * 0 = No collision
     * 1 = Player collision
     * 2 = Full collision (use sparingly)
     */
    public int collides = 1;
    /**
     * Valid types:
     * 0 = Steering Right / Starboard
     * 1 = Steering Left / Port
     * 2 = Sail Up
     * 3 = Sail Down
     * 4 = Sail Right / Starboard
     * 5 = Sail Left / Port
     * 6 = Barrier
     * 7 = Storage
     * 8 = Cannon
     */
    public int type = 0;
    public int modifier = 0;


    public Interactable(float x, float y, float z, float w, float h, int collidesIn, int typeIn, int modifierIn)
    {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        this.width = w;
        this.height = h;
        this.collides = collidesIn;
        this.type = typeIn;
        this.modifier = modifierIn;
    }


}
