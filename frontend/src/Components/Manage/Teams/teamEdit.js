import { Avatar, ListItemDecorator } from '@mui/joy';
import { Chip, Divider, FormControl, Grid, InputLabel, MenuItem, Select, TextField } from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import * as React from 'react';
import { HiUserGroup } from "react-icons/hi2";
import { toast } from 'react-toastify';
import { editNewTeam, getAllActiveMembers, getAllAdmins, getTeamById, logout, stringAvatar } from '../../../service/service-call';

export default function EditTeams({ editTeamModal, selectedTeamId }) {
    const [allAdmins, setAllAdmins] = React.useState([]);
    const [allMembers, setAllMembers] = React.useState([]);
    const [tname, setTName] = React.useState('');
    const [teamId, setTeamId] = React.useState('');
    const [selectedOption, setSelectedOption] = React.useState("");
    const [selectedTeamMembers, setSelectedTeamMembers] = React.useState([]);
    const [selectedTeamMemberIds, setSelectedTeamMemberIds] = React.useState([]);

    React.useEffect(() => {
        getTeamById(selectedTeamId).then(resp => {

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
        getAllAdmin();
        getAllMemberForTeam();
    }, []);

    function getAllAdmin() {
        getAllAdmins().then(resp => {

            resp.json().then(data => {

                setAllAdmins(data);

            });
        }).catch(error => {
            console.log("login user err " + error);
        });
    }

    function getAllMemberForTeam() {
        getAllActiveMembers().then(resp => {

            resp.json().then(data => {

                setAllMembers(data);

            });
        }).catch(error => {
            console.log("login user err " + error);
        });
    }


    const handleTeamNameChange = (e) => {
        setTName(e.target.value);
    };


    function addNewTeams() {
        let arr = [];
        selectedTeamMembers.map(e => {
            let idCode = e.split("(");
            let val = idCode[1].split(")");
            if (!arr.includes(val[0].trim()))
                arr.push(val[0].trim());
        })
        if (tname === "" || tname === undefined || selectedOption === "" || selectedOption === undefined) {
            toast.error('Fields cannot be blank');
            return false;
        }
        else if (arr === "" || arr === undefined || arr.length === 0) {
            toast.error('Team members cannot be blank');
            return false;
        } else {
            editNewTeam(selectedTeamId, tname, selectedOption, arr).then(resp => {
                toast.success('Team updated successfully');
                editTeamModal();
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
    }

    const CustomPaper = (props) => {
        return <Paper elevation={8} sx={{fontSize:'0.8rem !important'}} {...props} />;
      };
    return (
        <React.Fragment>
            <DialogContent>
                <Grid container >
                    <Box sx={{ flexGrow: 1, padding: '10px' }}>
                        <Paper
                            square
                            elevation={0}
                            sx={{
                                textAlign: 'center',
                                height: 50,
                                pl: 2,
                                bgcolor: 'background.default',
                            }}
                        >
                            <div style={{ textAlign: 'center', display: 'inline-flex' }} ><HiUserGroup size={"30px"} color='#0B6BCB' /></div>
                            <Typography style={{ textAlign: 'center', fontSize: 14, fontWeight: 'bold', color: '#0B6BCB' }} class="font-bold">{"TEAM DETAILS"}</Typography>
                        </Paper><br></br>
                        <Divider /><br></br>


                        <FormControl size='small' required={true} fullWidth variant="standard" style={{ textAlign: 'center' }}>
                            <TextField
                                id="standard-adornment-tname"
                                label="Team Name"
                                size='small'
                                type={'text'}
                                value={tname}
                                inputProps={{
                                    style: {
                                        fontSize: '0.9rem'
                                    },
                                    maxlength: 30
                                }}
                                helperText={`${tname.length}/${30}`}
                                onChange={handleTeamNameChange}
                            />
                        </FormControl>
                        <br></br><br></br>
                        <FormControl size='small' fullWidth variant="outlined" style={{ fontSize: '0.9rem' }}>
                            <InputLabel id="demo-simple-select-standard-label" >Team Lead</InputLabel>

                            <Select
                                labelId="demo-select-small"
                                id="demo-select-small"
                                variant="outlined"
                                value={selectedOption}
                                size='small'
                                label="Team Lead"
                                inputProps={{
                                    style: {
                                        fontSize: '0.9rem'
                                    }
                                }}
                                onChange={(event) => {
                                    setSelectedTeamMemberIds(event.target.value)
                                    setSelectedOption(event.target.value)
                                }}
                            >
                                {allAdmins.map((item) => (
                                    <MenuItem value={item.id} style={{ fontSize: '0.9rem' }}  >
                                        <ListItemDecorator>
                                            <Avatar color='neutral' size="sm" {...stringAvatar(item.firstName + " " + item.lastName)} />
                                        </ListItemDecorator>&nbsp;
                                        {item.firstName + " " + item.lastName + " (" + item.id + ")"}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                        <br></br><br></br>

                        <Autocomplete
                            multiple
                            id="tags-filled"
                            fullWidth
                            size='small'
                            PaperComponent={CustomPaper}
                            options={allMembers.map((option) => option.firstName + " " + option.lastName + " (" + option.id + ")")}
                            value={selectedTeamMembers}
                            onChange={(event, newValue) => {
                                setSelectedTeamMembers(
                                    newValue);
                            }}
                            renderTags={(value, getTagProps) =>
                                value.map((option, index) => (
                                    <Chip avatar={<Avatar size="lg" color='danger' {...stringAvatar(option)} />} variant="outlined" label={option} {...getTagProps({ index })} />
                                ))
                            }
                            renderInput={(params) => (
                                <TextField
                                    {...params}
                                    size='small'
                                    variant="outlined"
                                    InputProps={{ ...params.InputProps, style: { fontSize: "0.8rem" } }}
                                    InputLabelProps={{ ...params.InputLabelProps, style: { fontSize: "0.8rem" } }}
                                    label="Add Team Members"
                                    placeholder="Add Team Members"
                                />
                            )}
                        />


                    </Box>
                </Grid>

            </DialogContent>
            <DialogActions>
                <Button onClick={addNewTeams}>&nbsp;SUBMIT</Button>
            </DialogActions>

        </React.Fragment>
    );
}