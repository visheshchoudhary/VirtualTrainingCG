package entity;

import jakarta.persistence.*;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @ManyToOne
    private Department department;

    public Student() {
    }

    public Student(String name){
        this.name=name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department){
        this.department=department;
    }
}
