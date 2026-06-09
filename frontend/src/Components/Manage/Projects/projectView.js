
import { ListItemDecorator } from '@mui/joy';
import Button from '@mui/joy/Button';
import Card from '@mui/joy/Card';
import CardActions from '@mui/joy/CardActions';
import CardOverflow from '@mui/joy/CardOverflow';
import Typography from '@mui/joy/Typography';
import { DialogContent, Divider, Stack } from '@mui/material';
import * as React from 'react';
import { GrGroup } from "react-icons/gr";
import { IoIosPricetags } from "react-icons/io";
import { getAllMembers, getProjectById, logout } from '../../../service/service-call';

export default function ViewProjects({ viewProjectModal, project }) {
  const [allAdmins, setAllAdmins] = React.useState([]);
  const [allMembers, setAllMembers] = React.useState([]);
  const [tname, setTName] = React.useState('');
  const [projectId, setProjectId] = React.useState('');
  const [selectedOption, setSelectedOption] = React.useState("");
  const [selectedProjectMembers, setSelectedProjectMembers] = React.useState([]);
  const [selectedProjectMemberIds, setSelectedProjectMemberIds] = React.useState([]);

  React.useEffect(() => {
    getProjectById(project.id).then(resp => {

      if (resp.status === 401) {
        logout();
      }
      resp.json().then(data => {

        setTName(data.projectName);
        setSelectedOption(data.projectLeadId.id);
        let arr = [];
        data.members.map((option) => arr.push(option.firstName + " " + option.lastName + " (" + option.id + ")"));
        setSelectedProjectMembers(arr);
      });
    }).catch(error => {
      console.log("login user err " + error);
    });
  }, []);

  React.useEffect(() => {
    getAllAdmins();
    getAllMemberForProject();
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

  function getAllMemberForProject() {
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
              alignItems: 'center', fontSize: '0.8rem'
            }}>
              {project.projectName.toUpperCase()}

            </Typography>
          </CardOverflow>
          

          <Typography level="body-sm" sx={{ color: '#262673', padding: '10px' }}>
            {project.description}

          </Typography>
          {
            project.tags && project.tags.length > 0 ? (
              <Card style={{ fontSize: '0.8rem', maxHeight: '250px', overflowY: 'auto' }}>
                <Typography style={{ fontSize: '0.8rem' }}>TAGS:</Typography>
                <Stack direction={"column"}>
                  {
                    project.tags && project.tags.length > 0 ?
                      project.tags.map((each, ind) => (
                        (

                          <>
                            <Divider sx={{ m: '10px' }} />
                            <ListItemDecorator>

                              <Typography fontWeight="md" startDecorator={<IoIosPricetags />} class="font-bold" level="body-xs" sx={{ color: '#262673', fontSize: '0.7rem' }}>
                                &nbsp;{each.name}
                              </Typography>
                            </ListItemDecorator>

                          </>
                        ))) : ""
                  }
                </Stack>
              </Card>
            ) : ""
          }

          {
            project.teams && project.teams.length > 0 ? (
              <Card style={{ fontSize: '0.8rem', maxHeight: '250px', overflowY: 'auto' }}>
                <Typography style={{ fontSize: '0.8rem' }}>TEAMS:</Typography>
                <Stack direction={"column"}>
                  {
                    project.teams && project.teams.length > 0 ?
                      project.teams.map((each, ind) => (
                        (

                          <>
                            <Divider sx={{ m: '10px' }} />
                            <ListItemDecorator>

                              <Typography fontWeight="md" startDecorator={<GrGroup />} class="font-bold" level="body-xs" sx={{ color: '#262673', fontSize: '0.7rem' }}>
                                &nbsp;{each.teamName.toUpperCase()}
                              </Typography>
                            </ListItemDecorator>

                          </>
                        ))) : ""
                  }
                </Stack>
              </Card>
            ) : ""
          }

          <CardActions

          >
            <Button variant="plain" style={{ fontSize: '0.8rem' }} color="warning" onClick={() => viewProjectModal(null)}>
              CLOSE
            </Button>
          </CardActions>
        </Card>
      </DialogContent>
    </React.Fragment>
  );
}