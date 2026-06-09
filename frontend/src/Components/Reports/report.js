import SearchPIcon from "@mui/icons-material/Search";
import { Avatar, Button, Card, Stack, Typography } from "@mui/joy";
import { Chip, Paper, TextField } from "@mui/material";
import Autocomplete from "@mui/material/Autocomplete";
import { DesktopDatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import moment from "moment";
import React from "react";
import { toast } from "react-toastify";
import {
  getAllActiveMembers,
  getAllTeamMembers,
  getAllTeams,
  getAllTeamsForAdmin,
  getIndividualPerformance,
  getTeamById,
  logout,
  stringAvatar,
} from "../../service/service-call";
import ReportCard from "./reportDetailCard";


export default function Reports() {
  const [allTeams, setAllTeams] = React.useState([]);
  const [selectedTeams, setSelectedTeams] = React.useState("");
  const [selectedTeamIds, setSelectedTeamIds] = React.useState("");

  const [searchLoading, setSearchLoading] = React.useState(false);
  const [allMembers, setAllMembers] = React.useState([]);
  const [selectedTeamMembers, setSelectedTeamMembers] = React.useState("");
  const [performanceData, setPerformanceData] = React.useState([]);
    

  const [sdate, setSDate] = React.useState(
    moment().subtract(1, "days").format("YYYY-MM-DD")
  );
  const [edate, setEDate] = React.useState(moment().format("YYYY-MM-DD"));

  const handleEDateChange = (e) => {
    setEDate(moment(e).format("YYYY-MM-DD"));
  };

  const handleSDateChange = (e) => {
    setSDate(moment(e).format("YYYY-MM-DD"));
  };

React.useEffect(() => {
  const role = localStorage.getItem("role");
  if (role === "MANAGER") getAllTeam();
}, []);

React.useEffect(() => {
  if (selectedTeams || selectedTeamMembers || sdate || edate) {
    getIndividualPerformanceReport();
  }
}, [selectedTeams, selectedTeamMembers, sdate, edate]);

  function getAllTeam() {
    getAllTeams("")
      .then((resp) => {
        if (resp.status === 401) {
          logout();
        }
        resp.json().then((data) => {
          setAllTeams(data);
        });
      })
      .catch((error) => {
        console.log("login user err " + error);
      });
  }

  // function getAllTeamForAdmin() {
  //   getAllTeamsForAdmin("")
  //     .then((resp) => {
  //       if (resp.status === 401) {
  //         logout();
  //       }
  //       resp.json().then((data) => {
  //         setAllTeams(data);
  //       });
  //     })
  //     .catch((error) => {
  //       console.log("login user err " + error);
  //     });
  // }

  // function getAllMemberForTeam() {
  //   getAllActiveMembers()
  //     .then((resp) => {
  //       if (resp.status === 401) {
  //         logout();
  //       }
  //       resp.json().then((data) => {
  //         setAllMembers(data);
  //       });
  //     })
  //     .catch((error) => {
  //       console.log("login user err " + error);
  //     });
  // }

  // function getAllTeamMemberForAdmin() {
  //   getAllTeamMembers()
  //     .then((resp) => {
  //       if (resp.status === 401) {
  //         logout();
  //       }
  //       resp.json().then((data) => {
  //         setAllMembers(data);
  //       });
  //     })
  //     .catch((error) => {
  //       console.log("login user err " + error);
  //     });
  // }
function getIndividualPerformanceReport() {
  const role = localStorage.getItem("role");
  let userId = null, teamId = null;

  if (role === "MANAGER") {
    const idMatch = selectedTeamMembers?.match(/\(([^)]+)\)/);
    if (idMatch) userId = idMatch[1];
    teamId = allTeams.find(t => t.teamName === selectedTeams)?.id;
  }

  if (!sdate || !edate) {
    toast.error("Invalid dates");
    return;
  }

  setSearchLoading(true);
  getIndividualPerformance(teamId, userId, sdate, edate)
    .then((resp) => {
      if (!resp || resp.status === 401) {
        logout();
        return;
      }
      return resp.json();
    })
    .then((data) => {
      setPerformanceData(Array.isArray(data) ? data : []);
    })
    .catch((err) => {
      console.error(err);
      toast.error("Failed to load");
    })
    .finally(() => setSearchLoading(false));
}

  const CustomPaper = (props) => {
    return (
      <Paper elevation={8} sx={{ fontSize: "0.8rem !important" }} {...props} />
    );
  };

  return (
    <>
      <Stack direction={"row"}>
        {localStorage.getItem("role") !== "EMPLOYEE" ? (
          <>
            <Autocomplete
              id="tags-filled"
              style={{ width: "300px" }}
              options={allTeams.map((option) => option.teamName)}
              value={selectedTeams}
              PaperComponent={CustomPaper}
              onChange={(event, newValue) => {
                setSelectedTeamMembers(null);
                setSelectedTeams(newValue);

                const teamObj = allTeams.find((e) => e.teamName === newValue);

                if (teamObj) {
                  const selectedTeam = teamObj.id;
                  getTeamById(selectedTeam)
                    .then((resp) => {
                      if (resp.status === 401) {
                        logout();
                      }
                      resp.json().then((data) => {
                        setAllMembers(data.members); 
                      });
                    })
                    .catch((error) => {
                      console.log("login user err " + error);
                    });
                } else {
                  setAllMembers([]); 
                }
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
                  label="Select Team"
                  InputLabelProps={{
                    ...params.InputLabelProps,
                    style: { fontSize: "0.8rem" },
                  }}
                  placeholder="Select Team"
                />
              )}
            />
            &nbsp;&nbsp;&nbsp;
            <Autocomplete
              id="tags-filled"
              size="small"
              style={{ width: "300px" }}
              PaperComponent={CustomPaper}
              options={allMembers.map(
                (option) =>
                  option.firstName +
                  " " +
                  option.lastName +
                  " (" +
                  option.id +
                  ")"
              )}
              value={selectedTeamMembers}
              onChange={(event, newValue) => {
                setSelectedTeamMembers(newValue);
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
                  size="small"
                  variant="outlined"
                  InputProps={{
                    ...params.InputProps,
                    style: { fontSize: "0.8rem" },
                  }}
                  InputLabelProps={{
                    ...params.InputLabelProps,
                    style: { fontSize: "0.8rem" },
                  }}
                  label="Select Member"
                  placeholder="Select Member"
                />
              )}
            />
            &nbsp;&nbsp;&nbsp;
          </>
        ) : (
          ""
        )}
        <LocalizationProvider
          fullWidth
          dateAdapter={AdapterDateFns}
          style={{ maxWidth: "20% !important" }}
        >
          <DesktopDatePicker
            label="Start date"
            fullWidth
            value={moment(sdate).format("YYYY-MM-DD")}
           
            onChange={handleSDateChange}
            minutesStep={30}
            slotProps={{ field: { clearable: true } }}
            renderInput={(params) => (
              <TextField
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
                size="small"
              />
            )}
          />
        </LocalizationProvider>
        &nbsp;&nbsp;&nbsp;
        <LocalizationProvider
          fullWidth
          dateAdapter={AdapterDateFns}
          style={{ maxWidth: "20% !important" }}
        >
          <DesktopDatePicker
            label="End date"
            fullWidth
            minDateTime={sdate}
            value={moment(edate).format("YYYY-MM-DD")}
            onChange={handleEDateChange}
            minutesStep={30}
            slotProps={{ field: { clearable: true } }}
            renderInput={(params) => (
              <TextField
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
                size="small"
              />
            )}
          />
        </LocalizationProvider>
        &nbsp;
        <Button
          size="sm"
          sx={{ fontSize: "0.8rem" }}
          onClick={() => getIndividualPerformanceReport()}
          loading={searchLoading}
        >
          <SearchPIcon /> SEARCH
        </Button>
      </Stack>

      <br></br>
       <div style={{ maxHeight: '445px', minHeight: '445px', overflowY: 'auto' }}>
               {
  performanceData && performanceData.length > 0 ? (
    <ReportCard data={performanceData} />
  ) : (
    <Typography textAlign="center" sx={{ mt: 2 }}>
      No performance data available
    </Typography>
  )
}
</div> 

     
    </>
  );
}
