import {
  FormControl,
  Grid,
  Input,
  InputLabel,
  TextField,
  OutlinedInput,
  MenuItem,
  Select,
  ListItemText,
  FormLabel,
  Divider,
  Chip,
  Slider,
} from "@mui/material";
import Button from "@mui/material/Button";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import AssignmentIcon from "@mui/icons-material/Assignment";
import PunchClockIcon from "@mui/icons-material/PunchClock";
import DescriptionIcon from "@mui/icons-material/Description";
import { FaClipboardUser } from "react-icons/fa6";
import { RiLockPasswordFill } from "react-icons/ri";
import { HiUserGroup } from "react-icons/hi2";
import Box from "@mui/material/Box";
import { useTheme } from "@mui/material/styles";
import MobileStepper from "@mui/material/MobileStepper";
import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";
import KeyboardArrowLeft from "@mui/icons-material/KeyboardArrowLeft";
import KeyboardArrowRight from "@mui/icons-material/KeyboardArrowRight";
import CheckCircleRoundedIcon from "@mui/icons-material/CheckCircleRounded";
import { ToastContainer, toast } from "react-toastify";
import Radio from "@mui/joy/Radio";
import RadioGroup from "@mui/joy/RadioGroup";
import Sheet from "@mui/joy/Sheet";
import * as React from "react";
import Autocomplete from "@mui/material/Autocomplete";
import {
  createTask,
  getAllDesignersByProjectId,
  getAllDevelopersByProjectId,
  getAllMembersByProjectIdAndTeams,
  getAllProject,
  getAllProjectsUnderAdmin,
  getAllTagByProjectId,
  getAllTeamByProjectId,
  getAllTestersByProjectId,
  logout,
  sleep,
  stringAvatar,
} from "../../service/service-call";

import { Editor } from "react-draft-wysiwyg";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import { EditorState } from "draft-js";
import { convertToHTML } from "draft-convert";
import { Avatar, Card } from "@mui/joy";
import { DesktopDateTimePicker } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import moment from "moment";

