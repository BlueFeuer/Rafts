package com.bluefire.rafts.data;

import com.bluefire.rafts.entity.EntityPlatform;
import com.bluefire.rafts.entity.EntityPlatformClient;
import com.bluefire.rafts.entity.EntityRaft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaftTemplate {

    public static Map<Integer, RaftTemplate> raftTemplates = new HashMap<>();
    public static Map<Integer, Layout> layouts = new HashMap<>();

    static
    {
        addLayout(0, 0, new float[]{
                -127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,0f,0f,0f,0f,0f,0f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127f,-127});
        // steerAccel - 1 divided by ticks to full turning rate, speed - desired top speed multiplied by (1-drag)
        addRaftTemplate(0, 0, 100f, 1f/100f, 0.9f, 0.11f*(1f-0.98f), 0.98f);
        // mast
        addInteractable(0, 0f, 0f, 0f, 0.5f, 5.5f, 2, Types.BARRIER, 1);
        // steering
        addInteractable(0, -0.3f, 0.5f, -0.6f, 0.75f, 1.5f, 1, Types.STEERING, -30); // right, 30 = 34 ticks to 1000
        addInteractable(0, 0.3f, 0.5f, -0.6f, 0.75f, 1.5f, 1, Types.STEERING, 30); // left, 30 = 34 ticks to 1000
        // sails
        addInteractable(0, 0.0f, 3.0f, -0.4f, 0.75f, 1.0f, 1, Types.SAIL, -25); // up, 25 = 40 ticks to 1000
        addInteractable(0, 0.0f, 2.0f, -0.4f, 0.75f, 1.0f, 1, Types.SAIL, 50); // down, 50 = 20 ticks to 1000
    }


    public static void addRaftTemplate(int id, int layoutID, float hpIn, float steerAccIn, float steerMultIn, float speedIn, float dragIn) {
        raftTemplates.put(id, new RaftTemplate(layoutID, hpIn, steerAccIn, steerMultIn, speedIn, dragIn));
    }

    public static void addLayout(int layoutID, int sizeIn, float[] layoutIn) {
        layouts.put(layoutID, new Layout(layoutIn, sizeIn));
    }

    public static void addInteractable(int id, float x, float y, float z, float w, float h, int collidesIn, int typeIn, int modifierIn) {
        if (raftTemplates.containsKey(id)) {
            raftTemplates.get(id).addInteractable(new Interactable(x, y, z, w, h, collidesIn, typeIn, modifierIn));
        } else {
            // nothing
        }
    }




    public List<Interactable> interactables = new ArrayList<>();
    public int layout = 0;
    public float hp = 100f;
    public float steerAcc = 1f/100f;
    public float steerMult = 0.9f; // 0.9 = 20 seconds per full revolution
    public float speed = 0.1f;
    public float drag = 0.96f;

    public RaftTemplate()
    {
    }

    public RaftTemplate(int layoutIn, float hpIn, float steerAccIn, float steerMultIn, float speedIn, float dragIn)
    {
        this();
        this.layout = layoutIn;
        this.hp = hpIn;
        this.steerAcc = steerAccIn;
        this.steerMult = steerMultIn;
        this.speed = speedIn;
        this.drag = dragIn;
    }

    public void addInteractable(Interactable interactable) {
        this.interactables.add(interactable);
    }

    public float getMaxHP() {
        return this.hp;
    }

    public int getLayout() {
        return this.layout;
    }

    public List<Interactable> getInteractables() {
        return this.interactables;
    }

}
