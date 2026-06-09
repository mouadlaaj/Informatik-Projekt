import DeleteForeverRoundedIcon from '@mui/icons-material/DeleteForeverRounded';
import ModeRoundedIcon from '@mui/icons-material/ModeRounded';
import Card from '@mui/joy/Card';
import CardContent from '@mui/joy/CardContent';
import CardOverflow from '@mui/joy/CardOverflow';
import Typography from '@mui/joy/Typography';
import { BsDatabaseFillAdd } from "react-icons/bs";

import { Dialog, DialogTitle, FormControl, Grid, IconButton, SpeedDial, Stack, TextField, Tooltip } from '@mui/material';
import * as React from 'react';
import { FaCircle } from "react-icons/fa";
import { GrProjects } from "react-icons/gr";
import { IoIosPricetags } from "react-icons/io";

import { Button, Chip, CircularProgress, ListItem, ListItemContent, ListItemDecorator } from '@mui/joy';
import { styled } from '@mui/material/styles';
import { GrGroup } from "react-icons/gr";
import { getAllProject, logout } from '../../../service/service-call';
import AddProjects from './projectAdd';

import ProjectDelete from './projectDelete';
import EditProjects from './projectEdit';
import ViewProjects from './projectView';
import { HiMiniSquare3Stack3D } from 'react-icons/hi2';
import { MdAddchart } from 'react-icons/md';
export default function Projects() {
  const [searchText, setSearchText] = React.useState("");
  const [isViewProjectOpen, setIsViewProjectOpen] = React.useState(false);
  const [isAddProjectOpen, setIsAddProjectOpen] = React.useState(false);
  const [isEditProjectOpen, setIsEditProjectOpen] = React.useState(false);
  const [isDeleteProjectOpen, setIsDeleteProjectOpen] = React.useState(false);
  const [members, setProjects] = React.useState([]);
  const [selectedProjects, setSelectedProjects] = React.useState({});
  const [selectedProjectId, setSelectedProjectId] = React.useState("");
  const [selectedProjectName, setSelectedProjectName] = React.useState("");
  const [isLoading, setIsLoading] = React.useState(false);
  React.useEffect(() => {
    getAllProjectByUser(searchText);
  }, []);

  function toggleAddProjectModal() {
    setIsAddProjectOpen(!isAddProjectOpen);
    if (isAddProjectOpen === true) {
      getAllProjectByUser(searchText);
    }
  }

  function editProject(id) {

    setSelectedProjectId(id);
    toggleEditProjectModal();
  }

  function deleteProject(id, name) {
    setSelectedProjectId(id);
    setSelectedProjectName(name);
    toggleDeleteProjectModal();
  }

  function toggleEditProjectModal() {
    setIsEditProjectOpen(!isEditProjectOpen);
    if (isEditProjectOpen === true) {
      getAllProjectByUser(searchText);
    }
  }

  function toggleDeleteProjectModal() {
    setIsDeleteProjectOpen(!isDeleteProjectOpen);
    if (isDeleteProjectOpen === true) {
      getAllProjectByUser("");
    }
  }

  function toggleViewProjectModal(each) {
    setSelectedProjects(each);
    setIsViewProjectOpen(!isViewProjectOpen);

  }

  function getAllProjectByUser(value) {
    setIsLoading(true);
    getAllProject(value).then(resp => {
      setIsLoading(false);
      if (resp.status === 401) {
        logout();
      }
      resp.json().then(data => {

        setProjects(data);

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
  return (
    <>
      <Stack direction={"row"}>
        <FormControl required={true} fullWidth variant="standard" style={{ textAlign: 'center', width: '30%' }}>
          <TextField
            id="standard-adornment-fname"
            label="Search project here.."
            size="small"
            type={'text'}
            value={searchText}
            onChange={(e) => {
              setSearchText(e.target.value);
              getAllProjectByUser(e.target.value)
            }}
          />
        </FormControl>&nbsp;&nbsp;

        {
          localStorage.getItem("role") !== "EMPLOYEE" ? (
            <Tooltip title={"Add Project"} arrow className={"animate-[slide-in_1s_ease-in-out]"}>
              <SpeedDial
                ariaLabel="SpeedDial controlled open example"
                sx={{ position: 'absolute', top: "22%", right: 20 }}
                icon={<MdAddchart size={"25px"} />}
                color='success'
                onClick={toggleAddProjectModal}
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
              <Card orientation="horizontal" variant="soft" sx={{ maxWidth: "350px", minWidth: "350px", maxHeight: '170px', minHeight: '170px', "&:hover": { backgroundColor: "aliceblue", color: '#262673', boxShadow: 'rgba(0, 0, 0, 0.25) 0px 25px 50px -12px' } }} >
                <CardOverflow style={{ paddingLeft: '10px' }}>
                 
                </CardOverflow>
                <CardContent sx={{ color: '#262673', cursor: 'pointer' }} onClick={() => toggleViewProjectModal(each)} >
                  <Stack direction={"column"}>
                    <ListItem>
                      <ListItemDecorator>
                        <HiMiniSquare3Stack3D size={"37px"} color={each.status === "ACTIVE" ? "#66cc00" : '#ff3300'} />
                        &nbsp;
                      </ListItemDecorator>
                      <ListItemContent>
                        <Typography level="title-md" sx={{ color: '#262673' }}>{each.projectName}</Typography>
                        <Typography level="body-xs" noWrap>
                          {each.description.length <= 35 ? each.description : (each.description.substr(0, 35) + "...")}
                        </Typography>
                      </ListItemContent>
                    </ListItem>
                    <Stack style={{ display: 'inline-block' }} direction={"row"} spacing={1} marginTop={"5px"} marginBottom={"5px"}>

                      {
                        each.tags && each.tags.length > 0 ? (
                          each.tags.map((e, ind) => (
                            <Chip
                              size="sm"
                              variant="outlined"

                              sx={{ fontSize: '0.7rem', borderRadius: '5px', color: '#262673' }}
                              
                              startDecorator={<IoIosPricetags />}
                            >
                              {e.name}
                            </Chip>
                          ))
                        ) : ""}
                    </Stack>
                    <Grid direction={"row"} >
                      {
                        each.teams && each.teams.length > 0 ? (
                          each.teams.map((e, ind) => (
                            <>
                              <Chip
                                size="sm"
                                variant="solid"
                                color='primary'
                                sx={{ fontSize: '0.7rem', borderRadius: '5px' }}
                                
                                startDecorator={<GrGroup />}
                              >
                                {e.teamName}
                              </Chip> &nbsp;</>
                          ))
                        ) : ""}
                    </Grid>

                  </Stack>
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
                      <IconButton sx={{ "&:hover": { color: "green" } }} onClick={() => editProject(each.id)}>
                        <ModeRoundedIcon style={{ fontSize: '1rem' }} />
                      </IconButton>
                      <IconButton sx={{ "&:hover": { color: "red" } }} onClick={() => deleteProject(each.id, each.projectName)}>
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
        onClose={toggleAddProjectModal}
        aria-labelledby="customized-dialog-title"
        open={isAddProjectOpen}
      >
        

        <AddProjects addProjectsModal={toggleAddProjectModal} />

      </BootstrapDialog>

      <BootstrapDialog
        onClose={toggleEditProjectModal}
        aria-labelledby="customized-dialog-title"
        open={isEditProjectOpen}
      >
        

        <EditProjects editProjectModal={toggleEditProjectModal} selectedProjectId={selectedProjectId} />

      </BootstrapDialog>

      <BootstrapViewDialog
        onClose={toggleViewProjectModal}
        aria-labelledby="customized-dialog-title"
        open={isViewProjectOpen}
      >
        

        <ViewProjects viewProjectModal={toggleViewProjectModal} project={selectedProjects} />

      </BootstrapViewDialog>

      <BootstrapDialog
        onClose={toggleDeleteProjectModal}
        aria-labelledby="customized-dialog-title"
        open={isDeleteProjectOpen}
      >

        <ProjectDelete deleteProjectsModal={toggleDeleteProjectModal} projectId={selectedProjectId} name={selectedProjectName} />

      </BootstrapDialog>

    </>
  )
}