package it.polimi.ingsw.server.model;

import java.util.*;

public enum Color {

    /*
    GREEN("green"),
    RED("red"),
    YELLOW("yellow"),
    PINK("pink"),
    BLUE("blue");
    */

    GREEN("\u001b[32m", "green", "/images/students/student_green.png", "/images/professors/professor_green.png"),
    RED("\u001b[31m", "red", "/images/students/student_red.png", "/images/professors/professor_red.png"),
    YELLOW("\u001b[33m", "yellow", "/images/students/student_yellow.png", "/images/professors/professor_yellow.png"),
    PINK("\u001b[35m", "pink", "/images/students/student_pink.png", "/images/professors/professor_pink.png"),
    BLUE("\u001b[34m", "blue", "/images/students/student_blue.png", "/images/professors/professor_blue.png");

    public final String ansi;
    public final String label;

    public final String student;

    public final String professor;

    Color(String ansi , String label, String student, String professor) {
        this.ansi = ansi;
        this.label = label;
        this.student = student;
        this.professor = professor;
    }

    public static Optional<Color> getColorByLabel(String value) {
        return Arrays.stream(Color.values())
                .filter(color -> color.label.equals(value))
                .findFirst();
    }

    public static Optional<Color> getColorByStudentPath(String value) {
        return Arrays.stream(Color.values())
                .filter(color -> value.contains(color.student))
                .findFirst();
    }

    private static final List<Color> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Color getRandom()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}