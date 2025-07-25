package chatapp4;

import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

class User {
    String name;
    String phone;
    String username;
    String password;

    public User(String name, String phone, String username, String password) {
        this.name = name;
        this.phone = phone.startsWith("0") ? "+27" + phone.substring(1) : phone;
        this.username = username;
        this.password = password;
    }

    public String getDisplayName() {
        return name + " {" + phone + "}";
    }
}

class Message {
    String sender;
    String receiver;
    String content;
    boolean isRead;
    String timestamp;

    public Message(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.isRead = false;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd MMM yyyy");
        this.timestamp = now.format(formatter);
    }

    public void markAsRead() {
        isRead = true;
    }

    public String getStatus() {
        String green = "\u001B[32m";
        String red = "\u001B[31m";
        String reset = "\u001B[0m";
        return isRead ? green + "✓✓ Read" + reset : red + "✓ Sent" + reset;
    }

    public void displayMessage() {
        System.out.println("\n[" + timestamp + "]");
        System.out.println(sender + ": " + content + " [" + getStatus() + "]");
    }
}

public class ChatApp4 {
    static ArrayList<Message> chatHistory = new ArrayList<>();
    static ArrayList<User> users = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static User registerNewUser() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your phone number (starts with 0): ");
        String phone = scanner.nextLine();

        // Autogenerate username and password
        String username = name.toLowerCase().substring(0, Math.min(3, name.length())) + "_" + UUID.randomUUID().toString().substring(0, 2);
        String password = "Pwd@" + UUID.randomUUID().toString().substring(0, 4);

        System.out.println("\n✅ Registration successful!");
        System.out.println("Your generated credentials:");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password + "\n");

        User newUser = new User(name, phone, username, password);
        users.add(newUser);
        return newUser;
    }

    public static User loginUser() {
        while (true) {
            System.out.println("\n🔐 Login");
            System.out.print("Username: ");
            String inputUsername = scanner.nextLine();

            System.out.print("Password: ");
            String inputPassword = scanner.nextLine();

            for (User user : users) {
                if (user.username.equals(inputUsername) && user.password.equals(inputPassword)) {
                    System.out.println("✅ Welcome " + user.name + "!");
                    return user;
                }
            }
            System.out.println("❌ Incorrect username or password. Try again.");
        }
    }

    public static void sendMessage(User sender) {
        System.out.println("Who would you like to message?");
        for (int i = 0; i < users.size(); i++) {
            if (!users.get(i).equals(sender)) {
                System.out.println((i + 1) + ". " + users.get(i).getDisplayName());
            }
        }

        System.out.print("Enter the number of the user: ");
        int choice = Integer.parseInt(scanner.nextLine()) - 1;
        User receiver = users.get(choice);

        System.out.print("Enter your message: ");
        String content = scanner.nextLine();

        Message msg = new Message(sender.getDisplayName(), receiver.getDisplayName(), content);
        chatHistory.add(msg);
        System.out.println("📤 Message sent to " + receiver.getDisplayName() + "!\n");
    }

    public static void readMessages(User receiver) {
        System.out.println("\n📨 Inbox for " + receiver.getDisplayName() + ":");
        boolean hasMessages = false;

        for (Message msg : chatHistory) {
            if (msg.receiver.equals(receiver.getDisplayName())) {
                msg.displayMessage();
                msg.markAsRead();
                hasMessages = true;
            }
        }

        if (!hasMessages) {
            System.out.println("No new messages.\n");
        }
    }

    public static void checkSentMessages(User sender) {
        System.out.println("\n📤 Sent messages by " + sender.getDisplayName() + ":");
        boolean hasSent = false;

        for (Message msg : chatHistory) {
            if (msg.sender.equals(sender.getDisplayName())) {
                msg.displayMessage();
                hasSent = true;
            }
        }

        if (!hasSent) {
            System.out.println("No sent messages yet.\n");
        }
    }

    public static void main(String[] args) {
        System.out.println("💬 Welcome to Mini WhatsApp Console Chat!\n");

        // Default users
        users.add(new User("Jay", "0723456789", "jay_1", "Code@123"));
        users.add(new User("Bev", "0612345678", "bev_2", "Pass@456"));

        while (true) {
            System.out.println("\n========= MAIN MENU =========");
            System.out.println("1. Register New User");
            System.out.println("2. Login and Chat");
            System.out.println("3. Exit");
            System.out.print("Choose an option (1-3): ");
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    registerNewUser();
                    break;
                case "2":
                    User currentUser = loginUser();
                    boolean stayLoggedIn = true;

                    while (stayLoggedIn) {
                        System.out.println("\n========= CHAT MENU =========");
                        System.out.println("1. Send a Message");
                        System.out.println("2. Read Messages");
                        System.out.println("3. Check Sent Messages");
                        System.out.println("4. Logout");
                        System.out.print("Choose an option (1-4): ");
                        String option = scanner.nextLine();

                        switch (option) {
                            case "1":
                                sendMessage(currentUser);
                                break;
                            case "2":
                                readMessages(currentUser);
                                break;
                            case "3":
                                checkSentMessages(currentUser);
                                break;
                            case "4":
                                stayLoggedIn = false;
                                System.out.println("🔒 Logged out.");
                                break;
                            default:
                                System.out.println("⚠️ Invalid choice. Try again.");
                        }
                    }
                    break;
                case "3":
                    System.out.println("\n👋 Goodbye! Thanks for using Mini WhatsApp.");
                    return;
                default:
                    System.out.println("⚠️ Invalid choice. Try again.");
            }
        }
    }
}

