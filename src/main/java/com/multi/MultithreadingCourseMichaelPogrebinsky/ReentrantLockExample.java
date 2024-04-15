package com.multi.MultithreadingCourseMichaelPogrebinsky;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.animation.FillTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExample extends Application {
    
    // JavaFX - - это платформа и набор инструментов для разработки и построения богатых интерфейсов пользователя (GUI) для Java-приложений.
    // JavaFX был разработан как замена устаревшего пакета Swing
    // JavaFX также обеспечивает поддержку множества платформ, включая настольные компьютеры, мобильные устройства и встраиваемые системы, что делает его универсальным инструментом для разработки кроссплатформенных приложений
    
    // ReentrantLock == synchronized
    // преимущество: контроль над замком + дополнительные методы блокировки
    // getQueuedThreads() - метод возвращает список потоков ожидающих получение блокировки!
    // getOwner() - возвращает поток которому в данный момент принадлежит блокировка!
    // isHeldByCurrentThread() - запрашивает удерживается ли блокировка текущим потоком?
    // lockInterruptibly() - позволяет заблокированому потоку проснуться и перейти в catch чтобы очиститься и закрыть приложение, событие происходит если внешне вызван метод interrupted()
    // tryLock() - запрашивает, удерживается ли блокировка каким либо потоком прямо сейчас? САМЫЙ ВАЖНЫЙ! 
    
    public static void main(String[] args) {
        launch(args);
    }
    
    // Stage - это основное окно приложения, контейнер верхнего уровня
    // GridPane - это контейнер для управления компоновкой элементов пользовательского интерфейса в виде сетки, где элементы располагаются в рядах и столбцах
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cryptocurrency Prices");
        
        GridPane grid = createGrid();
        
        Map<String, Label> cryptoLabels = createCryptoPriceLabels();
        
        addLabelsToGrid(cryptoLabels, grid);
        
        double width = 300;
        double height = 250;
        
        StackPane root = new StackPane(); // контейнер,который располагает своих дочерних узлов в виде стека, последний добавленный узел будет находиться поверх остальных
        
        Rectangle background = createBackgroundRectangleWithAnimation(width, height);
        
        root.getChildren().add(background);
        root.getChildren().add(grid);
        
        primaryStage.setScene(new Scene(root, width, height)); // Scene - в JavaFX представляет собой контейнер для всех графических элементов (узлов)
        
        PricesContainer pricesContainer = new PricesContainer();
        
        PriceUpdater priceUpdater = new PriceUpdater(pricesContainer);
        
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (pricesContainer.getLockObject().tryLock()) {
                    try {
                        Label bitcoinLabel = cryptoLabels.get("BTC");
                        bitcoinLabel.setText(String.valueOf(pricesContainer.getBitcoinPrice()));

                        Label etherLabel = cryptoLabels.get("ETH");
                        etherLabel.setText(String.valueOf(pricesContainer.getEtherPrice()));

                        Label litecoinLabel = cryptoLabels.get("LTC");
                        litecoinLabel.setText(String.valueOf(pricesContainer.getLitecoinPrice()));

                        Label bitcoinCashLabel = cryptoLabels.get("BCH");
                        bitcoinCashLabel.setText(String.valueOf(pricesContainer.getBitcoinCashPrice()));

                        Label rippleLabel = cryptoLabels.get("XRP");
                        rippleLabel.setText(String.valueOf(pricesContainer.getRipplePrice()));
                    } finally {
                        pricesContainer.getLockObject().unlock();
                    }
                }
            }
        };
        
        addWindowResizeListener(primaryStage, background);
        
        animationTimer.start();
        
        priceUpdater.start();
        
        primaryStage.show();
    }
    
    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10); // Устанавливает горизонтальный зазор между ячейками сетки 10 пикселей
        grid.setVgap(10); // Устанавливает вертикальный зазор между ячейками сетки 10 пикселей
        grid.setAlignment(Pos.CENTER); // содержимое ячейки будет выравниваться по центру сетки как по горизонтали, так и по вертикали.
        return grid;
    }
    
    private Map<String, Label> createCryptoPriceLabels(){
        Label bitcoinPrice = new Label("0"); // начальное значение 0
        bitcoinPrice.setId("BTC");
        
        Label etherPrice = new Label("0");
        etherPrice.setId("ETH");

        Label liteCoinPrice = new Label("0");
        liteCoinPrice.setId("LTC");

        Label bitcoinCashPrice = new Label("0");
        bitcoinCashPrice.setId("BCH");

        Label ripplePrice = new Label("0");
        ripplePrice.setId("XRP");
        
        Map<String, Label> cryptoLabelsMap = new HashMap<>();
        cryptoLabelsMap.put("BTC", bitcoinPrice);
        cryptoLabelsMap.put("ETH", etherPrice);
        cryptoLabelsMap.put("LTC", liteCoinPrice);
        cryptoLabelsMap.put("BCH", bitcoinCashPrice);
        cryptoLabelsMap.put("XRP", ripplePrice);
        
        return cryptoLabelsMap;
        
    }
    
    private void addLabelsToGrid(Map<String, Label> labels, GridPane grid) {
        int row = 0; 
        for(Map.Entry<String, Label> entry : labels.entrySet()) {
            String cryptoName = entry.getKey();
            Label nameLabel = new Label(cryptoName);
            nameLabel.setTextFill(Color.BLUE); //  Устанавливает цвет текста метки на синий
            nameLabel.setOnMousePressed(event -> nameLabel.setTextFill(Color.RED)); // Устанавливает обработчик события для события "нажатие кнопки мыши" на метке. Когда метка нажата, цвет текста меняется на красный.
            nameLabel.setOnMouseReleased((EventHandler) event -> nameLabel.setTextFill(Color.BLUE)); // "отпускание кнопки мыши" на метке
            
            grid.add(nameLabel, 0, row); //Добавляет метку с именем криптовалюты в первый столбец (столбец с индексом 0) текущей строки row в GridPane.
            grid.add(entry.getValue(), 1, row); // Добавляет метку с ценой криптовалюты во второй столбец (столбец с индексом 1) текущей строки row в GridPane.
            row++;                // перейти к следующей строке
        }
    }
    
    private Rectangle createBackgroundRectangleWithAnimation(double width, double height) {
        Rectangle backround = new Rectangle(width, height);
        FillTransition fillTransition = new FillTransition(Duration.millis(1000), backround, Color.LIGHTGREEN, Color.LIGHTBLUE);
        fillTransition.setCycleCount(Timeline.INDEFINITE);
        fillTransition.setAutoReverse(true);
        fillTransition.play();
        return backround;
    }
    
    private void addWindowResizeListener(Stage stage, Rectangle background) {
        ChangeListener<Number> stageSizeListener = ((observable, oldValue, newValue) -> {
            background.setHeight(stage.getHeight());
            background.setWidth(stage.getWidth());
        });
        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
    }
    
    public static class PricesContainer {
        private Lock lockObject = new ReentrantLock();

        private double bitcoinPrice;
        private double etherPrice;
        private double litecoinPrice;
        private double bitcoinCashPrice;
        private double ripplePrice;

        public Lock getLockObject() {
            return lockObject;
        }

        public double getBitcoinPrice() {
            return bitcoinPrice;
        }

        public void setBitcoinPrice(double bitcoinPrice) {
            this.bitcoinPrice = bitcoinPrice;
        }

        public double getEtherPrice() {
            return etherPrice;
        }

        public void setEtherPrice(double etherPrice) {
            this.etherPrice = etherPrice;
        }

        public double getLitecoinPrice() {
            return litecoinPrice;
        }

        public void setLitecoinPrice(double litecoinPrice) {
            this.litecoinPrice = litecoinPrice;
        }

        public double getBitcoinCashPrice() {
            return bitcoinCashPrice;
        }

        public void setBitcoinCashPrice(double bitcoinCashPrice) {
            this.bitcoinCashPrice = bitcoinCashPrice;
        }

        public double getRipplePrice() {
            return ripplePrice;
        }

        public void setRipplePrice(double ripplePrice) {
            this.ripplePrice = ripplePrice;
        }
    }
    
    public static class PriceUpdater extends Thread{
        private PricesContainer pricesContainer;
        private Random random = new Random();
        
        public PriceUpdater(PricesContainer pricesContainer) {
            this.pricesContainer = pricesContainer;
        }
        
        @Override
        public void run() {
            while (true) {
                pricesContainer.getLockObject().lock();

                try {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Error 1");
                    }
                    pricesContainer.setBitcoinPrice(random.nextInt(20000));
                    pricesContainer.setEtherPrice(random.nextInt(2000));
                    pricesContainer.setLitecoinPrice(random.nextInt(500));
                    pricesContainer.setBitcoinCashPrice(random.nextInt(5000));
                    pricesContainer.setRipplePrice(random.nextDouble());
                } finally {
                    pricesContainer.getLockObject().unlock();
                }
                

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println("Error 2");
                }
            }
        }
    }
       

}
