package model;

/**
 * Implementation of IAttackResult interface.
 * Students will complete this implementation for Checkpoint 2.
 */
public class AttackResult implements IAttackResult {
    private final String attackerName;
    private final String defenderName;
    private final int damage;
    private final double effectiveness;
    private final int defenderRemainingHp;
    private final boolean isCritical;

    public AttackResult(String attackerName, String defenderName, int damage,
                       double effectiveness, int defenderRemainingHp, boolean isCritical) {
        this.attackerName = attackerName;
        this.defenderName = defenderName;
        this.damage = damage;
        this.effectiveness = effectiveness;
        this.defenderRemainingHp = defenderRemainingHp;
        this.isCritical = isCritical;
    }

    @Override
    public String getAttackerName() {
        return this.attackerName;
    }

    @Override
    public String getDefenderName() {
       return this.defenderName;
    }

    @Override
    public int getDamage() {
        return this.damage;
    }

    @Override
    public double getEffectiveness() {
        return this.effectiveness;
    }

    @Override
    public int getDefenderRemainingHp() {
        return this.defenderRemainingHp;
    }

    @Override
    public boolean isCritical() {
        return this.isCritical;
    }

    @Override
    public String getEffectivenessMessage() {
        if (this.effectiveness == 0.0) {
            return "It has no effect.";
        } else if (this.effectiveness < 1.0) {
            return "It's not very effective...";
        } else if (this.effectiveness > 1.0) {
            return "It's super effective!";
        } else {
            return "";

        }

    }

    @Override public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Attacker: " + this.attackerName + "\n");
        sb.append("Damage: " + this.damage + "\n");
        sb.append("Effectiveness: " + this.effectiveness + "\n");

        if( isCritical){
            sb.append("Critical hit!");
        }

        String effectivenessMessage = this.getEffectivenessMessage();
        if(!effectivenessMessage.isEmpty()){
            sb.append("\n" + effectivenessMessage);
        }

        sb.append("Defender: " + this.defenderName + "\n");
        sb.append("Defender HP: " + this.defenderRemainingHp + " remaining\n");

        return sb.toString();

    }
}