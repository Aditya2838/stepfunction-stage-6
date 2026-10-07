package com.example.batchdemo.domain;

public class SourceRecord {

    private Integer id;
    private String name;
    private Integer age;
    private String recordType; // PMA / CHARMS / PRICING

    public SourceRecord() {
    }

    public SourceRecord(
            Integer id,
            String name,
            Integer age,
            String recordType) {

        this.id = id;
        this.name = name;
        this.age = age;
        this.recordType = recordType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    @Override
    public String toString() {
        return "SourceRecord{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", recordType='" + recordType + '\'' +
                '}';
    }
}