package com.csun.classmanagement;


public class Department {

  private long departmentId;
  private String departmentName;
  private String dean;
  private long buildingId;


  public long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(long departmentId) {
    this.departmentId = departmentId;
  }


  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }


  public String getDean() {
    return dean;
  }

  public void setDean(String dean) {
    this.dean = dean;
  }


  public long getBuildingId() {
    return buildingId;
  }

  public void setBuildingId(long buildingId) {
    this.buildingId = buildingId;
  }

}
