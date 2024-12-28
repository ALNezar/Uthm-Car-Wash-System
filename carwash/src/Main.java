import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

// Base class Vehicle
class Vehicle {
    protected String model;
    protected boolean isAvailable;
    protected double pricePerDay;

    public Vehicle(String model, double pricePerDay) {
        this.model = model;
        this.isAvailable = true;  // When a vehicle is added, it's available by default
        this.pricePerDay = pricePerDay;
    }

    public void rentVehicle() {
        this.isAvailable = false;  // Renting a vehicle marks it as unavailable
    }

    public void returnVehicle() {
        this.isAvailable = true;  // Returning a vehicle makes it available again
    }

    public String getStatus() {
        return isAvailable ? "Available" : "Rented";  // Shows whether the vehicle is available or rented
    }

    public double getPricePerDay() {
        return pricePerDay;  // Price per day for renting the vehicle
    }
}

// Car class that represents each car in the rental system
class Car extends Vehicle {
    public Car(String model, double pricePerDay) {
        super(model, pricePerDay);
    }
}

// This class handles the car rental service, including adding cars, renting, returning, etc.
class CarRentalService {
    private ArrayList<Car> cars = new ArrayList<>();
    private HashMap<String, ArrayList<String>> rentalHistory = new HashMap<>();

    public void addCar(Car car) {
        cars.add(car);  // Add a new car to the list
    }

    public void removeCar(int index) {
        if (index >= 0 && index < cars.size()) {
            cars.remove(index);  // Remove a car by its index
        }
    }

    public ArrayList<Car> getCars() {
        return cars;  // Get the list of all cars
    }

    public void rentCar(int index, String user, int days) {
        if (index >= 0 && index < cars.size() && cars.get(index).isAvailable) {
            cars.get(index).rentVehicle();  // Rent the car at the specified index
            recordHistory(user, "Rented", cars.get(index).model + " for " + days + " days");
        }
    }

    public void returnCar(int index, String user) {
        if (index >= 0 && index < cars.size() && !cars.get(index).isAvailable) {
            cars.get(index).returnVehicle();  // Return the car at the specified index
            recordHistory(user, "Returned", cars.get(index).model);
        }
    }

    private void recordHistory(String user, String action, String detail) {
        rentalHistory.putIfAbsent(user, new ArrayList<>());
        rentalHistory.get(user).add(action + " " + detail);  // Record rental or return action for the user
    }

    public ArrayList<String> getRentalHistory(String user) {
        return rentalHistory.getOrDefault(user, new ArrayList<>());  // Retrieve the rental history of a user
    }

    public ArrayList<Car> searchCars(String query) {
        ArrayList<Car> result = new ArrayList<>();
        for (Car car : cars) {
            if (car.model.toLowerCase().contains(query.toLowerCase())) {
                result.add(car);  // Search for cars by model name
            }
        }
        return result;
    }
}

// Class that represents a user (student) of the rental system
class Student {
    String username;
    String email;

    public Student(String username, String email) {
        this.username = username;
        this.email = email;  // Student's username and email
    }
}

// The main GUI class for handling the car rental application interface
class CarRentalAppGUI extends JFrame {
    private CarRentalService service = new CarRentalService();
    private JList<String> carList;
    private DefaultListModel<String> listModel;
    private JButton rentButton, returnButton, addCarButton, removeCarButton, updateCarButton, viewHistoryButton, searchButton, exitButton;
    private JTextField searchField;
    private String currentUser;
    private int rentalDays;

    public CarRentalAppGUI(String user) {
        this.currentUser = user;
        setTitle("UTHM CAR RENTAL SYSTEM");
        setSize(800, 600);  // Adjusted dimensions for better layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create a panel with a background image (for visual appeal)
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundIcon = new ImageIcon("path/to/your/background.jpg"); // Use your background image path
                Image backgroundImage = backgroundIcon.getImage();
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Adding a title label at the top
        JLabel titleLabel = new JLabel("UTHM CAR RENTAL SYSTEM", JLabel.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD | Font.ITALIC, 36));
        titleLabel.setForeground(new Color(255, 215, 0)); // Gold color for the title text
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Adding some sample cars
        service.addCar(new Car("Toyota Sprinter Trueno AE86", 50));
        service.addCar(new Car("Subaru WRX STI Type R", 60));
        service.addCar(new Car("Nissan Skyline GT-R", 55));

        // Create and set up the car list model
        listModel = new DefaultListModel<>();
        updateCarList(service.getCars());

        // Configuring the car list (appearance)
        carList = new JList<>(listModel);
        carList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carList.setFont(new Font("Tahoma", Font.BOLD, 25));
        carList.setBackground(new Color(245, 245, 245)); // Light grey background for the list
        carList.setForeground(new Color(145, 74, 74));    // Darker red text color for the list
        mainPanel.add(new JScrollPane(carList), BorderLayout.CENTER);

