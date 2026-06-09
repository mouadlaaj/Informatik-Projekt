import DeleteForeverRoundedIcon from '@mui/icons-material/DeleteForeverRounded';
import ModeRoundedIcon from '@mui/icons-material/ModeRounded';
import { Avatar, CircularProgress } from '@mui/joy';
import Badge, { badgeClasses } from '@mui/joy/Badge';
import Card from '@mui/joy/Card';
import CardContent from '@mui/joy/CardContent';
import CardOverflow from '@mui/joy/CardOverflow';
import { Dialog, DialogTitle, FormControl, Grid, IconButton, ListItemText, SpeedDial, Stack, TextField, Tooltip } from '@mui/material';
import { styled } from '@mui/material/styles';
import * as React from 'react';
import { TbUserPlus } from "react-icons/tb";
import profile from '../../assets/profile.png';
import profile1 from '../../assets/profile1.png';
import { SuperAdminUserList, getAllMembers, logout, stringAvatar } from '../../service/service-call';
import MemberDelete from './memberDelete';
import AddMembers from './membersAdd';
import EditMembers from './membersEdit';
import ViewMembers from './membersView';
export default function Members() {
  const [searchText, setSearchText] = React.useState("");
  const [isViewMemberOpen, setIsViewMemberOpen] = React.useState(false);
  const [isAddMemberOpen, setIsAddMemberOpen] = React.useState(false);
  const [isEditMemberOpen, setIsEditMemberOpen] = React.useState(false);
  const [isDeleteMemberOpen, setIsDeleteMemberOpen] = React.useState(false);
  const [members, setMembers] = React.useState([]);
  const [selectedMembers, setSelectedMembers] = React.useState({});
  const [selectedMemberId, setSelectedMemberId] = React.useState("");
  const [selectedMemberName, setSelectedMemberName] = React.useState("");
  const [isLoading, setIsLoading] = React.useState(false);

  React.useEffect(() => {
    getAllMembersByUser("");
  }, []);

  function toggleAddMemberModal() {

    setIsAddMemberOpen(!isAddMemberOpen);
    if (isAddMemberOpen === true) {
      getAllMembersByUser("");
    }
  }

  function editMember(id) {

    setSelectedMemberId(id);
    toggleEditMemberModal();
  }

  function deleteMember(id, name) {
    setSelectedMemberId(id);
    setSelectedMemberName(name);
    toggleDeleteMemberModal();
  }

  function toggleEditMemberModal() {
    setIsEditMemberOpen(!isEditMemberOpen);
    if (isEditMemberOpen === true) {
      getAllMembersByUser("");
    }
  }

  function toggleDeleteMemberModal() {
    setIsDeleteMemberOpen(!isDeleteMemberOpen);
    if (isDeleteMemberOpen === true) {
      getAllMembersByUser("");
    }
  }

  function toggleViewMemberModal(each) {
    setSelectedMembers(each);
    setIsViewMemberOpen(!isViewMemberOpen);

  }

  function getAllMembersByUser(text) {
    setIsLoading(true);
    getAllMembers(text).then(resp => {
      setIsLoading(false);
      if (resp.status === 401) {
        logout();
      }
      resp.json().then(data => {
        setMembers(data);

      });
    }).catch(error => {
      console.log("login user err " + error);
    });
  }

  const BootstrapDialog = styled(Dialog)(({ theme }) => ({
    '& .MuiDialog-paper': {
      minWidth: '400px !important',
      height: 'auto'
    },
    '& .MuiDialogActions-root': {
      padding: theme.spacing(1),
    },
  }));

  return (
    <div style={{ fontSize: '0.5rem' }}>
      <Stack direction={"row"}>
        <FormControl required={true} variant="standard" size="small" style={{ textAlign: 'center', fontSize: '0.5rem', width: '30%' }}>
          <TextField
            id="standard-adornment-fname"
            label="Search member here.."
            size="small"
            type={'text'}
            value={searchText}
            style={{ fontSize: '0.5rem' }}
            onChange={(e) => {
              setSearchText(e.target.value);
              getAllMembersByUser(e.target.value)
            }}
          />
        </FormControl>&nbsp;&nbsp;
        {
          localStorage.getItem("role") === "MANAGER" ? (
            <Tooltip title={"Add Member"} arrow className={"animate-[slide-in_1s_ease-in-out]"}>
              <SpeedDial

                ariaLabel="SpeedDial controlled open example"
                sx={{ position: 'absolute', top: "22%", right: 20 }}
                icon={<TbUserPlus size={"25px"} />}
                color='success'
                onClick={toggleAddMemberModal}
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


      <Grid container direction="row" spacing={1} columns={{ xs: 4, md: 12 }} columnSpacing={{ xs: 1, sm: 2, md: 1 }} sx={{ marginTop: '5px' }} style={{
        minHeight: '350px', maxHeight: '350px', msOverflowStyle: 'none',
        scrollbarWidth: 'none',
        overflowX: 'scroll',
        '&::-webkit-scrollbar': {
          display: 'none',
        },
      }}>

        {
          members && members.length > 0 && members.map((each, ind) => (
            <Grid item key={ind} >
              <Card orientation="horizontal" variant="soft" sx={{ width: 300, fontSize: '0.5rem', "&:hover": { backgroundColor: "aliceblue", color: '#262673', boxShadow: 'rgba(0, 0, 0, 0.25) 0px 25px 50px -12px' } }} >
                <CardOverflow style={{ paddingLeft: '10px' }}>

                  <Badge
                    anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                    badgeInset="20%"
                    variant={(each.status === "DROP") ? "outlined" : "solid"}
                    color={(each.status === "ACTIVE") ? "success" : "neutral"}
                    sx={{
                      padding: '5px',
                      [`& .${badgeClasses.badge}`]: {
                        '&::after': {
                          position: 'absolute',
                          top: 0,
                          left: 0,
                          width: '100%',
                          height: '100%',
                          borderRadius: '50%',
                          animation: (each.status === "ACTIVE") ? "ripple 1.2s infinite ease-in-out" : "",
                          border: '2px solid',
                          borderColor: (each.status === "ACTIVE") ? 'success' : '',
                          content: '""',
                        },
                      },
                      '@keyframes ripple': {
                        '0%': {
                          transform: 'scale(1)',
                          opacity: 1,
                        },
                        '100%': {
                          transform: 'scale(2)',
                          opacity: 0,
                        },
                      },
                    }}
                  >

                    <Avatar size="lg" {...stringAvatar(each.firstName + " " + each.lastName)} src={each.gender?.toLowerCase() === "male" ? profile : profile1}></Avatar>
                  </Badge>
                </CardOverflow>
                <CardContent sx={{ color: '#262673', cursor: 'pointer', fontSize: '0.5rem', paddingTop: '10px' }} onClick={() => toggleViewMemberModal(each)} >

                  <ListItemText primaryTypographyProps={{
                    color: '#262673', fontSize: '0.8rem'
                  }} secondaryTypographyProps={{
                    fontSize: '0.7rem'
                  }} primary={each.firstName.toUpperCase() + " " + each.lastName.toUpperCase()}
                    secondary={each.designation}></ListItemText>

                </CardContent>
                <CardOverflow
                  variant="soft"
                  color="primary"
                  class={""}
                  sx={{
                    writingMode: 'vertical-rl',
                    justifyContent: 'center',
                    fontSize: 'xs',
                    fontWeight: 'xl',
                    letterSpacing: '1px',
                    textTransform: 'uppercase',
                    borderColor: 'divider',
                  }}
                >

                  {
                    localStorage.getItem("role") === "MANAGER" && each.id !== localStorage.getItem("userId") && (
                      <>
                        <IconButton sx={{ "&:hover": { color: "green" } }} onClick={() => editMember(each.id)}>
                          <ModeRoundedIcon style={{ fontSize: '1rem' }} />
                        </IconButton>

                        <IconButton sx={{ "&:hover": { color: "red" } }} onClick={() => deleteMember(each.id, each.firstName + " " + each.lastName)}>
                          <DeleteForeverRoundedIcon style={{ fontSize: '1rem' }} />
                        </IconButton>
                      </>
                    )
                  }


                </CardOverflow>
              </Card>
            </Grid>
          ))
        }
      </Grid>
      <BootstrapDialog
        onClose={toggleAddMemberModal}
        aria-labelledby="customized-dialog-title"
        open={isAddMemberOpen}
      >

        <AddMembers addMemberModal={toggleAddMemberModal} />

      </BootstrapDialog>

      <BootstrapDialog
        onClose={toggleEditMemberModal}
        aria-labelledby="customized-dialog-title"
        open={isEditMemberOpen}
      >

        <EditMembers editMemberModal={toggleEditMemberModal} selectedMemberId={selectedMemberId} />

      </BootstrapDialog>

      <BootstrapDialog
        onClose={toggleViewMemberModal}
        aria-labelledby="customized-dialog-title"
        open={isViewMemberOpen}
      >
        <ViewMembers viewMemberModal={toggleViewMemberModal} member={selectedMembers} />

      </BootstrapDialog>

      <BootstrapDialog
        onClose={toggleDeleteMemberModal}
        aria-labelledby="customized-dialog-title"
        open={isDeleteMemberOpen}
      >
        <MemberDelete deleteMembersModal={toggleDeleteMemberModal} memberId={selectedMemberId} name={selectedMemberName} />

      </BootstrapDialog>

    </div>
  )
}