import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class LectureManager {

    public static class Laboratory {
        private int crn;
        private String roomNumber;

        public Laboratory(int crn, String roomNumber) {
            this.crn = crn;
            this.roomNumber = roomNumber;
        }

        public int getCrn() {
            return crn;
        }

        public String getRoomNumber() {
            return roomNumber;
        }
    }

    public static class Lecture {
        private int crn;
        private boolean isOnline;
        private String prefix;
        private String buildingNumber;
        private String roomNumber;
        private String title;
        private String type;
        private ArrayList<Laboratory> labs = new ArrayList<>();

        public Lecture(int crn, String prefix, String title, String type, String buildingNumber, String roomNumber) {
            this.crn = crn;
            this.prefix = prefix;
            this.title = title;
            this.type = type;
            this.buildingNumber = buildingNumber;
            this.roomNumber = roomNumber;
            this.isOnline = "ONLINE".equalsIgnoreCase(buildingNumber);
        }

        public void addLab(Laboratory lab) {
            labs.add(lab);
        }

        public boolean isOnline() {
            return isOnline;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public ArrayList<Laboratory> getLabs() {
            return labs;
        }

        public int getCrn() {
            return crn;
        }

        @Override
        public String toString() {
            if (isOnline) {
                return String.format("%d, %s, %s, %s, Online", crn, prefix, title, type);
            } else {
                return String.format("%d, %s, %s, %s, %s, %s", crn, prefix, title, type, buildingNumber, roomNumber);
            }
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        ArrayList<Lecture> lectures = readLectures("lec.txt");
        int numOnline = 0;

        for (Lecture lecture : lectures) {
            if (lecture.isOnline()) {
                numOnline++;
            }
        }

        System.out.printf("There are %d online lectures offered.\n", numOnline);
        System.out.print("Enter Room Number: ");
        String roomNumber = input.nextLine();

        for (Lecture lecture : lectures) {
            if (lecture.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                System.out.println(lecture);
                for (Laboratory lab : lecture.getLabs()) {
                    if (lab.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                        System.out.println("Lab CRN: " + lab.getCrn());
                    }
                }
            }
        }
        input.close();

        writeLecturesToFile(lectures, "lecturesOnly.txt");
    }

    public static ArrayList<Lecture> readLectures(String fileName) {
        ArrayList<Lecture> lectures = new ArrayList<>();
        File inputFile = new File(fileName);

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            Lecture lastLecture = null;

            while ((line = br.readLine()) != null) {
                String[] fields = line.trim().split(",");

                if (fields.length >= 6) { // Expecting at least 6 fields for a lecture
                    int crn = Integer.parseInt(fields[0].trim());
                    String prefix = fields[1].trim();
                    String title = fields[2].trim();
                    String type = fields[3].trim();
                    String buildingCode = fields[4].trim();
                    String roomNumber = fields[5].trim();
                    lastLecture = new Lecture(crn, prefix, title, type, buildingCode, roomNumber);
                    lectures.add(lastLecture);
                } else if (fields.length == 2 && lastLecture != null) { // Laboratory associated with the last lecture
                    int crn = Integer.parseInt(fields[0].trim());
                    String labRoomNumber = fields[1].trim();
                    lastLecture.addLab(new Laboratory(crn, labRoomNumber));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return lectures;
    }

    public static void writeLecturesToFile(ArrayList<Lecture> lectures, String outputFileName) {
        try (PrintWriter writer = new PrintWriter(outputFileName)) {
            writer.println("Online Lectures:");
            for (Lecture lecture : lectures) {
                if (lecture.isOnline()) {
                    writer.println                    lecture + "\n");
                }
            }
            writer.println("\nLectures without Labs:");
            for (Lecture lecture : lectures) {
                if (!lecture.isOnline() && lecture.getLabs().isEmpty()) {
                    writer.println(lecture + "\n");
                }
            }
            System.out.printf("%s has been created.\n", outputFileName);
        } catch (FileNotFoundException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}