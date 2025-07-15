document.getElementById("courseForm").addEventListener("submit", function(e) {
        e.preventDefault();

        const courseData = {
            courseid: document.getElementById("courseid").value,
            coursename: document.getElementById("coursename").value,
            duration: parseInt(document.getElementById("duration").value),
        };

        fetch("/api/courses/add", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(courseData),
        })
        .then(response => response.json())
        .then(data => {
			alert("course added successfully");
            document.getElementById("message").textContent = "Course added successfully!";
            document.getElementById("courseForm").reset();
            fetchCourses(); // refresh the course list
        })
        .catch(error => {
            console.error("Error adding course:", error);
            document.getElementById("message").textContent = "Error adding course.";
        });
    });

    function fetchCourses() {
        fetch("/api/courses/all")
            .then(response => response.json())
            .then(data => {
                const tbody = document.getElementById("courseTableBody");
                tbody.innerHTML = "";

                data.forEach(course => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <td>${course.courseid}</td>
                        <td>${course.coursename}</td>
                        <td>${course.duration}</td>
                        <td><button class="delete-btn" onclick="deleteCourse('${course.courseid}')">Delete</button></td>
                    `;
                    tbody.appendChild(row);
                });
            });
    }

    function deleteCourse(courseid) {
        if (!confirm("Are you sure you want to delete this course?")) return;

        fetch(`/api/courses/delete/${courseid}`, {
            method: "DELETE"
        })
        .then(response => {
            if (response.ok) {
                document.getElementById("message").textContent = "Course deleted successfully!";
                fetchCourses(); // refresh the table
            } else {
                throw new Error("Delete failed");
            }
        })
        .catch(error => {
            console.error("Error deleting course:", error);
            document.getElementById("message").textContent = "Failed to delete course.";
        });
    }


    // Load existing courses on page load
    window.onload = fetchCourses;