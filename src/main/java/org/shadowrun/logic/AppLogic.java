package org.shadowrun.logic;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.shadowrun.models.Campaign;
import org.shadowrun.models.PlayerCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class AppLogic {

    private static final Logger LOG = LoggerFactory.getLogger(AppLogic.class);

    private ObjectProperty<Campaign> activeCampaign;

    private ObjectProperty<File> campaignFile;


    public AppLogic() {
        activeCampaign = new SimpleObjectProperty<>(null);
        campaignFile = new SimpleObjectProperty<>(null);
    }

    public void newCampaign(String name) {
        activeCampaign.setValue(new Campaign(name));
    }

    public void newCharacter(String name) {
        activeCampaign.get().getPlayers().add(new PlayerCharacter(name));
    }

    public void openCampaign(File file) {
        campaignFile.set(file);

    }

    public void saveCampaign() {

    }

    public void saveAsCampaign(File file) {
        campaignFile.set(file);
        saveCampaign();
    }

    public void closeCampaign() {
        activeCampaign.set(null);
        campaignFile.set(null);
    }

    public BooleanBinding hasCampaign() {
        return activeCampaign.isNull();
    }

    public BooleanBinding hasFile() {
        return campaignFile.isNull();
    }

    public ObjectProperty<Campaign> activeCampaignProperty() {
        return activeCampaign;
    }

    public Campaign getActiveCampaign() {
        return activeCampaign.get();
    }

    public File getCampaignFile() {
        return campaignFile.get();
    }

    public ObjectProperty<File> campaignFileProperty() {
        return campaignFile;
    }
}