export default function TaskAdd({ addTasksModal }) {
  const [editorState, setEditorState] = React.useState(() =>
    EditorState.createEmpty()
  );

  const [description, setDescription] = React.useState(null);

  React.useEffect(() => {
    let html = convertToHTML(editorState.getCurrentContent());
    if (html && html.length - 7 <= 3000) {
      console.log(html.length - 7);
      setDescription(html);
    }
  }, [editorState]);

  const priorityObj = [
    {
      label: "High",
      value: "HIGH",
    },
    {
      label: "Medium",
      value: "MEDIUM",
    },
    {
      label: "Low",
      value: "LOW",
    },
  ];
  const [tname, setTName] = React.useState("");
  const [shortDescription, setShortDescription] = React.useState("");
  const [sdate, setSDate] = React.useState("");
  const [edate, setEDate] = React.useState("");
  const [allProjects, setAllProjects] = React.useState([]);
  const [allTags, setAllTags] = React.useState([]);
  const [allMembers, setAllMembers] = React.useState([]);
  const [allDesigners, setAllDesigners] = React.useState([]);
  const [allDevelopers, setAllDevelopers] = React.useState([]);
  const [allTesters, setAllTesters] = React.useState([]);

  const [selectedOption, setSelectedOption] = React.useState("");
  const [selectedTags, setSelectedTags] = React.useState([]);

  const [priority, setPriority] = React.useState("");
  const [complexity, setComplexity] = React.useState("");

  const [selectedDesigner, setSelectedDesigner] = React.useState("");
  const [selectedDeveloper, setSelectedDeveloper] = React.useState("");
  const [selectedTester, setSelectedTester] = React.useState("");

  const [estimationTimeForDesigner, setEstimationTimeForDesigner] =
    React.useState("");
  const [estimationTimeForDeveloper, setEstimationTimeForDeveloper] =
    React.useState("");
  const [estimationTimeForTester, setEstimationTimeForTester] =
    React.useState("");

  function getAllTagByProject(id) {
    getAllTagByProjectId(id)
      .then((resp) => {
        if (resp.status === 401) {
          logout();
        }
        resp.json().then((data) => {
          console.log("TAGS", data);
          setAllTags(data);
        });
      })
      .catch((error) => {
        console.log("login user err " + error);
      });
  }

  function getAllMemberForTeam(proj) {
    getAllMembersByProjectIdAndTeams(proj)
      .then((resp) => {
        let data = resp.data;

        setAllMembers(data);
      })
      .catch((error) => {
        console.log("login user err " + error);
      });
  }

  function getAllDesignersForTeam(proj) {
    getAllDesignersByProjectId(proj)
      .then((resp) => {
        let data = resp.data;
        setAllDesigners(data);
      })
      .catch((error) => {
        console.log("getAllDesigners error: ", error);
      });
  }

  function getAllDevelopersForTeam(proj) {
    getAllDevelopersByProjectId(proj)
      .then((resp) => {
        let data = resp.data;
        setAllDevelopers(data);
      })
      .catch((error) => {
        console.log("getAllDevelopers error: ", error);
      });
  }

  function getAllTestersForTeam(proj) {
    getAllTestersByProjectId(proj)
      .then((resp) => {
        let data = resp.data;
        setAllTesters(data);
      })
      .catch((error) => {
        console.log("getAllTesters error: ", error);
      });
  }

  const extractMemberId = (selectedValue) => {
    if (!selectedValue) return null;

    const idMatch = selectedValue.match(/\(([^)]+)\)$/);
    if (idMatch && idMatch[1]) {
      let extractedId = idMatch[1].trim();

      if (extractedId.startsWith("DESIGNER ")) {
        extractedId = extractedId.replace("DESIGNER ", "");
      }

      return extractedId;
    }

    console.error("Could not extract member ID from:", selectedValue);
    return null;
  };

  const handleDesignerChange = (event, newValue) => {
    setSelectedDesigner(newValue);
  };

  const handleDeveloperChange = (event, newValue) => {
    setSelectedDeveloper(newValue);
  };

  const handleTesterChange = (event, newValue) => {
    setSelectedTester(newValue);
  };

  const handleTaskNameChange = (e) => {
    setTName(e.target.value);
  };

  const handleShortDescriptionChange = (e) => {
    setShortDescription(e.target.value);
  };

  const handleEDateChange = (e) => {
    const localDate = moment(e).local();
    const formattedDate = localDate.format('YYYY-MM-DD HH:mm:ss');
    setEDate(formattedDate);
  };

  const handleSDateChange = (e) => {
    const localDate = moment(e).local();
    const formattedDate = localDate.format('YYYY-MM-DD HH:mm:ss');
    setSDate(formattedDate);
  };

  const handlePriorityChange = (e) => {
    setPriority(e.target.value);
  };

  const ITEM_HEIGHT = 48;
  const ITEM_PADDING_TOP = 8;
  const MenuProps = {
    PaperProps: {
      style: {
        maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
        width: 250,
      },
    },
  };

  function getAllProjects() {
    getAllProjectsUnderAdmin("")
      .then((resp) => {
        if (resp.status === 401) {
          logout();
        }
        resp.json().then((data) => {
          setAllProjects(data);
        });
      })
      .catch((error) => {
        console.log("login user err " + error);
      });
  }

  function validateBasic() {
    if (
      tname === "" ||
      tname === undefined ||
      shortDescription === "" ||
      shortDescription === undefined
    ) {
      toast.error("Fields cannot be blank");
      return false;
    } else {
      getAllProjects();
      return true;
    }
  }

  function validateCredential() {
    if (description !== "" && description.length > 3000) {
      toast.error(
        "Description should be less than or equal to 3000 characters"
      );
      return false;
    }
    return true;
  }

  function validateRange() {
    return true;
  }

  function addNewTask() {
    let arr = [];
    selectedTags.map((r) => {
      let d = allTags.find((e) => e.name === r);
      arr.push(d.id); 
    });

    if (!selectedOption) {
      toast.error("Project cannot be blank");
      return false;
    }

    let assignedTo = [];

    if (selectedDesigner && estimationTimeForDesigner) {
      const designerId = extractMemberId(selectedDesigner);
      console.log("Designer selected:", selectedDesigner);
      console.log("Designer ID extracted:", designerId);
      if (designerId) {
        assignedTo.push({
          memberId: designerId,
          estimatedTime: parseInt(estimationTimeForDesigner),
        });
      }
    }

    if (selectedDeveloper && estimationTimeForDeveloper) {
      const developerId = extractMemberId(selectedDeveloper);
      console.log("Developer selected:", selectedDeveloper);
      console.log("Developer ID extracted:", developerId);
      if (developerId) {
        assignedTo.push({
          memberId: developerId,
          estimatedTime: parseInt(estimationTimeForDeveloper),
        });
      }
    }

    if (selectedTester && estimationTimeForTester) {
      const testerId = extractMemberId(selectedTester);
      console.log("Tester selected:", selectedTester);
      console.log("Tester ID extracted:", testerId);
      if (testerId) {
        assignedTo.push({
          memberId: testerId,
          estimatedTime: parseInt(estimationTimeForTester),
        });
      }
    }

    console.log("Final assignedTo array:", assignedTo);

    createTask(
      tname,
      shortDescription,
      description,
      priority,
      sdate,
      edate,
      selectedOption,
      arr,
      assignedTo,
      complexity
    )
      .then((resp) => {
        toast.success("Task created successfully");
        addTasksModal();
      })
      .catch((error) => {
        if (
          error &&
          error.response &&
          error.response.data &&
          error.response.data.message
        ) {
          toast.error(error.response.data.message);
        } else if (
          error.response &&
          error.response.data &&
          error.response.data.errors &&
          error.response.data.errors.length > 0
        ) {
          toast.error(error.response.data.errors[0]);
        } else {
          toast.error("Internal server error, contact support team");
        }
      });
  }

  const steps = [
    {
      label: "TASK DETAILS",
      icon: <AssignmentIcon sx={{ color: "#0B6BCB", fontSize: "30px" }} />,
      validate: validateBasic,
      style: { width: "400px" },
      description: (
        <>
          <FormControl
            required={true}
            fullWidth
            variant="standard"
            style={{ textAlign: "center" }}
          >
            <TextField
              id="standard-adornment-tname"
              label="Title"
              size="small"
              type={"text"}
              value={tname}
              inputProps={{
                maxlength: 70,
                style: {
                  fontSize: "0.9rem",
                },
              }}
              InputLabelProps={{ style: { fontSize: "0.9rem" } }}
              helperText={`${tname.length}/${70}`}
              onChange={handleTaskNameChange}
            />
          </FormControl>
          <br></br>
          <br></br>
          <FormControl
            required={true}
            fullWidth
            variant="standard"
            style={{ textAlign: "center" }}
          >
            <TextField
              id="standard-adornment-tname"
              label="Short description"
              multiline
              rows={"3"}
              size="small"
              type={"text"}
              inputProps={{
                maxlength: 90,
                style: {
                  fontSize: "0.9rem",
                },
              }}
              InputLabelProps={{ style: { fontSize: "0.9rem" } }}
              helperText={`${shortDescription.length}/${90}`}
              value={shortDescription}
              onChange={handleShortDescriptionChange}
            />
          </FormControl>

          <br></br>
          <br></br>
        </>
      ),
    },
    {
      label: "DETAILED DESCRIPTION",
      icon: <DescriptionIcon sx={{ color: "#0B6BCB", fontSize: "30px" }} />,
      style: { width: "750px" },
      validate: validateCredential,
      description: (
        <>
          <Card style={{ minHeight: "250px", marginTop: "5px", width: "95%" }}>
            <Editor
              wrapperClassName=""
              editorClassName=""
              toolbar={{
                options: [
                  "inline",
                  "blockType",
                  "fontSize",
                  "list",
                  "textAlign",
                  "history",
                  "link",
                ],
                inline: { inDropdown: true },
                list: { inDropdown: true },
                textAlign: { inDropdown: true },
                link: { inDropdown: true },
                history: { inDropdown: true },
              }}
              editorState={editorState}
              onEditorStateChange={setEditorState}
              hashtag={{}}
            />
          </Card>
          <Typography fontSize={"0.8rem"} color={"lightgrey"}>
            {description && description.length - 7}/3000
          </Typography>
          <br></br>
          <br></br>
        </>
      ),
    },
    {
      label: "DUE DATE & COMPLEXITY",
      icon: <PunchClockIcon sx={{ color: "#0B6BCB", fontSize: "30px" }} />,
      validate: validateRange,
      style: { width: "350px" },
      description: (
        <>
          <Card>
            <FormControl>
              <FormLabel style={{ fontSize: "0.9rem" }}>Priority</FormLabel>
              <RadioGroup
                overlay
                name="member"
                value={priority}
                onChange={handlePriorityChange}
                orientation="horizontal"
                sx={{ gap: 2 }}
              >
                {priorityObj.map((num) => (
                  <Sheet
                    component="label"
                    key={num.value}
                    variant="outlined"
                    sx={{
                      p: 2,
                      display: "flex",
                      flexDirection: "column",
                      alignItems: "center",
                      boxShadow: "md",
                      borderRadius: "md",
                    }}
                  >
                    <Radio
                      value={num.value}
                      checkedIcon={<CheckCircleRoundedIcon />}
                      variant="soft"
                      sx={{
                        mb: 2,
                      }}
                    />
                    <Typography
                      level="body-sm"
                      sx={{ mt: 1, fontSize: "0.9rem" }}
                    >
                      {num.label}
                    </Typography>
                  </Sheet>
                ))}
              </RadioGroup>
            </FormControl>
          </Card>
          <br></br>
          <Card>
            <FormControl>
              <FormLabel style={{ fontSize: "0.9rem" }}>Due Date</FormLabel>
              <br></br>
              <LocalizationProvider
                fullWidth
                dateAdapter={AdapterDateFns}
                style={{ maxWidth: "20% !important", fontSize: "0.9rem" }}
               >
                <DesktopDateTimePicker
                  label="Start date"
                  fullWidth
                  closeOnSelect
                  value={moment(sdate)}
                  disablePast="true"
                  onChange={handleSDateChange}
                  renderInput={(params) => (
                    <TextField {...params} size="small" />
                  )}
                />
              </LocalizationProvider>{" "}
              &nbsp;&nbsp;&nbsp;
              <LocalizationProvider
                fullWidth
                dateAdapter={AdapterDateFns}
                style={{ maxWidth: "20% !important" }}
              >
                <DesktopDateTimePicker
                  label="End date"
                  fullWidth
                  value={moment(edate)}
                  minDateTime={sdate}
                  closeOnSelect
                  disablePast="true"
                  onChange={handleEDateChange}
                  renderInput={(params) => (
                    <TextField {...params} size="small" />
                  )}
                />
              </LocalizationProvider>
            </FormControl>
          </Card>
          <br></br>
          <Card>
            <FormControl>
              <Typography style={{ fontSize: "0.9rem" }}>
                Complexity:
              </Typography>
              <Box sx={{ width: 270 }}>
                <Slider
                  aria-label="Small steps"
                  defaultValue={1}
                  step={1}
                  marks
                  value={complexity}
                  onChange={(e) => setComplexity(e.target.value)}
                  min={1}
                  max={10}
                  valueLabelDisplay="auto"
                />
              </Box>
            </FormControl>
          </Card>
          <br></br>
          <br></br>
        </>
      ),
    },
    {
      label: "PROJECT & MEMBERS",
      icon: <HiUserGroup size={"30px"} color="#0B6BCB" />,
      style: { width: "400px" },
      validate: validateRange,
      description: (
        <>
          <FormControl fullWidth variant="outlined" size="small">
            <InputLabel size="small" id="demo-simple-select-standard-label">
              Project
            </InputLabel>

            <Select
              labelId="demo-select-small"
              id="demo-select-small"
              variant="outlined"
              value={selectedOption}
              size="small"
              label="Projects"
              style={{ fontSize: "0.9rem" }}
              onChange={(event) => {
                setSelectedOption(event.target.value);
                getAllTagByProject(event.target.value);
                getAllMemberForTeam(event.target.value);
                getAllDesignersForTeam(event.target.value);
                getAllDevelopersForTeam(event.target.value);
                getAllTestersForTeam(event.target.value);
              }}
            >
              {allProjects.map((item) => (
                <MenuItem
                  key={item.id}
                  value={item.id}
                  style={{ fontSize: "0.9rem" }}
                >
                  {item.projectName}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <br></br>
          <br></br>

          <Autocomplete
            multiple
            id="tags-filled"
            fullWidth
            options={allTags.map((option) => option.name)}
            value={selectedTags}
            style={{ fontSize: "0.9rem" }}
            onChange={(event, newValue) => {
              setSelectedTags(newValue);
            }}
            renderInput={(params) => (
              <TextField
                {...params}
                size="small"
                variant="outlined"
                label="Add Tags"
                style={{ fontSize: "0.9rem" }}
                placeholder="Add Tags"
              />
            )}
          />

          <br></br>
          <br></br>

          <div
            style={{
              border: "1px solid #ccc",
              padding: "15px",
              borderRadius: "8px",
            }}
          >
            <Typography
              variant="h6"
              style={{
                fontSize: "1rem",
                marginBottom: "15px",
                color: "#0B6BCB",
              }}
            >
              Assigned To Details
            </Typography>

            <Typography
              variant="subtitle2"
              style={{
                fontSize: "0.9rem",
                marginBottom: "8px",
                fontWeight: "bold",
              }}
            >
              Designer
            </Typography>
            <Autocomplete
              id="designer-autocomplete"
              fullWidth
              style={{ fontSize: "0.9rem", marginBottom: "8px" }}
              options={allDesigners.map(
                (option) =>
                  option.firstName +
                  " " +
                  option.lastName +
                  " (" +
                  option.id +
                  ")"
              )}
              value={selectedDesigner}
              onChange={handleDesignerChange}
              renderInput={(params) => (
                <TextField
                  {...params}
                  size="small"
                  variant="outlined"
                  style={{ fontSize: "0.9rem" }}
                  label="Select Designer"
                  placeholder="Select Designer"
                />
              )}
            />
            <TextField
              type="number"
              fullWidth
              size="small"
              style={{ fontSize: "0.9rem", marginBottom: "15px" }}
              label="Designer Estimation Time (minutes)"
              placeholder="Enter estimation time in minutes"
              value={estimationTimeForDesigner}
              onChange={(e) => setEstimationTimeForDesigner(e.target.value)}
              inputProps={{ min: 0 }}
            />

            <Typography
              variant="subtitle2"
              style={{
                fontSize: "0.9rem",
                marginBottom: "8px",
                fontWeight: "bold",
              }}
            >
              Developer
            </Typography>
            <Autocomplete
              id="developer-autocomplete"
              fullWidth
              style={{ fontSize: "0.9rem", marginBottom: "8px" }}
              options={allDevelopers.map(
                (option) =>
                  option.firstName +
                  " " +
                  option.lastName +
                  " (" +
                  option.id +
                  ")"
              )}
              value={selectedDeveloper}
              onChange={handleDeveloperChange}
              renderInput={(params) => (
                <TextField
                  {...params}
                  size="small"
                  variant="outlined"
                  style={{ fontSize: "0.9rem" }}
                  label="Select Developer"
                  placeholder="Select Developer"
                />
              )}
            />
            <TextField
              type="number"
              fullWidth
              size="small"
              style={{ fontSize: "0.9rem", marginBottom: "15px" }}
              label="Developer Estimation Time (minutes)"
              placeholder="Enter estimation time in minutes"
              value={estimationTimeForDeveloper}
              onChange={(e) => setEstimationTimeForDeveloper(e.target.value)}
              inputProps={{ min: 0 }}
            />

            <Typography
              variant="subtitle2"
              style={{
                fontSize: "0.9rem",
                marginBottom: "8px",
                fontWeight: "bold",
              }}
            >
              Tester
            </Typography>
            <Autocomplete
              id="tester-autocomplete"
              fullWidth
              style={{ fontSize: "0.9rem", marginBottom: "8px" }}
              options={allTesters.map(
                (option) =>
                  option.firstName +
                  " " +
                  option.lastName +
                  " (" +
                  option.id +
                  ")"
              )}
              value={selectedTester}
              onChange={handleTesterChange}
              renderInput={(params) => (
                <TextField
                  {...params}
                  size="small"
                  variant="outlined"
                  style={{ fontSize: "0.9rem" }}
                  label="Select Tester"
                  placeholder="Select Tester"
                />
              )}
            />
            <TextField
              type="number"
              fullWidth
              size="small"
              style={{ fontSize: "0.9rem" }}
              label="Tester Estimation Time (minutes)"
              placeholder="Enter estimation time in minutes"
              value={estimationTimeForTester}
              onChange={(e) => setEstimationTimeForTester(e.target.value)}
              inputProps={{ min: 0 }}
            />
          </div>

          <br></br>
        </>
      ),
    },
  ];

  const theme = useTheme();
  const [activeStep, setActiveStep] = React.useState(0);
  const maxSteps = steps.length;

  const handleNext = () => {
    if (steps[activeStep].validate()) {
      setActiveStep((prevActiveStep) => prevActiveStep + 1);
    }
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  return (
    <React.Fragment>
      <DialogContent>
        <Grid container style={{ minWidth: steps[activeStep].style.width }}>
          <br></br>
          <br></br>

          <Box
            sx={{
              flexGrow: 1,
              padding: "10px",
              minWidth: steps[activeStep].style.width,
            }}
          >
            <Paper
              square
              elevation={0}
              sx={{
                textAlign: "center",
                height: 50,
                pl: 2,
                bgcolor: "background.default",
              }}
            >
              <div style={{ textAlign: "center", display: "inline-flex" }}>
                {steps[activeStep].icon}
              </div>
              <Typography
                style={{
                  textAlign: "center",
                  fontSize: 14,
                  fontWeight: "bold",
                  color: "#0B6BCB",
                }}
                class="font-bold"
              >
                {steps[activeStep].label}
              </Typography>
            </Paper>
            <br></br>
            <Divider />
            <br></br>
            <Box class="">{steps[activeStep].description}</Box>
            <MobileStepper
              variant="progress"
              steps={maxSteps}
              position="static"
              activeStep={activeStep}
              nextButton={
                activeStep !== maxSteps - 1 ? (
                  <Button
                    size="small"
                    onClick={handleNext}
                    disabled={activeStep === maxSteps - 1}
                  >
                    {theme.direction === "rtl" ? (
                      <KeyboardArrowLeft />
                    ) : (
                      <KeyboardArrowRight />
                    )}
                    NEXT
                  </Button>
                ) : (
                  <Button onClick={addNewTask}>&nbsp;SUBMIT</Button>
                )
              }
              backButton={
                <Button
                  size="small"
                  onClick={handleBack}
                  disabled={activeStep === 0}
                >
                  {theme.direction === "rtl" ? (
                    <KeyboardArrowRight />
                  ) : (
                    <KeyboardArrowLeft />
                  )}
                  BACK
                </Button>
              }
            />
          </Box>
        </Grid>
      </DialogContent>
    </React.Fragment>
  );
}
