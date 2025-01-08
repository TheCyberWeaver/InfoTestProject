package io.github.infotest.item;

import io.github.infotest.util.MyAssetManager;

public class Weapon extends Item {
    private int damage;
    private float range;
    private String material;
    private float weight;

public Weapon(MyAssetManager assetManager, int damage, float range, String material) {
        super("weapon", "just a weapon", assetManager.getLoadingScreenTexture());
    this.damage = damage;
    this.range = range;
    this.weight = weight;
    this.material = material;
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
public void setRange(float pRange){
    this.range = pRange;
}

public String getMaterial(){
    return material;
}
public void setMaterial(String pMaterial){
this.material = pMaterial;
}

public void showStats() {
        System.out.println("Weapon Stats:");
        System.out.println("Damage: " + damage);
        System.out.println("Range: " + range + " meters");
        System.out.println("Weight: " + weight + " kg");
        System.out.println("Material: " + material);
}

 public void render(){

    }
}
