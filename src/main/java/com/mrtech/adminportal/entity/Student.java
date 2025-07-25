		package com.mrtech.adminportal.entity;
		
		import jakarta.persistence.Entity;
		import jakarta.persistence.GeneratedValue;
		import jakarta.persistence.GenerationType;
		import jakarta.persistence.Id;
		import jakarta.persistence.Table;
		
		@Entity
		@Table(name = "student")
		public class Student {
		    @Id
		    @GeneratedValue(strategy = GenerationType.IDENTITY)
		    private int id;
		    private String name;
		    private String email;
		    private String mobile;
		    private String dob;
		    private String gender;
		    private String address;
		    private String course;
		    private String qualification;
			private String duration;
		    private String joiningDate;
		    private String batch;
		    private String coursefee;
		    private String discount;
		    private String totalfee;
		    private String term_1;  
		    private String duefee;
		    // Getters and Setters //Term-I
		
		    public int getId() {
		        return id;
		    }
		
		    public void setId(int id) {
		        this.id = id;
		    }
		
		    public String getName() {
		        return name;
		    }
		
		    public void setName(String name) {
		        this.name = name;
		    }
		
		    public String getEmail() {
		        return email;
		    }
		
		    public void setEmail(String email) {
		        this.email = email;
		    }
		
		    public String getMobile() {
		        return mobile;
		    }
		
		    public void setMobile(String mobile) {
		        this.mobile = mobile;
		    }
		
		    public String getDob() {
		        return dob;
		    }
		
		    public void setDob(String dob) {
		        this.dob = dob;
		    }
		
		    public String getGender() {
		        return gender;
		    }
		
		    public void setGender(String gender) {
		        this.gender = gender;
		    }
		
		    public String getAddress() {
		        return address;
		    }
		
		    public void setAddress(String address) {
		        this.address = address;
		    }
		
		    public String getCourse() {
		        return course;
		    }
		
		    public void setCourse(String course) {
		        this.course = course;
		    }
		    
		    public String getQualification() {
				return qualification;
			}
		
			public void setQualification(String qualification) {
				this.qualification = qualification;
			}
		
		    public String getDuration() {
		        return duration;
		    }
		
		    public void setDuration(String duration) {
		        this.duration = duration;
		    }
		
		    public String getJoiningDate() {
		        return joiningDate;
		    }
		
		    public void setJoiningDate(String joiningDate) {
		        this.joiningDate = joiningDate;
		    }
		    public String getBatch() {
		        return batch;
		    }
		
		    public void setBatch(String batch) {
		        this.batch = batch;
		    }
		    public String getCoursefee() {
		        return coursefee;
		    }
		
		    public void setCoursefee(String coursefee) {
		        this.coursefee = coursefee;
		    }
		    public String getDiscount() {
		        return discount;
		    }
		
		    public void setDiscount(String discount) {
		        this.discount = discount;
		    }
		    public String getTotalfee() {
		        return totalfee;
		    }
		
		    public void setTotalfee(String totalfee) {
		        this.totalfee = totalfee;
		    }
		    public String getTerm_1() {
		        return term_1;
		    }
		
		    public void setTerm_1(String term_1) {
		        this.term_1 = term_1;
		    }
		    public String getDuefee() {
		        return duefee;
		    }
		
		    public void setDuefee(String duefee) {
		        this.duefee = duefee;
		    }
		
		}
		
