# ExceptionLab: ICT261 Unit 4 (Java Exception Handling)

Console importer that reads student marks from `marks.txt` and reports bad records clearly instead of crashing.

**Student:** Hope Lishiko (202508681)
**University:** Mulungushi University, School of Engineering & Technology

## Files

- `src/main/java/MarksImporter.java`: importer, `main()`, and the custom `DuplicateStudentException`
- `marks.txt`: sample input
- `Answers.md`: Activity A and D answers and T1 to T5 results
- `import.log`: technical log written when the program runs

## How to run

Open the project in IntelliJ, open `MarksImporter.java`, and click the green Run button beside `main`. The file `marks.txt` must stay in the project root, beside `build.gradle.kts`.

## Decision: skip or stop on an invalid record?

I chose to skip an invalid record and continue the import. A single bad line, such as a non-numeric mark, an out-of-range mark, or a duplicate student number, should not throw away all the valid marks in the file. Each bad record is caught inside the loop, logged with its exception type and cause, reported to the user in plain language, and then skipped. A missing file is different: there is nothing to import, so that error stops the import and is handled in `main()` with a friendly message. The `finally` block always prints "Import attempt finished".