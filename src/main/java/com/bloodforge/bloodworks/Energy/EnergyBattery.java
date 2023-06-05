package com.bloodforge.bloodworks.Energy;

import net.minecraftforge.energy.EnergyStorage;

public class EnergyBattery extends EnergyStorage {

    public EnergyBattery(int maxTransfer, int capacity) {
        super(capacity, maxTransfer);
        this.maxReceive = 0;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void generatePower(int energy) {
        this.energy += energy;
        if(this.energy > capacity)
            this.energy = capacity;
    }

    public void consumePower(int energy) {
        this.energy -= energy;
        if(this.energy < 0) {
            this.energy = 0;
        }
    }

    public boolean isFullEnergy() {
        return getEnergyStored() >= getMaxEnergyStored();
    }

    public int getCapacity() {
        return getMaxEnergyStored();
    }
    public int getStored() {
        return getEnergyStored();
    }
}