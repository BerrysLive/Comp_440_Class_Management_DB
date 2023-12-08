package com.csun.classmanagement;


public class Instructor {

  private long instructorId;
  private long departmentId;
  private String name;


  public long getInstructorId() {
    return instructorId;
  }

  public void setInstructorId(long instructorId) {
    this.instructorId = instructorId;
  }


  public long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(long departmentId) {
    this.departmentId = departmentId;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
