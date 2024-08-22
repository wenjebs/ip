import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class WenJigglyBot {
    static List<Task> tasks = new ArrayList<>(100);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = "WenJigglyBot";
        System.out.println("Sup im " + name);
        System.out.println("What can I do for you?");
        String task;
        boolean flag = true;
        while (flag) {
            task = scanner.nextLine();
            task = task.trim();
            Command command = null;
            try {
                command = parseCommand(task);
            } catch (InvalidCommandException e) {
                System.out.println(e);
                continue;
            }

            String[] strings;
            String action;
            int idx;
            String taskName;

            switch (Objects.requireNonNull(command)) {
                case LIST:
                    displayTasks();
                    break;
                case MARK:
                    strings = task.split(" ");
                    action = "mark";
                    idx = Integer.parseInt(strings[1].trim()) - 1;
                    toggleTask(action, idx);
                    break;
                case UNMARK:
                    action = "unmark";
                    strings = task.split(" ");
                    idx = Integer.parseInt(strings[1].trim()) - 1;
                    toggleTask(action, idx);
                    break;
                case TODO:
                    taskName = task.replaceFirst("todo", "").trim();
                    addTask(new ToDoTask(taskName));
                    break;
                case DEADLINE:
                    try {
                        String[] parts = processDeadlineTask(task);
                        taskName = parts[0].trim();
                        String deadline = parts[1].trim();
                        addTask(new DeadlineTask(taskName, deadline));
                    } catch (DeadlineException deadlineException) {
                        System.out.println(deadlineException);
                    }
                    break;
                case EVENT:
                    // Split the string by "/from" and "/to"
                    try {
                        String[] processedEvent = processEventTask(task);
                        addTask(new EventTask(processedEvent[0], processedEvent[1], processedEvent[2]));
                    } catch (EventException eventException) {
                        System.out.println(eventException);
                    }
                    break;
                case DELETE:
                    strings = task.split(" ");
                    idx = Integer.parseInt(strings[1].trim()) - 1;
                    deleteTask(idx);
                    break;
                case BYE:
                    flag = false;
                    break;

            }
        }
        System.out.println("Goodbye!");
    }

    private static Command parseCommand(String command) throws InvalidCommandException {
        for (Command cmd : Command.values()) {
            if (command.startsWith(cmd.name().toLowerCase())) {
                return cmd;
            }
        }
        throw new InvalidCommandException();
    }

    private static String[] processEventTask(String task) throws EventException {
        String[] fromParts = task.split("/from");
        if (fromParts.length != 2) {
            throw new EventException();
        }
        String[] toParts = fromParts[1].split("/to");
        if (toParts.length != 2) {
            throw new EventException();
        }
        // Extract the event description, start time, and end time
        String event = fromParts[0].replaceFirst("event", "").trim();
        String startTime = toParts[0].trim();
        String endTime = toParts[1].trim();
        return new String[]{event, startTime, endTime};
    }

    private static String[] processDeadlineTask(String task) throws DeadlineException {
        // remove deadline tag
        String taskNameAndDeadline = task.replaceFirst("deadline", "").trim();

        // split the title and deadline
        String[] parts = taskNameAndDeadline.split("/by");
        if (parts.length != 2) {
            throw new DeadlineException();
        }
        return parts;
    }

    private static void deleteTask(int idx) {
        if (idx < 0 || idx > tasks.size() - 1) {
            System.out.println("You entered an invalid index you fool!");
            return;
        }
        System.out.println("____________________________________________________________");
        System.out.println("\tRemoving this task!");
        System.out.println("\t\t" + tasks.get(idx));
        tasks.remove(idx);
        System.out.println("\tYou now have " + tasks.size() + " tasks");
        System.out.println("____________________________________________________________");
    }

    private static void toggleTask(String action, int idx) {
        // handle invalid index
        if (idx < 0 || idx > tasks.size() - 1) {
            System.out.println("You entered an invalid index you fool!");
            return;
        }
        Task task = tasks.get(idx);
        if (action.equals("mark")) {
            System.out.println("____________________________________________________________");
            System.out.println("\tYay! Task Completed!");
            task.markTask();
            System.out.println("\t" + task);
            System.out.println("____________________________________________________________");
        } else {
            System.out.println("____________________________________________________________");
            System.out.println("\tGet to work boy, why not done!!!");
            tasks.get(idx).unmarkTask();
            System.out.println("\t" + task);
            System.out.println("____________________________________________________________");
        }
    }

    private static void addTask(Task task) {
        tasks.add(task);
        System.out.println("____________________________________________________________");
        System.out.printf("\tAdding %s\n", task.taskType());
        System.out.printf("\tDone! Added: %s\n", task.getDescription());
        System.out.printf("You now have %d tasks!\n", tasks.size());
        System.out.println("____________________________________________________________");
    }

    private static void displayTasks() {
        System.out.println("____________________________________________________________");
        System.out.println("Here are your tasks :)");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf("\t%d. %s%n", i + 1, tasks.get(i).toString());
        }
        System.out.println("____________________________________________________________");
    }
}
