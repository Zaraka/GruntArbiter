package org.shadowrun.models;

import javafx.beans.property.IntegerProperty;

public class Host {
    private IntegerProperty ratting;

    private IntegerProperty attack;

    private IntegerProperty sleeze;

    private IntegerProperty firewall;

    private IntegerProperty dataProcessing;

    private IntegerProperty overwatchScore;

    public int getRatting() {
        return ratting.get();
    }

    public void setRatting(int ratting) {
        this.ratting.setValue(ratting);
    }

    public int getAttack() {
        return attack.get();
    }

    public void setAttack(int attack) {
        this.attack.setValue(attack);
    }

    public int getSleeze() {
        return sleeze.get();
    }

    public void setSleeze(int sleeze) {
        this.sleeze.setValue(sleeze);
    }

    public int getFirewall() {
        return firewall.get();
    }

    public void setFirewall(int firewall) {
        this.firewall.setValue(firewall);
    }

    public int getDataProcessing() {
        return dataProcessing.get();
    }

    public void setDataProcessing(int dataProcessing) {
        this.dataProcessing.setValue(dataProcessing);
    }

    public int getOverwatchScore() {
        return overwatchScore.get();
    }

    public void setOverwatchScore(int overwatchScore) {
        this.overwatchScore.setValue(overwatchScore);
    }

    public IntegerProperty rattingProperty() {
        return ratting;
    }

    public IntegerProperty attackProperty() {
        return attack;
    }

    public IntegerProperty sleezeProperty() {
        return sleeze;
    }

    public IntegerProperty firewallProperty() {
        return firewall;
    }

    public IntegerProperty dataProcessingProperty() {
        return dataProcessing;
    }

    public IntegerProperty overwatchScoreProperty() {
        return overwatchScore;
    }
}
