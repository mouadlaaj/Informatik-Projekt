import React from 'react';
import styledx from '@xstyled/styled-components';
import AccessAlarmIcon from '@mui/icons-material/AccessAlarm';
import { IoChatbubbleEllipsesOutline } from "react-icons/io5";
import { GrAttachment } from "react-icons/gr";
import { keyframes } from '@mui/system';

import { Dialog, DialogActions, DialogContent, styled } from '@mui/material';
import Popup from './Popup';
import { Avatar, AvatarGroup, Button, Card, CardContent, Chip, Stack, Tooltip, Typography } from '@mui/joy';
import { stringAvatar } from '../../service/service-call';
import moment from 'moment';
import { marginLeft } from '@xstyled/system';
import { IoBugOutline } from "react-icons/io5";


const Container = styledx.a`
text-decoration: none;
`;


function getStyle(provided, style) {
  if (!style) {
    return provided.draggableProps.style;
  }

  return {
    ...provided.draggableProps.style,
    ...style,
  };
}

function QuoteItem(props) {
  const { quote, isDragging, isGroupedOver, provided, style, index } = props;
  const [open, setOpen] = React.useState(false);
  const [selectedValue, setSelectedValue] = React.useState(quote.id);


  const handleClickOpen = () => {
    localStorage.setItem("isPopUpOpen", "true");
    toggleViewTaskModal();
  };

  function toHoursAndMinutes(totalMinutes) {
    var sign = totalMinutes < 0 ? "-" : "";
    totalMinutes = Math.abs(totalMinutes);
    var hours = Math.floor(totalMinutes / 60);
    var minutes = totalMinutes % 60;
    return hours + ":" + (minutes < 10 ? "0" : "") + minutes + " hrs";
  }

  function toggleViewTaskModal() {
    localStorage.setItem("isPopUpOpen", !open);
    setOpen(!open);

    if (open === true) {
      props.getAllTasksForUser();
    }
  }


  const blink = keyframes`
  from { opacity: 0; }
  to { opacity: 1; }
`;

  const BootstrapDialog = styled(Dialog)(({ theme }) => ({
    '& .MuiDialog-paper': {
      minWidth: '1100px !important',
      height: 'auto'
    },
    '& .MuiDialogActions-root': {
      padding: theme.spacing(1),
    },
  }));

  return (
    <Container

      isDragging={isDragging}
      isGroupedOver={isGroupedOver}
      colors={"red"}
      ref={provided.innerRef}
      {...provided.draggableProps}
      {...provided.dragHandleProps}
      style={getStyle(provided, style)}
      data-is-dragging={isDragging}
      data-testid={quote.id}
      data-index={index}
      aria-label={`${quote.title} quote ${quote.title}`}
    >

      <Card orientation="vertical" variant="outlined"

        sx={{
          fontSize: '0.5rem',
          gap: '0.1rem',
          marginTop: '5px',
          backgroundColor: !(quote.status === "DONE" || quote.status === "HOLD"
            || quote.status === "DROP" || quote.status === "QA")
            && quote.endDate
            && moment(moment()).isAfter(quote.endDate) ? 'peachpuff' :
            (quote.status === "QA" && quote.qcEndDate
              && moment(moment()).isAfter(quote.qcEndDate)) ? 'peachpuff' : "",
          "&:hover": { color: '#262673', border: '#262673 2px SOLID' }
        }} >
        <Stack direction={"row"} >
          <Chip
            size="sm"
            variant="solid"
            sx={{ fontSize: '0.8rem', borderRadius: '5px', backgroundColor: '#262673', color: 'white' }}


          >
            {quote.id}
          </Chip>&nbsp;

          {
            quote.projectId && quote.projectId.projectName ?
              (
                <Chip
                  size="sm"
                  variant="outlined"
                  sx={{ fontSize: '0.8rem', borderRadius: '5px' }}
                  color={"success"}

                >
                  {quote.projectId.projectName}
                </Chip>
              ) : ""
          }

        </Stack>
        <CardContent onClick={handleClickOpen} sx={{ color: '#262673', cursor: 'pointer', fontSize: '0.5rem' }}  >
          <div >
            <Typography fontWeight="md" class="font-bold" sx={{ color: '#262673', fontSize: '0.7rem' }}>
              {quote.title}
            </Typography>

            <Typography level="body-xs" sx={{ fontSize: '0.6rem' }}>
              {quote.shortDescription.length <= 90 ? quote.shortDescription : (quote.shortDescription.substr(0, 90) + "...")}

            </Typography>
            <Stack direction={"row"} marginTop={"3px"}>

              {
                quote.priority ?
                  (
                    <Chip
                      size="sm"
                      variant={(quote.priority === "LOW") ? "soft" : (quote.priority === "MEDIUM") ? "soft" : "solid"}
                      sx={{ fontSize: '0.6rem', borderRadius: '2px', animation: (quote.priority === "HIGH") ? `${blink} 0.5s linear infinite` : "" }}
                      color={(quote.priority === "LOW") ? "neutral" : (quote.priority === "MEDIUM") ? "primary" : "warning"}
                    >
                      {quote.priority}
                    </Chip>
                  ) : ""
              }
              {
                quote.taskStatusTrack ?
                  (
                    <>&nbsp;<Chip
                      size="sm"
                      variant="solid"
                      sx={{ fontSize: '0.6rem', borderRadius: '2px', color: 'white', backgroundColor: 'orange' }}

                    >
                      {quote.taskStatusTrack}
                    </Chip></>
                  ) : ""
              }
              &nbsp;
              {
                quote.status !== "DONE" ?
                  (
                    <Tooltip title={"Due date for this task"} >
                      <Chip
                        size="sm"
                        variant="outlined"
                        sx={{ fontSize: '0.6rem', borderRadius: '5px' }}
                        color={"success"}
                        startDecorator={<AccessAlarmIcon style={{ fontSize: '0.7rem' }} />}
                      >
                        {toHoursAndMinutes(moment(quote.endDate).diff(moment(), 'minutes'))} {moment(quote.endDate).diff(moment(), 'minutes') < 0 ? "elapsed" : "remaining"}
                      </Chip>
                    </Tooltip>
                  
                  ) : quote.modifiedDate && quote.status === "DONE" ? (
                    <Tooltip title={"Completed on"} >
                      <Chip
                        size="sm"
                        variant="outlined"
                        sx={{ fontSize: '0.6rem', borderRadius: '5px' }}
                        color={"success"}
                        startDecorator={<AccessAlarmIcon style={{ fontSize: '0.7rem' }} />}
                      >
                        {moment(quote.modifiedDate).format("DD-MM-YYYY hh:mm A")}
                      </Chip>
                    </Tooltip>
                  ) : ""
              }

            </Stack>
          </div>
          <Stack direction={"row"} fontSize={"0.7rem"} spacing={1} alignItems="center" >
            <Tooltip title={"Created by " + quote.createdBy.firstName + " " + quote.createdBy.lastName} >
              <Avatar size="sm" {...stringAvatar(quote.createdBy.firstName.toUpperCase() + " " + quote.createdBy.lastName.toUpperCase())}></Avatar>
            </Tooltip>
            <IoChatbubbleEllipsesOutline fontSize={"1rem"} />{quote.commentsCount}

            <GrAttachment fontSize={"1rem"} />{quote.attachmentsCount}
            <IoBugOutline fontSize={"1rem"} />{quote.bugsCount}

            <AvatarGroup

              style={
                { marginLeft: "2rem" }
              }
              max={4}

              sx={{

                '& .MuiAvatar-root': {
                  width: 32,
                  height: 32,

                  fontSize: 14,
                  border: '2px solid white',
                  marginLeft: '-5px',
                  position: 'relative',
                },
                '& .MuiAvatar-root:nth-of-type(1)': {
                  zIndex: 3,
                  marginLeft: 0,
                },
                '& .MuiAvatar-root:nth-of-type(2)': {
                  zIndex: 2,
                },
                '& .MuiAvatar-root:nth-of-type(3)': {
                  zIndex: 1,
                },
              }}
            >
              {quote.assignedTo.slice(0, 3).map((assignment, index) => (
                <Tooltip
                  key={index}
                  title={`Assigned to ${assignment?.member?.firstName || ''} ${assignment?.member?.lastName || ''}`}
                >
                  <Avatar
                    size="sm"
                    {...stringAvatar(`${assignment?.member?.firstName || ''} ${assignment?.member?.lastName || ''}`)}
                  />
                </Tooltip>
              ))}

              {quote.assignedTo.length > 3 && (
                <Avatar size="sm">+{quote.assignedTo.length - 3}</Avatar>
              )}
            </AvatarGroup>
          </Stack>
        </CardContent>

      </Card>

      <BootstrapDialog
        onClose={toggleViewTaskModal}
        aria-labelledby="customized-dialog-title"
        onBackdropClick={"false"}
        open={open}
      >

        <Popup viewTaskModal={toggleViewTaskModal} taskId={selectedValue} />

      </BootstrapDialog>

    </Container>
  );
}

export default React.memo(QuoteItem);