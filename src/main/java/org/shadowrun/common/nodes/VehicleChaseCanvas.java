package org.shadowrun.common.nodes;

import com.sun.javafx.geom.Vec2d;
import com.sun.javafx.geom.Vec3d;
import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.shadowrun.common.constants.TerrainType;
import org.shadowrun.common.constants.VehicleChaseRole;
import org.shadowrun.models.Battle;
import org.shadowrun.models.RectangleF;
import org.shadowrun.models.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class VehicleChaseCanvas extends Canvas {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleChaseCanvas.class);

    private static final int TILE_SIZE = 128;
    private static final int LANE_HEIGHT = 100;
    private static final int SPACE_SIZE = 15;
    private static final int VEHICLE_BLOCK_SIZE = 230;

    private static final double GROUND_SPEED = 0.0000001;
    private static final double SKY_LAND = 0.00000001;
    private static final double SKY_CLOUDS = 0.00000005;

    private ObjectProperty<Vehicle> selectedVehicle;

    private Image sandTexture;
    private Image waterTexture;
    private Image skyCloud;
    private Image selectBox;
    private Image unkownVehicle;

    private GraphicsContext context;

    private Battle battle;

    private Vehicle hoveredVehicle;

    private Map<Vehicle, RectangleF> vehicleBoxes;
    private Map<Vehicle, Double> amplitude;
    private Map<Vehicle, Double> speed;
    private Map<Vehicle, Double> phase;
    private Map<Vehicle, Image> vehicleImages;
    private Map<TerrainType, Double> textureSpeeds;

    private List<Vec2d> particlesPosition;

    private Image background;
    private double lastBackgroundPosition = 0;

    private double lastWidth;
    private double lastHeight;

    private AnimationTimer redrawTimer;

    private long frames = 0;

    private ThreadLocalRandom threadLocalRandom;

    public VehicleChaseCanvas() {
        context = getGraphicsContext2D();
        this.battle = null;
        background = null;
        lastWidth = 0.0;
        lastHeight = 0.0;

        selectedVehicle = new SimpleObjectProperty<>(null);

        ClassLoader classLoader = getClass().getClassLoader();
        sandTexture = new Image(classLoader.getResource("textures/sand.jpg").toExternalForm());
        waterTexture = new Image(classLoader.getResource("textures/water.jpg").toExternalForm());
        selectBox = new Image(classLoader.getResource("objects/selection.png").toExternalForm());
        skyCloud = new Image(classLoader.getResource("textures/cloud.png").toExternalForm());
        unkownVehicle = new Image(classLoader.getResource("objects/car.png").toExternalForm());
        vehicleBoxes = new HashMap<>();
        amplitude = new HashMap<>();
        speed = new HashMap<>();
        phase = new HashMap<>();
        vehicleImages = new HashMap<>();
        textureSpeeds = new HashMap<>();
        particlesPosition = new ArrayList<>();
        threadLocalRandom = ThreadLocalRandom.current();

        textureSpeeds.put(TerrainType.GROUND, GROUND_SPEED);
        textureSpeeds.put(TerrainType.SKY, SKY_LAND);
        textureSpeeds.put(TerrainType.WATER, GROUND_SPEED);

        redrawTimer = new AnimationTimer() {

            private long lastUpdate = System.nanoTime();

            @Override
            public void handle(long now) {
                long dt = now - lastUpdate;
                if (dt >= 33_000_000) {
                    redraw(dt);
                    increaseFrames();
                    lastUpdate = now;
                }
            }

        };

        addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            Optional<Vehicle> hovered = posToVehicle(event.getX(), event.getY());
            hoveredVehicle = hovered.orElse(null);
        });

        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Optional<Vehicle> clicked = posToVehicle(event.getX(), event.getY());
            selectedVehicle.setValue(clicked.orElse(null));
        });
    }

    public Vehicle getSelectedVehicle() {
        return selectedVehicle.get();
    }

    public ObjectProperty<Vehicle> selectedVehicleProperty() {
        return selectedVehicle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
        battle.vehicleChaseProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null)
                redrawTimer.start();
            else
                redrawTimer.stop();
        });

        battle.getVehicles().addListener((ListChangeListener<? super Vehicle>) c -> updateVehicles());
        updateVehicles();
    }


    private void init() {
        lastWidth = getWidth();
        lastHeight = getHeight();

        Image texture = null;
        int scaledTile = TILE_SIZE;

        switch (battle.getVehicleChase().getTerrainType()) {
            case GROUND:
                texture = sandTexture;
                break;
            case WATER:
                texture = waterTexture;
                break;
            case SKY:
                texture = sandTexture;
                scaledTile /= 2;
                particlesPosition.add(new Vec2d(
                        getWidth() + threadLocalRandom.nextDouble(50, 400),
                        threadLocalRandom.nextDouble(-100, getHeight() / 2)));
                particlesPosition.add(new Vec2d(
                        getWidth() + threadLocalRandom.nextDouble(150, 600),
                        threadLocalRandom.nextDouble(-100, getHeight() / 2)));
                particlesPosition.add(new Vec2d(
                        getWidth() + threadLocalRandom.nextDouble(500, 800),
                        threadLocalRandom.nextDouble(-100, getHeight() / 2)));
                particlesPosition.add(new Vec2d(
                        getWidth() + threadLocalRandom.nextDouble(700, 1000),
                        threadLocalRandom.nextDouble(-100, getHeight() / 2)));
                particlesPosition.add(new Vec2d(
                        getWidth() + threadLocalRandom.nextDouble(900, 1200),
                        threadLocalRandom.nextDouble(-100, getHeight() / 2)));
                particlesPosition.add(new Vec2d(
                        getWidth() + threadLocalRandom.nextDouble(200, 1500),
                        threadLocalRandom.nextDouble(-100, getHeight() / 2)));
                break;
        }


        for(int x = 0; x < getWidth(); x += scaledTile) {
            for(int y = 0; y < getHeight(); y += scaledTile) {
                context.drawImage(texture, x, y, scaledTile, scaledTile);
            }
        }

        background = snapshot(new SnapshotParameters(), null);
    }

    private void increaseFrames() {
        frames++;
    }

    /**
     * @param deltaTime - in nanoseconds
     */
    private void redraw(long deltaTime) {
        if(lastWidth != getWidth() || lastHeight != getHeight())
            init();

        context.setFill(Color.BLACK);
        context.fillRect(0,0, getWidth(), getHeight());

        context.drawImage(background, lastBackgroundPosition, 0);
        context.drawImage(background,  lastBackgroundPosition + getWidth(), 0);
        lastBackgroundPosition -= deltaTime * textureSpeeds.get(battle.getVehicleChase().getTerrainType());
        if(lastBackgroundPosition <= -getWidth())
            lastBackgroundPosition = 0;
        //lastBackgroundPosition++;

        if(battle.getVehicleChase().getTerrainType() == TerrainType.SKY) {
            for(Vec2d positions : particlesPosition) {
                context.drawImage(skyCloud, positions.x, positions.y);
                positions.x -= deltaTime * SKY_CLOUDS;
                if(positions.x <= -skyCloud.getWidth())
                    positions.x = getWidth();
            }
        }

        int lane = 0;
        for(Vehicle vehicle : battle.getVehicles()) {
            Double vehicleAmplitude = amplitude.get(vehicle);
            Double vehicleSpeed = speed.get(vehicle);
            Double vehiclePhase = phase.get(vehicle);


            Image vehicleImage = vehicleImages.get(vehicle);
            ObservableMap<String, Integer> positions = battle.getVehicleChase().getPositions();
            double x = SPACE_SIZE + (VEHICLE_BLOCK_SIZE * positions.get(vehicle.getUuid()));
            double y = (lane * LANE_HEIGHT) + (LANE_HEIGHT / 2) - (vehicleImage.getHeight() / 2) +
                    (vehicleAmplitude * Math.sin((frames+vehiclePhase) / vehicleSpeed));
            context.drawImage(vehicleImage, x, y);
            lane++;

            RectangleF rect = vehicleBoxes.get(vehicle);
            rect.x = x;
            rect.y = y;

            //draw Condition monitor
            context.setStroke(Color.RED);
            context.setLineWidth(2);
            context.strokeLine(x + 35, y + LANE_HEIGHT, x + 35 + 100, y + LANE_HEIGHT);

            context.setStroke(Color.GREEN);
            double hp = (vehicle.getConditionMonitor().getCurrent() * 100) / vehicle.getConditionMonitor().getMax();
            if(hp > 0) {
                context.strokeLine(x + 35,
                        y + LANE_HEIGHT,
                        x + 35 + hp,
                        y + LANE_HEIGHT);
            }

            VehicleChaseRole role = battle.getVehicleChase().getChaseRoles().get(vehicle.getUuid());
            if(role == VehicleChaseRole.PURSUER)
                context.setStroke(Color.RED);
            else
                context.setStroke(Color.GREEN);
            context.setLineWidth(1);
            context.setFont(Font.font(11));
            context.strokeText(role.toString(),
                    x + 35,
                    y + LANE_HEIGHT - 4);


            if(hoveredVehicle == vehicle || selectedVehicle.getValue() == vehicle) {
                context.drawImage(selectBox, x, y);
            }

        }
    }

    private void updateVehicles() {
        for(Vehicle vehicle : battle.getVehicles()) {
            Double vehicleAmplitude = amplitude.get(vehicle);
            if(vehicleAmplitude == null) {
                vehicleAmplitude = ThreadLocalRandom.current().nextDouble(1, 4) * 4;
                amplitude.put(vehicle, vehicleAmplitude);
            }


            Double vehicleSpeed = speed.get(vehicle);
            if(vehicleSpeed == null) {
                vehicleSpeed = ThreadLocalRandom.current().nextDouble(2, 4) * 3;
                speed.put(vehicle, vehicleSpeed);
            }

            Double vehiclePhase = phase.get(vehicle);
            if(vehiclePhase == null) {
                vehiclePhase = ThreadLocalRandom.current().nextDouble(0, 500);
                phase.put(vehicle, vehiclePhase);
            }

            if(!vehicleBoxes.containsKey(vehicle)) {
                vehicleBoxes.put(vehicle, new RectangleF(0, 0, VEHICLE_BLOCK_SIZE, LANE_HEIGHT));
            }


            if(vehicleImages.get(vehicle) == null) {
                if(getClass().getClassLoader().getResource(vehicle.getImage()) == null)
                    vehicleImages.put(vehicle, unkownVehicle);
                else
                    vehicleImages.put(vehicle, new Image(getClass().getClassLoader()
                        .getResource(vehicle.getImage()).toExternalForm()));
            }
        }
    }

    private Optional<Vehicle> posToVehicle(double x, double y) {
        return vehicleBoxes.entrySet().stream().filter(mapEntry -> mapEntry.getValue()
                .contains(x, y)).map(Map.Entry::getKey).findAny();
    }


}
