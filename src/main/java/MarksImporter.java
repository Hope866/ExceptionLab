import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * ICT261 Unit 4 Lab: Exception Handling and Error Management
 * Author: Hope Lishiko (202508681)
 *
 * Recovery strategy: a bad RECORD is skipped (logged + reported) and the import
 * continues. A missing FILE stops the import, because there is nothing to read.
 */
public class MarksImporter {

    private static final Logger LOGGER = Logger.getLogger(MarksImporter.class.getName());
    private static final String FILE_NAME = "marks.txt"; // change to "absent.txt" for test T5

    // Technical details go to import.log; the console only shows friendly messages.
    private static void setupLogger() {
        LOGGER.setUseParentHandlers(false); // stop the default console output (no raw traces)
        try {
            FileHandler handler = new FileHandler("import.log", true);
            handler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(handler);
        } catch (IOException e) {
            System.out.println("Warning: could not create the log file.");
        }
    }

    // "throws" declares that a checked IOException may escape this method.
    static void importMarks(String filename) throws IOException {
        Set<String> seen = new HashSet<>();
        int lineNumber = 0;

        // try-with-resources: the BufferedReader is closed automatically when the
        // block ends, whether it finishes normally or an exception occurs, so no
        // file handle is leaked and no manual close() is needed.
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue; // ignore blank lines
                }

                try {
                    String[] parts = line.split(",");

                    // TODO 1: exactly 2 fields
                    if (parts.length != 2) {
                        throw new IllegalArgumentException(
                                "Expected 2 fields but found " + parts.length);
                    }

                    String studentId = parts[0].trim();

                    // TODO 2: parse mark and validate range 0..100
                    int mark = Integer.parseInt(parts[1].trim()); // may throw NumberFormatException
                    if (mark < 0 || mark > 100) {
                        throw new IllegalArgumentException(
                                "Mark must be between 0 and 100 but was " + mark);
                    }

                    // TODO 3: detect repeated student ID
                    if (!seen.add(studentId)) { // add() returns false if already present
                        throw new DuplicateStudentException(studentId);
                    }

                    // TODO 4: display accepted record
                    System.out.println("Accepted: " + studentId + " -> " + mark);

                } catch (NumberFormatException e) {
                    // Must come BEFORE IllegalArgumentException (it is a subclass of it)
                    LOGGER.log(Level.WARNING, "Line " + lineNumber + " skipped: "
                            + e.getClass().getSimpleName() + " - " + e.getMessage());
                    System.out.println("Line " + lineNumber + " skipped: the mark is not a number.");
                } catch (DuplicateStudentException e) {
                    LOGGER.log(Level.WARNING, "Line " + lineNumber + " skipped: "
                            + e.getClass().getSimpleName() + " - " + e.getMessage());
                    System.out.println("Line " + lineNumber + " skipped: duplicate student "
                            + e.getStudentId() + ".");
                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.WARNING, "Line " + lineNumber + " skipped: "
                            + e.getClass().getSimpleName() + " - " + e.getMessage());
                    System.out.println("Line " + lineNumber + " skipped: invalid record (wrong format or mark out of range 0-100).");
                }
            }
        }
    }

    public static void main(String[] args) {
        setupLogger();
        try {
            importMarks(FILE_NAME);
        } catch (FileNotFoundException e) { // must come BEFORE IOException (its parent)
            LOGGER.log(Level.SEVERE, e.getClass().getSimpleName() + " - " + e.getMessage());
            System.out.println("Marks file not found; check the file name.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getClass().getSimpleName() + " - " + e.getMessage());
            System.out.println("Could not read the marks file. Please try again.");
        } catch (DuplicateStudentException | IllegalArgumentException e) {
            // Safety net: normally these are handled per record inside importMarks.
            LOGGER.log(Level.WARNING, e.getClass().getSimpleName() + " - " + e.getMessage());
            System.out.println("Invalid data found: " + e.getMessage());
        } finally {
            System.out.println("Import attempt finished");
        }
    }
}

// Custom unchecked exception for repeated student numbers
class DuplicateStudentException extends RuntimeException {
    private final String studentId;

    public DuplicateStudentException(String studentId) {
        super("Duplicate student number: " + studentId);
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }
}