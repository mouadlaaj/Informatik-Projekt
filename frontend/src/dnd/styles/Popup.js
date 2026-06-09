import TimelineIcon from "@mui/icons-material/Timeline";
import {
  Box,
  Button,
  Card,
  Chip,
  Divider,
  Grid,
  IconButton,
  Link,
  ListItem,
  ListItemContent,
  ListItemDecorator,
  Radio,
  RadioGroup,
  Sheet,
  Slider,
  Tooltip,
  Typography,
} from "@mui/joy";
import {
  Autocomplete,
  Chip as Chipd,
  Dialog,
  DialogActions,
  DialogContent,
  MenuItem,
  Paper,
  Popover,
  Select,
  TextField,
  styled,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import { MdBugReport, MdDelete, MdLowPriority, MdOutlineViewTimeline, MdPriorityHigh, MdReportProblem } from "react-icons/md";

import { LuFileSearch} from "react-icons/lu";

import { MdOutlineDoneOutline } from "react-icons/md";

import PanToolIcon from "@mui/icons-material/PanTool";

import CheckCircleRoundedIcon from "@mui/icons-material/CheckCircleRounded";
import Avatar from "@mui/joy/Avatar";
import FormControl from "@mui/joy/FormControl";
import FormLabel from "@mui/joy/FormLabel";
import Stack from "@mui/joy/Stack";
import Timeline from "@mui/lab/Timeline";
import TimelineConnector from "@mui/lab/TimelineConnector";
import TimelineContent from "@mui/lab/TimelineContent";
import TimelineDot from "@mui/lab/TimelineDot";
import TimelineItem from "@mui/lab/TimelineItem";
import TimelineOppositeContent from "@mui/lab/TimelineOppositeContent";
import TimelineSeparator from "@mui/lab/TimelineSeparator";
import { convertToHTML } from "draft-convert";
import { useRef } from "react";
import { BsListTask } from "react-icons/bs";
import { CiCircleRemove } from "react-icons/ci";
import { FaPauseCircle, FaRegComments } from "react-icons/fa";
import { GrAttachment, GrInProgress } from "react-icons/gr";
import { ImParagraphLeft } from "react-icons/im";
import {
  IoCheckmarkDoneCircleOutline,
  IoPricetagsOutline,
} from "react-icons/io5";
import {
  LuAlarmClock,
  LuClock,
  LuDownload,
  LuListTodo,
  LuUsers,
} from "react-icons/lu";
import { MdAccessAlarm, MdDeleteOutline } from "react-icons/md";
import {
  PiCellSignalFullBold,
  PiClockBold,
  PiWifiHighFill,
} from "react-icons/pi";
import { RiUserSearchLine } from "react-icons/ri";

import { IoIosPricetags } from "react-icons/io";
import { PiWifiLowFill, PiWifiMediumFill } from "react-icons/pi";
import { RxActivityLog } from "react-icons/rx";

import { DesktopDateTimePicker } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import link from "./../../assets/link.png";
import pdf from "./../../assets/pdf.svg";

import { ContentState, EditorState, convertFromHTML } from "draft-js";
import moment from "moment";
import { Editor } from "react-draft-wysiwyg";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import { VscFeedback } from "react-icons/vsc";
import { toast } from "react-toastify";
import {
  addAttachmnts,
  addComments,
  addRating,
  assignQcMember,
  deleteAttachmentById,
  deleteCommentById,
  deleteTask,
  getAllDesignersByProjectId,
  getAllDevelopersByProjectId,
  getAllMembersByProjectIdAndTeams,
  getAllTagByProjectId,
  getAllTestersByProjectId,
  getParticularTaskById,
  logout,
  patchUpdateTask,
  reportBugs,
  stringAvatar,
  updateBugStatus,
} from "../../service/service-call";
import { BugReportOutlined, DeleteOutline } from "@mui/icons-material";
import BugReportForm from "./BugReportForm";
import { keyframes } from "@emotion/react";
const BootstrapDialog = styled(Dialog)(({ theme }) => ({
  "& .MuiDialogContent-root": {
    padding: theme.spacing(2),
  },
  "& .MuiDialogActions-root": {
    padding: theme.spacing(1),
  },
}));

const role = localStorage.getItem("role")?.toUpperCase();

function Popup(props) {

 
  let timelineDataArray = {
    TODO: {
      icon: <LuListTodo size={"20px"} />,
    },
    DESIGN: {
      icon: <GrInProgress size={"20px"} />,
    },
    DEVELOPMENT: {
      icon: <LuFileSearch size={"20px"} />,
    },
    TESTING: {
      icon: <LuFileSearch size={"20px"} />,
    },
    DONE: {
      icon: <MdOutlineDoneOutline size={"20px"} />,
    },
    BLOCKER: {
      icon: <PanToolIcon size={"20px"} />,
    }
   
  };
  const [deleteTaskShow, setDeleteTaskShow] = useState(false);
  const { viewTaskModal, taskId } = props;
  const [description, setDescription] = useState(null);
  const [commentMsg, setCommentMsg] = useState(null);
  const [projectId, setProjectId] = useState("");
  const [isTitleEditing, setIsTitleEditing] = useState(false);
  const [title, setTitle] = useState("");
  const [status, setStatus] = useState("");
  const [members, setMembers] = useState("");
  const [qcmembers, setQcMembers] = useState("");
  const [taskMembers, setTaskMembers] = useState([]);
  const [qcFeedback, setQcFeedback] = useState("");
  const [sdate, setSDate] = React.useState("");
  const [edate, setEDate] = React.useState("");
  const [qcSdate, setQcSDate] = React.useState("");
  const [qcdate, setQcDate] = React.useState("");
  const [isSdescriptionEditing, setIsSdescriptionEditing] = useState(false);
  const [shortDescription, setShortDescription] = useState("");
  const [allMembers, setAllMembers] = React.useState([]);

  const [selectedRole, setSelectedRole] = useState("");

  const [allTags, setAllTags] = React.useState([]);
  const [tags, settags] = React.useState([]);
  const [selectedTags, setSelectedTags] = React.useState([]);
  const [loadingIcon, setLoadingIcon] = useState("save");
  const [attachments, setAttachments] = useState([]);
  const [comments, setComments] = useState([]);
  const [activity, setActivity] = useState([]);
  const [priority, setPriority] = useState("");
  const [complexity, setComplexity] = useState("");
  const [complexityReason, setComplexityReason] = useState("");
  const [dueDateReason, setDueDateReason] = useState("");
  const [priorityReason, setPriorityReason] = useState("");
  const [selectedFile, setSelectedFile] = useState("");
  const [timeline, setTimeline] = useState([]);
  const [selectedDesigner, setSelectedDesigner] = useState("");
  const [estimationTimeForDesigner, setEstimationTimeForDesigner] =
    useState("");
  const designation = localStorage.getItem("designation")?.toUpperCase();

  const [selectedDeveloper, setSelectedDeveloper] = useState("");
  const [estimationTimeForDeveloper, setEstimationTimeForDeveloper] =
    useState("");

  const [selectedTester, setSelectedTester] = useState("");
  const [estimationTimeForTester, setEstimationTimeForTester] = useState("");

  const [anchorMemberEl, setAnchorMemberEl] = React.useState(null);
  const [anchorQCMemberEl, setAnchorQCMemberEl] = React.useState(null);
  const [anchorComplexityEl, setAnchorComplexityEl] = React.useState(null);
  const [anchorDueDateEl, setAnchorDueDateEl] = React.useState(null);
  const [anchorPriorityEl, setAnchorPriorityEl] = React.useState(null);
  const [anchorAttachmentEl, setAnchorAttachmentEl] = React.useState(null);
  const [anchorRatingsEl, setAnchorRatingsEl] = React.useState(null);
  const [anchorTagsEl, setAnchorTagsEl] = React.useState(null);

  const [allDesigners, setAllDesigners] = React.useState([]);
  const [allDevelopers, setAllDevelopers] = React.useState([]);
  const [allTesters, setAllTesters] = React.useState([]);

  const activityRef = useRef(null);
  const commentRef = useRef(null);
  const timelineRef = useRef(null);
  const bugListRef = useRef(null);

  const [localStatusMap, setLocalStatusMap] = useState({});
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);


  const getSeverityIcon = (severity) => {
    switch ((severity || "").toUpperCase()) {
      case "HIGH":
        return <MdPriorityHigh />;
      case "MEDIUM":
        return <MdReportProblem />;
      case "LOW":
        return <MdLowPriority />;
      default:
        return null;
    }
  };


  const handleMemberClick = (event) => {
    getAllMemberForTeam(projectId);
    getAllDesignersForTeam(projectId?.id);
    getAllDevelopersForTeam(projectId?.id);
    getAllTestersForTeam(projectId?.id);

    setAnchorMemberEl(event.currentTarget);
  };

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

  useEffect(() => {
    if (Array.isArray(members)) {
      members.forEach((m) => {
        const name = `${m.member.firstName} ${m.member.lastName} (${m.member.id})`;

        switch (m.designation?.toLowerCase()) {
          case "designer":
            setSelectedDesigner(name);
            setEstimationTimeForDesigner(m.estimationTime || 0);
            break;
          case "developer":
            setSelectedDeveloper(name);
            setEstimationTimeForDeveloper(m.estimationTime || 0);
            break;
          case "tester":
            setSelectedTester(name);
            setEstimationTimeForTester(m.estimationTime || 0);
            break;
          default:
            break;
        }
      });
    }
  }, [members]);

  const handleDesignerChange = (event, newValue) => {
    setSelectedDesigner(newValue);
  };

  const handleDeveloperChange = (event, newValue) => {
    setSelectedDeveloper(newValue);
  };

  const handleTesterChange = (event, newValue) => {
    setSelectedTester(newValue);
  };

  const handleQCMemberClick = async (event, roleStatus) => {
    setAnchorQCMemberEl(event.currentTarget);
    setSelectedRole(roleStatus.toLowerCase());

    try {
      let resp;
      if (roleStatus === "DEVELOPMENT") {
        resp = await getAllDevelopersByProjectId(projectId?.id);
      } else if (roleStatus === "TESTING") {
        resp = await getAllTestersByProjectId(projectId?.id);
      }

      const members = resp?.data || [];
      setAllMembers(members);
    } catch (error) {
      toast.error("Failed to load members");
    }
  };

  const handleAssignDeveloper = async () => {
    const memberToAssign =
      selectedRole === "development" ? selectedDeveloper : selectedTester;

    if (!memberToAssign || !startDate || !endDate) return;

    try {
      await assignQcMember(
        taskId,
        moment(startDate).format("YYYY-MM-DDTHH:mm:ss"),
        moment(endDate).format("YYYY-MM-DDTHH:mm:ss")
      );

      toast.success("Assigned successfully");
      handleQCMemberClose();
    } catch (error) {
      toast.error(error?.response?.data?.errors[0] || "Please check the satrt date and end date");
    }
  };

  const handleTagsClick = (event) => {
    getAllTagsForProject(projectId);
    setAnchorTagsEl(event.currentTarget);
  };

  const toggleDeleteTaskModal = () => {
    setDeleteTaskShow(!deleteTaskShow);
  };

  const deleteTaskById = () => {
    deleteTask(taskId)
      .then((resp) => {
        toast.success("Task deleted successfully");
        toggleDeleteTaskModal();
        viewTaskModal();
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
  };

  const handleTagsClose = () => {
    setAnchorTagsEl(null);
  };

  const handleMemberClose = () => {
    setAnchorMemberEl(null);
  };
  const handleQCMemberClose = () => {
    setAnchorQCMemberEl(null);
  };

  const handleComplexityClose = () => {
    setAnchorComplexityEl(null);
  };

  const handleDueDateClick = (event) => {
    setAnchorDueDateEl(event.currentTarget);
  };

  const handleComplexityClick = (event) => {
    setAnchorComplexityEl(event.currentTarget);
  };


  const handleDueDateClose = () => {
    setAnchorDueDateEl(null);
  };

  const handlePriorityClick = (event) => {
    setAnchorPriorityEl(event.currentTarget);
  };

  const handlePriorityClose = () => {
    setAnchorPriorityEl(null);
  };

  const handleAttachmentClick = (event) => {
    setAnchorAttachmentEl(event.currentTarget);
  };

  const handleAttachmentClose = () => {
    setAnchorAttachmentEl(null);
  };

  const openMember = Boolean(anchorMemberEl);
  const idMember = openMember ? "simple-popover" : undefined;

  const openQCMember = Boolean(anchorQCMemberEl);
  const idQCMember = openQCMember ? "simple-popover" : undefined;

  const openDueDate = Boolean(anchorDueDateEl);
  const idDueDate = openDueDate ? "simple-popover" : undefined;

  const openComplexity = Boolean(anchorComplexityEl);
  const idComplexity = openComplexity ? "simple-popover" : undefined;

  const openPriority = Boolean(anchorPriorityEl);
  const idPriority = openPriority ? "simple-popover" : undefined;

  const openAttachment = Boolean(anchorAttachmentEl);
  const idAttachment = openAttachment ? "simple-popover" : undefined;

  const openRatings = Boolean(anchorRatingsEl);
  const idRatings = openRatings ? "simple-popover" : undefined;

  const openTags = Boolean(anchorTagsEl);
  const idTags = openTags ? "simple-popover" : undefined;

  const [bugs, setBugs] = useState([]);

  const blink = keyframes`
  from { opacity: 0; }
  to { opacity: 1; }
`;

  useEffect(() => {
    getCurrenttask(taskId);
  }, [taskId]);

  const _contentState = ContentState.createFromBlockArray(
    convertFromHTML(description ? description : "")
  );
  const [editorState, setEditorState] = useState(() =>
    EditorState.createWithContent(_contentState)
  );

  useEffect(() => {
    let html = convertToHTML(editorState.getCurrentContent());
    setDescription(html);
  }, [editorState]);

  const [editorCommentState, setEditorCommentState] = useState(() =>
    EditorState.createEmpty()
  );

  useEffect(() => {
    let html = convertToHTML(editorCommentState.getCurrentContent());
    setCommentMsg(html);
  }, [editorCommentState]);

  function getAllMemberForTeam(projId) {
    getAllMembersByProjectIdAndTeams(projId.id)
      .then((resp) => {
        let data = resp.data;
        setAllMembers(data);
      })
      .catch((error) => {
        console.log("login user err " + error);
      });
  }

  function getAllTagsForProject(projId) {
    getAllTagByProjectId(projId.id)
      .then((resp) => {
        if (resp.status === 401) {
          logout();
        }
        resp.json().then((data) => {
          setAllTags(data);
        });
      })
      .catch((error) => {
        console.log("login user err " + error);
      });
  }

  const handleStartDateChange = (newValue) => {
    if (newValue) {
      setStartDate(moment(newValue).format("YYYY-MM-DDTHH:mm:ss"));
    } else {
      setStartDate(null);
    }
  };

  const handleEndDateChange = (newValue) => {
    if (newValue) {
      setEndDate(moment(newValue).format("YYYY-MM-DDTHH:mm:ss"));
    } else {
      setEndDate(null);
    }
  };

  function getCurrenttask(taskId) {
    getParticularTaskById(taskId)
      .then((resp) => {
        if (resp.status === 401) {
          logout();
        }
        resp.json().then((data) => {
          setTitle(data.title);
          setDescription(data.description);
          const _contentState = ContentState.createFromBlockArray(
            convertFromHTML(data.description)
          );
          setEditorState(EditorState.createWithContent(_contentState));

          setShortDescription(data.shortDescription);
          setProjectId(data.projectId);
          setPriority(data.priority);
          setComplexity(data.complexity);
          setStatus(data.status);
          setQcMembers(data.qcAssignedTo);
          setQcFeedback(data.qcFeedBack);
          setSDate(data.startDate || null);
          setEDate(data.endDate || null);
          setMembers(data.assignedTo);

          let arr = [];
          if (data.assignedTo) {
            arr.push(data.assignedTo);
          }
          if (
            data.qcAssignedTo &&
            data.assignedTo &&
            data.qcAssignedTo.id !== data.assignedTo.id
          ) {
            arr.push(data.qcAssignedTo);
          }

          setTaskMembers(arr);
          settags(data.tags);
          let tagArr = [];
          data.tags &&
            data.tags.length > 0 &&
            data.tags.map((m, i) => {
              tagArr.push(m.name);
            });
          setSelectedTags(tagArr);
          setComments(data.comments);
          setBugs(data.bugs || []);
          setActivity(data.activity);
          setTimeline(data.taskStatusTrack);
          setAttachments(data.attachments);
          // setSelectedTeamMembers(
          //   data.assignedTo
          //     ? data.assignedTo.firstName +
          //     " " +
          //     data.assignedTo.lastName +
          //     " (" +
          //     data.assignedTo.id +
          //     ")"
          //     : ""
          // );
          // setSelectedQCTeamMembers(
          //   data.qcAssignedTo
          //     ? data.qcAssignedTo.firstName +
          //     " " +
          //     data.qcAssignedTo.lastName +
          //     " (" +
          //     data.qcAssignedTo.id +
          //     ")"
          //     : ""
          // );
          setQcDate(data.qcEndDate || null);
          setQcSDate(data.qcStartDate || null);

          // setAllRatings(data.ratings);
        });
      })
      .catch((error) => {
        console.log("login user err " + error);
      });
  }

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

  const handleSDescriptionClick = () => {
    if (
      localStorage.getItem("role") !== "EMPLOYEE" &&
      status !== "DONE" &&
      status !== "BLOCKER"
    ) {
      setIsSdescriptionEditing(!isSdescriptionEditing);
      if (isSdescriptionEditing === true) {
        patchUpdateTask(taskId, {
          shortDescription: shortDescription,
          modifiedBy: localStorage.getItem("userId"),
        })
          .then((resp) => { })
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
    }
  };

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

  const savePriority = () => {
    if (localStorage.getItem("role") !== "EMPLOYEE") {
      if (!priorityReason || !priority) {
        toast.error("Fields cannot be blank");
        return false;
      }
      patchUpdateTask(taskId, {
        priority: priority,
        reason: priorityReason,
        modifiedBy: localStorage.getItem("userId"),
      })
        .then((resp) => {
          setPriorityReason("");
          toast.success("Priority updated successfully");
          handlePriorityClose();
          getCurrenttask(taskId);
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
    } else {
      toast.error("You dont have permission to change");
    }
  };

  const handlePriorityChange = (e) => {
    if (localStorage.getItem("role") !== "EMPLOYEE") {
      setPriority(e.target.value);
    } else {
      toast.error("You dont have permission to change");
    }
  };

  const saveComplexity = () => {
    if (localStorage.getItem("role") !== "EMPLOYEE") {
      if (!complexityReason || !complexity) {
        toast.error("Fields cannot be blank");
        return false;
      }

      patchUpdateTask(taskId, {
        complexity: complexity,
        reason: complexityReason,
        modifiedBy: localStorage.getItem("userId"),
      })
        .then((resp) => {
          setComplexityReason("");
          toast.success("Complexity updated successfully");
          handleComplexityClose();
          getCurrenttask(taskId);
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
    } else {
      toast.error("You dont have permission to change");
    }
  };

  const handleComplexityChange = (e) => {
    if (localStorage.getItem("role") !== "EMPLOYEE") {
      setComplexity(e.target.value);
    }
  };

  const refreshTaskData = () => {
    getCurrenttask(taskId);
  };

  const saveDescription = () => {
    if (localStorage.getItem("role") !== "EMPLOYEE") {
      setLoadingIcon("saving...");
      patchUpdateTask(taskId, {
        description: description,
        modifiedBy: localStorage.getItem("userId"),
      })
        .then((resp) => {
          if (resp.status) {
            setLoadingIcon("saved");
            toast.success("Description updated successfully");
          }
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
    } else {
      toast.error("You dont have permission to change");
    }
  };

  const saveMember = () => {
    const assignedTo = [];

    const findMemberById = (id) =>
      allMembers.find((member) => member.id === id);

    if (selectedDesigner) {
      const designerId = selectedDesigner.match(/\((.*?)\)$/)?.[1];
      const designer = findMemberById(designerId);
      if (designer) {
        assignedTo.push({
          memberId: designer.id,
          estimatedTime: parseInt(estimationTimeForDesigner || "0"),
        });
      }
    }

    if (selectedDeveloper) {
      const developerId = selectedDeveloper.match(/\((.*?)\)$/)?.[1];
      const developer = findMemberById(developerId);
      if (developer) {
        assignedTo.push({
          memberId: developer.id,
          estimatedTime: parseInt(estimationTimeForDeveloper || "0"),
        });
      }
    }

    if (selectedTester) {
      const testerId = selectedTester.match(/\((.*?)\)$/)?.[1];
      const tester = findMemberById(testerId);
      if (tester) {
        assignedTo.push({
          memberId: tester.id,
          estimatedTime: parseInt(estimationTimeForTester || "0"),
        });
      }
    }

    patchUpdateTask(taskId, {
      assignedTo: assignedTo,
      assignedBy: localStorage.getItem("userId"),
      modifiedBy: localStorage.getItem("userId"),
    })
      .then((resp) => {
        getCurrenttask(taskId);
        toast.success("Members updated successfully");
        handleMemberClose();
      })
      .catch((error) => {
        console.error("Error updating members:", error);
        toast.error("Failed to update members");
      });
  };

  const saveTags = () => {
    if (localStorage.getItem("role") !== "EMPLOYEE") {
      patchUpdateTask(taskId, {
        tags: selectedTags,
        modifiedBy: localStorage.getItem("userId"),
      })
        .then((resp) => {
          getCurrenttask(taskId);
          if (resp.status) {
            toast.success("Tags updated successfully");
            handleTagsClose();
          }
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
    } else {
      toast.error("You dont have permission to modify tags");
    }
  };
  const saveDueDate = () => {
    if (localStorage.getItem("role") !== "EMPLOYEE") {
      if (!sdate || !edate || !dueDateReason) {
        toast.error("Start date , end date, reason cannot be blank");
        return false;
      }
      patchUpdateTask(taskId, {
        startDate: moment(sdate).format("YYYY-MM-DD HH:mm:ss"),
        endDate: moment(edate).format("YYYY-MM-DD HH:mm:ss"),
        reason: dueDateReason,
        modifiedBy: localStorage.getItem("userId"),
      })
        .then((resp) => {
          if (resp.status) {
            setDueDateReason("");
            toast.success("Due dates updated successfully");
            handleDueDateClose();
            getCurrenttask(taskId);
          }
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
    } else {
      toast.error("You dont have permission to modify due dates");
    }
  };

  const handleSDescriptionChange = (event) => {
    setShortDescription(event.target.value);
  };

  const handleTitleClick = () => {
    if (
      localStorage.getItem("role") !== "EMPLOYEE" &&
      status !== "DONE" &&
      status !== "BLOCKER"
    ) {
      setIsTitleEditing(!isTitleEditing);
      if (isTitleEditing === true) {
        patchUpdateTask(taskId, {
          title: title,
          modifiedBy: localStorage.getItem("userId"),
        })
          .then((resp) => { })
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
    }
  };

  const handleTitleChange = (event) => {
    setTitle(event.target.value);
  };

  const addCommentsToTask = () => {
    console.log(commentMsg);
    if (!commentMsg || (commentMsg != null && commentMsg === "<p></p>")) {
      toast.error("Message cannot be blank");
      return false;
    }

    addComments(taskId, commentMsg).then((resp) => {
      if (resp.status) {
        setEditorCommentState(EditorState.createEmpty());
        toast.success("Comment added successfully");
        getCurrenttask(taskId);
       
      }
    });
  };

  const deleteAttachment = (id) => {
    deleteAttachmentById(id, taskId).then((resp) => {
      if (resp.status) {
        getCurrenttask(taskId);
        toast.success("Attachment deleted successfully!!");
      }
    });
  };
  const deleteComment = (id) => {
    deleteCommentById(id, taskId).then((resp) => {
      if (resp.status) {
        getCurrenttask(taskId);
        toast.success("Comment deleted successfully!!");
      }
    });
  };

  function getExtension(filename) {
    return filename.split(".").pop();
  }

  const onFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
  };

  

  const onFileUpload = () => {
    if (!selectedFile) {
      toast.error("Please choose file to upload");
      return false;
    }
    const formData = new FormData();
    formData.append("file", selectedFile);

    formData.append(
      "requestAttachment",
      JSON.stringify({
        uploadedBy: localStorage.getItem("userId"),
        taskId: taskId,
      })
    );

    toast.info("Uploading...");
    addAttachmnts(formData)
      .then((resp) => {
        let data = resp.data;

        getCurrenttask(taskId);
        handleAttachmentClose();
        setSelectedFile(null);
        toast.success("File uploaded successfully!");
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
  };

  const fileData = () => {
    if (selectedFile) {
      return (
        <div>
          <Typography variant="soft" level="title-md">
            File Details:
          </Typography>
          <Typography variant="soft" level="body-sm">
            File Name: {selectedFile.name}
          </Typography>

          <Typography variant="soft" level="body-sm">
            File Type: {selectedFile.type}
          </Typography>

          <Typography variant="soft" level="body-sm">
            Last Modified: {selectedFile.lastModifiedDate.toDateString()}
          </Typography>
        </div>
      );
    } else {
      return (
        <div>
          <br />
          <Typography variant="soft" level="title-sm">
            Choose before Pressing the Upload button
          </Typography>
        </div>
      );
    }
  };

  const CustomPaper = (props) => {
    return (
      <Paper elevation={8} sx={{ fontSize: "0.8rem !important" }} {...props} />
    );
  };
  const BootstrapCopyDialog = styled(Dialog)(({ theme }) => ({
    "& .MuiDialog-paper": {
      minWidth: "400px !important",
      height: "auto",
    },
    "& .MuiDialogActions-root": {
      padding: theme.spacing(1),
    },
  }));
  return (
    <>
      <DialogContent>
        <Stack direction={"row"} spacing={2} style={{ width: "100%" }}>
          <Typography
            level="h4"
            style={{ color: "#262672", fontSize: "1rem", fontWeight: "bold" }}
          >
            {taskId}:{" "}
          </Typography>

          {isTitleEditing &&
            status !== "DONE" &&
            status !== "BLOCKER" ? (
            <div style={{ width: "70%" }}>
              <TextField
                type="text"
                inputRef={(input) => input && input.focus()}
                variant="outlined"
                fullWidth
                size="small"
                value={title}
                inputProps={{
                  maxlength: 70,
                  style: {
                    fontSize: "0.8rem",
                  },
                }}
                helperText={`${title.length}/${70}`}
                onChange={handleTitleChange}
                onBlur={handleTitleClick}
              />
            </div>
          ) : (
            <Typography
              level="h4"
              onClick={handleTitleClick}
              style={{ color: "#262672", fontSize: "1rem", fontWeight: "bold" }}
            >
              {title}
            </Typography>
          )}
          {localStorage.getItem("role") !== "EMPLOYEE" && status === "TODO" ? (
            <div>
              <Tooltip title={"Delete task"}>
                <Button
                  variant="outlined"
                  color="danger"
                  startDecorator={<MdDelete size={"19px"} />}
                  size="sm"
                  onClick={toggleDeleteTaskModal}
                >
                  DELETE
                </Button>
              </Tooltip>
            </div>
          ) : (
            ""
          )}
        </Stack>

        {projectId?.projectName && (
          <Tooltip
            title={`This task is under the project (${projectId.projectName})`}
          >
            <Chip variant="soft" color="primary">
              {projectId.projectName}
            </Chip>
          </Tooltip>
        )}

        {status && (
          <Tooltip title={`Current status is (${status})`}>
            <Chip
              variant="soft"
              color="primary"
              startDecorator={
                status === "TODO" ? (
                  <LuListTodo />
                ) : status === "DESIGN" ? (
                  <GrInProgress />
                ) : status === "DEVELOPMENT" ? (
                      <GrInProgress />
                ) : status === "TESTING" ? (
                  <IoCheckmarkDoneCircleOutline />
                ) : status === "DONE" ? (
                  <FaPauseCircle />
                ) : status === "BLOCKER" ? (
                  <CiCircleRemove />
                ) : (
                  <BsListTask />
                )
              }
            >
              {status}
            </Chip>
          </Tooltip>
        )}


        {sdate && (
          <Tooltip title={`Task Start date is (${moment(sdate).format("DD-MM-YYYY hh:mm A")})`}>
            <Chip variant="soft" color="primary" startDecorator={<LuClock />}>
              {moment(sdate).format("DD-MM-YYYY hh:mm A")}
            </Chip>
          </Tooltip>
        )}

        {edate && (
          <Tooltip title={`Deadline for this task is (${moment(edate).format("DD-MM-YYYY hh:mm A")})`}>
            <Chip variant="soft" color="primary" startDecorator={<LuAlarmClock />}>
              {moment(edate).format("DD-MM-YYYY hh:mm A")}
            </Chip>
          </Tooltip>
        )}


        {priority && (
          <Tooltip title={`Priority is (${priority})`}>
            <Chip
              variant="soft"
              color="primary"
              startDecorator={
                priority === "LOW" ? (
                  <PiWifiLowFill />
                ) : priority === "MEDIUM" ? (
                  <PiWifiMediumFill />
                ) : (
                  <PiWifiHighFill />
                )
              }
            >
              {priority}
            </Chip>
          </Tooltip>
        )}

        {selectedTags?.map((each, ind) => (
          <Tooltip
            key={ind}
            title={`This task is assigned under the tags (${each})`}
          >
            <Chip
              variant="soft"
              color="primary"
              startDecorator={<IoIosPricetags />}
            >
              {each}
            </Chip>
          </Tooltip>
        ))}

        <div style={{ marginTop: "5px" }}>
          {isSdescriptionEditing &&
            status !== "DONE" &&
            status !== "BLOCKER" ? (
            <div style={{ width: "80%", height: "auto" }}>
              <TextField
                multiline
                rows={2}
                type="text"
                fullWidth
                inputRef={(input) => input && input.focus()}
                variant="outlined"
                size="small"
                value={shortDescription}
                inputProps={{
                  maxlength: 70,
                }}
                helperText={`${shortDescription.length}/${70}`}
                onChange={handleSDescriptionChange}
                onBlur={handleSDescriptionClick}
              />
            </div>
          ) : (
            <Typography level="body-sm" onClick={handleSDescriptionClick}>
              {shortDescription}
            </Typography>
          )}
        </div>
        <br></br>
        <Grid container spacing={3}>
          <Grid
            item
            md={9}
            style={{
              maxHeight: "400px",
              overflowY: "auto",
              scrollbarWidth: "thin",
            }}
          >
            <Typography
              level="title-md"
              style={{ color: "#262672" }}
              startDecorator={<ImParagraphLeft />}
            >
              Description
            </Typography>
            

            <Card
              style={{ minHeight: "300px", marginTop: "5px", width: "95%" }}
            >
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
                editorStyle={{
                  border: "1px solid lightgrey",
                  borderRadius: "5px",
                  padding: "5px",
                  minHeight: "150px",
                }}
                onEditorStateChange={setEditorState}
                hashtag={{}}
              />

              {localStorage.getItem("role") !== "EMPLOYEE" &&
                status !== "DONE" ? (
                <Button
                  variant="soft"
                  size="sm"
                  style={{ width: "20%" }}
                  onClick={saveDescription}
                >
                  {loadingIcon}
                </Button>
              ) : (
                ""
              )}
            </Card>

            {attachments && attachments.length > 0 ? (
              <div style={{ marginTop: "60px" }}>
                <Divider style={{ border: "1px grey solid" }} />
                <br></br>
                <Typography
                  level="title-md"
                  style={{
                    color: "#262672",
                    display: "flex",
                    alignItems: "center",
                    flexWrap: "wrap",
                  }}
                  startDecorator={<GrAttachment />}
                >
                  Attachments:
                </Typography>
                <Grid container>
                  {attachments &&
                    attachments.length > 0 &&
                    attachments.map((e, i) => (
                      <Card sx={{ width: "95%", marginTop: "5px" }}>
                        <Stack direction={"row"}>
                          <Grid item md={2}>
                            {getExtension(e.url) === "png" ||
                              getExtension(e.url) === "jpeg" ||
                              getExtension(e.url) === "jpg" ? (
                              <img
                                src={e.url}
                                style={{ borderRadius: "20px" }}
                              ></img>
                            ) : getExtension(e.url) === "pdf" ? (
                              <img src={pdf}></img>
                            ) : (
                              <img
                                src={link}
                                style={{
                                  width: "30%",
                                  marginLeft: "25%",
                                  marginTop: "10%",
                                }}
                              ></img>
                            )}
                          </Grid>
                          <Grid item md={7} sx={{ marginLeft: "10px" }}>
                            <>
                              <ListItem>
                                <ListItemDecorator>
                                  <Avatar
                                    size="md"
                                    variant="solid"
                                    {...stringAvatar(
                                      e.uploadedBy.firstName +
                                      " " +
                                      e.uploadedBy.lastName
                                    )}
                                  ></Avatar>
                                </ListItemDecorator>
                                <ListItemContent>
                                  <Typography level="title-sm">
                                    &nbsp;
                                    {e.uploadedBy.firstName +
                                      " " +
                                      e.uploadedBy.lastName}
                                  </Typography>
                                  <Typography level="body-sm" noWrap>
                                    &nbsp;uploaded on&nbsp;{e.uploadedDate}
                                  </Typography>

                                </ListItemContent>
                              </ListItem>

                              <Stack direction={"row"}>
                                <IconButton>
                                  <Link target="_blank" href={e.url}>
                                    <LuDownload />
                                  </Link>
                                </IconButton>

                                {localStorage.getItem("role") !==
                                  "EMPLOYEE" ? (
                                  <IconButton
                                    onClick={() => deleteAttachment(e.id)}
                                  >
                                    <DeleteOutline
                                      style={{
                                        fontSize: "18px",
                                        color: "#0B6BCB",
                                      }}
                                    />
                                  </IconButton>
                                ) : localStorage.getItem("role") ===
                                  "EMPLOYEE" &&
                                  localStorage.getItem("userId") ===
                                  e.uploadedBy.id ? (
                                  <IconButton
                                    onClick={() => deleteAttachment(e.id)}
                                  >
                                    <MdDeleteOutline />
                                  </IconButton>
                                ) : (
                                  ""
                                )}
                              </Stack>
                            </>
                          </Grid>
                        </Stack>
                      </Card>
                    ))}
                </Grid>
              </div>
            ) : (
              ""
            )}

            <div style={{ marginTop: "50px" }}>
              <Divider style={{ border: "1px grey solid" }} />
              <br></br>
              <Typography
                ref={commentRef}
                level="title-md"
                style={{
                  color: "#262672",
                  display: "flex",
                  alignItems: "center",
                  flexWrap: "wrap",
                }}
                startDecorator={<FaRegComments />}
              >
                Comments:
              </Typography>

              {comments.length > 0 &&
                comments.map((e, i) => (
                  <>
                    <Stack direction={"row"} spacing={2}>
                      <Avatar
                        variant="solid"
                        {...stringAvatar(
                          e.createdBy.firstName + " " + e.createdBy.lastName
                        )}
                      ></Avatar>
                      <Stack direction={"column"} style={{ width: "100%" }}>
                        <Stack direction={"column"} style={{ width: "100%" }}>
                          <Typography level={"title-md"}>
                            {e.createdBy.firstName + " " + e.createdBy.lastName}
                          </Typography>

                          <Typography level={"body-sm"}>
                            {" "}
                            commented on {e.createdDate}{" "}
                          </Typography>
                        </Stack>
                        <Card style={{ width: "95%" }}>
                          <div
                            className="content"
                            dangerouslySetInnerHTML={{ __html: e.message }}
                          ></div>
                        </Card>
                        {i === comments.length - 1 &&
                          e.createdBy.id === localStorage.getItem("userId") ? (
                          <Button
                            size="sm"
                            variant="soft"
                            style={{
                              width: "13%",
                              marginTop: "3px",
                              float: "right",
                            }}
                            onClick={() => deleteComment(e.id)}
                            startDecorator={
                              <DeleteOutline style={{ fontSize: "18px" }} />
                            }
                          >
                            DELETE
                          </Button>
                        ) : (
                          ""
                        )}
                      </Stack>
                    </Stack>
                  </>
                ))}
              <br></br>

              <Stack direction={"row"} spacing={2}>
                <Avatar
                  variant="solid"
                  {...stringAvatar(
                    localStorage.getItem("firstname") +
                    " " +
                    localStorage.getItem("lastname")
                  )}
                ></Avatar>
                <Stack
                  direction={"column"}
                  spacing={2}
                  justifyContent={"flex-start"}
                  sx={{ width: "95%" }}
                >
                  <Card style={{ minHeight: "200px", width: "95%" }}>
                    <Editor
                      wrapperClassName="demo-wrapper"
                      editorClassName="demo-editor hideScroll"
                      toolbarClassName="toolbar-class"
                      toolbarStyle={{}}
                      editorStyle={{ height: "auto" }}
                      placeholder="Enter your comments here..."
                      toolbar={{
                        options: [
                          "inline",
                          "blockType",
                          "fontSize",
                          "list",
                          "textAlign",
                          "history",
                          "link",
                          "emoji",
                        ],
                        inline: { inDropdown: true },
                        list: { inDropdown: true },
                        textAlign: { inDropdown: true },
                        link: { inDropdown: true },
                        history: { inDropdown: true },
                      }}
                      editorState={editorCommentState}
                      onEditorStateChange={setEditorCommentState}
                      hashtag={{}}
                    />
                  </Card>
                  <Button
                    variant="soft"
                    size="sm"
                    style={{ width: "20%" }}
                    onClick={addCommentsToTask}
                  >
                    SUBMIT
                  </Button>
                </Stack>
              </Stack>
            </div>

            <div style={{ marginTop: "50px" }}>
              <Divider style={{ border: "1px grey solid" }} />
              <br></br>
              <Typography
                ref={activityRef}
                level="title-md"
                style={{
                  color: "#262672",
                  display: "flex",
                  alignItems: "center",
                  flexWrap: "wrap",
                }}
                startDecorator={<RxActivityLog />}
              >
                Activity:
              </Typography>

              {activity &&
                activity.length > 0 &&
                activity.map((e, i) => (
                  <>
                    <Stack direction={"row"} spacing={1}>
                      <Avatar
                        variant="solid"
                        size="sm"
                        {...stringAvatar(
                          e.createdBy
                            ? e.createdBy.firstName + " " + e.createdBy.lastName
                            : "U U"
                        )}
                      ></Avatar>

                      <Stack direction={"column"} style={{ width: "100%" }}>
                        <Typography level={"title-md"}>
                          {e.createdBy
                            ? e.createdBy.firstName + " " + e.createdBy.lastName
                            : ""}
                          <Typography level={"body-sm"} fontWeight={"400"}>
                            {" " + e.message}
                          </Typography>
                        </Typography>

                        <Stack direction={"row"}>
                          <Typography level={"body-sm"} fontWeight={"400"}>
                            {moment(e.createdDate).format("llll")}
                          </Typography>
                        </Stack>
                      </Stack>
                    </Stack>
                    <br></br>
                  </>
                ))}
            </div>

            <div style={{ marginTop: "50px" }}>
              <Divider style={{ border: "1px grey solid" }} />
              <br></br>
              <Typography
                ref={timelineRef}
                level="title-md"
                style={{
                  color: "#262672",
                  display: "flex",
                  alignItems: "center",
                  flexWrap: "wrap",
                }}
                startDecorator={<TimelineIcon />}
              >
                Timeline:
              </Typography>
              <Timeline position="right">
                {timeline &&
                  timeline.length > 0 &&
                  timeline.map((e, i) => (
                    <>
                      <TimelineItem>
                        <TimelineOppositeContent
                          sx={{ m: "auto 0" }}
                          align="right"
                          variant="overline"
                          color="text.primary"
                        >
                          {e.changedStatus}
                        </TimelineOppositeContent>
                        <TimelineSeparator>
                          <TimelineConnector />
                          {/* <TimelineDot color="success">
                            {timelineDataArray[e.changedStatus].icon}
                          </TimelineDot> */}
                          <TimelineConnector />
                        </TimelineSeparator>
                        <TimelineContent sx={{ py: "12px", px: 2 }}>
                          <Card>
                            <Stack direction={"row"} spacing={1}>
                              <Avatar
                                variant="soft"
                                size="sm"
                                {...stringAvatar(
                                  e.member
                                    ? e.member.firstName +
                                    " " +
                                    e.member.lastName
                                    : "U U"
                                )}
                              ></Avatar>

                              <Stack
                                direction={"column"}
                                style={{ width: "100%" }}
                              >
                                <Typography level={"title-md"}>
                                  {e.member
                                    ? e.member.firstName +
                                    " " +
                                    e.member.lastName
                                    : ""}
                                </Typography>
                              </Stack>
                            </Stack>
                            <Typography level="title-sm" fontStyle={"italic"}>
                              {'"' + e.message + '"'}
                            </Typography>
                            <Typography level="body-sm">
                              {moment(e.changedTime).format("llll")}
                            </Typography>
                          </Card>
                        </TimelineContent>
                      </TimelineItem>
                    </>
                  ))}
              </Timeline>
            </div>

            <div style={{ marginTop: '50px' }}>
              <Divider style={{ border: '1px grey solid' }} />
              <br />
              <Typography
                ref={bugListRef}
                level="title-md"
                style={{
                  color: '#262672',
                  display: 'flex',
                  alignItems: 'center',
                  flexWrap: 'wrap',
                }}
                startDecorator={<BugReportOutlined />}
              >
                Bug Lists:
              </Typography>

              {bugs.length > 0 ? (
                <div
                  style={{
                    display: "grid",
                    gridTemplateColumns: "repeat(2, 1fr)",
                    gap: "20px",
                    marginTop: '16px',
                    marginBottom: '16px',
                  }}
                >
                  {bugs.map((bug, index) => {
                    const currentStatus = localStatusMap[bug.id] ?? bug.status ?? "";

                    return (
                      <div key={index} style={{ border: "1px solid #ccc", padding: "12px", borderRadius: "6px" }}>
                        {/* <Typography
                          level="title-sm"
                          style={{ display: "flex", alignItems: "center", gap: "8px" }}
                        >
                          <MdBugReport  />
                          {bug.title || "Title missing"} ({bug.severity || "Severity missing"})
                        </Typography> */}

                        <Typography
                          level="title-sm"
                          style={{
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "space-between",
                            gap: "8px",
                          }}
                        >
                          <span style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                            <MdBugReport />
                            {bug.title || "Title missing"}
                          </span>

                          <Chip
                            size="sm"
                            variant={(bug.severity === "LOW") ? "soft" : (bug.severity === "MEDIUM") ? "soft" : "solid"}
                            sx={{ fontSize: '0.6rem', borderRadius: '2px', animation: (bug.severity === "HIGH") ? `${blink} 0.5s linear infinite` : "" }}
                            color={(bug.severity === "LOW") ? "neutral" : (bug.severity === "MEDIUM") ? "primary" : "warning"}
                          >
                            {bug.severity || "Severity missing"}
                          </Chip>
                        </Typography>


                        <Typography level="body-sm" style={{ marginTop: "8px" }}>
                          {bug.description || "Description missing"}
                        </Typography>

                        <div style={{ display: "flex", alignItems: "center", marginTop: "8px" }}>
                          <Select
                            size="small"
                            value={currentStatus}
                            onChange={(e) =>
                              setLocalStatusMap((prev) => ({
                                ...prev,
                                [bug.id]: e.target.value,
                              }))
                            }
                            style={{ minWidth: "160px" }}
                            disabled={
                              status === "DONE" ||
                              localStorage.getItem("designation")?.toUpperCase() === "DESIGNER" ||
                              (localStorage.getItem("designation")?.toUpperCase() === "TESTER" &&
                                status === "DEVELOPMENT") ||
                              (localStorage.getItem("designation")?.toUpperCase() === "DEVELOPER" &&
                                status === "TESTING") ||
                              localStorage.getItem("role")?.toUpperCase() === "MANAGER"
                            }

                          >
                            {[
                              { value: "FIXED", label: "Fixed" },
                              { value: "NOT_AN_ISSUE", label: "Not An Issue" },
                              { value: "NOT_FIXED", label: "Not Fixed" },
                              { value: "VERIFIED", label: "Verified" },
                            ].map((option) => {
                              const isDisabled =
                                (designation === "TESTER" &&
                                  ["FIXED", "WIP", "NOT_AN_ISSUE"].includes(option.value)) ||
                                (designation === "DEVELOPER" &&
                                  ["NOT_FIXED", "VERIFIED"].includes(option.value));

                              return (
                                <MenuItem
                                  key={option.value}
                                  value={option.value}
                                  disabled={isDisabled}
                                >
                                  {option.label}
                                </MenuItem>
                              );
                            })}
                          </Select>



                        </div>
                      </div>
                    );
                  })}
                </div>
              ) : (
                <Typography level="body-sm" style={{ color: "gray", marginTop: '16px' }}>
                  No bugs reported yet.
                </Typography>
              )}


              {bugs.length > 0 && status !== "DONE" && localStorage.getItem("designation")?.toUpperCase() !== "DESIGNER" &&
                localStorage.getItem("role")?.toUpperCase() !== "MANAGER" && (
                  <div style={{ marginTop: '24px', marginBottom: '16px' }}>
                    {!(designation === "DEVELOPER" && status === "TESTING") &&
                      !(designation === "TESTER" && status === "DEVELOPMENT") && (
                        <div style={{ marginTop: '24px', marginBottom: '16px' }}>
                          <Button
                            variant="soft"
                            size="sm"
                            onClick={async () => {
                              const updates = bugs
                                .map((bug) => {
                                  const status = localStatusMap[bug.id] ?? bug.status;
                                  return status ? { id: bug.id, status } : null;
                                })
                                .filter(Boolean);

                              if (updates.length === 0) {
                                toast.error("Please select status for at least one bug.");
                                return;
                              }

                              try {
                                await updateBugStatus(taskId, updates);
                                toast.success("Statuses updated successfully.");
                                refreshTaskData();
                              } catch (err) {
                                console.error(err);
                                toast.error(err?.response?.data?.message);
                              }
                            }}
                          >
                            Update All
                          </Button>
                        </div>
                      )}
                  </div>
                )}


              {localStorage.getItem("designation")?.toUpperCase() === "TESTER" && status === "TESTING" && (
                <div style={{ marginTop: "32px" }}>
                  <h1>Report Bug</h1>
                  <BugReportForm
                    taskId={taskId}
                    reportBugs={reportBugs}
                    onBugReported={refreshTaskData}
                    bug={bugs}
                  />
                </div>
              )}
            </div>


          </Grid>
          <Grid item md={3}>
            <Button
              variant={"soft"}
              style={{ marginBottom: "5px", justifyContent: "flex-start" }}
              startDecorator={<LuUsers />}
              fullWidth
              aria-describedby={idMember}
              onClick={handleMemberClick}
            >
              MEMBER
            </Button>
            {(status === "DEVELOPMENT" &&
              role !== "MANAGER" &&
              designation === "DEVELOPER") ||
              (status === "TESTING" &&
                role !== "MANAGER" &&
                designation === "TESTER") ? (
              <Button
                variant={"soft"}
                style={{ marginBottom: "5px", justifyContent: "flex-start" }}
                startDecorator={<RiUserSearchLine />}
                fullWidth
                aria-describedby={idQCMember}
                onClick={(e) => handleQCMemberClick(e, status)}
              >
                ASSIGN {status}
              </Button>
            ) : null}

            <Button
              variant={"soft"}
              style={{ marginBottom: "5px", justifyContent: "flex-start" }}
              startDecorator={<PiCellSignalFullBold />}
              fullWidth
              aria-describedby={idComplexity}
              onClick={handleComplexityClick}
            >
              COMPLEXITY
            </Button>
            <Button
              variant={"soft"}
              style={{ marginBottom: "5px", justifyContent: "flex-start" }}
              startDecorator={<MdAccessAlarm />}
              fullWidth
              aria-describedby={idDueDate}
              onClick={handleDueDateClick}
            >
              DUE DATE
            </Button>
            <Button
              variant={"soft"}
              style={{ marginBottom: "5px", justifyContent: "flex-start" }}
              startDecorator={<LuUsers />}
              fullWidth
              aria-describedby={idPriority}
              onClick={handlePriorityClick}
            >
              PRIORITY
            </Button>
            <Button
              variant={"soft"}
              style={{ marginBottom: "5px", justifyContent: "flex-start" }}
              startDecorator={<IoPricetagsOutline />}
              fullWidth
              aria-describedby={idTags}
              onClick={handleTagsClick}
            >
              TAGS
            </Button>
            {status !== "DONE" ? (
              <Button
                variant={"soft"}
                style={{ marginBottom: "5px", justifyContent: "flex-start" }}
                startDecorator={<GrAttachment />}
                fullWidth
                aria-describedby={idAttachment}
                onClick={handleAttachmentClick}
              >
                ATTACHMENTS
              </Button>
            ) : (
              ""
            )}
            <Button
              variant={"soft"}
              style={{ marginBottom: "5px", justifyContent: "flex-start" }}
              startDecorator={<FaRegComments />}
              fullWidth
              onClick={() => commentRef.current.scrollIntoView()}
            >
              COMMENTS
            </Button>
            <Button
              variant={"soft"}
              style={{ marginBottom: "5px", justifyContent: "flex-start" }}
              startDecorator={<RxActivityLog />}
              fullWidth
              onClick={() => activityRef.current.scrollIntoView()}
            >
              ACTIVITY
            </Button>
            <Button
              variant={"soft"}
              style={{ marginBottom: "5px", justifyContent: "flex-start" }}
              startDecorator={<MdOutlineViewTimeline />}
              fullWidth
              onClick={() => timelineRef.current.scrollIntoView()}
            >
              TIMELINE
            </Button>

            <Button
              variant={"soft"}
              style={{ marginBottom: "5px", justifyContent: "flex-start" }}
              startDecorator={<MdBugReport />}
              fullWidth
              onClick={() => bugListRef.current.scrollIntoView()}
            >
              BUG
            </Button>
            {/* {status === "DONE" ? (
              <Button
                variant={"soft"}
                style={{ marginBottom: "5px", justifyContent: "flex-start" }}
                startDecorator={<VscFeedback />}
                fullWidth
                aria-describedby={idRatings}
                onClick={handleRatingsClick}
              >
                RATINGS
              </Button>
            ) : (
              ""
            )} */}

            <Popover
              id={idMember}
              open={openMember}
              anchorEl={anchorMemberEl}
              onClose={handleMemberClose}
              anchorOrigin={{
                vertical: "top",
                horizontal: "left",
              }}
              transformOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
              PaperProps={{
                style: { width: "30%" },
              }}
            >
              <Stack direction={"column"} sx={{ padding: "30px" }}>
                {/* Designer */}
                <Box mb={2}>
                  <Typography
                    variant="subtitle2"
                    sx={{ fontSize: "0.9rem", fontWeight: "bold", mb: 1 }}
                  >
                    Designer
                  </Typography>
                  <Autocomplete
                    fullWidth
                    disabled={status === "DONE"}
                    options={allDesigners.map(
                      (option) =>
                        `${option.firstName} ${option.lastName} (${option.id})`
                    )}
                    value={selectedDesigner}
                    onChange={handleDesignerChange}
                    renderInput={(params) => (
                      <TextField
                        {...params}
                        size="small"
                        variant="outlined"
                        label="Select Designer"
                        placeholder="Select Designer"
                        InputProps={{
                          ...params.InputProps,
                          style: { fontSize: "0.8rem" },
                        }}
                      />
                    )}
                  />
                  <TextField
                    type="number"
                    fullWidth
                    size="small"
                    label="Designer Estimation Time (minutes)"
                    placeholder="Enter estimation time in minutes"
                    value={estimationTimeForDesigner}
                    onChange={(e) =>
                      setEstimationTimeForDesigner(e.target.value)
                    }
                    inputProps={{ min: 0 }}
                    sx={{ mt: 1 }}
                  />
                </Box>

                {/* Developer */}

                <Box mb={2}>
                  <Typography
                    variant="subtitle2"
                    sx={{ fontSize: "0.9rem", fontWeight: "bold", mb: 1 }}
                  >
                    Developer
                  </Typography>
                  <Autocomplete
                    fullWidth
                    disabled={status === "DONE"}
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
                        label="Select Developer"
                        placeholder="Select Developer"
                        InputProps={{
                          ...params.InputProps,
                          style: { fontSize: "0.8rem" },
                        }}
                      />
                    )}
                  />

                  <TextField
                    type="number"
                    fullWidth
                    size="small"
                    variant="outlined"
                    label="Developer Estimation Time (minutes)"
                    placeholder="Enter estimation time in minutes"
                    value={estimationTimeForDeveloper}
                    onChange={(e) =>
                      setEstimationTimeForDeveloper(e.target.value)
                    }
                    inputProps={{ min: 0 }}
                    InputLabelProps={{ shrink: true }}
                    sx={{ fontSize: "0.9rem", mt: 1 }}
                  />
                </Box>

                {/* Tester */}

                <Box mb={2}>
                  <Typography
                    variant="subtitle2"
                    sx={{ fontSize: "0.9rem", fontWeight: "bold", mb: 1 }}
                  >
                    Tester
                  </Typography>
                  <Autocomplete
                    fullWidth
                    disabled={status === "DONE"}
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
                        label="Select Tester"
                        placeholder="Select Tester"
                        InputProps={{
                          ...params.InputProps,
                          style: { fontSize: "0.8rem" },
                        }}
                      />
                    )}
                  />

                  <TextField
                    type="number"
                    fullWidth
                    size="small"
                    variant="outlined"
                    label="Tester Estimation Time (minutes)"
                    placeholder="Enter estimation time in minutes"
                    value={estimationTimeForTester}
                    onChange={(e) => setEstimationTimeForTester(e.target.value)}
                    inputProps={{ min: 0 }}
                    InputLabelProps={{ shrink: true }}
                    sx={{ fontSize: "0.9rem", mt: 1 }}
                  />
                </Box>

                {localStorage.getItem("role") !== "EMPLOYEE" &&
                  status !== "DONE" && (
                    <Button
                      variant="soft"
                      size="sm"
                      style={{ width: "50%" }}
                      onClick={saveMember}
                    >
                      Save
                    </Button>
                  )}

                <Divider />
              </Stack>
            </Popover>

            <Popover
              id={idQCMember}
              open={openQCMember}
              anchorEl={anchorQCMemberEl}
              onClose={handleQCMemberClose}
              anchorOrigin={{
                vertical: "top",
                horizontal: "left",
              }}
              transformOrigin={{
                vertical: "center",
                horizontal: "right",
              }}
              PaperProps={{
                style: { width: "30%" },
              }}
            >
              <Stack
                direction="column"
                spacing={2}
                sx={{ padding: "30px" }}
              >
                <Autocomplete
                  id="tags-filled"
                  style={{ width: "100%" }}
                  PaperComponent={CustomPaper}
                  options={allMembers.map(
                    (option) => `${option.firstName} ${option.lastName} (${option.id})`
                  )}
                  disabled={status === "DONE"}
                  value={selectedRole === 'development' ? selectedDeveloper : selectedTester}
                  onChange={(event, newValue) => {
                    if (selectedRole === 'development') {
                      setSelectedDeveloper(newValue);
                    } else {
                      setSelectedTester(newValue);
                    }
                  }}
                  renderInput={(params) => (
                    <TextField
                      {...params}
                      variant="outlined"
                      size="small"
                      fullWidth
                      disabled={status === "DONE"}
                      InputProps={{
                        ...params.InputProps,
                        style: { fontSize: "0.8rem" },
                      }}
                      InputLabelProps={{
                        ...params.InputLabelProps,
                        style: { fontSize: "0.8rem" },
                      }}
                      label={`Add ${selectedRole === 'development' ? 'Developers' : 'Testers'}`}
                      placeholder={`Add ${selectedRole === 'development' ? 'Developers' : 'Testers'}`}
                    />
                  )}
                />

                <LocalizationProvider dateAdapter={AdapterDateFns}>
                  <DesktopDateTimePicker
                    label="Start date"
                    value={startDate ? moment(startDate) : null}
                    onChange={handleStartDateChange}
                    disablePast
                    disabled={status === "DONE"}
                    renderInput={(params) => (
                      <TextField
                        {...params}
                        size="small"
                        fullWidth
                        sx={{
                          "& .MuiInputBase-input.Mui-disabled": {
                            color: "#000",
                            WebkitTextFillColor: "#000",
                          },
                          "& .MuiOutlinedInput-notchedOutline": {
                            borderColor: "#000",
                          },
                        }}
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

                <LocalizationProvider dateAdapter={AdapterDateFns}>
                  <DesktopDateTimePicker
                    label="End date"
                    value={endDate ? moment(endDate) : null}
                    onChange={handleEndDateChange}
                    disablePast
                    minDateTime={startDate ? moment(startDate) : null}
                    disabled={status === "DONE"}
                    renderInput={(params) => (
                      <TextField
                        {...params}
                        size="small"
                        fullWidth
                        sx={{
                          "& .MuiInputBase-input.Mui-disabled": {
                            color: "#000",
                            WebkitTextFillColor: "#000",
                          },
                          "& .MuiOutlinedInput-notchedOutline": {
                            borderColor: "#000",
                          },
                        }}
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

                {status !== "DONE" && (
                  <Button
                    variant="soft"
                    size="sm"
                    style={{ width: "50%" }}
                    onClick={handleAssignDeveloper}
                    disabled={
                      (selectedRole === "development" && !selectedDeveloper) ||
                      (selectedRole === "testing" && !selectedTester) ||
                      !startDate ||
                      !endDate
                    }
                  >
                    {selectedRole === "development"
                      ? "ASSIGN DEVELOPER"
                      : "ASSIGN TESTER"}
                  </Button>
                )}
              </Stack>

            </Popover>

            <Popover
              id={idComplexity}
              open={openComplexity}
              anchorEl={anchorComplexityEl}
              onClose={handleComplexityClose}
              anchorOrigin={{
                vertical: "top",
                horizontal: "left",
              }}
              transformOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
              PaperProps={{
                style: { width: "30%" },
              }}
            >
              <Stack direction={"column"} sx={{ padding: "30px" }}>
                <Box sx={{ width: 270 }}>
                  <Typography>Complexity:</Typography>
                  <Slider
                    aria-label="Small steps"
                    defaultValue={1}
                    step={1}
                    marks
                    
                    value={complexity}
                    onChange={status === "DONE" ? "" : handleComplexityChange}
                    min={1}
                    max={10}
                    valueLabelDisplay="auto"
                  />

                  {localStorage.getItem("role") !== "EMPLOYEE" &&
                    status !== "DONE" ? (
                    <>
                      <br></br>

                      <TextField
                        id="outlined-multiline-flexible"
                        label="Reason"
                        multiline
                        minRows={3}
                        maxRows={5}
                        type="text"
                        value={complexityReason}
                        inputProps={{
                          maxlength: 200,
                          style: {
                            fontSize: "0.8rem",
                          },
                        }}
                        helperText={`${complexityReason.length}/${200}`}
                        onChange={(e) => setComplexityReason(e.target.value)}
                        fullWidth
                        inputRef={(input) => input && input.focus()}
                        variant="outlined"
                        size="small"
                      />
                      <Tooltip variant="soft" title="Save complexity">
                        <Button variant="soft" onClick={saveComplexity}>
                          SUBMIT
                        </Button>
                      </Tooltip>
                    </>
                  ) : (
                    ""
                  )}
                </Box>
              </Stack>
            </Popover>
            <Popover
              id={idDueDate}
              open={openDueDate}
              anchorEl={anchorDueDateEl}
              onClose={handleDueDateClose}
              anchorOrigin={{
                vertical: "top",
                horizontal: "left",
              }}
              transformOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
            >
              <Stack direction={"column"} sx={{ padding: "30px" }}>
 
  <LocalizationProvider
    fullWidth
    dateAdapter={AdapterDateFns}
    style={{ maxWidth: "20% !important" }}
   >
    <DesktopDateTimePicker
      label="Start date"
      fullWidth
      value={(sdate)}
      disablePast={true}
      disabled={status === "DONE"}
      size="small"
      onChange={handleSDateChange}
      renderInput={(params) => (
        <TextField
          size="small"
          {...params}
          sx={{
            "& .MuiInputBase-input.Mui-disabled": {
              color: "#000",
              WebkitTextFillColor: "#000",
            },
            "& .MuiOutlinedInput-notchedOutline": {
              borderColor: "#000",
            },
          }}
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
  </LocalizationProvider>{" "}
  &nbsp;&nbsp;&nbsp;

  {/* End Date Picker */}
  <LocalizationProvider
    fullWidth
    dateAdapter={AdapterDateFns}
    style={{ maxWidth: "20% !important" }}
  >
    <DesktopDateTimePicker
      label="End date"
      fullWidth
      value={(edate)}
      disablePast={true}
      disabled={status === "DONE"}
      size="small"
      minDateTime={sdate}
      onChange={handleEDateChange}
      renderInput={(params) => (
        <TextField
          size="small"
          {...params}
          sx={{
            "& .MuiInputBase-input.Mui-disabled": {
              color: "#000",
              WebkitTextFillColor: "#000",
            },
            "& .MuiOutlinedInput-notchedOutline": {
              borderColor: "#000",
            },
          }}
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
  </LocalizationProvider>{" "}
  &nbsp;&nbsp;&nbsp;

  {/* Reason Section */}
  {localStorage.getItem("role") !== "EMPLOYEE" && status !== "DONE" ? (
    <>
      <br></br>
      <TextField
        id="outlined-multiline-flexible"
        label="Reason"
        multiline
        minRows={3}
        maxRows={5}
        type="text"
        value={dueDateReason}
        inputProps={{
          maxlength: 200,
          style: {
            fontSize: "0.8rem",
          },
        }}
        helperText={`${dueDateReason.length}/${200}`}
        onChange={(e) => setDueDateReason(e.target.value)}
        fullWidth
        inputRef={(input) => input && input.focus()}
        variant="outlined"
        size="small"
      />
      <br></br>
      <Button variant="soft" onClick={saveDueDate}>
        SUBMIT
      </Button>
    </>
  ) : (
    ""
  )}
</Stack>


            </Popover>
            <Popover
              id={idTags}
              open={openTags}
              anchorEl={anchorTagsEl}
              onClose={handleTagsClose}
              anchorOrigin={{
                vertical: "top",
                horizontal: "left",
              }}
              transformOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
              PaperProps={{
                style: { width: "30%" },
              }}
            >
              <Stack direction={"column"} sx={{ padding: "30px" }}>
                <Autocomplete
                  multiple
                  id="tags-filled"
                  style={{ width: "100%" }}
                  PaperComponent={CustomPaper}
                  size="small"
                  disabled={status === "DONE"}
                  options={
                    allTags &&
                    allTags.length > 0 &&
                    allTags.map((option) => option.name)
                  }
                  value={selectedTags}
                  onChange={(event, newValue) => {
                    settags(newValue);
                    setSelectedTags(newValue);
                  }}
                  renderInput={(params) => (
                    <TextField
                      {...params}
                      variant="outlined"
                      style={{ width: "100%" }}
                      fullWidth
                      InputProps={{
                        ...params.InputProps,
                        style: { fontSize: "0.8rem" },
                      }}
                      InputLabelProps={{
                        ...params.InputLabelProps,
                        style: { fontSize: "0.8rem" },
                      }}
                      size="small"
                      label="Add Tags"
                      placeholder="Add Tags"
                    />
                  )}
                />
                <br></br>

                {status === "DONE" ? (
                  <></>
                ) : (
                  <Button
                    variant="soft"
                    size="sm"
                    style={{ width: "50%" }}
                    onClick={saveTags}
                  >
                    Save
                  </Button>
                )}

                <Divider />

                <Typography
                  level={"title-md"}
                  color="primary"
                  style={{ marginTop: "5px" }}
                >
                  Tags
                </Typography>
                {tags && tags.length > 0
                  ? tags.map((each, ind) => (
                    <>
                      <ListItemDecorator>
                        <Typography
                          fontWeight="md"
                          class="font-bold"
                          startDecorator={<IoPricetagsOutline />}
                          sx={{ color: "#262673", fontSize: "0.7rem" }}
                        >
                          {each.name}
                        </Typography>
                      </ListItemDecorator>
                    </>
                  ))
                  : ""}
              </Stack>
            </Popover>
            <Popover
              id={idAttachment}
              open={openAttachment}
              anchorEl={anchorAttachmentEl}
              onClose={handleAttachmentClose}
              anchorOrigin={{
                vertical: "top",
                horizontal: "left",
              }}
              transformOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
            >
              <Stack direction={"column"} sx={{ padding: "30px" }}>
                <div>
                  <input type="file" onChange={onFileChange} />
                  <Button variant="soft" onClick={onFileUpload}>
                    Upload!
                  </Button>
                </div>
                {fileData()}
              </Stack>
            </Popover>

            <Popover
              id={idPriority}
              open={openPriority}
              anchorEl={anchorPriorityEl}
              onClose={handlePriorityClose}
              anchorOrigin={{
                vertical: "top",
                horizontal: "left",
              }}
              transformOrigin={{
                vertical: "top",
                horizontal: "right",
              }}
            >
              <Stack direction={"column"} sx={{ padding: "30px" }}>
                <FormControl>
                  <FormLabel>Priority</FormLabel>
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
                          disabled={status === "DONE"}
                          variant="soft"
                          sx={{
                            mb: 2,
                          }}
                        />
                        <Typography level="body-sm" sx={{ mt: 1 }}>
                          {num.label}
                        </Typography>
                      </Sheet>
                    ))}
                  </RadioGroup>
                </FormControl>
                {localStorage.getItem("role") !== "EMPLOYEE" &&
                  status !== "DONE" ? (
                  <>
                    <br></br>

                    <TextField
                      id="outlined-multiline-flexible"
                      label="Reason"
                      multiline
                      minRows={3}
                      maxRows={5}
                      type="text"
                      value={priorityReason}
                      inputProps={{
                        maxlength: 200,
                        style: {
                          fontSize: "0.8rem",
                        },
                      }}
                      helperText={`${priorityReason.length}/${200}`}
                      onChange={(e) => setPriorityReason(e.target.value)}
                      fullWidth
                      inputRef={(input) => input && input.focus()}
                      variant="outlined"
                      size="small"
                    />
                    <Button variant="soft" onClick={savePriority}>
                      SUBMIT
                    </Button>
                  </>
                ) : (
                  ""
                )}
              </Stack>
            </Popover>
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button onClick={viewTaskModal}>Close</Button>
      </DialogActions>

      <BootstrapCopyDialog
        onClose={toggleDeleteTaskModal}
        aria-labelledby="customized-dialog-title"
        open={deleteTaskShow}
      >
        <DialogContent>
          <Typography>Do you want to delete this task "{taskId}"</Typography>
        </DialogContent>
        <DialogActions>
          <Button variant="soft" onClick={deleteTaskById}>
            Yes delete it
          </Button>
          <Button variant="outlined" onClick={toggleDeleteTaskModal}>
            Close
          </Button>
        </DialogActions>
      </BootstrapCopyDialog>
    </>
  );
}

export default Popup;
