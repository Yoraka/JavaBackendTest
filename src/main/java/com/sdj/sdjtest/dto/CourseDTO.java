package com.sdj.sdjtest.dto;
@lombok.Data
@lombok.ToString
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class CourseDTO {
    private Long id;
    private String courseName;
    private String teacherName;
    private int state;
    private String courseType;
    private String payType;
}
