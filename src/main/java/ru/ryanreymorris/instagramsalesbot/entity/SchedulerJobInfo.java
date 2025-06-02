package ru.ryanreymorris.instagramsalesbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity(name = "SchedulerJobInfo")
@Table(name = "scheduler_job_info")
public class SchedulerJobInfo {

    @Id
    private String jobClass;
    private String jobGroup;
    private String cronExpression;

    public SchedulerJobInfo() {
    }

    public SchedulerJobInfo(String jobClass, String jobGroup) {
        this.jobClass = jobClass;
        this.jobGroup = jobGroup;
    }

    public SchedulerJobInfo(String jobClass, String jobGroup, String cronExpression) {
        this.jobClass = jobClass;
        this.jobGroup = jobGroup;
        this.cronExpression = cronExpression;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchedulerJobInfo that = (SchedulerJobInfo) o;
        return Objects.equals(jobClass, that.jobClass) && Objects.equals(jobGroup, that.jobGroup) && Objects.equals(cronExpression, that.cronExpression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobClass, jobGroup, cronExpression);
    }

    @Override
    public String toString() {
        return "SchedulerJobInfo{" +
                "jobClass='" + jobClass + '\'' +
                ", jobGroup='" + jobGroup + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                '}';
    }
}
