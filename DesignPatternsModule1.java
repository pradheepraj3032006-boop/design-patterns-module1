/*

 * Single-file version: all SOLID principle demos and GoF design pattern
 * demos in one file for easy upload/reference.
 *
 * Compile:  javac DesignPatternsModule1.java
 * Run:      java DesignPatternsModule1
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DesignPatternsModule1 {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" SOLID PRINCIPLES");
        System.out.println("=================================================");

        header("SRP - Bad Example");
        SrpBadDemo.run();
        header("SRP - Good Example");
        SrpGoodDemo.run();

        header("OCP - Bad Example");
        OcpBadDemo.run();
        header("OCP - Good Example");
        OcpGoodDemo.run();

        header("LSP - Good Example");
        LspGoodDemo.run();
        header("LSP - Bad Example (intentionally throws - shows the violation)");
        try {
            LspBadDemo.run();
        } catch (UnsupportedOperationException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        header("ISP - Good Example");
        IspGoodDemo.run();

        header("DIP - Good Example");
        DipGoodDemo.run();

        System.out.println();
        System.out.println("=================================================");
        System.out.println(" CREATIONAL PATTERNS");
        System.out.println("=================================================");

        header("Singleton Pattern");
        SingletonDemo.run();

        header("Factory Method Pattern");
        FactoryMethodDemo.run();

        header("Builder Pattern");
        BuilderDemo.run();

        System.out.println();
        System.out.println("=================================================");
        System.out.println(" STRUCTURAL PATTERNS");
        System.out.println("=================================================");

        header("Adapter Pattern");
        AdapterDemo.run();

        header("Decorator Pattern");
        DecoratorDemo.run();

        header("Proxy Pattern");
        ProxyDemo.run();

        System.out.println();
        System.out.println("=================================================");
        System.out.println(" BEHAVIORAL PATTERNS");
        System.out.println("=================================================");

        header("Observer Pattern");
        ObserverDemo.run();

        header("Strategy Pattern");
        StrategyDemo.run();

        header("Command Pattern");
        CommandDemo.run();
    }

    private static void header(String title) {
        System.out.println();
        System.out.println("--- " + title + " ---");
    }
}

/* =========================================================
 * SRP - Single Responsibility Principle
 * ========================================================= */

// BAD: This class has more than one reason to change -
// invoice math, printing, AND persistence all in one place.
class SrpInvoiceBad {
    private double amount;

    SrpInvoiceBad(double amount) { this.amount = amount; }

    double calculateTotal() { return amount * 1.18; }

    void printInvoice() {
        System.out.println("Invoice Total: " + calculateTotal());
    }

    void saveToDatabase() {
        System.out.println("Saving invoice to database...");
    }
}

class SrpBadDemo {
    static void run() {
        SrpInvoiceBad invoice = new SrpInvoiceBad(1000);
        invoice.printInvoice();
        invoice.saveToDatabase();
    }
}

// GOOD: Each class has a single, well-defined responsibility.
class SrpInvoiceGood {
    private double amount;
    SrpInvoiceGood(double amount) { this.amount = amount; }
    double calculateTotal() { return amount * 1.18; }
}

class SrpInvoicePrinter {
    void print(SrpInvoiceGood invoice) {
        System.out.println("Invoice Total: " + invoice.calculateTotal());
    }
}

class SrpInvoiceRepository {
    void save(SrpInvoiceGood invoice) {
        System.out.println("Saving invoice to database...");
    }
}

class SrpGoodDemo {
    static void run() {
        SrpInvoiceGood invoice = new SrpInvoiceGood(1000);
        new SrpInvoicePrinter().print(invoice);
        new SrpInvoiceRepository().save(invoice);
    }
}

/* =========================================================
 * OCP - Open/Closed Principle
 * ========================================================= */

// BAD: Every new shape forces us to MODIFY this class.
class OcpCircleBad {
    double radius;
    OcpCircleBad(double radius) { this.radius = radius; }
}

class OcpRectangleBad {
    double width, height;
    OcpRectangleBad(double width, double height) {
        this.width = width;
        this.height = height;
    }
}

