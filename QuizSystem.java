import java.io.*;
import java.util.Scanner;

public class QuizSystem {
    
    static final int MAX_QUESTIONS = 100;
    static String[] questions = new String[MAX_QUESTIONS];
    static String[][] options = new String[MAX_QUESTIONS][4];
    static String[] correctAnswers = new String[MAX_QUESTIONS];
    static int totalQuestions = 0;
    static final int QUESTION_TIME_LIMIT = 30;
    static final String ADMIN_PASSWORD = "admin123";
    static final String ADMIN_USERNAME = "Rootadmin";
    static final String QUESTIONS_FILE = "questions.txt";
    static final String PERFORMANCE_FILE = "performance.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== EXAM SIMULATOR MENU =====");
            System.out.println("1. Take Quiz");
            System.out.println("2. Admin Mode");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                loadQuestionsFromFile(QUESTIONS_FILE);
                if (totalQuestions == 0) {
                    System.out.println("No questions found!");
                } else {
                    shuffleQuestions();
                    takeQuiz();
                }
            } else if (choice.equals("2")) {
                System.out.print("Enter admin username: ");
                String userName = scanner.nextLine();
                System.out.print("Enter admin password: ");
                String pass = scanner.nextLine();
                if (pass.equals(ADMIN_PASSWORD) && userName.equals(ADMIN_USERNAME)) {
                    adminMenu();
                } else {
                    System.out.println("Incorrect credentials.");
                }
            } else if (choice.equals("3")) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
        scanner.close();
    }

    public static void adminMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View Questions");
            System.out.println("2. Add New Question");
            System.out.println("3. Edit Question");
            System.out.println("4. Delete Question");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choice: ");
            String choice = sc.nextLine();

            if (choice.equals("1")) {
                viewAllQuestions();
            } else if (choice.equals("2")) {
                addNewQuestion();
            } else if (choice.equals("3")) {
                editQuestion();
            } else if (choice.equals("4")) {
                deleteQuestion();
            } else if (choice.equals("5")) {
                break;
            } else {
                System.out.println("Invalid input.");
            }
        }
    }

    public static void viewAllQuestions() {
        loadQuestionsFromFile(QUESTIONS_FILE);
        if (totalQuestions == 0) {
            System.out.println("No questions available!");
            return;
        }
        for (int i = 0; i < totalQuestions; i++) {
            System.out.println("\nQuestion " + (i + 1) + ":");
            System.out.println(questions[i]);
            for (int j = 0; j < 4; j++) {
                System.out.println(options[i][j]);
            }
            System.out.println("Answer: " + correctAnswers[i]);
        }
    }

    public static void addNewQuestion() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter question text: ");
        String q = sc.nextLine();

        String[] opts = new String[4];
        for (int i = 0; i < 4; i++) {
            System.out.print("Enter option " + (char) ('A' + i) + ": ");
            opts[i] = sc.nextLine();
        }

        System.out.print("Enter correct option (A/B/C/D): ");
        String ans = sc.nextLine().trim().toUpperCase();

        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(QUESTIONS_FILE), true))) {
            writer.println("Q: " + q);
            for (int i = 0; i < 4; i++) {
                writer.println((char) ('A' + i) + ") " + opts[i]);
            }
            writer.println("ANS: " + ans);
            writer.println();
            System.out.println("Question added successfully.");
        } catch (IOException e) {
            System.out.println("Error adding question: " + e.getMessage());
        }
    }

    public static void editQuestion() {
        viewAllQuestions();
        if (totalQuestions == 0){
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter question number to edit: ");
        int qNum = Integer.parseInt(sc.nextLine()) - 1;

        if (qNum < 0 || qNum >= totalQuestions) {
            System.out.println("Invalid question number!");
            return;
        }

        System.out.println("\nCurrent Question:");
        System.out.println(questions[qNum]);
        for (int j = 0; j < 4; j++) {
            System.out.println(options[qNum][j]);
        }
        System.out.println("Answer: " + correctAnswers[qNum]);

        System.out.print("\nEnter new question text (or press Enter to keep current): ");
        String newQ = sc.nextLine();
        if (!newQ.isEmpty()) {
            questions[qNum] = "Q: " + newQ;
        }

        String[] newOpts = new String[4];
        for (int i = 0; i < 4; i++) {
            System.out.print("Enter new option " + (char) ('A' + i) +
                    " (or press Enter to keep current): ");
            String opt = sc.nextLine();
            if (opt.isEmpty()) {
                newOpts[i] = options[qNum][i];
            } else {
                newOpts[i] = (char) ('A' + i) + ") " + opt;
            }
        }
        options[qNum] = newOpts;

        System.out.print("Enter new correct option (A/B/C/D) (or press Enter to keep current): ");
        String newAns = sc.nextLine().trim().toUpperCase();
        if (!newAns.isEmpty()) {
            correctAnswers[qNum] = newAns;
        }

        saveAllQuestionsToFile();
        System.out.println("Question updated successfully.");
    }

    public static void deleteQuestion() {
        viewAllQuestions();
        if (totalQuestions == 0){
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter question number to delete: ");
        int qNum = Integer.parseInt(sc.nextLine()) - 1;

        if (qNum < 0 || qNum >= totalQuestions) {
            System.out.println("Invalid question number!");
            return;
        }

        System.out.println("\nAre you sure you want to delete this question?");
        System.out.println(questions[qNum]);
        for (int j = 0; j < 4; j++) {
            System.out.println(options[qNum][j]);
        }
        System.out.print("Confirm deletion (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();

        if (confirm.equalsIgnoreCase("y")) {
            // Shift all questions up to fill the gap
            for (int i = qNum; i < totalQuestions - 1; i++) {
                questions[i] = questions[i + 1];
                options[i] = options[i + 1];
                correctAnswers[i] = correctAnswers[i + 1];
            }
            totalQuestions--;
            saveAllQuestionsToFile();
            System.out.println("Question deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    public static void saveAllQuestionsToFile() {
        try (PrintWriter writer = new PrintWriter(new File(QUESTIONS_FILE))) {
            for (int i = 0; i < totalQuestions; i++) {
                writer.println(questions[i]);
                for (int j = 0; j < 4; j++) {
                    writer.println(options[i][j]);
                }
                writer.println("ANS: " + correctAnswers[i]);
                writer.println();
            }
        } catch (IOException e) {
            System.out.println("Error saving questions: " + e.getMessage());
        }
    }

    public static void loadQuestionsFromFile(String filename) {
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            int index = 0;
            while (fileScanner.hasNextLine() && index < MAX_QUESTIONS) {
                String line = fileScanner.nextLine();
                if (line.startsWith("Q: ")) {
                    questions[index] = line;
                    for (int i = 0; i < 4; i++) {
                        options[index][i] = fileScanner.nextLine();
                    }
                    String ansLine = fileScanner.nextLine();
                    correctAnswers[index] = ansLine.substring(5).trim().toUpperCase();
                    if (fileScanner.hasNextLine()) fileScanner.nextLine(); // Skip empty line
                    index++;
                }
            }
            totalQuestions = index;
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public static void shuffleQuestions() {
        for (int i = 0; i < totalQuestions; i++) {
            int j = (int)(Math.random() * totalQuestions);

            String tempQ = questions[i];
            questions[i] = questions[j];
            questions[j] = tempQ;


            String[] tempO = options[i];
            options[i] = options[j];
            options[j] = tempO;

            String tempA = correctAnswers[i];
            correctAnswers[i] = correctAnswers[j];
            correctAnswers[j] = tempA;
        }
    }

    public static void takeQuiz() {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String user = input.nextLine();
        int score = 0;
        long quizStart = System.currentTimeMillis();
        long[] questionTime = new long[totalQuestions];

        System.out.println("You have " + QUESTION_TIME_LIMIT + " seconds for each question.");

        for (int i = 0; i < totalQuestions; i++) {
            long questionStart = System.currentTimeMillis();

            System.out.println("\n" + questions[i]);
            for (int j = 0; j < 4; j++) {
                System.out.println(options[i][j]);
            }

            System.out.print("Your answer (A/B/C/D): ");
            String answer = input.nextLine().trim().toUpperCase();
            long questionEnd = System.currentTimeMillis();

            long timeTaken = (questionEnd - questionStart) / 1000; // in seconds
            questionTime[i] = timeTaken;

            if (timeTaken > QUESTION_TIME_LIMIT) {
                System.out.println("Time's up! You took " + formatTime(timeTaken));
                System.out.println("Correct answer was: " + correctAnswers[i]);
            } else if (answer.equals(correctAnswers[i])) {
                score++;
                System.out.println("Correct!");
            } else {
                System.out.println("Wrong! Correct answer was: " + correctAnswers[i]);
            }
        }

        long quizEnd = System.currentTimeMillis();
        long totalTime = (quizEnd - quizStart) / 1000;

        double percent = ((double) score / totalQuestions) * 100.0;
        System.out.println("\n--- Quiz Report ---");
        System.out.println("User: " + user);
        System.out.println("Score: " + score + "/" + totalQuestions);
        System.out.printf("Percentage: %.2f%%\n", percent);
        System.out.println("Total time: " + formatTime(totalTime));

        // Save report
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(PERFORMANCE_FILE), true))) {
            writer.println("User: " + user + ", Score: " + score + "/" + totalQuestions +
                    ", Time: " + formatTime(totalTime));
        } catch (IOException e) {
            System.out.println("Could not save performance: " + e.getMessage());
        }
    }


    public static String formatTime(long seconds) {
        if (seconds < 60) {
            return seconds + " seconds";
        } else {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            return minutes + " minutes " + remainingSeconds + " seconds";
        }
    }
}