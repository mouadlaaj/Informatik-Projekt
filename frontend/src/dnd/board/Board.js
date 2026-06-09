import CheckCircleRoundedIcon from "@mui/icons-material/CheckCircleRounded";
import {
  Avatar,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Sheet,
  Stack,
} from "@mui/joy";
import {
  Autocomplete,
  Dialog,
  FormControl,
  FormLabel,
  IconButton,
  InputLabel,
  MenuItem,
  Paper,
  Popover,
  Radio,
  RadioGroup,
  Select,
  TextField,
  Typography,
  styled,
} from "@mui/material";
import { DesktopDatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { Stomp } from "@stomp/stompjs";
import styleds from "@xstyled/styled-components";
import moment from "moment";
import PropTypes from "prop-types";
import React, { useState } from "react";
import { DragDropContext, Droppable } from "react-beautiful-dnd";
import { IoMdCloseCircleOutline } from "react-icons/io";
import { MdManageSearch } from "react-icons/md";
import { VscClearAll} from "react-icons/vsc";
import { toast } from "react-toastify";
import { CardHeader } from "reactstrap";
import SockJS from "sockjs-client";
import {
  WEBSOCKET_URL,
  changeTaskStatus,
  getAllMembersByProjectIdAndTeams,
  getAllProject,
  getAllTagByProjectId,
  getAllTasks,
  getTasksByStatus,
  logout,
  stringAvatar,
} from "../../service/service-call";
import reorder, { reorderQuoteMap } from "../reorder";
import Column from "./Column";
import { Clear } from "@mui/icons-material";

const Container = styleds.div`
  display: inline-flex;

`;

const Board = ({
  isCombineEnabled,
  containerHeight,
  withScrollableColumns,
}) => {
  const [columns, setColumns] = useState([]);
  const [ordered, setOrdered] = useState([]);
  const [isload, setIsload] = useState("false");
  const [data, setData] = useState([]);
  const [startPickerDate, setStartPickerDate] = React.useState(null);
  const [endPickerDate, setEndPickerDate] = React.useState(null);
  const [sdate, setSDate] = React.useState("");
  const [edate, setEDate] = React.useState("");
  const [tname, setTName] = React.useState("");
  const [assignedPickerDate, setAssignedPickerDate] = React.useState(null);
  const [completedPickerDate, setCompletedPickerDate] = React.useState(null);
  const [assignedDate, setAssignedDate] = React.useState("");
  const [completedDate, setCompletedDate] = React.useState("");
  const [allProjects, setAllProjects] = React.useState([]);
  const [allTags, setAllTags] = React.useState([]);
  const [allMembers, setAllMembers] = React.useState([]);
  const [selectedOption, setSelectedOption] = React.useState("");
  const [selectedTask, setSelectedTask] = React.useState("");
  const [movedStatus, setMovedStatus] = React.useState("");
  const [selectedTags, setSelectedTags] = React.useState("");
  const [selectedTeamMembers, setSelectedTeamMembers] = React.useState(null);
  const [showClearFilter, setShowClearFilter] = React.useState(false);

  const [isLoading, setIsLoading] = React.useState(false);
  const [priority, setPriority] = React.useState("");
  const priorityObj = [
    {
      label: "All",
      value: "",
    },
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

  const BootstrapDialog = styled(Dialog)(({ theme }) => ({
    "& .MuiDialog-paper": {
      minWidth: "600px",
      height: "auto",
    },
    "& .MuiDialogActions-root": {
      padding: theme.spacing(1),
    },
  }));
  
  const getTaskCountByKey = (key) => {
  const countMap = {
    todo: data.todoTaskCount,
    design: data.designTaskCount,
    development: data.developmentTaskCount,
    testing: data.testTaskCount, 
    done: data.doneTaskCount,
    blocker: data.blockerTaskCount,
  };

  return countMap[key.toLowerCase()] ?? 0;
};


  const [anchorSearchEl, setAnchorSearchEl] = React.useState(null);
  const openSearch = Boolean(anchorSearchEl);
  const idSearch = openSearch ? "simple-popover" : undefined;

  const handleSearchClick = (event) => {
    getAllProjects();
    setAnchorSearchEl(event.currentTarget);
  };

  const handleSearchClose = () => {
    setAnchorSearchEl(null);
  };

  const handleEDateChange = (e) => {
    setEndPickerDate(e);
    setEDate(moment(e).format("YYYY-MM-DD"));
    setShowClearFilter(true);
    let selectedMember = null;
    if (selectedTeamMembers) {
      let idCode = selectedTeamMembers.split("(");
      selectedMember = idCode[1].split(")")[0];
    }
    searchTasksByData(
      tname,
      selectedOption,
      selectedTags,
      selectedMember,
      sdate,
      moment(e).format("YYYY-MM-DD"),
      completedDate,
      assignedDate,
      priority
    );
  };

  const handleTaskNameChange = (e) => {
    setTName(e.target.value);
    setShowClearFilter(true);
    let selectedMember = null;
    if (selectedTeamMembers) {
      let idCode = selectedTeamMembers.split("(");
      selectedMember = idCode[1].split(")")[0];
    }
    searchTasksByData(
      e.target.value,
      selectedOption,
      selectedTags,
      selectedMember,
      sdate,
      edate,
      completedDate,
      assignedDate,
      priority
    );
  };

  const handlePriorityChange = (e) => {
    setPriority(e.target.value);
    setShowClearFilter(true);
    let selectedMember = null;
    if (selectedTeamMembers) {
      let idCode = selectedTeamMembers.split("(");
      selectedMember = idCode[1].split(")")[0];
    }
    searchTasksByData(
      tname,
      selectedOption,
      selectedTags,
      selectedMember,
      sdate,
      edate,
      completedDate,
      assignedDate,
      e.target.value
    );
  };

  const handleSDateChange = (e) => {
    console.log(e);
    setStartPickerDate(e);
    setShowClearFilter(true);
    setSDate(moment(e).format("YYYY-MM-DD"));
    let selectedMember = null;
    if (selectedTeamMembers) {
      let idCode = selectedTeamMembers.split("(");
      selectedMember = idCode[1].split(")")[0];
    }
    searchTasksByData(
      tname,
      selectedOption,
      selectedTags,
      selectedMember,
      moment(e).format("YYYY-MM-DD"),
      edate,
      completedDate,
      assignedDate,
      priority
    );
  };

  const handleClearDueDates = () => {
    setStartPickerDate(null);
    setEndPickerDate(null);
    setSDate(null);
    setEDate(null);
    let selectedMember = null;
    if (selectedTeamMembers) {
      let idCode = selectedTeamMembers.split("(");
      selectedMember = idCode[1].split(")")[0];
    }
    searchTasksByData(
      tname,
      selectedOption,
      selectedTags,
      selectedMember,
      null,
      null,
      completedDate,
      assignedDate,
      priority
    );
  };

  const handleClearActualDates = () => {
    setAssignedPickerDate(null);
    setCompletedPickerDate(null);
    setAssignedDate(null);
    setCompletedDate(null);
    let selectedMember = null;
    if (selectedTeamMembers) {
      let idCode = selectedTeamMembers.split("(");
      selectedMember = idCode[1].split(")")[0];
    }
    searchTasksByData(
      tname,
      selectedOption,
      selectedTags,
      selectedMember,
      sdate,
      edate,
      null,
      null,
      priority
    );
  };

  const handleAssignedDateChange = (e) => {
    setAssignedPickerDate(e);
    setAssignedDate(moment(e).format("YYYY-MM-DD"));
    setShowClearFilter(true);
    let selectedMember = null;
    if (selectedTeamMembers) {
      let idCode = selectedTeamMembers.split("(");
      selectedMember = idCode[1].split(")")[0];
    }
    searchTasksByData(
      tname,
      selectedOption,
      selectedTags,
      selectedMember,
      sdate,
      edate,
      completedDate,
      moment(e).format("YYYY-MM-DD"),
      priority
    );
  };

  const handleCompletedDateChange = (e) => {
    console.log(e);
    setShowClearFilter(true);
    setCompletedPickerDate(e);
    setCompletedDate(moment(e).format("YYYY-MM-DD"));
    let selectedMember = null;
    if (selectedTeamMembers) {
      let idCode = selectedTeamMembers.split("(");
      selectedMember = idCode[1].split(")")[0];
    }
    searchTasksByData(
      tname,
      selectedOption,
      selectedTags,
      selectedMember,
      sdate,
      edate,
      moment(e).format("YYYY-MM-DD"),
      assignedDate,
      priority
    );
  };
  React.useEffect(() => {
    connect();
    getAllTasksForUserOnLoad();
  }, []);

  const connect = () => {
    var sock = new SockJS(WEBSOCKET_URL);
    let stompClient = Stomp.over(sock);

    sock.onopen = function () {
    };
    stompClient.connect({}, function (frame) {
      stompClient.subscribe("/all/users", function (greeting) {
        if (
          !localStorage.getItem("isPopUpOpen") ||
          localStorage.getItem("isPopUpOpen") === "false"
        ) {
          getAllTasksForUserOnLoad();
        }
      });
    });
  };

  function getAllProjects() {
    getAllProject("")
      .then((resp) => {
        console.log(resp);
        if (resp.status === 401) {
          logout();
        }
        resp.json().then((data) => {
          console.log(data);
          setAllProjects(data);
        });
      })
      .catch((error) => {
        console.log("login user err " + error);
      });
  }

  function getAllTagByProject(id) {
    getAllTagByProjectId(id)
      .then((resp) => {
        console.log(resp);
        if (resp.status === 401) {
          logout();
        }
        resp.json().then((data) => {
          console.log(data);
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
        console.log(resp);
        let data = resp.data;
        console.log(data);
        setAllMembers(data);
      })
      .catch((error) => {
        console.log("login user err " + error);
      });
  }

  function getAllTasksForUserOnLoad(value) {
    setIsLoading(true);
    getAllTasks(value)
      .then((resp) => {
        setIsLoading(false);
        if (resp.status === 401) {
          logout();
        }
        resp.json().then((data) => {
          setColumns(data.tasks);
          setData(data);
          setOrdered(Object.keys(data.tasks));
        });
      })
      .catch((error) => {
        console.log(error);
      });
  }

  async function getAllTasksForUser(page, value) {
    if (value && value !== "") {
      await getTasksByStatus(
        value,
        page,
        10,
        tname,
        selectedOption,
        selectedTags,
        selectedTeamMembers,
        sdate,
        edate,
        completedDate,
        assignedDate,
        priority
      )
        .then((resp) => {
          if (resp.status === 401) {
            logout();
          }
          resp.json().then((data) => {
            let col = columns;
            col[value] = data.content;
            console.log(col);
            setColumns(col);
            setIsload(!isload);
          });
        })
        .catch((error) => {
          console.log("Your session expired, Please login again", error);
        });
    } else {
      getAllTasks(
        tname,
        selectedOption,
        selectedTags,
        selectedTeamMembers,
        sdate,
        edate,
        completedDate,
        assignedDate,
        priority
      )
        .then((resp) => {
          if (resp.status === 401) {
            logout();
          }
          setIsload(!isload);
          resp.json().then((data) => {
            setColumns(data.tasks);
            setData(data);
            setOrdered(Object.keys(data.tasks));
          });
        })
        .catch((error) => {
          console.log("Your session expired, Please login again", error);
        });
    }
  }

  const clearFilters = () => {
    setShowClearFilter(false);
    setTName("");
    setSelectedOption("");
    setSelectedTags("");
    setAllTags([]);
    setAllMembers([]);
    setSelectedTeamMembers(null);
    setSDate("");
    setStartPickerDate(null);
    setEndPickerDate(null);
    setEDate("");
    setCompletedDate("");
    setAssignedDate("");
    setCompletedPickerDate(null);
    setAssignedPickerDate(null);
    setPriority("");
    getAllTasks(null, null, null, null, null, null, null, null, null)
      .then((resp) => {
        if (resp.status === 401) {
          logout();
        }
        setIsload(!isload);
        resp.json().then((data) => {
          setColumns(data.tasks);
          setData(data);
          setOrdered(Object.keys(data.tasks));
        });
      })
      .catch((error) => {
        console.log("Your session expired, Please login again", error);
      });
  };

  const searchTasksByData = (
    name,
    proj,
    tg,
    mem,
    stdate,
    endate,
    cDate,
    aDate,
    prior
  ) => {
    getAllTasks(name, proj, tg, mem, stdate, endate, cDate, aDate, prior)
      .then((resp) => {
        if (resp.status === 401) {
          logout();
        }
        setIsload(!isload);
        resp.json().then((data) => {
          setColumns(data.tasks);
          setData(data);
          setOrdered(Object.keys(data.tasks));
        });
      })
      .catch((error) => {
        console.log("Your session expired, Please login again", error);
      });
  };

  function selectedTaskForStatusChange(taskId, movedStatus, fromStatus) {
    setSelectedTask(taskId);
    setMovedStatus(movedStatus);
    localStorage.setItem("isPopUpOpen", "true");
    
  }
  const onDragEnd = (result) => {
    if (
      result.type === "QUOTE" &&
      result.destination.droppableId !== result.source.droppableId
    ) {
      if (
        result.destination.droppableId === "BLOCKER" ||
        result.source.droppableId === "BLOCKER"
      ) {
        selectedTaskForStatusChange(
          result.draggableId,
          result.destination.droppableId,
          result.source.droppableId
        );
      }
      else if (
        result.source.droppableId === "DEVELOPMENT" &&
        (result.destination.droppableId === "TODO" ||
          result.destination.droppableId === "DESIGN")
      ) {
        selectedTaskForStatusChange(
          result.draggableId,
          result.destination.droppableId,
          result.source.droppableId
        );
      }
      else if (
        result.source.droppableId === "TESTER" &&
        (result.destination.droppableId === "TODO" ||
          result.destination.droppableId === "DESIGN" ||
          result.destination.droppableId === "DEVELOPMENT")
      ) {
        selectedTaskForStatusChange(
          result.draggableId,
          result.destination.droppableId,
          result.source.droppableId
        );
      }
      else if (
        result.source.droppableId === "DONE" &&
        (result.destination.droppableId === "TODO" ||
          result.destination.droppableId === "DESIGN" ||
          result.destination.droppableId === "DEVELOPMENT" ||
          result.destination.droppableId === "TESTER")
      ) {
        selectedTaskForStatusChange(
          result.draggableId,
          result.destination.droppableId,
          result.source.droppableId
        );
      }
      else {
        changeTaskStatus(result.draggableId, result.destination.droppableId, "")
          .then((resp) => {
            console.log(resp);
            toast.success("Task status changed successfully");
            getAllTasksForUser();
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
            getAllTasksForUser();
          });
      }
    }

    if (result.combine) {
      if (result.type === "COLUMN") {
        const shallow = [...ordered];
        shallow.splice(result.source.index, 1);
        setOrdered(shallow);
        return;
      }

      const column = columns[result.source.droppableId];
      const withQuoteRemoved = [...column];

      withQuoteRemoved.splice(result.source.index, 1);

      const orderedColumns = {
        ...columns,
        [result.source.droppableId]: withQuoteRemoved,
      };
      setColumns(orderedColumns);
      return;
    }

    if (!result.destination) {
      return;
    }

    const source = result.source;
    const destination = result.destination;

    if (
      source.droppableId === destination.droppableId &&
      source.index === destination.index
    ) {
      return;
    }

    if (result.type === "COLUMN") {
      const reorderedorder = reorder(ordered, source.index, destination.index);
      setOrdered(reorderedorder);
      return;
    }

    const data = reorderQuoteMap({
      quoteMap: columns,
      source,
      destination,
    });

    setColumns(data.quoteMap);
  };

  const CustomPaper = (props) => {
    return (
      <Paper elevation={8} sx={{ fontSize: "0.8rem !important" }} {...props} />
    );
  };

  return (
    <>
     
        <>
          &nbsp;&nbsp;
          <Button
            variant="soft"
            size="sm"
            startDecorator={<MdManageSearch size={"25px"} />}
            aria-describedby={idSearch}
            onClick={handleSearchClick}
          >
            FILTERS
          </Button>
          {showClearFilter ? (
            <Button
              size="sm"
              variant="soft"
              aria-describedby={idSearch}
              startDecorator={<VscClearAll size={"25px"} />}
              style={{ marginLeft: "10px" }}
              onClick={clearFilters}
            >
              CLEAR FILTERS
            </Button>
          ) : (
            ""
          )}
        </>
      <Box
        sx={{
          minHeight: "85vh",
          maxHeight: "85vh",
          width: "90%",
          position: "absolute",
          overflowX: "auto",
          scrollbarWidth: "thin",
        }}
      >
        {isLoading ? (
          <div
            style={{
              
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            {<CircularProgress />}
          </div>
        ) : (
          ""
        )}

     
          <DragDropContext onDragEnd={onDragEnd}>
            <Droppable
              droppableId="board"
              type="COLUMN"
              direction="horizontal"
              ignoreContainerClipping={Boolean(containerHeight)}
              isCombineEnabled={isCombineEnabled}
            >
              {(provided) => (
                <Container ref={provided.innerRef} {...provided.droppableProps}>
                  {ordered.map((key, index) => (
                    <Column
                      key={key}
                      index={index}
                      title={key}
                      quotes={columns[key]}
                      count={getTaskCountByKey(key)}
                      getAllTasksForUser={getAllTasksForUser}
                      isScrollable={withScrollableColumns}
                      isCombineEnabled={isCombineEnabled}
                     
                    />
                  ))}
                  {provided.placeholder}
                </Container>
              )}
            </Droppable>
          </DragDropContext>
      
      </Box>
      <Popover
        id={idSearch}
        open={openSearch}
        anchorEl={anchorSearchEl}
        onClose={handleSearchClose}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "left",
        }}
        className="rounded-3xl"
      >
        <Card
          style={{ width: "1100px", padding: "30px", backgroundColor: "white" }}
        >
          <CardHeader>
            <Box display="flex" alignItems="center">
              <Typography flexGrow={1} fontWeight={700}>
                {"FILTERS"}
              </Typography>
              <Box>
                <IconButton onClick={handleSearchClose}>
                  <IoMdCloseCircleOutline />
                </IconButton>
              </Box>
            </Box>
          </CardHeader>
          <CardContent
            style={{
              minHeight: "270px",
              maxHeight: "270px",
              scrollbarWidth: "thin",
            }}
          >
            <Card style={{ borderRadius: "20px" }}>
              <div class={"grid grid-cols-4 gap-4"}>
                <FormControl
                  required={true}
                  fullWidth
                  variant="standard"
                  style={{ textAlign: "center" }}
                >
                  <TextField
                    id="standard-adornment-tname"
                    label="Title or Task Id"
                    size="small"
                    type={"text"}
                    value={tname}
                    InputProps={{ style: { fontSize: "0.8rem" } }}
                    InputLabelProps={{ style: { fontSize: "0.8rem" } }}
                    onChange={handleTaskNameChange}
                  />
                </FormControl>

                <FormControl fullWidth variant="outlined" size="small">
                  <InputLabel
                    size="small"
                    id="demo-simple-select-standard-label"
                  >
                    Project
                  </InputLabel>

                  <Select
                    labelId="demo-select-small"
                    id="demo-select-small"
                    variant="outlined"
                    defaultValue=""
                    value={selectedOption}
                    size="small"
                    label="Projects"
                    sx={{
                      fontSize: "0.8rem",
                      "& .MuiInputLabel-root": { fontSize: "0.8rem" },
                      "& .MuiOutlinedInput-root": { fontSize: "0.8rem" },
                    }}
                    onChange={(event) => {
                      console.log(event);
                      setSelectedTeamMembers("");
                      setSelectedTags("");
                      setSelectedOption(event.target.value);
                      if (event.target.value) {
                        let proj = allProjects.find(
                          (e) => e.projectName === event.target.value
                        );
                        setShowClearFilter(true);
                        let selectedMember = null;
                        if (selectedTeamMembers) {
                          let idCode = selectedTeamMembers.split("(");
                          selectedMember = idCode[1].split(")")[0];
                        }
                        searchTasksByData(
                          tname,
                          event.target.value,
                          selectedTags,
                          selectedMember,
                          sdate,
                          edate,
                          completedDate,
                          assignedDate,
                          priority
                        );

                        getAllTagByProject(proj.id);
                        getAllMemberForTeam(proj.id);
                      } else {
                        setAllMembers([]);
                        setAllTags([]);
                        setSelectedTeamMembers("");
                        setSelectedTags("");
                        searchTasksByData(
                          tname,
                          "",
                          selectedTags,
                          "",
                          sdate,
                          edate,
                          completedDate,
                          assignedDate,
                          priority
                        );
                      }
                    }}
                  >
                    <MenuItem value={null} sx={{ fontSize: "0.8rem" }}>
                      Select Project
                    </MenuItem>
                    {allProjects.map((item) => (
                      <MenuItem
                        value={item.projectName}
                        sx={{ fontSize: "0.8rem" }}
                      >
                        {item.projectName}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
                <Autocomplete
                  id="tags-filled"
                  fullWidth
                  PaperComponent={CustomPaper}
                  options={allTags.map((option) => option.name)}
                  value={selectedTags}
                  onChange={(event, newValue) => {
                    setSelectedTags(newValue);
                    setShowClearFilter(true);
                    let selectedMember = null;
                    if (selectedTeamMembers) {
                      let idCode = selectedTeamMembers.split("(");
                      selectedMember = idCode[1].split(")")[0];
                    }

                    searchTasksByData(
                      tname,
                      selectedOption,
                      newValue,
                      selectedMember,
                      sdate,
                      edate,
                      completedDate,
                      assignedDate,
                      priority
                    );
                  }}
                  renderInput={(params) => (
                    <TextField
                      {...params}
                      variant="outlined"
                      size="small"
                      InputProps={{
                        ...params.InputProps,
                        style: { fontSize: "0.8rem" },
                      }}
                      InputLabelProps={{
                        ...params.InputLabelProps,
                        style: { fontSize: "0.8rem" },
                      }}
                      label="Add Tags"
                      placeholder="Add Tags"
                    />
                  )}
                />

                <Autocomplete
                  id="tags-filled"
                  fullWidth
                  options={allMembers.map((option) => ({
                    label: `${option.firstName} ${option.lastName} (${option.id})`,
                    value: option.id,
                  }))}
                  value={selectedTeamMembers}
                  PaperComponent={CustomPaper}
                  onChange={(event, newValue) => {
                    setShowClearFilter(true);
                    setSelectedTeamMembers(newValue);
                    let idCode = newValue.split("(");
                    let val = idCode[1].split(")");
                    searchTasksByData(
                      tname,
                      selectedOption,
                      selectedTags,
                      newValue?.value || "",
                      sdate,
                      edate,
                      completedDate,
                      assignedDate,
                      priority
                    );
                  }}
                  renderTags={(value, getTagProps) =>
                    value.map((option, index) => (
                      <Chip
                        avatar={
                          <Avatar
                            size="lg"
                            color="danger"
                            {...stringAvatar(option)}
                          />
                        }
                        variant="outlined"
                        label={option}
                        {...getTagProps({ index })}
                      />
                    ))
                  }
                  renderInput={(params) => (
                    <TextField
                      {...params}
                      variant="outlined"
                      size="small"
                      InputProps={{
                        ...params.InputProps,
                        style: { fontSize: "0.8rem" },
                      }}
                      InputLabelProps={{
                        ...params.InputLabelProps,
                        style: { fontSize: "0.8rem" },
                      }}
                      label="Assigned to"
                      placeholder="Assigned to"
                    />
                  )}
                />
              </div>
            </Card>

            <br></br>
            <Stack direction={"row"} spacing={3}>
              <Card
                style={{
                  borderRadius: "20px",
                  fontSize: "0.8rem",
                  width: "80%",
                }}
              >
                <FormLabel sx={{ fontSize: "0.8rem" }}>Due Dates:</FormLabel>
                <IconButton
                  style={{
                    position: "absolute",
                    top: ".7rem",
                    margin: "auto",
                    right: "1rem",
                    fontSize: "15px",
                  }}
                  onClick={handleClearDueDates}
                >
                  <Clear fontSize="10px" />
                </IconButton>
                <LocalizationProvider fullWidth dateAdapter={AdapterDateFns}>
                  <DesktopDatePicker
                    label="Start date"
                    fullWidth
                    value={startPickerDate}
                    size="small"
                    disableFuture
                    onChange={handleSDateChange}
                    renderInput={(params) => (
                      <TextField
                        size="small"
                        {...params}
                        InputProps={{
                          ...params.InputProps,
                          size: "small",
                          style: { fontSize: "0.8rem" },
                        }}
                        InputLabelProps={{
                          ...params.InputLabelProps,
                          style: { fontSize: "0.8rem" },
                        }}
                      />
                    )}
                  />
                </LocalizationProvider>

                <LocalizationProvider fullWidth dateAdapter={AdapterDateFns}>
                  <DesktopDatePicker
                    label="End date"
                    fullWidth
                    value={endPickerDate}
                    size="small"
                    minDate={startPickerDate}
                    onChange={handleEDateChange}
                    disableFuture
                    renderInput={(params) => (
                      <TextField
                        size="small"
                        {...params}
                        InputProps={{
                          ...params.InputProps,
                          size: "small",
                          style: { fontSize: "0.8rem" },
                        }}
                        InputLabelProps={{
                          ...params.InputLabelProps,
                          style: { fontSize: "0.8rem" },
                        }}
                      />
                    )}
                  />
                </LocalizationProvider>
              </Card>

              <Card style={{ borderRadius: "20px", width: "80%" }}>
                <FormLabel sx={{ fontSize: "0.8rem" }}>Actual Dates:</FormLabel>
                <IconButton
                  style={{
                    position: "absolute",
                    top: ".7rem",
                    margin: "auto",
                    right: "1rem",
                    fontSize: "15px",
                  }}
                  onClick={handleClearActualDates}
                >
                  <Clear fontSize="10px" />
                </IconButton>
                <LocalizationProvider
                  fullWidth
                  dateAdapter={AdapterDateFns}
                  style={{ maxWidth: "15% !important" }}
                >
                  <DesktopDatePicker
                    label="Assigned date"
                    fullWidth
                    value={assignedPickerDate}
                    size="small"
                    disableFuture
                    closeOnSelect
                    onChange={handleAssignedDateChange}
                    renderInput={(params) => (
                      <TextField
                        size="small"
                        {...params}
                        InputProps={{
                          ...params.InputProps,
                          size: "small",
                          style: { fontSize: "0.8rem" },
                        }}
                        InputLabelProps={{
                          ...params.InputLabelProps,
                          style: { fontSize: "0.8rem" },
                        }}
                      />
                    )}
                  />
                </LocalizationProvider>

                <LocalizationProvider
                  fullWidth
                  dateAdapter={AdapterDateFns}
                  style={{ minWidth: "5% !important" }}
                >
                  <DesktopDatePicker
                    label="Completed date"
                    fullWidth
                    value={completedPickerDate}
                    minDate={assignedPickerDate}
                    size="small"
                    closeOnSelect
                    disableFuture
                    onChange={handleCompletedDateChange}
                    renderInput={(params) => (
                      <TextField
                        size="small"
                        {...params}
                        InputProps={{
                          ...params.InputProps,
                          size: "small",
                          style: { fontSize: "0.8rem" },
                        }}
                        InputLabelProps={{
                          ...params.InputLabelProps,
                          style: { fontSize: "0.8rem" },
                        }}
                      />
                    )}
                  />
                </LocalizationProvider>
              </Card>

              <Card style={{ borderRadius: "20px" }}>
                <FormControl>
                  <FormLabel sx={{ fontSize: "0.8rem" }}>Priority</FormLabel>
                  <RadioGroup
                    overlay
                    name="member"
                    value={priority}
                    sx={{ gap: 2 }}
                    onChange={handlePriorityChange}
                  >
                    <Stack
                      direction={"row"}
                      justifyContent={"center"}
                      spacing={2}
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
                          />
                          <Typography
                            level="body-sm"
                            sx={{ fontSize: "0.8rem" }}
                          >
                            {num.label}
                          </Typography>
                        </Sheet>
                      ))}
                    </Stack>
                  </RadioGroup>
                </FormControl>
              </Card>
            </Stack>
          </CardContent>
        </Card>
      </Popover>
    </>
  );
};

Board.defaultProps = {
  isCombineEnabled: false,
};

Board.propTypes = {
  isCombineEnabled: PropTypes.bool,
};

export default Board;
