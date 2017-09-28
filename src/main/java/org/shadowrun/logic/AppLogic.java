package org.shadowrun.logic;

import com.google.gson.Gson;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
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
import java.util.HashMap;

public class AppLogic {

    private static final Logger LOG = LoggerFactory.getLogger(AppLogic.class);

    private static final Gson gson = FxGson.createWithExtras();

    private ObjectProperty<Campaign> activeCampaign;

    private ObjectProperty<File> campaignFile;

    private BooleanProperty showRealWorld;

    private BooleanProperty showMatrix;

    private BooleanProperty showAstralPlane;

    private AppConfig config;

    private InvalidationListener invalidationListener;

    private BooleanProperty changesSaved;

    private ObservableMap<String, PlayerCharacter> playersMap;


    public AppLogic() {
        activeCampaign = new SimpleObjectProperty<>(null);
        campaignFile = new SimpleObjectProperty<>(null);
        showRealWorld = new SimpleBooleanProperty(true);
        showAstralPlane = new SimpleBooleanProperty(true);
        showMatrix = new SimpleBooleanProperty(true);
        changesSaved = new SimpleBooleanProperty(true);
        config = new AppConfig();
        invalidationListener = null;
        playersMap = FXCollections.observableHashMap();

        activeCampaign.addListener((observable, oldValue, newValue) -> {
            if(oldValue != null && invalidationListener != null) {
                oldValue.removeListener(invalidationListener);
            }

            if(newValue != null && invalidationListener != null) {
                newValue.addListener(invalidationListener);
            }
        });

        addCampignListener(observable -> {
            changesSaved.setValue(false);
        });
    }

    public void newCampaign(String name) {
        campaignFile.setValue(null);
        activeCampaign.setValue(new Campaign(name, config.getVersion()));
        changesSaved.setValue(true);
    }

    public void newCharacter(String name) {
        PlayerCharacter playerCharacter = new PlayerCharacter(name, 10, 10, 0);
        activeCampaign.get().getPlayers().add(playerCharacter);
        playersMap.put(playerCharacter.getUuid(), playerCharacter);
    }

    public void loadCampaign(File file, Campaign campaign) {
        campaignFile.set(file);
        activeCampaign.setValue(campaign);
        getConfig().insertOrRefreshRecentCampaign(getCampaignFile().toPath());
        playersMap.clear();
        campaign.getPlayers().forEach(playerCharacter -> {
            playersMap.put(playerCharacter.getUuid(), playerCharacter);
        });
        changesSaved.setValue(true);
    }

    public void openCampaign(File file) throws IOException, IncompatibleVersionsException {
        Campaign campaign = gson.fromJson(Files.newBufferedReader(file.toPath()), Campaign.class);

        if(!campaign.getVersion().isCompatible(config.getVersion())) {
            throw new IncompatibleVersionsException(campaign);
        }
        loadCampaign(file, campaign);
        changesSaved.setValue(true);
    }

    public void saveCampaign() throws IOException {
        Files.write(getCampaignFile().toPath(), gson.toJson(getActiveCampaign()).getBytes());

        getConfig().insertOrRefreshRecentCampaign(getCampaignFile().toPath());

        changesSaved.setValue(true);
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

    public boolean isChangesSaved() {
        return changesSaved.get();
    }

    public BooleanProperty changesSavedProperty() {
        return changesSaved;
    }

    public AppConfig getConfig() {
        return config;
    }

    public String getAppTitle() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Grunt Arbiter");
        if(activeCampaign.isNotNull().get()) {
            stringBuilder.append(" - ");
            stringBuilder.append(activeCampaign.get().getName());
            if(campaignFileProperty().isNotNull().get()) {
                stringBuilder.append(" [");
                stringBuilder.append(campaignFileProperty().get().toPath().toString());
                if(!changesSaved.get()) {
                    stringBuilder.append("*");
                }
                stringBuilder.append("]");
            }
        }
        return stringBuilder.toString();
    }

    public void addCampignListener(InvalidationListener listener) {
        invalidationListener = listener;
    }

    public PlayerCharacter getPlayer(String UUID) {
        return playersMap.get(UUID);
    }
}
