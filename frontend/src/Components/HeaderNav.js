import { Accordion, AccordionGroup, AccordionSummary, Avatar, Badge, Box, IconButton, ListItemDecorator, Menu, MenuItem, Typography, accordionDetailsClasses, accordionSummaryClasses } from '@mui/joy';
import { Button, Dialog, List, ListItem, ListItemAvatar, ListItemButton, ListItemText, Popover, Tooltip } from '@mui/material';
import AppBar from '@mui/material/AppBar';
import Stack from '@mui/material/Stack';
import Toolbar from '@mui/material/Toolbar';
import { Stomp } from '@stomp/stompjs';
import React, { useEffect, useRef } from 'react';
import { IoNotificationsCircleOutline, IoNotificationsOutline } from 'react-icons/io5';
import { LuMail, LuMailOpen } from 'react-icons/lu';
import addNotification from 'react-push-notification';
import { toast } from 'react-toastify';
import SockJS from 'sockjs-client';
import logo from '../assets/logo.png';
import notifysound from '../assets/notification.mp3';
import profile from '../assets/profile.png';
import profile1 from '../assets/profile1.png';
import { WEBSOCKET_URL, getNotifications, logoutUser, markAsReadNotify, stringAvatar, timeAgo } from '../service/service-call';
import { styled } from '@mui/material';