class OcpAreaCalculatorBad {
    double calculateArea(Object shape) {
        if (shape instanceof OcpCircleBad) {
            OcpCircleBad c = (OcpCircleBad) shape;
            return Math.PI * c.radius * c.radius;
        } else if (shape instanceof OcpRectangleBad) {
            OcpRectangleBad r = (OcpRectangleBad) shape;
            return r.width * r.height;
        }
        return 0;
    }
}

class OcpBadDemo {
    static void run() {
        OcpAreaCalculatorBad calc = new OcpAreaCalculatorBad();
        System.out.println(calc.calculateArea(new OcpCircleBad(5)));
        System.out.println(calc.calculateArea(new OcpRectangleBad(4, 6)));
    }
}

// GOOD: Open for extension (new shapes), closed for modification.
interface OcpShape {
    double area();
}

class OcpCircle implements OcpShape {
    private double radius;
    OcpCircle(double radius) { this.radius = radius; }
    public double area() { return Math.PI * radius * radius; }
}

class OcpRectangle implements OcpShape {
    private double width, height;
    OcpRectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    public double area() { return width * height; }
}

// New shape added WITHOUT touching OcpAreaCalculator.
class OcpTriangle implements OcpShape {
    private double base, height;
    OcpTriangle(double base, double height) {
        this.base = base;
        this.height = height;
    }
    public double area() { return 0.5 * base * height; }
}

class OcpAreaCalculator {
    double calculateArea(OcpShape shape) { return shape.area(); }
}

class OcpGoodDemo {
    static void run() {
        OcpAreaCalculator calc = new OcpAreaCalculator();
        System.out.println(calc.calculateArea(new OcpCircle(5)));
        System.out.println(calc.calculateArea(new OcpRectangle(4, 6)));
        System.out.println(calc.calculateArea(new OcpTriangle(4, 6)));
    }
}

/* =========================================================
 * LSP - Liskov Substitution Principle
 * ========================================================= */

// BAD: Ostrich "is-a" Bird, but it cannot fly.
// Substituting Ostrich for Bird breaks client expectations.
class LspBirdBad {
    void fly() { System.out.println("Flying high!"); }
}

class LspSparrowBad extends LspBirdBad { }

class LspOstrichBad extends LspBirdBad {
    @Override
    void fly() {
        throw new UnsupportedOperationException("Ostriches can't fly!");
    }
}

class LspBadDemo {
    static void makeItFly(LspBirdBad bird) { bird.fly(); }

    static void run() {
        makeItFly(new LspSparrowBad()); // works
        makeItFly(new LspOstrichBad()); // throws
    }
}

// GOOD: Separate flying ability from being a bird so subclasses
// are always substitutable for their parent type.
abstract class LspBird {
    abstract void eat();
}

interface LspFlyable {
    void fly();
}

class LspSparrow extends LspBird implements LspFlyable {
    void eat() { System.out.println("Sparrow eating seeds"); }
    public void fly() { System.out.println("Sparrow flying"); }
}

class LspOstrich extends LspBird {
    void eat() { System.out.println("Ostrich eating plants"); }
    // No fly() method forced on it - contract stays honest.
}

class LspGoodDemo {
    static void makeItFly(LspFlyable flyer) { flyer.fly(); }

    static void run() {
        LspSparrow sparrow = new LspSparrow();
        sparrow.eat();
        makeItFly(sparrow);

        LspOstrich ostrich = new LspOstrich();
        ostrich.eat(); // no fly() call possible - compiler enforced safety
    }
}

/* =========================================================
 * ISP - Interface Segregation Principle
 * ========================================================= */

// GOOD: Split into smaller, role-specific interfaces
// (bad version omitted here - see module README for the fat-interface version).
interface IspWorkable {
    void work();
}

interface IspEatable {
    void eat();
}

class IspHumanWorker implements IspWorkable, IspEatable {
    public void work() { System.out.println("Human working"); }
    public void eat() { System.out.println("Human eating lunch"); }
}

// Robot only implements what's relevant to it - no forced eat().
class IspRobotWorker implements IspWorkable {
    public void work() { System.out.println("Robot working"); }
}

class IspGoodDemo {
    static void run() {
        IspHumanWorker human = new IspHumanWorker();
        human.work();
        human.eat();

        IspRobotWorker robot = new IspRobotWorker();
        robot.work();
    }
}

