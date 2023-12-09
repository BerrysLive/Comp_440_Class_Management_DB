package com.csun.classmanagement;


public class Courses {

  private long courseId;
  private long instructorId;
  private String courseTitle;
  private String subject;


  public long getCourseId() {
    return courseId;
  }

  public void setCourseId(long courseId) {
    this.courseId = courseId;
  }

  public long getInstructorId() {
    return instructorId;
  }

  public void setInstructorId(long instructorId) {
    this.instructorId = instructorId;
  }

  public String getCourseTitle() {
    return courseTitle;
  }

  public void setCourseTitle(String courseTitle) {
    this.courseTitle = courseTitle;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

}