export default function HeaderNav() {

  const [notifications, setNotifications] = React.useState([]);
  const [notificationUnreadCount, setNotificationUnreadCount] = React.useState("");
  const audioRef = useRef(null);

  const playNotificationSound = () => {
    if (audioRef.current) {
      audioRef.current.play().catch((error) => {
        console.error('Failed to play the sound:', error);
      });
    }
  };

  useEffect(() => {
    connect();
    getAllNotifications();
  }, []);

  function getAllNotifications() {
    getNotifications().then(resp => {
      if (resp.status === 401) {
        logout();
      }
      resp.json().then(data => {
        setNotifications(data);
        let unread = data.filter(e => {
          return e.viewStatus === false
        });
        setNotificationUnreadCount(unread.length);
      });
    }).catch(error => {
    });
  }

  const connect = () => {
    var sock = new SockJS(WEBSOCKET_URL);
    let stompClient = Stomp.over(sock);

    sock.onopen = function () {
      console.log('open');
    }
    stompClient.connect({}, function (frame) {
      stompClient.subscribe("/user/" + localStorage.getItem("userId") + "/reply", function (greeting) {
        let payload = JSON.parse(greeting.body)
        playNotificationSound();
        notifyUser(payload.from.firstName + " " + payload.from.lastName, payload.message);
        getAllNotifications();
      });
    });
  }


  function notifyUser(sender, message) {
    addNotification({
      title: 'Task-management',
      subtitle: "",
      message: sender + ": " + message,
      theme: 'darkblue',
      native: true
    });
  }
  const account = {
    photoURL: localStorage.getItem("gender") && localStorage.getItem("gender") === "female" ? profile1 : profile,
    displayName: localStorage.getItem("firstname") + " " + localStorage.getItem("lastname"),
    role: "software devloper"
  }
  const [notifyEl, setNotifyEl] = React.useState(null);
  const [invisible, setInvisible] = React.useState(false);
  const open = Boolean(notifyEl);
  const id = open ? "simpe-popover" : undefined;
  const notifyOpen = Boolean(notifyEl);
  const notifyId = notifyOpen ? "simpe-notify" : undefined;

  const handleBadgeVisibility = () => {
    setInvisible(!invisible);
  };
  const handleNotifyOpen = (e) => {
    setNotifyEl(e.currentTarget);
    if (!invisible) {
      handleBadgeVisibility();
    }
  };

  const handleNotifyClose = () => {
    setNotifyEl(null);
  };

  const logout = () => {
    localStorage.removeItem("firstname");
    localStorage.removeItem("lastname");
    localStorage.removeItem("email");
    localStorage.removeItem("userId");
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("isTL")

    logoutUser().then(resp => {
      if (resp.status === 401) {
        toast.error('Unauthorized entry!');
      }
      window.location.replace("/login");
    }).catch(error => {
      console.log("logout user err ", error);
      if (error.response && error.response.status === 400) {
        toast.error(error.response.data.message);
      }
      if (error.response && error.response.status === 417) {
        toast.error(error.response.data.message);
      }
      window.location.replace("/login");
    });
  }

  const [anchorTagsEl, setAnchorTagsEl] = React.useState(null);
  const openTags = Boolean(anchorTagsEl);
  const idTags = openTags ? 'simple-popover' : undefined;

  const handleTagsClick = (event) => {
    setAnchorTagsEl(event.currentTarget);
  };

  const handleTagsClose = () => {
    setAnchorTagsEl(null);
  };

  function markAsRead(id, status) {
    markAsReadNotify(id, !status).then(resp => {
      setNotifications(resp.data);
      let unread = resp.data.filter(e => {
        return e.viewStatus === false
      });
      setNotificationUnreadCount(unread.length);
    }).catch(error => {
      if (error && error.response && error.response.data && error.response.data.message) {
        toast.error(error.response.data.message);
      } else if (error.response && error.response.data && error.response.data.errors && error.response.data.errors.length > 0) {
        toast.error(error.response.data.errors[0]);
      } else {
        toast.error("Internal server error, contact support team");
      }

    })
  }
  const BootstrapDialog = styled(Dialog)(({ theme }) => ({
    '& .MuiDialog-paper': {
      minWidth: '600px',
      height: 'auto'
    },
    '& .MuiDialogActions-root': {
      padding: theme.spacing(1),
    },
  }));
  const renderAccount = (

    <>

      <Stack direction="row" alignItems="center" spacing={1}>

        {
          notificationUnreadCount > 0 ?
            (
              <Badge size='sm' badgeContent={notificationUnreadCount > 0 ? notificationUnreadCount : ""} style={{ cursor: 'pointer' }}>
                <IoNotificationsCircleOutline onClick={handleTagsClick} color='warning' size={"30px"} />
              </Badge>
            ) : (
              <Badge size='sm' variant='plain' badgeContent={notificationUnreadCount > 0 ? notificationUnreadCount : ""} style={{ cursor: 'pointer' }}>
                <IoNotificationsCircleOutline onClick={handleTagsClick} color='warning' size={"30px"} style={{ cursor: 'pointer' }} />
              </Badge>
            )
        }

        &nbsp; &nbsp;&nbsp; &nbsp;
        {account.displayName}
        &nbsp; &nbsp;
        <Button aria-describedby={id} onClick={handleNotifyOpen}>
          <Avatar src={account.photoURL} alt="photoURL"
            sx={{
              width: 36,
              height: 36,
              border: (theme) => `solid 2px ${theme.palette.background.default}`,
            }}
          >
            {account.displayName.charAt(0).toUpperCase()}
          </Avatar>
        </Button>
      </Stack>
      <Popover
        id={notifyId}
        open={notifyOpen}
        anchorEl={notifyEl}
        onClose={handleNotifyClose}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "left"
        }}
      >
        <List disablePadding>
          <ListItem disablePadding onClick={logout}>
            <ListItemButton>
              <ListItemText primary="Logout" />
            </ListItemButton>
          </ListItem>
        </List>
      </Popover>
      <Box>
        <Popover
          id={idTags}
          open={openTags}
          anchorEl={anchorTagsEl}
          onClose={handleTagsClose}
          anchorOrigin={{
            vertical: "bottom",
          }}
          transformOrigin={{
            horizontal: 'center',
          }}
          slotProps={{
            paper: {
              sx: { borderRadius: '10px' }
            }
          }}
          sx={{ minWidth: '300px', maxHeight: '500px' }}
        >
          <Box sx={{ backgroundColor: '#262673', color: 'white', padding: '10px', alignItems: "center" }}>
            <Typography startDecorator={<IoNotificationsOutline />} sx={{ color: 'white', textAlign: 'center' }} >Notifications</Typography>
          </Box>

          <AccordionGroup
            variant="plain"
            transition="0.2s"
            sx={{
              maxWidth: 400,
              borderRadius: 'md',
              [`& .${accordionDetailsClasses.content}.${accordionDetailsClasses.expanded}`]:
              {
                paddingBlock: '1rem',
              },
              [`& .${accordionSummaryClasses.button}`]: {
                paddingBlock: '1rem',
              },
            }}
          >
            <Accordion>
              {
                notifications && notifications.length === 0 ? (
                  <AccordionSummary indicator={""} color={'neutral'}>
                    <ListItemDecorator sx={{ justifyContent: 'space-around', width: '500px' }}>
                      <ListItem alignItems="flex-start" >

                        <ListItemText
                          primary={"No messages found"}
                        />
                      </ListItem>
                    </ListItemDecorator>
                  </AccordionSummary>
                ) : ""
              }
              {
                notifications && notifications.length > 0 &&
                notifications.map((e, i) => (
                  <>
                    <AccordionSummary indicator={""} color={e.viewStatus ? 'neutral' : 'warning'}>
                      <ListItemDecorator onClick={() => markAsRead(e.id, e.viewStatus)} sx={{ justifyContent: 'space-around', width: '100%' }}>
                        <ListItem alignItems="flex-start" secondaryAction={
                          e.viewStatus ? <Tooltip title="Mark as unread"><IconButton style={{ float: 'right' }}><LuMailOpen /></IconButton></Tooltip> : <Tooltip title="Mark as read"><IconButton style={{ float: 'right' }}><LuMail /></IconButton></Tooltip>
                        }>
                          <ListItemAvatar>
                            <Avatar size="lg" color='primary' variant='soft' {...stringAvatar(e.from.firstName + " " + e.from.lastName)}></Avatar>
                          </ListItemAvatar>
                          <ListItemText
                            primary={<Typography level='body-sm'><b>{e.from.firstName + " " + e.from.lastName}</b>&nbsp;{e.message}</Typography>}
                            secondary={<Typography level='body-sx' fontWeight={'400'} sx={{ color: 'grey' }}>{timeAgo(e.time)}</Typography>}
                          />
                        </ListItem>
                      </ListItemDecorator>
                    </AccordionSummary>
                  </>
                ))
              }
            </Accordion>
          </AccordionGroup>

        </Popover>
      </Box>
    </>
  );
  return (
    <>
      <AppBar position="sticky"
        style={{ width: "full", boxShadow: 'none', backgroundColor: 'white', fontFamily: 'Segoe UI Emoji', color: '#262672', border: '1px solid #F0F0F0' }}
      >
        <Toolbar
          sx={{
            height: 1,
            px: { lg: 5 },
          }}
        >
          <div type="title" color="inherit" style={{ flex: 1, display: "flex", alignItems: "center" }}>
            <img src={logo} style={{ height: '40px', marginRight: '10px' }} alt="Logo" />
            <h1 style={{
              color: '#262673',
              fontWeight: 'bold',
              fontSize: '1.5rem',
              margin: 0
            }}>
              Task Manager
            </h1>
          </div>


          <audio ref={audioRef} src={notifysound} preload="auto" />
          {renderAccount}
        </Toolbar>
      </AppBar>
    </>
  )
}