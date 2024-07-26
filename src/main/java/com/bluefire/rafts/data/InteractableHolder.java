package com.bluefire.rafts.data;

import com.bluefire.rafts.entity.EntityInteractable;

public class InteractableHolder {

    public Interactable interactable;
    public EntityInteractable entity;

    public InteractableHolder(Interactable interactableIn) {
        this.interactable = interactableIn;
        this.entity = null;
    }

}
