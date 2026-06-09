import DeleteForeverRoundedIcon from '@mui/icons-material/DeleteForeverRounded';
import ModeRoundedIcon from '@mui/icons-material/ModeRounded';
import { Avatar, Button, Chip, CircularProgress, ListItem, ListItemContent, ListItemDecorator } from '@mui/joy';
import Card from '@mui/joy/Card';
import CardContent from '@mui/joy/CardContent';
import CardOverflow from '@mui/joy/CardOverflow';
import Typography from '@mui/joy/Typography';
import { AvatarGroup, Dialog, DialogTitle, FormControl, Grid, IconButton, SpeedDial, Stack, TextField, Tooltip } from '@mui/material';
import { styled } from '@mui/material/styles';
import * as React from 'react';
import { FaUserTie } from "react-icons/fa";
import { FaPeopleGroup } from "react-icons/fa6";
import { getAllTeams, logout, stringAvatar } from '../../../service/service-call';
import AddTeams from './teamAdd';
import TeamDelete from './teamDelete';
import EditTeams from './teamEdit';
import ViewTeams from './teamView';
import { HiMiniSquare3Stack3D, HiMiniUserGroup } from 'react-icons/hi2';
import { RiGroup2Fill } from 'react-icons/ri';
export default function Teams() {
  const [searchText, setSearchText] = React.useState("");
  const [isViewTeamOpen, setIsViewTeamOpen] = React.useState(false);
  const [isAddTeamOpen, setIsAddTeamOpen] = React.useState(false);
  const [isEditTeamOpen, setIsEditTeamOpen] = React.useState(false);
  const [isDeleteTeamOpen, setIsDeleteTeamOpen] = React.useState(false);
  const [members, setTeams] = React.useState([]);
  const [selectedTeams, setSelectedTeams] = React.useState({});
  const [selectedTeamId, setSelectedTeamId] = React.useState("");
  const [selectedTeamName, setSelectedTeamName] = React.useState("");
  const [isLoading, setIsLoading] = React.useState(false);
  React.useEffect(() => {
    getAllTeamsByUser(searchText);
  }, []);

  function toggleAddTeamModal() {
    setIsAddTeamOpen(!isAddTeamOpen);
    if (isAddTeamOpen === true) {
      getAllTeamsByUser(searchText);
    }
  }

  function editTeam(id) {

    setSelectedTeamId(id);
    toggleEditTeamModal();
  }

  function deleteTeam(id, name) {
    setSelectedTeamId(id);
    setSelectedTeamName(name);
    toggleDeleteTeamModal();
  }

  function toggleEditTeamModal() {
    setIsEditTeamOpen(!isEditTeamOpen);
    if (isEditTeamOpen === true) {
      getAllTeamsByUser(searchText);
    }
  }

  function toggleDeleteTeamModal() {
    setIsDeleteTeamOpen(!isDeleteTeamOpen);
    if (isDeleteTeamOpen === true) {
      getAllTeamsByUser("");
    }
  }

  function toggleViewTeamModal(each) {
    setSelectedTeams(each);
    setIsViewTeamOpen(!isViewTeamOpen);
  }

  function getAllTeamsByUser(value) {
    setIsLoading(true);
    getAllTeams(value).then(resp => {
      setIsLoading(false);

      if (resp.status === 401) {
        logout();
      }
      resp.json().then(data => {
        setTeams(data);

      });
    }).catch(error => {
      console.log("login user err " + error);
    });
  }

  const BootstrapViewDialog = styled(Dialog)(({ theme }) => ({
    '& .MuiDialog-paper': {
      minWidth: '500px !important',
      height: 'auto'
    },
    '& .MuiDialogActions-root': {
      padding: theme.spacing(1),
    }
  }));
  const BootstrapDialog = styled(Dialog)(({ theme }) => ({
    '& .MuiDialog-paper': {
      minWidth: '400px !important',
      height: 'auto'
    },
    '& .MuiDialogActions-root': {
      padding: theme.spacing(1),
    },
  }));

  const BootstrapDialogTitle = (props) => {
    const { children, onClose, ...other } = props;
    return (
      <DialogTitle sx={{
        alignItems: "center",
        justifyContent: "center",
        alignContent: "space-between"
      }} {...other}>
        {children}
        {onClose ? (
          <IconButton
            aria-label="close"
            onClick={onClose}
          >
          </IconButton>
        ) : null}
      </DialogTitle>
    );
  };

  function getRemainingNames(members) {
    return (
      <span>
        {members.slice(3).map((member, index) => (
          <React.Fragment key={index}>
            {member.firstName} {member.lastName}
            <br />
          </React.Fragment>
        ))}
      </span>
    );
  }

  return (
    <>
      <Stack direction={"row"}>
        <FormControl required={true} fullWidth variant="standard" style={{ textAlign: 'center', width: '30%' }}>
          <TextField
            id="standard-adornment-fname"
            label="Search team here.."
            size="small"
            type={'text'}
            value={searchText}
            onChange={(e) => {
              setSearchText(e.target.value);
              getAllTeamsByUser(e.target.value)
            }}
          />
        </FormControl>&nbsp;&nbsp;

        {
          localStorage.getItem("role") !== "EMPLOYEE" ? (
            <Tooltip title={"Add Team"} arrow className={"animate-[slide-in_1s_ease-in-out]"}>
              <SpeedDial
                ariaLabel="SpeedDial controlled open example"
                sx={{ position: 'absolute', top: "22%", right: 20 }}
                icon={<FaPeopleGroup size={"25px"} />}
                color='success'
                onClick={toggleAddTeamModal}
                className='hover:-translate-y-1 hover:scale-110 duration-300'
              >
              </SpeedDial></Tooltip>
          ) : ""}
      </Stack>

      {
        isLoading ? (
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center"
            }}
          >
            {
              <CircularProgress />
            }
          </div>
        ) : ""
      }

      <Grid container direction="row" rowSpacing={2} spacing={{ xs: 2, md: 4 }} columns={{ xs: 4, sm: 8, md: 12 }} sx={{ marginTop: '5px' }} style={{
        minHeight: '350px', maxHeight: '350px', msOverflowStyle: 'none',
        scrollbarWidth: 'none',
        overflowX: 'scroll',
        '&::-webkit-scrollbar': {
          display: 'none',
        },
      }}>

        {
          members.length > 0 && members.map((each, ind) => (
            <Grid item key={ind}>
              <Card orientation="horizontal" variant="soft" sx={{ width: 300, "&:hover": { backgroundColor: "aliceblue", color: '#262673', boxShadow: 'rgba(0, 0, 0, 0.25) 0px 25px 50px -12px' } }} >
                <CardOverflow style={{ paddingLeft: '10px' }}>
                 
                </CardOverflow>
                <CardContent sx={{ color: '#262673', cursor: 'pointer' }} onClick={() => toggleViewTeamModal(each)} >
                  
                  <ListItem>
                    <ListItemDecorator>
                      <HiMiniUserGroup size={"37px"} color={each.status === "ACTIVE" ? "#66cc00" : '#ff3300'} />
                      &nbsp;
                    </ListItemDecorator>
                    <ListItemContent>
                      <Typography level="title-md" sx={{ color: '#262673' }}>{each.teamName}</Typography>
                      <Typography level="body-xs" noWrap startDecorator={<FaUserTie />}>
                        {each.teamLeadId.firstName + " " + each.teamLeadId.lastName}
                      </Typography>
                    </ListItemContent>
                  </ListItem>
                  
                  <br></br>
                  {
                    each.members && each.members.length > 3 ? (
                      <AvatarGroup>
                        <>
                          <Tooltip arrow title={each.members[0].firstName + " " + each.members[0].lastName} >
                            <Avatar size="md" color='primary' {...stringAvatar(each.members[0].firstName + " " + each.members[0].lastName)}></Avatar>
                          </Tooltip>
                          <Tooltip arrow title={each.members[1].firstName + " " + each.members[1].lastName} >
                            <Avatar size="md" color='primary' {...stringAvatar(each.members[1].firstName + " " + each.members[1].lastName)}></Avatar>
                          </Tooltip>
                          <Tooltip arrow title={each.members[2].firstName + " " + each.members[2].lastName} >
                            <Avatar size="md" color='primary' {...stringAvatar(each.members[2].firstName + " " + each.members[2].lastName)}></Avatar>
                          </Tooltip>

                        </>
                        {
                          each.members.length - 3 === 1 ? (
                            <>
                              <Tooltip arrow title={each.members[3].firstName + " " + each.members[3].lastName} >
                                <Avatar size="md" color='primary' {...stringAvatar(each.members[3].firstName + " " + each.members[3].lastName)}></Avatar>
                              </Tooltip>
                            </>
                          ) : (
                            <>
                              <Tooltip arrow title={getRemainingNames(each.members)} sx={{ whiteSpace: 'pre-line' }}>
                                <Avatar size="md" color='primary'>+{each.members.length - 3}</Avatar>
                              </Tooltip>
                            </>
                          )
                        }
                      </AvatarGroup>
                    ) : (
                      <AvatarGroup>
                        {
                          each.members && each.members.length > 0 ? each.members.map((eac, ind) => (
                            <>
                              <Tooltip arrow title={eac.firstName + " " + eac.lastName} ><Avatar color='primary' size="md" {...stringAvatar(eac.firstName + " " + eac.lastName)}></Avatar>
                              </Tooltip>

                            </>
                          )) : ""
                        }


                      </AvatarGroup>
                    )
                  }

                </CardContent>
                {
                  localStorage.getItem("role") === "MANAGER" && (
                    <CardOverflow
                      variant="soft"
                      color="primary"
                      sx={{
                        px: 0.2,
                        writingMode: 'vertical-rl',
                        justifyContent: 'center',
                        fontSize: 'xs',
                        fontWeight: 'xl',
                        letterSpacing: '1px',
                        textTransform: 'uppercase',
                        borderLeft: '1px solid',
                        borderColor: 'divider',
                      }}
                    >
                      <IconButton sx={{ "&:hover": { color: "green" } }} onClick={() => editTeam(each.id)}>
                        <ModeRoundedIcon style={{ fontSize: '1rem' }} />
                      </IconButton>

                      <IconButton sx={{ "&:hover": { color: "red" } }} onClick={() => deleteTeam(each.id, each.teamName)}>
                        <DeleteForeverRoundedIcon style={{ fontSize: '1rem' }} />
                      </IconButton>
                    </CardOverflow>
                  )
                }



              </Card>
            </Grid>
          ))
        }
      </Grid>
      <BootstrapDialog
        onClose={toggleAddTeamModal}
        aria-labelledby="customized-dialog-title"
        open={isAddTeamOpen}
      >

        <AddTeams addTeamsModal={toggleAddTeamModal} />

      </BootstrapDialog>

      <BootstrapDialog
        onClose={toggleEditTeamModal}
        aria-labelledby="customized-dialog-title"
        open={isEditTeamOpen}
      >

        <EditTeams editTeamModal={toggleEditTeamModal} selectedTeamId={selectedTeamId} />

      </BootstrapDialog>

      <BootstrapViewDialog
        onClose={toggleViewTeamModal}
        aria-labelledby="customized-dialog-title"
        open={isViewTeamOpen}
      >
        
        <ViewTeams viewTeamModal={toggleViewTeamModal} team={selectedTeams} />

      </BootstrapViewDialog>

      <BootstrapDialog
        onClose={toggleDeleteTeamModal}
        aria-labelledby="customized-dialog-title"
        open={isDeleteTeamOpen}
      >
        <TeamDelete deleteTeamsModal={toggleDeleteTeamModal} teamId={selectedTeamId} name={selectedTeamName} />

      </BootstrapDialog>

    </>
  )
}