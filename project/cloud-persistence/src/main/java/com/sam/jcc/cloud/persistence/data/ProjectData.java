package com.sam.jcc.cloud.persistence.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.sam.jcc.cloud.i.Experimental;

import lombok.Data;
import lombok.ToString;

/**
 * @author Alexey Zhytnik
 * @since 16.01.2017
 */
@Data
@Entity
@ToString(exclude = "sources")
@Experimental("Entity of all project data")
public class ProjectData {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "groupId")
	private String groupId;

	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@Column(name = "type", nullable = false)
	private String type = "none";

	@Column(name = "jobName", unique = true)
	private String jobName;

	@Column(name = "ci")
	private String ci;

	@Column(name = "vcs", unique = true)
	private String vcs;

	@Column(name = "db")
	private Boolean dataSupport = false;

	@Lob
	@Column(columnDefinition = "BLOB")
	private byte[] sources;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
}
