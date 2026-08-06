package com.example.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deptName;

    @OneToMany(mappedBy = "department",
            cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();

    public void addEmployee(Employee employee){
        employees.add(employee);
        employee.setDepartment(this);
    }

    public void removeEmployee(Employee employee){
        employees.remove(employee);
        employee.setDepartment(null);
    }

    public Long getId() {
        return id;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}