/* =========================================================
 * DIP - Dependency Inversion Principle
 * ========================================================= */

// GOOD: Both high-level and low-level modules depend on an abstraction.
interface DipMessageSender {
    void send(String message);
}

class DipEmailSender implements DipMessageSender {
    public void send(String message) {
        System.out.println("Email sent: " + message);
    }
}

class DipSmsSender implements DipMessageSender {
    public void send(String message) {
        System.out.println("SMS sent: " + message);
    }
}

class DipNotificationService {
    private final DipMessageSender sender;

    DipNotificationService(DipMessageSender sender) { this.sender = sender; }

    void notifyUser(String message) { sender.send(message); }
}

class DipGoodDemo {
    static void run() {
        DipNotificationService emailService = new DipNotificationService(new DipEmailSender());
        emailService.notifyUser("Your order has shipped!");

        DipNotificationService smsService = new DipNotificationService(new DipSmsSender());
        smsService.notifyUser("Your OTP is 4321");
    }
}

/* =========================================================
 * CREATIONAL: Singleton Pattern
 * ========================================================= */

class Singleton {
    private static class Holder {
        private static final Singleton INSTANCE = new Singleton();
    }

    private Singleton() { System.out.println("Singleton instance created"); }

    static Singleton getInstance() { return Holder.INSTANCE; }

    void showMessage() { System.out.println("Hello from the Singleton instance!"); }
}

class SingletonDemo {
    static void run() {
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        s1.showMessage();
        System.out.println("Same instance? " + (s1 == s2));
    }
}

/* =========================================================
 * CREATIONAL: Factory Method Pattern
 * ========================================================= */

interface Notification {
    void notifyUser();
}

class SMSNotification implements Notification {
    public void notifyUser() { System.out.println("Sending an SMS notification"); }
}

class EmailNotification implements Notification {
    public void notifyUser() { System.out.println("Sending an Email notification"); }
}

class PushNotification implements Notification {
    public void notifyUser() { System.out.println("Sending a Push notification"); }
}

class NotificationFactory {
    static Notification createNotification(String type) {
        if (type == null) return null;
        switch (type.toLowerCase()) {
            case "sms": return new SMSNotification();
            case "email": return new EmailNotification();
            case "push": return new PushNotification();
            default: throw new IllegalArgumentException("Unknown notification type: " + type);
        }
    }
}

class FactoryMethodDemo {
    static void run() {
        Notification notification = NotificationFactory.createNotification("sms");
        notification.notifyUser();

        notification = NotificationFactory.createNotification("email");
        notification.notifyUser();
    }
}

/* =========================================================
 * CREATIONAL: Builder Pattern
 * ========================================================= */

class Computer {
    private final String cpu;
    private final String ram;
    private final String storage;
    private final boolean hasGraphicsCard;
    private final boolean hasBluetooth;

    private Computer(Builder builder) {
        this.cpu = builder.cpu;
        this.ram = builder.ram;
        this.storage = builder.storage;
        this.hasGraphicsCard = builder.hasGraphicsCard;
        this.hasBluetooth = builder.hasBluetooth;
    }

    @Override
    public String toString() {
        return "Computer{cpu=" + cpu + ", ram=" + ram + ", storage=" + storage
                + ", graphicsCard=" + hasGraphicsCard + ", bluetooth=" + hasBluetooth + "}";
    }

    static class Builder {
        private final String cpu;
        private final String ram;
        private String storage = "256GB SSD";
        private boolean hasGraphicsCard = false;
        private boolean hasBluetooth = false;

        Builder(String cpu, String ram) {
            this.cpu = cpu;
            this.ram = ram;
        }

        Builder storage(String storage) { this.storage = storage; return this; }
        Builder graphicsCard(boolean v) { this.hasGraphicsCard = v; return this; }
        Builder bluetooth(boolean v) { this.hasBluetooth = v; return this; }

        Computer build() { return new Computer(this); }
    }
}

class BuilderDemo {
    static void run() {
        Computer gamingPC = new Computer.Builder("Intel i9", "32GB")
                .storage("1TB SSD")
                .graphicsCard(true)
                .bluetooth(true)
                .build();

        Computer officePC = new Computer.Builder("Intel i5", "8GB").build();

        System.out.println(gamingPC);
        System.out.println(officePC);
    }
}