        // Creating the buttons and search field
        rentButton = new JButton("Rent Car");
        returnButton = new JButton("Return Car");
        addCarButton = new JButton("Add Car");
        removeCarButton = new JButton("Remove Car");
        updateCarButton = new JButton("Update Car");
        viewHistoryButton = new JButton("View Rental History");
        searchButton = new JButton("Search");
        exitButton = new JButton("Exit");
        searchField = new JTextField(20);

        // Exit Button (Red and placed on top)
        exitButton.setBackground(Color.RED);
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Exit confirmation dialog
                int confirmExit = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
                if (confirmExit == JOptionPane.YES_OPTION) {
                    System.exit(0);  // Close the application if confirmed
                }
            }
        });

        // Panel to hold the search field and search button
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(112, 146, 219));  // Matching background color
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Add action listeners to buttons
        rentButton.addActionListener(e -> rentCarAction());
        returnButton.addActionListener(e -> returnCarAction());
        addCarButton.addActionListener(e -> addCarAction());
        removeCarButton.addActionListener(e -> removeCarAction());
        updateCarButton.addActionListener(e -> updateCarAction());
        viewHistoryButton.addActionListener(e -> viewHistoryAction());
        searchButton.addActionListener(e -> searchAction());

        // Button panel with a nice layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(200, 200, 200));  // Light grey background for buttons
        buttonPanel.add(rentButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(addCarButton);
        buttonPanel.add(removeCarButton);
        buttonPanel.add(updateCarButton);
        buttonPanel.add(viewHistoryButton);

        // Add components to the main panel
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the Exit button in a separate top panel so it doesn't cover anything
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(exitButton, BorderLayout.WEST);  // Exit button aligned to the left of the top panel

        add(topPanel, BorderLayout.NORTH);  // Add topPanel with Exit button to the frame
        add(mainPanel);
    }

    // Action methods
    private void rentCarAction() {
        int index = carList.getSelectedIndex();
        if (index != -1 && service.getCars().get(index).isAvailable) {
            String daysStr = JOptionPane.showInputDialog("Enter rental duration in days:");
            try {
                rentalDays = Integer.parseInt(daysStr);
                if (rentalDays <= 0) {
                    JOptionPane.showMessageDialog(null, "⚠️ Rental duration must be greater than zero.");
                    return;
                }
                service.rentCar(index, currentUser, rentalDays);
                updateCarList(service.getCars());
                JOptionPane.showMessageDialog(null, "✅ Car rented successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "⚠️ Invalid number of days.");
            }
        }
    }

    private void returnCarAction() {
        int index = carList.getSelectedIndex();
        if (index != -1 && !service.getCars().get(index).isAvailable) {
            service.returnCar(index, currentUser);
            updateCarList(service.getCars());
            JOptionPane.showMessageDialog(null, "✅ Car returned successfully!");
        }
    }

    private void addCarAction() {
        String model = JOptionPane.showInputDialog("Enter car model:");
        String priceStr = JOptionPane.showInputDialog("Enter price per day:");
        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                JOptionPane.showMessageDialog(null, "⚠️ Price must be greater than zero.");
                return;
            }
            service.addCar(new Car(model, price));
            updateCarList(service.getCars());
            JOptionPane.showMessageDialog(null, "✅ Car added successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "⚠️ Invalid price.");
        }
    }

    private void removeCarAction() {
        int index = carList.getSelectedIndex();
        if (index != -1) {
            service.removeCar(index);
            updateCarList(service.getCars());
            JOptionPane.showMessageDialog(null, "❌ Car removed successfully!");
        }
    }

    private void updateCarAction() {
        int index = carList.getSelectedIndex();
        if (index != -1) {
            String model = JOptionPane.showInputDialog("Enter new car model:");
            String priceStr = JOptionPane.showInputDialog("Enter new price per day:");
            try {
                double price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    JOptionPane.showMessageDialog(null, "⚠️ Price must be greater than zero.");
                    return;
                }
                service.getCars().get(index).model = model;
                service.getCars().get(index).pricePerDay = price;
                updateCarList(service.getCars());
                JOptionPane.showMessageDialog(null, "✅ Car updated successfully!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "⚠️ Invalid price.");
            }
        }
    }

    private void viewHistoryAction() {
        ArrayList<String> history = service.getRentalHistory(currentUser);
        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No rental history found.");
        } else {
            StringBuilder historyMessage = new StringBuilder("Rental History:\n");
            for (String record : history) {
                historyMessage.append(record).append("\n");
            }
            JOptionPane.showMessageDialog(null, historyMessage.toString());
        }
    }

    private void searchAction() {
        String query = searchField.getText();
        ArrayList<Car> searchResults = service.searchCars(query);
        updateCarList(searchResults);
    }

    // Helper method to update car list in the GUI
    private void updateCarList(ArrayList<Car> cars) {
        listModel.clear();
        for (Car car : cars) {
            listModel.addElement(car.model + " - " + car.getStatus() + " - $" + car.getPricePerDay() + "/day");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CarRentalAppGUI app = new CarRentalAppGUI("Test User");
            app.setVisible(true);
        });
    }
}