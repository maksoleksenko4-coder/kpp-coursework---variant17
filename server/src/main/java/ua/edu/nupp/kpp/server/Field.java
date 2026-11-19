package ua.edu.nupp.kpp.server;

public class Field {
    private Long id;
    private String name;
    private String cropType;
    private double area;

    public Field() {
    }

    public Field(Long id, String name, String cropType, double area) {
        this.id = id;
        this.name = name;
        this.cropType = cropType;
        this.area = area;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }
    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }
}