/* =========================================================
 * STRUCTURAL: Adapter Pattern
 * ========================================================= */

interface MediaPlayer {
    void play(String fileName);
}

class AdvancedMediaPlayer {
    void playVlc(String fileName) { System.out.println("Playing VLC file: " + fileName); }
    void playMp4(String fileName) { System.out.println("Playing MP4 file: " + fileName); }
}

class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedPlayer = new AdvancedMediaPlayer();
    private String type;

    MediaAdapter(String type) { this.type = type; }

    public void play(String fileName) {
        if (type.equalsIgnoreCase("vlc")) {
            advancedPlayer.playVlc(fileName);
        } else if (type.equalsIgnoreCase("mp4")) {
            advancedPlayer.playMp4(fileName);
        }
    }
}

class AudioPlayer implements MediaPlayer {
    public void play(String fileName) {
        if (fileName.endsWith(".mp3")) {
            System.out.println("Playing MP3 file: " + fileName);
        } else if (fileName.endsWith(".vlc") || fileName.endsWith(".mp4")) {
            String type = fileName.endsWith(".vlc") ? "vlc" : "mp4";
            new MediaAdapter(type).play(fileName);
        } else {
            System.out.println("Unsupported format: " + fileName);
        }
    }
}

class AdapterDemo {
    static void run() {
        AudioPlayer player = new AudioPlayer();
        player.play("song.mp3");
        player.play("movie.mp4");
        player.play("video.vlc");
        player.play("clip.avi");
    }
}

/* =========================================================
 * STRUCTURAL: Decorator Pattern
 * ========================================================= */

interface Coffee {
    String getDescription();
    double getCost();
}

class SimpleCoffee implements Coffee {
    public String getDescription() { return "Coffee"; }
    public double getCost() { return 50.0; }
}

abstract class CoffeeDecorator implements Coffee {
    protected Coffee decoratedCoffee;
    CoffeeDecorator(Coffee coffee) { this.decoratedCoffee = coffee; }
    public String getDescription() { return decoratedCoffee.getDescription(); }
    public double getCost() { return decoratedCoffee.getCost(); }
}

class MilkDecorator extends CoffeeDecorator {
    MilkDecorator(Coffee coffee) { super(coffee); }
    public String getDescription() { return decoratedCoffee.getDescription() + " + Milk"; }
    public double getCost() { return decoratedCoffee.getCost() + 10.0; }
}

class SugarDecorator extends CoffeeDecorator {
    SugarDecorator(Coffee coffee) { super(coffee); }
    public String getDescription() { return decoratedCoffee.getDescription() + " + Sugar"; }
    public double getCost() { return decoratedCoffee.getCost() + 5.0; }
}

class DecoratorDemo {
    static void run() {
        Coffee coffee = new SimpleCoffee();
        System.out.println(coffee.getDescription() + " -> Rs." + coffee.getCost());

        coffee = new MilkDecorator(coffee);
        coffee = new SugarDecorator(coffee);
        System.out.println(coffee.getDescription() + " -> Rs." + coffee.getCost());
    }
}

/* =========================================================
 * STRUCTURAL: Proxy Pattern
 * ========================================================= */

interface Image {
    void display();
}

class RealImage implements Image {
    private String fileName;

    RealImage(String fileName) {
        this.fileName = fileName;
        loadFromDisk();
    }

    private void loadFromDisk() {
        System.out.println("Loading " + fileName + " from disk (expensive operation)");
    }

    public void display() { System.out.println("Displaying " + fileName); }
}

class ProxyImage implements Image {
    private RealImage realImage;
    private String fileName;

    ProxyImage(String fileName) { this.fileName = fileName; }

    public void display() {
        if (realImage == null) {
            realImage = new RealImage(fileName); // lazy initialization
        }
        realImage.display();
    }
}

class ProxyDemo {
    static void run() {
        Image image = new ProxyImage("photo.jpg");
        System.out.println("Image object created, but not loaded yet.");
        image.display(); // loads and displays
        System.out.println("---");
        image.display(); // already loaded, just displays
    }
}

/* =========================================================
 * BEHAVIORAL: Observer Pattern
 * ========================================================= */

