package org.cs225.GUI;

/*
    Gabriel worked on this class

    This class is 
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public final class CarSpriteLoader {

    public static final double DEFAULT_SPRITE_WIDTH = 110;
    public static final double DEFAULT_SPRITE_HEIGHT = 55;

    private static final Map<CarSpriteType, Image> IMAGE_CACHE = new EnumMap<>(CarSpriteType.class);

    private CarSpriteLoader() {
    }

    public static Node createCarGraphic(CarSpriteType spriteType) {
        return createCarGraphic(spriteType, DEFAULT_SPRITE_WIDTH, DEFAULT_SPRITE_HEIGHT);
    }

    public static Node createCarGraphic(CarSpriteType spriteType, double fitWidth, double fitHeight) {
        ImageView imageView = createCarImageView(spriteType, fitWidth, fitHeight);

        if (imageView != null) {
            return imageView;
        }

        return createPlaceholderGraphic(spriteType, fitWidth, fitHeight);
    }

    public static ImageView createCarImageView(CarSpriteType spriteType) {
        return createCarImageView(spriteType, DEFAULT_SPRITE_WIDTH, DEFAULT_SPRITE_HEIGHT);
    }

    public static ImageView createCarImageView(CarSpriteType spriteType, double fitWidth, double fitHeight) {
        Image image = loadCarImage(spriteType);

        if (image == null || image.isError()) {
            return null;
        }

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        return imageView;
    }

    public static CarSpriteType getSpriteForIndex(int carIndex) {
        return switch (carIndex) {
            case 0 -> CarSpriteType.BLUE;
            case 1 -> CarSpriteType.RED;
            case 2 -> CarSpriteType.GREEN;
            case 3 -> CarSpriteType.GRAY;
            default -> throw new IllegalArgumentException("Unknown car index: " + carIndex);
        };
    }

    private static Image loadCarImage(CarSpriteType spriteType) {
        if (IMAGE_CACHE.containsKey(spriteType)) {
            return IMAGE_CACHE.get(spriteType);
        }

        try (InputStream inputStream = CarSpriteLoader.class.getResourceAsStream(spriteType.getResourcePath())) {
            if (inputStream == null) {
                IMAGE_CACHE.put(spriteType, null);
                return null;
            }

            Image image = new Image(inputStream);
            if (image.isError()) {
                IMAGE_CACHE.put(spriteType, null);
                return null;
            }

            IMAGE_CACHE.put(spriteType, image);
            return image;
        } catch (IOException exception) {
            IMAGE_CACHE.put(spriteType, null);
            return null;
        }
    }

    private static StackPane createPlaceholderGraphic(CarSpriteType spriteType, double width, double height) {
        Rectangle placeholder = new Rectangle(width, height);
        placeholder.setArcWidth(12);
        placeholder.setArcHeight(12);
        placeholder.setFill(spriteType.getPlaceholderColor());
        placeholder.setStroke(Color.BLACK);

        Label missingLabel = new Label(spriteType.getShortName());
        missingLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: white;");

        StackPane placeholderPane = new StackPane(placeholder, missingLabel);
        placeholderPane.setAlignment(Pos.CENTER);
        placeholderPane.setMinSize(width, height);
        placeholderPane.setPrefSize(width, height);
        placeholderPane.setMaxSize(width, height);
        return placeholderPane;
    }

    public enum CarSpriteType {
        BLUE("Blue Car", "BLUE", "/images/car_blue.png", Color.DODGERBLUE),
        RED("Red Car", "RED", "/images/car_red.png", Color.CRIMSON),
        GREEN("Green Car", "GREEN", "/images/car_green.png", Color.SEAGREEN),
        GRAY("Gray Car", "GRAY", "/images/car_gray.png", Color.DIMGRAY);

        private final String displayName;
        private final String shortName;
        private final String resourcePath;
        private final Color placeholderColor;

        CarSpriteType(String displayName, String shortName, String resourcePath, Color placeholderColor) {
            this.displayName = displayName;
            this.shortName = shortName;
            this.resourcePath = resourcePath;
            this.placeholderColor = placeholderColor;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getShortName() {
            return shortName;
        }

        public String getResourcePath() {
            return resourcePath;
        }

        public Color getPlaceholderColor() {
            return placeholderColor;
        }
    }
}
