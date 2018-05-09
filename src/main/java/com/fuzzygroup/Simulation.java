package com.fuzzygroup;

import com.fuzzygroup.entities.Car;
import com.fuzzygroup.entities.Cell;
import com.fuzzygroup.entities.CrazyCar;
import com.fuzzygroup.exceptions.DisplayException;
import com.fuzzygroup.view.Shape;
import com.fuzzygroup.view.*;
import com.fuzzygroup.view.enums.ActionType;
import com.fuzzygroup.view.enums.ColorEnum;
import com.fuzzygroup.view.input.KeyboardEvent;
import com.fuzzygroup.view.lwjgl_engine.LwjglEngine;
import com.fuzzygroup.view.utils.MathUtils;
import com.fuzzygroup.view.utils.Physics;
import com.ngeneration.graphic.engine.ThreeVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.lwjgl.glfw.GLFW.*;

public class Simulation {

    private static final Logger logger = LoggerFactory.getLogger(Display.class);
    public static final String CAR_SIMULATOR_DISPLAY_NAME = "car_simulator";
    public static final String CELL_MAP_SERIALISATION_FILE = "tmp/cellmap.ser";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.US).withZone(ZoneOffset.UTC);
    private static DataWriter writer = new DataWriter("data/data_"
            + formatter
            .format(Instant.now())
            .replaceAll("[\\\\]", "-")
            .replaceAll("[/]", "-")
            + (int) (Math.random() * 1000) + ".csv"
    );

    public static void main(String[] args) {
        // view
        Display display = new Display(CAR_SIMULATOR_DISPLAY_NAME,
                new LwjglEngine(), 900, 900);
        display.setFps(60);
        display.disableHistoryLogging();
        try {
            display.startContinuousDrawing();
        } catch (DisplayException e) {
            e.printStackTrace();
        }

        // controller
        ApplicationController controller = new ApplicationController();
        controller.addOnExitHook(display::shutdown);

        // business logic
        final int fieldSideSize = 100;
        final List<Cell> cells = new CopyOnWriteArrayList<>();
        final Cell[][] cellsArray = generateMap(fieldSideSize, cells);

        List<Car> cars = new ArrayList<>();
        double speed = 0;
        Car car;
        car = generateSingleControlledCar(speed);
        cars.add(car);
        car.setPosition(new ThreeVector(50, 50, 0));
        car.setRotation(Math.PI / 2);
        car.setQualifier("Prima");
        car.addToMask(VisualRepresentationEntity.DefaultMasks.ACCOUNTABLE);
        car.setInLoopRoom(true);
        car.enableReporting();
        car.bindToDisplay(CAR_SIMULATOR_DISPLAY_NAME);
        EntityUtils.enableControl(car, 141.95, 2, 0.05);
        EntityUtils.updateStateEach(20, car);
//        car.addStateReporter(2, state -> {
//            display.setShift(state.getPosition().minus(new ThreeVector(50, 50, 0).divide(display.getScale())).multiple(-1));
//        });
        enableCarMovingLogging(cellsArray, car, "\t");

        EntityUtils.enableCarMovingEstimate(cells, cars);
        EntityUtils.enableMapDrawing(display, cells);
//        display.addAction(new KeyboardEvent(() -> saveMap(cells), ActionType.RELEASED, GLFW_KEY_S));
        display.addAction(new KeyboardEvent(() -> car.fixInCenter(false), ActionType.RELEASED, GLFW_KEY_Q));
        display.addAction(new KeyboardEvent(() -> car.fixInCenter(true), ActionType.RELEASED, GLFW_KEY_W));
    }

    public static void saveMap(List<Cell> list) {
        System.out.println("Start save cell map");
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(CELL_MAP_SERIALISATION_FILE);
            oos = new ObjectOutputStream(fos);
            List<Cell> copy = new CopyOnWriteArrayList<>();
            copy.addAll(list);
//            Collections.copy(copy, list);
            oos.writeObject(copy);
            oos.flush();
            fos.flush();
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.flush();
                    oos.close();
                }
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Cell> loadMap() {
        System.out.println("Start loading cell map");
        List<Cell> list;
        FileInputStream fis = null;
        ObjectInputStream iis = null;
        try {
//            FileInputStream fileIn = new FileInputStream(CELL_MAP_SERIALISATION_FILE);
//            ObjectInputStream in = new ObjectInputStream(fileIn);
//            c = (Cell[][]) in.readObject();
//            in.close();
//            fileIn.close();
            fis = new FileInputStream(CELL_MAP_SERIALISATION_FILE);
            iis = new ObjectInputStream(fis);
            list = (List<Cell>) iis.readObject();
//            System.out.println("c = " + list);
            return list;
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("Deserialisation error: {}", e.getMessage());
            return null;
        } finally {
            try {
                if (iis != null) {
                    iis.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    public static void saveMap(ArrayList[][] list) {
//        System.out.println("Start save cell map");
//        FileOutputStream fos = null;
//        ObjectOutputStream oos = null;
//        try {
//            fos = new FileOutputStream(CELL_MAP_SERIALISATION_FILE);
//            oos = new ObjectOutputStream(fos);
//            oos.writeObject(cells);
//            oos.flush();
//            fos.flush();
//            oos.close();
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (oos != null) {
//                    oos.flush();
//                    oos.close();
//                }
//                if (fos != null) {
//                    fos.flush();
//                    fos.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static Cell[][] loadMap() {
//        System.out.println("Start loading cell map");
//        Cell[][] c;
//        FileInputStream fis = null;
//        ObjectInputStream iis = null;
//        try {
////            FileInputStream fileIn = new FileInputStream(CELL_MAP_SERIALISATION_FILE);
////            ObjectInputStream in = new ObjectInputStream(fileIn);
////            c = (Cell[][]) in.readObject();
////            in.close();
////            fileIn.close();
//            fis = new FileInputStream(CELL_MAP_SERIALISATION_FILE);
//            iis = new ObjectInputStream(fis);
//            c = (Cell[][]) iis.readObject();
//            System.out.println("c = " + c);
//            return c;
//        } catch (IOException | ClassNotFoundException e) {
//            logger.warn("Deserialisation error: {}", e.getMessage());
//            return null;
//        } finally {
//            try {
//                if (iis != null) {
//                    iis.close();
//                }
//                if (fis != null) {
//                    fis.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private static Cell[][] generateMap(int fieldSideSize, List<Cell> cells) {
        List<Cell> loadedCells = loadMap();
        boolean loaded = loadedCells != null && loadedCells.size() != 0;

        Cell[][] cellsArray = new Cell[40][fieldSideSize];
        System.out.println(loaded ? "Use loaded data" : "Create new data");
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < fieldSideSize; j++) {
                double size = 5;
                Cell cell;
                if (loaded) {
                    int finalJ = j;
                    int finalI = i;
                    cell = loadedCells.stream().filter(c -> c.getX() == finalI && c.getY() == finalJ).findFirst()
                            .orElse(null);
                    if (cell == null) {
                        System.exit(1);
                    }
                } else {
                    cell = new Cell(new ThreeVector(size * (1 + i), size * (1 + j), 0),
                            new ThreeVector(0, 0, 0),
                            new ThreeVector(0, 0, 0),
                            new ThreeVector(size, size, 0), Shape.RECT_2,
                            ColorEnum.WHITE, true);
                }
                cell.setXY(i, j);
                cell.setDirection(Direction.NONE);
                cell.bindToDisplay(CAR_SIMULATOR_DISPLAY_NAME);

                cells.add(cell);
                cellsArray[i][j] = cell;

                EntityUtils.updateStateEach(20, cell);
            }
        }
        return cellsArray;
    }

    private static void generateCars(List<Cell> cells, Cell[][] cellsArray, int carsAmount, int mode, List<Car> cars) {
        for (int i = 0; i < carsAmount; i++) {
            for (int j = 0; j < carsAmount; j++) {
                double speed = 25;
                Car car;
                switch (mode) {
                    case 1:
                        car = generateRoadPlotter(carsAmount, i, j, speed);
                        crazyCarBrakeRoad(cells, cars);
                        break;
                    case 2:
                        if (carsAmount == 1) {
                            car = generateSingleControlledCar(speed);
                            car.setQualifier("Prima");
                            car.addToMask(VisualRepresentationEntity.DefaultMasks.ACCOUNTABLE);
                        } else
                            car = generateControlledCar(carsAmount, i, j, speed);
                        break;
                    default:
                        car = generateControlledCar(carsAmount, i, j, speed);
                }
                cars.add(car);
                car.setInLoopRoom(false);
                car.enableReporting();
                car.bindToDisplay(CAR_SIMULATOR_DISPLAY_NAME);
                EntityUtils.enableControl(car, 141.95, 2, 0.05);
                EntityUtils.updateStateEach(20, car);
                enableCarMovingLogging(cellsArray, car, "\t");
            }
        }
    }

    private static void enableCarMovingLogging(Cell[][] cellsArray, Car car, String delimiter) {
        car.<Car>addStateReporter(100, state -> {
            int fieldLength = 5;
            int fieldWidth = 3;
            Cell[][] nearestCells = cellsFromVisibilityArea(car, cellsArray, fieldLength, fieldWidth);
            Cell intersected = getIntersectedCell(car, cellsArray);
            if (intersected != null
                    && nearestCells != null && nearestCells.length == fieldLength
                    && nearestCells[0].length == fieldWidth
                    && state.getSpeed().module() > 0.04) {
                Stream<Cell> cellStream = Arrays.stream(nearestCells).flatMap(Arrays::stream);
                ThreeVector deltaVector = intersected.getPosition().minus(state.getPosition());
                ThreeVector.PolarCoordinateSystemVector polar = deltaVector.toPolar();
                polar.setRadian(polar.getRadian()
                        + (1 - Direction.of(car.getRotation()).intValue()) * Math.PI / 2);
                String row =
                        Stream.concat(
                                Stream.of(
                                        polar.toFlatCartesianVector().getX(),
                                        polar.toFlatCartesianVector().getY(),
                                        Math.cos(state.getRotation()),
                                        Math.sin(state.getRotation())
//                                        state.getSpeed().module()
                                ),
                                cellStream
                                        .filter(Objects::nonNull)
                                        .flatMap(cell -> Stream.of(cell.getDirection().intValue()))
                        )
                                .map(Number::doubleValue)
                                .map(MathUtils::round)
                                .map(String::valueOf)
                                .reduce((s1, s2) -> s1 + delimiter + s2).orElse("empty row");
                writer.appendLine(row);
            } else {
//                logger.trace("Data saving: cell field is empty");
            }
        });
    }


    private static Cell[][] cellsFromVisibilityArea(Car car, Cell[][] cells, int length, int width) {
        Direction carDirection = Direction.of(car.getRotation());
        Cell intersected = getIntersectedCell(car, cells);

        if (intersected != null) {
            int iIntersected = intersected.getY();
            int jIntersected = intersected.getX();
            Cell[][] nearestCells = new Cell[length][width];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < length; j++) {
                    int curRow = -1;
                    int curColumn = -1;
                    if (carDirection == Direction.TOP
                            || carDirection == Direction.BOTTOM) {
                        curColumn = iIntersected - j;
                        curRow = jIntersected + i - width / 2;
                        if (carDirection == Direction.BOTTOM) {
                            curColumn = iIntersected - j;
                        } else {
                            curColumn = iIntersected + j;
                        }
                    } else if (carDirection == Direction.LEFT
                            || carDirection == Direction.RIGHT) {
                        if (jIntersected + j >= 0 && jIntersected + j < cells.length
                                && iIntersected + i - width / 2 >= 0 && iIntersected + i - width / 2 < cells[0].length) {
                            nearestCells[j][i] = cells[jIntersected + j][iIntersected + i - width / 2];
                            if (carDirection == Direction.LEFT) {
                                curRow = jIntersected - j;
                            } else {
                                curRow = jIntersected + j;
                            }
                            curColumn = iIntersected + i - width / 2;
                        }
                    }
                    if (curRow >= 0 && curColumn >= 0
                            && curRow < cells.length && curColumn < cells[i].length) {
                        nearestCells[j][i] = cells[curRow][curColumn];
                    }
                }
            }
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    Cell cell = cells[i][j];
                    cell.removeFromMask(VisualRepresentationEntity.DefaultMasks.SELECTED);
                }
            }
            for (int i = 0; i < nearestCells.length; i++) {
                for (int j = 0; j < nearestCells[i].length; j++) {
                    Cell nearestCell = nearestCells[i][j];
                    if (nearestCell != null) {
                        nearestCell.addToMask(VisualRepresentationEntity.DefaultMasks.SELECTED);
                    } else {
                        return null;
                    }
                }
            }
            return nearestCells;
        } else {
            // todo throw Exception?
            return null;
        }
    }

    private static Cell getIntersectedCell(Car car, Cell[][] cells) {
        Cell intersected = null;
        for (int i = 0; i < cells.length && intersected == null; i++) {
            for (int j = 0; j < cells[i].length && intersected == null; j++) {
                Cell cell = cells[i][j];
                try {
                    if (Physics.intersect(car, cell.getPosition().getX(), cell.getPosition().getY())) {
                        intersected = cell;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return intersected;
    }

    private static Car generateControlledCar(int n, int i, int j, double speed) {
        return new Car(new ThreeVector(10 + 3 * i, 1 + 3 * j, 0),
                new ThreeVector(speed, speed, speed),
                new ThreeVector(0, 0, 0),
                new ThreeVector(10d / (Math.sqrt(n)), 10d / (Math.sqrt(n)), 0), Shape.TRIANGLE,
                ColorEnum.BLUE, true);
    }

    private static Car generateSingleControlledCar(double speed) {
        return new Car(new ThreeVector(20, 70, 0),
                new ThreeVector(speed, speed, speed),
                new ThreeVector(0, 0, 0),
                new ThreeVector(3, 3, 3), Shape.TRIANGLE,
                ColorEnum.BLUE, true);
    }

    private static CrazyCar generateRoadPlotter(int n, int i, int j, double speed) {
        return new CrazyCar(new ThreeVector(10 + 3 * i, 1 + 3 * j, 0),
                new ThreeVector(speed, speed, speed),
                new ThreeVector(0, 0, 0),
                new ThreeVector(10d / (Math.sqrt(n)), 10d / (Math.sqrt(n)), 0), Shape.TRIANGLE,
                ColorEnum.BLUE, true);
    }

    private static void crazyCarBrakeRoad(List<Cell> cells, List<Car> cars) {
        Thread brakeRoad = new Thread(() -> {
            while (true) {
                cars
                        .forEach(car -> cells.stream().filter(cell -> Physics.intersect(cell,
                                car.getPosition().getX(), car.getPosition().getY())).forEach(
                                cell -> cell.setDirection(Direction.of(car.getRotation()))));
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "brake-road-thread");
        brakeRoad.setDaemon(true);
        brakeRoad.start();
    }


}
