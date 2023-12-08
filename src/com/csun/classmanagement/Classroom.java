package com.csun.classmanagement;


public class Classroom {

  private int classroomId;
  private int buildingId;
  private String roomNum;
  private int capacity;
  private String equipment;
  private java.sql.Time blackoutStart;
  private java.sql.Time blackoutEnd;

  public Classroom(int classroomId, int buildingId, String roomNum, int capacity, String equipment, java.sql.Time blackoutStart, java.sql.Time blackoutEnd) {
    this.classroomId = classroomId;
    this.buildingId = buildingId;
    this.roomNum = roomNum;
    this.capacity = capacity;
    this.equipment = equipment;
    // Convert LocalTime to java.sql.Time for blackoutStart and blackoutEnd
    this.blackoutStart = blackoutStart != null ? java.sql.Time.valueOf(blackoutStart.toLocalTime()) : null;
    this.blackoutEnd = blackoutEnd != null ? java.sql.Time.valueOf(blackoutEnd.toLocalTime()) : null;
  }




  public int getClassroomId() {
    return classroomId;
  }

  public void setClassroomId(int classroomId) {
    this.classroomId = classroomId;
  }


  public int getBuildingId() {
    return buildingId;
  }

  public void setBuildingId(int buildingId) {
    this.buildingId = buildingId;
  }


  public String getRoomNum() {
    return roomNum;
  }

  public void setRoomNum(String roomNum) {
    this.roomNum = roomNum;
  }


  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }


  public String getEquipment() {
    return equipment;
  }

  public void setEquipment(String equipment) {
    this.equipment = equipment;
  }


  public java.sql.Time getBlackoutStart() {
    return blackoutStart;
  }

  public void setBlackoutStart(java.sql.Time blackoutStart) {
    this.blackoutStart = blackoutStart;
  }


  public java.sql.Time getBlackoutEnd() {
    return blackoutEnd;
  }

  public void setBlackoutEnd(java.sql.Time blackoutEnd) {
    this.blackoutEnd = blackoutEnd;
  }

  public int getId() {
    return classroomId;
  }
}
