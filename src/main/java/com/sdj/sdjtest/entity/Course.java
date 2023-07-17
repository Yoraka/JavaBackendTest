package com.sdj.sdjtest.entity;
@lombok.Data
@lombok.ToString
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class Course {
    private Long id;
    private String courseName;
    private String teacherName;
    private int state;
    private String courseType;
    private String payType;
}
