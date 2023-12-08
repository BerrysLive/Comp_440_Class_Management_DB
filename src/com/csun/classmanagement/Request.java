package com.csun.classmanagement;


public class Request implements Comparable<Request> {

  private long requestId;
  private long buildingId;
  private long classroomId;
  private java.sql.Time startTime;
  private java.sql.Time endTime;
  private String equipment;

  //Getters Setters

  public long getRequestId() {
    return requestId;
  }

  public void setRequestId(long requestId) {
    this.requestId = requestId;
  }


  public long getBuildingId() {
    return buildingId;
  }

  public void setBuildingId(long buildingId) {
    this.buildingId = buildingId;
  }


  public long getClassroomId() {
    return classroomId;
  }

  public void setClassroomId(long classroomId) {
    this.classroomId = classroomId;
  }


  public java.sql.Time getStartTime() {
    return startTime;
  }

  public void setStartTime(java.sql.Time startTime) {
    this.startTime = startTime;
  }


  public java.sql.Time getEndTime() {
    return endTime;
  }

  public void setEndTime(java.sql.Time endTime) {
    this.endTime = endTime;
  }


  public String getEquipment() {
    return equipment;
  }

  public void setEquipment(String equipment) {
    this.equipment = equipment;
  }

  @Override
  public int compareTo(Request other) {
    return this.startTime.compareTo(other.startTime);
  }



}