interface Observer {
    void update(String eventData);
}

interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String eventData);
}

class YouTubeChannel implements Subject {
    private List<Observer> subscribers = new ArrayList<>();
    private String channelName;

    YouTubeChannel(String channelName) { this.channelName = channelName; }

    public void addObserver(Observer observer) { subscribers.add(observer); }
    public void removeObserver(Observer observer) { subscribers.remove(observer); }

    public void notifyObservers(String eventData) {
        for (Observer o : subscribers) o.update(eventData);
    }

    void uploadVideo(String title) {
        System.out.println(channelName + " uploaded: " + title);
        notifyObservers(title);
    }
}

class Subscriber implements Observer {
    private String name;
    Subscriber(String name) { this.name = name; }

    public void update(String eventData) {
        System.out.println(name + " received notification: New video - " + eventData);
    }
}

class ObserverDemo {
    static void run() {
        YouTubeChannel channel = new YouTubeChannel("Tech With Pradeep");
        Subscriber s1 = new Subscriber("Alice");
        Subscriber s2 = new Subscriber("Bob");

        channel.addObserver(s1);
        channel.addObserver(s2);
        channel.uploadVideo("Design Patterns Explained");

        channel.removeObserver(s1);
        channel.uploadVideo("SOLID Principles Deep Dive");
    }
}

/* =========================================================
 * BEHAVIORAL: Strategy Pattern
 * ========================================================= */

interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    CreditCardPayment(String cardNumber) { this.cardNumber = cardNumber; }
    public void pay(double amount) {
        System.out.println("Paid Rs." + amount + " using Credit Card ending in "
                + cardNumber.substring(cardNumber.length() - 4));
    }
}

class UpiPayment implements PaymentStrategy {
    private String upiId;
    UpiPayment(String upiId) { this.upiId = upiId; }
    public void pay(double amount) {
        System.out.println("Paid Rs." + amount + " using UPI ID " + upiId);
    }
}

class CashOnDelivery implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Rs." + amount + " to be paid on delivery");
    }
}

class ShoppingCart {
    private PaymentStrategy paymentStrategy;

    void setPaymentStrategy(PaymentStrategy strategy) { this.paymentStrategy = strategy; }

    void checkout(double amount) { paymentStrategy.pay(amount); }
}

class StrategyDemo {
    static void run() {
        ShoppingCart cart = new ShoppingCart();

        cart.setPaymentStrategy(new UpiPayment("pradeep@upi"));
        cart.checkout(1500);

        cart.setPaymentStrategy(new CreditCardPayment("1234567812345678"));
        cart.checkout(3200);

        cart.setPaymentStrategy(new CashOnDelivery());
        cart.checkout(750);
    }
}

/* =========================================================
 * BEHAVIORAL: Command Pattern
 * ========================================================= */

interface Command {
    void execute();
    void undo();
}

class Light {
    private boolean isOn = false;

    void turnOn() { isOn = true; System.out.println("Light is ON"); }
    void turnOff() { isOn = false; System.out.println("Light is OFF"); }
}

class TurnOnCommand implements Command {
    private Light light;
    TurnOnCommand(Light light) { this.light = light; }
    public void execute() { light.turnOn(); }
    public void undo() { light.turnOff(); }
}

class TurnOffCommand implements Command {
    private Light light;
    TurnOffCommand(Light light) { this.light = light; }
    public void execute() { light.turnOff(); }
    public void undo() { light.turnOn(); }
}

class RemoteControl {
    private Deque<Command> history = new ArrayDeque<>();

    void pressButton(Command command) {
        command.execute();
        history.push(command);
    }

    void pressUndo() {
        if (!history.isEmpty()) {
            history.pop().undo();
        } else {
            System.out.println("Nothing to undo");
        }
    }
}

class CommandDemo {
    static void run() {
        Light livingRoomLight = new Light();
        RemoteControl remote = new RemoteControl();

        remote.pressButton(new TurnOnCommand(livingRoomLight));
        remote.pressButton(new TurnOffCommand(livingRoomLight));
        remote.pressUndo(); // undoes turn off -> light turns back on
        remote.pressUndo(); // undoes turn on -> light turns off
    }
}
