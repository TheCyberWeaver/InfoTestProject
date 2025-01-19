package io.github.infotest.item;

import io.github.infotest.character.Actor;
import io.github.infotest.util.MyAssetManager;

public class Weapon extends Item {
    private int damage;
    private float range;
    //private String material;
    //private float weight;
    private int durability;

    public Weapon(String id,MyAssetManager assetManager, int damage, float range) {
        super(id,"weapon", "just a weapon", assetManager.getLoadingScreenTexture());
        this.damage = damage;
        this.range = range;
        //this.weight = weight;
        //this.material = material;
        this.durability = durability;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int pDamage) {
        this.damage = pDamage;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float pRange) {
        this.range = pRange;
    }

//public String getMaterial(){
    //  return material;
//}
//public void setMaterial(String pMaterial){
//this.material = pMaterial;
//}


    public void attack(Actor target) {
        if (durability > 0) {
            if (target != null && target.getHealthPoints() > 0) {
                target.takeDamage(damage);
                durability--;
                //System.out.println("Attacked target for " + damage + " damage. Remaining HP: " + target.getHealthPoints());
                System.out.println("Remaining durability: " + durability);
            } else {
                System.out.println("Target is invalid or already dead.");
            }
        } else {
            System.out.println("Out of durability!");
        }
    }

    public void showStats() {
        System.out.println("Weapon Stats:");
        System.out.println("Damage: " + damage);
        System.out.println("Range: " + range + " meters");
        //System.out.println("Weight: " + weight + " kg");
        // System.out.println("Material: " + material);
    }
}




