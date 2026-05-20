package lilit.hakobyan.olympmathmentor;

public class Classroom {
    private String classId;
    private String className;
    private String classCode;
    private String teacherId;

    public Classroom() {
        // Դատարկ կոնստրուկտոր Firebase-ի համար
    }

    public Classroom(String classId, String className, String classCode, String teacherId) {
        this.classId = classId;
        this.className = className;
        this.classCode = classCode;
        this.teacherId = teacherId;
    }

    public String getClassId() { return classId; }
    public String getClassName() { return className; }
    public String getClassCode() { return classCode; }
    public String getTeacherId() { return teacherId; }
}