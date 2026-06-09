
import { Avatar, Chip, ListItemDecorator } from '@mui/joy';
import Button from '@mui/joy/Button';
import Card from '@mui/joy/Card';
import CardActions from '@mui/joy/CardActions';
import CardOverflow from '@mui/joy/CardOverflow';
import Typography from '@mui/joy/Typography';
import { DialogContent, Divider, Stack } from '@mui/material';
import * as React from 'react';
import { FaUserTie } from "react-icons/fa";
import { getAllMembers, getTeamById, logout, stringAvatar } from '../../../service/service-call';

export default function ViewTeams({ viewTeamModal, team }) {
  const [allAdmins, setAllAdmins] = React.useState([]);
  const [allMembers, setAllMembers] = React.useState([]);
  const [tname, setTName] = React.useState('');
  const [teamId, setTeamId] = React.useState('');
  const [selectedOption, setSelectedOption] = React.useState("");
  const [selectedTeamMembers, setSelectedTeamMembers] = React.useState([]);
  const [selectedTeamMemberIds, setSelectedTeamMemberIds] = React.useState([]);

  React.useEffect(() => {
    getTeamById(team.id).then(resp => {

      if (resp.status === 401) {
        logout();
      }
      resp.json().then(data => {

        setTName(data.teamName);
        setSelectedOption(data.teamLeadId.id);
        let arr = [];
        data.members.map((option) => arr.push(option.firstName + " " + option.lastName + " (" + option.id + ")"));
        setSelectedTeamMembers(arr);
      });
    }).catch(error => {
      console.log("login user err " + error);
    });
  }, []);

  React.useEffect(() => {
    getAllAdmins();
    getAllMemberForTeam();
  }, []);

  function getAllAdmins() {
    getAllMembers("").then(resp => {

      if (resp.status === 401) {
        logout();
      }
      resp.json().then(data => {

        setAllAdmins(data);

      });
    }).catch(error => {
      console.log("login user err " + error);
    });
  }

  function getAllMemberForTeam() {
    getAllMembers("").then(resp => {

      if (resp.status === 401) {
        logout();
      }
      resp.json().then(data => {

        setAllMembers(data);

      });
    }).catch(error => {
      console.log("login user err " + error);
    });
  }

  return (
    <React.Fragment>
      <DialogContent>
        <Card
          sx={{
            width: "auto",
            
            '--icon-size': '70px',
          }}
        >
          <CardOverflow variant="soft" color="primary" >

            <Typography level="title-lg" sx={{
              color: '#262673', padding: '10px',
              textAlign: 'center',
              alignItems: 'center', fontSize: '0.8rem', fontWeight: 'bold'
            }}>
              {team.teamName.toUpperCase()}

            </Typography>
          </CardOverflow>
          <Chip startDecorator={<FaUserTie />} size='sm' color='success' variant='outlined' sx={{ fontSize: '0.7rem' }}>
            {team.teamLeadId.firstName.toUpperCase() + " " + team.teamLeadId.lastName.toUpperCase()}
          </Chip>

          <Card style={{ fontSize: '0.8rem', maxHeight: '250px', overflowY: 'auto' }}>
            <Typography style={{ fontSize: '0.8rem', fontWeight: 'bold' }}>TEAM MEMBERS:</Typography>
            <Stack direction={"column"}>
              {
                team.members && team.members.length > 0 ?
                  team.members.map((each, ind) => (
                    (

                      <>
                        <Divider sx={{ m: '10px' }} />
                        <ListItemDecorator>
                          <Avatar size="md" {...stringAvatar(each.firstName + " " + each.lastName)}></Avatar>

                          <Typography fontWeight="md" class="font-bold" level="body-xs" sx={{ color: '#262673', fontSize: '0.7rem' }}>
                            &nbsp;{each.firstName.toUpperCase() + " " + each.lastName.toUpperCase()}
                          </Typography>
                        </ListItemDecorator>

                      </>
                    ))) : ""
              }
            </Stack>
          </Card>

          <CardActions

          >
            <Button variant="plain" style={{ fontSize: '0.8rem' }} color="warning" onClick={() => viewTeamModal(null)}>
              CLOSE
            </Button>
          </CardActions>
        </Card>




      </DialogContent>
    </React.Fragment>
  );
}