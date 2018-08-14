package org.shadowrun.common.nodes;

import com.sun.javafx.geom.Vec2d;
import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Bounds;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.shadowrun.common.constants.TerrainType;
import org.shadowrun.common.constants.VehicleChaseRole;
import org.shadowrun.common.utils.FontUtils;
import org.shadowrun.logic.Animation;
import org.shadowrun.models.Battle;
import org.shadowrun.models.RectangleF;
import org.shadowrun.models.Vehicle;
import org.shadowrun.models.VehicleChase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
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
    private static final double ARROW_SPEED = 0.000000005;
    private static final long MESSAGE_CYCLE = 1000000000;

    private Font digitalFont;

    private ObjectProperty<Vehicle> selectedVehicle;

    // Images and Textures
    private Image sandTexture;
    private Image waterTexture;
    private Image grassTexture;
    private Image concreteTexture;
    private Image skyCloud;
    private Image unkownVehicle;
    private Image arrowGreen;
    private Image arrowRed;

    // Animations
    private Animation selectionAnimation;

    private GraphicsContext context;

    private Battle battle;

    private boolean onlyPursuers;
    private boolean onlyRunners;

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
    private double cameraPosition = 0;
    private double arrowPhase = 0;

    private int maxVehiclePos;
    private int minVehiclePos;

    private double lastWidth;
    private double lastHeight;

    private AnimationTimer redrawTimer;
    private long frames = 0;
    private boolean messageCycle = true;

    private Vec2d lastPos = new Vec2d();

    private ThreadLocalRandom threadLocalRandom;

    public VehicleChaseCanvas() {
        URL res = VehicleChaseCanvas.class.getClassLoader().getResource("fonts/CourierPrimeSans.ttf");
        digitalFont = Font.font("Courier Prime Sans", 26);
        context = getGraphicsContext2D();
        this.battle = null;
        background = null;
        lastWidth = 0.0;
        lastHeight = 0.0;

        selectedVehicle = new SimpleObjectProperty<>(null);

        ClassLoader classLoader = getClass().getClassLoader();
        sandTexture = new Image(classLoader.getResource("textures/sand.jpg").toExternalForm());
        waterTexture = new Image(classLoader.getResource("textures/water.jpg").toExternalForm());
        concreteTexture = new Image(classLoader.getResource("textures/concrete.png").toExternalForm());
        grassTexture = new Image(classLoader.getResource("textures/grass.png").toExternalForm());
        skyCloud = new Image(classLoader.getResource("textures/cloud.png").toExternalForm());
        unkownVehicle = new Image(classLoader.getResource("objects/car.png").toExternalForm());
        arrowRed = new Image(classLoader.getResource("objects/arrow_red.png").toExternalForm());
        arrowGreen = new Image(classLoader.getResource("objects/arrow_green.png").toExternalForm());

        List<Image> selectBox = new ArrayList<>();
        selectBox.add(new Image(classLoader.getResource("objects/selection.png").toExternalForm()));
        selectBox.add(new Image(classLoader.getResource("objects/selection2.png").toExternalForm()));
        selectBox.add(new Image(classLoader.getResource("objects/selection3.png").toExternalForm()));
        selectBox.add(new Image(classLoader.getResource("objects/selection4.png").toExternalForm()));
        selectionAnimation = new Animation(selectBox, 8);

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
            private long lastMessageCycle = 0;

            @Override
            public void handle(long now) {
                long dt = now - lastUpdate;
                if (dt >= 33_000_000) {
                    redraw(dt);
                    increaseFrames();
                    lastUpdate = now;

                    lastMessageCycle += dt;
                    if(lastMessageCycle >= MESSAGE_CYCLE) {
                        messageCycle = !messageCycle;
                        lastMessageCycle -= MESSAGE_CYCLE;
                    }
                }
            }

        };

        setFocusTraversable(true);

        addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            Optional<Vehicle> hovered = posToVehicle(event.getX(), event.getY());
            hoveredVehicle = hovered.orElse(null);
        });


        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Optional<Vehicle> clicked = posToVehicle(event.getX(), event.getY());
            selectedVehicle.setValue(clicked.orElse(null));
        });

        setOnMousePressed(event -> {
            lastPos.set(event.getX(), event.getY());
        });

        setOnMouseDragged(event -> {
            cameraPosition += lastPos.x - event.getX();
            lastPos.set(event.getX(), event.getY());
            updateCameraLimits();
        });

        setOnScroll(event -> {
            cameraPosition += event.getDeltaY();
            updateCameraLimits();
        });

        addEventFilter(MouseEvent.ANY, (e) -> requestFocus());

        setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT:
                    cameraPosition -= 15;
                    break;
                case RIGHT:
                    cameraPosition += 15;
                    break;
                default:
            }
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
        battle.vehicleChaseProperty().addListener((observable, oldValue, newVehicleChase) -> {
            if (newVehicleChase != null) {

                newVehicleChase.getPositions().addListener((MapChangeListener<String, Integer>) change -> {
                    updateVehiclePos();
                    updateCameraLimits();
                });

                newVehicleChase.getChaseRoles().addListener((MapChangeListener<String, VehicleChaseRole>) change -> {
                    updateRoleStatus();
                });

                updateVehiclePos();
                updateRoleStatus();

                redrawTimer.start();
            } else {
                redrawTimer.stop();
            }
        });

        battle.getVehicles().addListener((ListChangeListener<? super Vehicle>) c -> updateVehicles());
        updateVehicles();
    }

    private void updateCameraLimits() {
        double minCameraPos = minVehiclePos * VEHICLE_BLOCK_SIZE;
        double maxCameraPos = (maxVehiclePos + 1) * VEHICLE_BLOCK_SIZE + SPACE_SIZE;
        double scrollableRange = maxCameraPos - minCameraPos - getWidth();

        if (cameraPosition < minCameraPos)
            cameraPosition = minCameraPos;


        if (scrollableRange < 0)
            scrollableRange = 0;

        if (cameraPosition > scrollableRange) {
            cameraPosition = scrollableRange;
        }
    }

    private void updateVehiclePos() {
        Optional<Integer> maxPosition = battle.getVehicleChase().getPositions().values().stream().max(Integer::compareTo);
        maxVehiclePos = maxPosition.isPresent() ? maxPosition.get() : 0;

        Optional<Integer> minPosition = battle.getVehicleChase().getPositions().values().stream().min(Integer::compareTo);
        minVehiclePos = minPosition.isPresent() ? minPosition.get() : 0;
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


        for (int x = 0; x < getWidth(); x += scaledTile) {
            for (int y = 0; y < getHeight(); y += scaledTile) {
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
        if (lastWidth != getWidth() || lastHeight != getHeight())
            init();

        context.setFill(Color.BLACK);
        context.fillRect(0, 0, getWidth(), getHeight());

        context.drawImage(background, lastBackgroundPosition - cameraPosition, 0);
        context.drawImage(background, lastBackgroundPosition + getWidth() - cameraPosition, 0);
        lastBackgroundPosition -= deltaTime * textureSpeeds.get(battle.getVehicleChase().getTerrainType());
        if (lastBackgroundPosition <= -getWidth() + cameraPosition)
            lastBackgroundPosition = cameraPosition;

        if (battle.getVehicleChase().getTerrainType() == TerrainType.SKY) {
            for (Vec2d positions : particlesPosition) {
                context.drawImage(skyCloud, positions.x - cameraPosition, positions.y);
                positions.x -= deltaTime * SKY_CLOUDS;
                if (positions.x <= -skyCloud.getWidth())
                    positions.x = getWidth();
            }
        }

        int lane = 0;
        for (Vehicle vehicle : battle.getVehicles()) {
            VehicleChaseRole vehicleChaseRole = battle.getVehicleChase().getChaseRoles().get(vehicle.getUuid());
            Double vehicleAmplitude = amplitude.get(vehicle);
            Double vehicleSpeed = speed.get(vehicle);
            Double vehiclePhase = phase.get(vehicle);

            // draw car (even outside bounds)
            Image vehicleImage = vehicleImages.get(vehicle);
            ObservableMap<String, Integer> positions = battle.getVehicleChase().getPositions();
            double x = SPACE_SIZE + (VEHICLE_BLOCK_SIZE * positions.get(vehicle.getUuid())) - cameraPosition;
            double y = (lane * LANE_HEIGHT) + (LANE_HEIGHT / 2) - (vehicleImage.getHeight() / 2) +
                    (vehicleAmplitude * Math.sin((frames + vehiclePhase) / vehicleSpeed));
            context.drawImage(vehicleImage, x, y);

            // draw arrow if outside bounds
            arrowPhase = (arrowPhase + deltaTime * ARROW_SPEED) % 12;
            Image arrow = vehicleChaseRole == VehicleChaseRole.PURSUER ? arrowRed : arrowGreen;
            if (x > getWidth() + SPACE_SIZE) {
                double arrowY = (lane * LANE_HEIGHT) + (LANE_HEIGHT / 2) - (arrow.getHeight() / 2);
                context.drawImage(arrow, getWidth() - 64 + arrowPhase, arrowY);
            } else if (x < -(SPACE_SIZE + LANE_HEIGHT)) {
                double arrowY = (lane * LANE_HEIGHT) + (LANE_HEIGHT / 2) - (arrow.getHeight() / 2);
                context.drawImage(arrow, 0, 0, arrow.getWidth(), arrow.getHeight(), 64 - arrowPhase, arrowY, -arrow.getWidth(), arrow.getHeight());
            }


            lane++;

            RectangleF rect = vehicleBoxes.get(vehicle);
            rect.x = x;
            rect.y = y;

            //draw Condition monitor
            context.setStroke(Color.RED);
            context.setLineWidth(2);
            context.strokeLine(x + 35, y + LANE_HEIGHT, x + 35 + 100, y + LANE_HEIGHT);

            context.setStroke(Color.GREEN);
            int hp = (vehicle.getConditionMonitor().getCurrent() * 100) / vehicle.getConditionMonitor().getMax();
            if (hp > 0) {
                context.strokeLine(x + 35,
                        y + LANE_HEIGHT,
                        x + (double) (35 + hp),
                        y + LANE_HEIGHT);
            }

            VehicleChaseRole role = battle.getVehicleChase().getChaseRoles().get(vehicle.getUuid());
            if (role == VehicleChaseRole.PURSUER)
                context.setStroke(Color.RED);
            else
                context.setStroke(Color.GREEN);
            context.setLineWidth(1);
            context.setFont(Font.font(11));
            context.strokeText(role.toString(),
                    x + 35,
                    y + LANE_HEIGHT - 4);


            if (hoveredVehicle == vehicle || selectedVehicle.getValue() == vehicle) {
                selectionAnimation.update(deltaTime);
                context.drawImage(selectionAnimation.getCurrentFrame(), x, y);
            }

        }

        // draw overlay
        if(messageCycle){
            String message = null;
            Color textColor = null;
            if (onlyPursuers) {
                message = "NO RUNNERS";
                textColor = Color.RED;
            } else if (onlyRunners) {
                message = "NO PURSUERS";
                textColor = Color.GREEN;
            }

            if (message != null) {
                context.setFill(textColor);
                Font font = digitalFont;
                context.setFont(font);
                Bounds messageBounds = FontUtils.reportSize(message, font);
                context.fillText(message,
                        getWidth() / 2 - messageBounds.getWidth() / 2,
                        getHeight() - 15 - messageBounds.getHeight());
            }
        }

    }

    /**
     * Called everytime vehicles are removed or added
     */
    private void updateVehicles() {

        for (Vehicle vehicle : battle.getVehicles()) {
            Double vehicleAmplitude = amplitude.get(vehicle);
            if (vehicleAmplitude == null) {
                vehicleAmplitude = ThreadLocalRandom.current().nextDouble(1, 4) * 3;
                amplitude.put(vehicle, vehicleAmplitude);
            }


            Double vehicleSpeed = speed.get(vehicle);
            if (vehicleSpeed == null) {
                vehicleSpeed = ThreadLocalRandom.current().nextDouble(2, 6) * 2;
                speed.put(vehicle, vehicleSpeed);
            }

            Double vehiclePhase = phase.get(vehicle);
            if (vehiclePhase == null) {
                vehiclePhase = ThreadLocalRandom.current().nextDouble(0, 500);
                phase.put(vehicle, vehiclePhase);
            }

            if (!vehicleBoxes.containsKey(vehicle)) {
                vehicleBoxes.put(vehicle, new RectangleF(0, 0, VEHICLE_BLOCK_SIZE, LANE_HEIGHT));
            }


            if (vehicleImages.get(vehicle) == null) {
                if (getClass().getClassLoader().getResource(vehicle.getImage()) == null)
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

    private void updateRoleStatus() {
        onlyPursuers = true;
        onlyRunners = true;
        VehicleChase vehicleChase = battle.getVehicleChase();
        for (VehicleChaseRole vehicleChaseRole : vehicleChase.getChaseRoles().values()) {
            if (vehicleChaseRole == VehicleChaseRole.PURSUER)
                onlyRunners = false;
            else
                onlyPursuers = false;
        }
    }


}
