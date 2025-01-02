// Player.js

/**
 * Player class represents a game character with various attributes and actions
 */
class Player {
    constructor(id, name) {
        /**
         * The socket ID associated with this player
         */
        this.id = id;

        /**
         * The player's name
         */
        this.name = name;

        /**
         * The player's current position
         */
        this.position = { x: 0, y: 0 };

        /**
         * The items that the player is currently carrying
         */
        this.items = [];

        /**
         * The player's hit points
         */
        this.hp = 100;


        /**
         * The player's classtype
         */
        this.classtype = "";

        this.level=0;

        this.mana=0;

        this.rotation = { x: 0, y: 0 };
    }

    /**
     * Set a new position for the player
     * @param {number} x - The X coordinate
     * @param {number} y - The Y coordinate
     */
    setPosition(x, y) {
        this.position.x = x;
        this.position.y = y;
    }
    setRotation(x,y){
        this.rotation.x = x;
        this.rotation.y = y;
    }
    setMana(m){
        this.mana=m;
    }
    setLevel(l){
        this.level=l;
    }
    /**
     * Player picks up an item and adds it to the inventory
     * @param {string} item - The item to pick up
     */
    pickItem(item) {
        this.items.push(item);
    }

    /**
     * Player drops an item from the inventory
     * @param {string} item - The item to drop
     */
    dropItem(item) {
        const index = this.items.indexOf(item);
        if (index !== -1) {
            this.items.splice(index, 1);
        }
    }

    /**
     * Player performs a normal attack
     * @param {Player} target - The target player
     */
    normalAttack(target,damage) {
        // Example logic for normal attack
        target.hp -= damage;
    }

    /**
     * Player performs a profession-specific skill
     * @param {Player} target - The target player
     */
    professionAttack(target, damage) {
        target.hp -= damage;
    }
}

module.exports = Player;

