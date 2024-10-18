package fr.istic.vv;

public class TCCResult {
    private String packageName;
    private String className;
    private double tccValue;

    public TCCResult(String packageName, String className, double tccValue) {
        this.packageName = packageName;
        this.className = className;
        this.tccValue = tccValue;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public double getTccValue() {
        return tccValue;
    }
}
