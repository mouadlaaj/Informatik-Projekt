import React, { useState, useEffect } from "react";
import { TextField, IconButton, MenuItem } from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import { deleteBugById } from "../../service/service-call";
import { toast } from "react-toastify";
import { Button } from "@mui/joy";
function BugReportForm({ taskId, reportBugs, onBugReported, bug = [] }) {
  const [bugs, setBugs] = useState([]);
  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    if (bug.length > 0) {
      setBugs(
        bug.map((b) => ({
          id: b.id || null,
          title: b.title || "",
          description: b.description || "",
          severity: b.severity || "",
        }))
      );
      setShowForm(true);
    }
  }, [bug]);

  const handleAddBug = () => {
    setShowForm(true);
    setBugs([...bugs, { id: null, title: "", description: "", severity: "" }]);
  };

  const handleChange = (index, field, value) => {
    const updatedBugs = [...bugs];
    updatedBugs[index][field] = value;
    setBugs(updatedBugs);
  };

  const handleRemoveBug = async (index) => {
    const bugToDelete = bugs[index];

    if (bugToDelete.id) {
      try {
        await deleteBugById(taskId, bugToDelete.id);
        toast.success("Bug deleted successfully");
      } catch (error) {
        console.error("Failed to delete bug:", error.response?.data || error.message);
        toast.error(error?.response?.data?.message);
        return;
      }
    }

    const updatedBugs = [...bugs];
    updatedBugs.splice(index, 1);
    setBugs(updatedBugs);
  };


  const handleBugSubmit = async () => {
    const allFieldsFilled = bugs.every(
      (bug) => bug.title.trim() && bug.description.trim() && bug.severity.trim()
    );

    if (!allFieldsFilled) {
      toast("Please fill in all fields for each bug");
      return;
    }

    const formData = {
      taskId,
      bugs: bugs.map((bug) => ({
        id: bug.id,
        title: bug.title.trim(),
        description: bug.description.trim(),
        severity: bug.severity.trim(),
      })),
    };

    try {
      await reportBugs(formData);
      toast.success("Bugs reported successfully!");
      setBugs([]);
      setShowForm(false);
      if (onBugReported) {
        onBugReported();
      }
    } catch (error) {
      toast.error(error?.response?.data?.message);
      console.error("API Error:", error.response?.data || error.message);
    }
  };

  return (
    <div style={{ padding: 16, maxWidth: 600 }}>
      {showForm &&
        bugs.map((bug, index) => (
          <div
            key={index}
            style={{
              marginBottom: 20,
              borderBottom: "1px solid #ccc",
              paddingBottom: 10,
            }}
          >
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                gap: 12,
              }}
            >
              <TextField
                size="small"
                label="Title"
                variant="outlined"
                value={bug.title}
                onChange={(e) => handleChange(index, "title", e.target.value)}
                style={{ flex: 1 }}
                margin="normal"
              />
              <TextField
                size="small"
                label="Severity"
                variant="outlined"
                select
                value={bug.severity}
                onChange={(e) => handleChange(index, "severity", e.target.value)}
                style={{ width: 150 }}
                margin="normal"
              >
                <MenuItem value="HIGH">High</MenuItem>
                <MenuItem value="MEDIUM">Medium</MenuItem>
                <MenuItem value="LOW">Low</MenuItem>
              </TextField>

              <IconButton
                onClick={() => handleRemoveBug(index)}
                size="small"
                color="error"
                style={{ marginTop: 8 }}
              >
                <DeleteIcon />
              </IconButton>
            </div>
            <TextField
              size="small"
              fullWidth
              label="Description"
              variant="outlined"
              value={bug.description}
              onChange={(e) => handleChange(index, "description", e.target.value)}
              margin="normal"
              multiline
              rows={3}
            />
          </div>
        ))}

      <div
        style={{ display: "flex", justifyContent: "space-between", marginTop: 16 }}
      >
        <Button
          variant="soft"
          size="small"
          onClick={handleBugSubmit}
          disabled={bugs.length === 0}
          style={{ width: "48%", height: "36px" }}
        >
          Save All Bugs
        </Button>

        <Button
          variant="soft"
          size="small"
          style={{ width: "48%" }}
          onClick={handleAddBug}
        >
          Add Bug
        </Button>
      </div>
    </div>
  );
}

export default BugReportForm;
