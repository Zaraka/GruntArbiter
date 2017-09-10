package org.shadowrun.logic;

import com.google.gson.Gson;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.hildan.fxgson.FxGson;
import org.shadowrun.common.exceptions.IncompatibleVersionsException;
import org.shadowrun.models.AppConfig;
import org.shadowrun.models.Campaign;
import org.shadowrun.models.PlayerCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AppLogic {

    private static final Logger LOG = LoggerFactory.getLogger(AppLogic.class);

    private static final Gson gson = FxGson.createWithExtras();

    private ObjectProperty<Campaign> activeCampaign;

    private ObjectProperty<File> campaignFile;

    private BooleanProperty showRealWorld;

    private BooleanProperty showMatrix;

    private BooleanProperty showAstralPlane;

    private AppConfig config;


    public AppLogic() {
        activeCampaign = new SimpleObjectProperty<>(null);
        campaignFile = new SimpleObjectProperty<>(null);
        showRealWorld = new SimpleBooleanProperty(true);
        showAstralPlane = new SimpleBooleanProperty(true);
        showMatrix = new SimpleBooleanProperty(true);
        config = new AppConfig();
    }

    public void newCampaign(String name) {
        campaignFile.setValue(null);
        activeCampaign.setValue(new Campaign(name, config.getVersion()));
    }

    public void newCharacter(String name) {
        activeCampaign.get().getPlayers().add(new PlayerCharacter(name, 10, 10, 0));
    }

    public void loadCampaign(File file, Campaign campaign) {
        campaignFile.set(file);
        activeCampaign.setValue(campaign);
        getConfig().insertOrRefreshRecentCampaign(getCampaignFile().toPath());
    }

    public void openCampaign(File file) throws IOException, IncompatibleVersionsException {
        Campaign campaign = gson.fromJson(Files.newBufferedReader(file.toPath()), Campaign.class);

        if(!campaign.getVersion().isCompatible(config.getVersion())) {
            throw new IncompatibleVersionsException(campaign);
        }
        loadCampaign(file, campaign);
    }

    public void saveCampaign() throws IOException {
        Files.write(getCampaignFile().toPath(), gson.toJson(getActiveCampaign()).getBytes());

        getConfig().insertOrRefreshRecentCampaign(getCampaignFile().toPath());
    }

    public void saveAsCampaign(File file) throws IOException {
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

    public boolean isShowRealWorld() {
        return showRealWorld.get();
    }

    public BooleanProperty showRealWorldProperty() {
        return showRealWorld;
    }

    public boolean isShowMatrix() {
        return showMatrix.get();
    }

    public BooleanProperty showMatrixProperty() {
        return showMatrix;
    }

    public boolean isShowAstralPlane() {
        return showAstralPlane.get();
    }

    public BooleanProperty showAstralPlaneProperty() {
        return showAstralPlane;
    }

    public AppConfig getConfig() {
        return config;
    }
}
