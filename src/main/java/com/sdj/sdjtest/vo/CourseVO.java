package com.sdj.sdjtest.vo;
@lombok.Data
@lombok.ToString
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class CourseVO {
    private Long id;
    private String courseName;
    private String teacherName;
    private int state;
    private String courseType;
    private String payType;
}
