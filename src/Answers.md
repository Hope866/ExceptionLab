# ICT261 Unit 4 Lab: Written Answers

**Name:** Hope Lishiko
**Student number:** 202508681

## Activity A: Predict and Explain

| Statement | Exception | Checked or Unchecked | Prevention / Handling Strategy |
|---|---|---|---|
| `Integer.parseInt("ABC");` | `NumberFormatException` | Unchecked (it extends `IllegalArgumentException`, which extends `RuntimeException`) | Wrap the call in try/catch for `NumberFormatException` and report the bad value, or validate the text before parsing. |
| `String name = null; name.length();` | `NullPointerException` | Unchecked (extends `RuntimeException`) | Check `if (name != null)` before using the object, or make sure the variable is always initialised. |
| `new java.io.FileReader("missing.txt");` | `FileNotFoundException` | Checked (a subclass of `IOException`) | Catch it with try/catch and show a friendly message, or declare `throws IOException`. |

**Why OutOfMemoryError should not normally be caught just to continue execution:**
`OutOfMemoryError` is an `Error`, not an `Exception`. It means the JVM has run out of memory, so the program is already unstable and may fail again, even inside the catch block. The right fix is to remove the cause (for example a memory leak or loading too much data) and let the program stop safely.

## Activity C: Test Results

Run with `marks.txt` containing the five lines above.

**T1 to T4 console output:**

```
Accepted: ICT001 -> 78
Line 2 skipped: the mark is not a number.
Line 3 skipped: duplicate student ICT001.
Accepted: ICT003 -> 90
Line 5 skipped: invalid record (wrong format or mark out of range 0-100).
Import attempt finished
```

**T5 console output** (filename changed to `absent.txt`):

```
Marks file not found; check the file name.
Import attempt finished
```

| Test | Input | Result | Done |
|---|---|---|---|
| T1 | `ICT001, 78` | Accepted | ☑ |
| T2 | `ICT002, ABC` | Reported safely and skipped | ☑ |
| T3 | `ICT001, 65` after `ICT001, 78` | Duplicate identified and skipped | ☑ |
| T4 | `ICT004, 120` | Out-of-range mark rejected | ☑ |
| T5 | filename `absent.txt` | Friendly message, `finally` ran | ☑ |

Technical details (exception type and cause) are written to `import.log`.

## Activity D: Engineer's Review

**1. Which of your exceptions is checked, and where does `throws` appear? Why?**
`IOException` (and its subclass `FileNotFoundException`) is checked. `throws IOException` appears on the `importMarks` method. A file can be missing or unreadable for reasons outside my program's control, so Java forces the caller to handle it. My `DuplicateStudentException` is unchecked because it extends `RuntimeException`. It is a data problem, so it needs no `throws`.

**2. Difference between `throw` and `throws`:**
`throw` actually raises an exception: `throw new DuplicateStudentException(studentId);`
`throws` only declares that a method may pass an exception to its caller: `static void importMarks(String filename) throws IOException {`

**3. Why a friendly message for the user but technical details in the logger?**
Ordinary users cannot understand stack traces, and raw errors look alarming and confusing. They can also reveal internal details such as file paths and class names. A friendly message tells the user what to do next, while the logger keeps the exact exception type and cause so a developer can find and fix the problem.

**4. What goes wrong with an empty catch block, or with catching `Exception` before `FileNotFoundException`?**
An empty catch block swallows the error silently. The program carries on with missing or bad data, nobody is told, and the bug becomes very hard to trace. If `Exception` is caught before `FileNotFoundException`, the broad catch handles everything first, so the specific catch can never run. Java reports this as a compile error, and the friendly file-not-found message would never be shown.