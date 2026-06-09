import Typography from '@mui/joy/Typography';
import { Divider, FormControl, Grid, TextField } from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import Paper from '@mui/material/Paper';
import * as React from 'react';
import { GrProjects } from "react-icons/gr";
import { TagsInput } from 'react-tag-input-component';
import { toast } from 'react-toastify';
import { addNewProject, getAllMembers, getAllTeams, logout } from '../../../service/service-call';
//import { addNewProject } from '../util/apiCalls';

export default function AddProjects({ addProjectsModal }) {
    const [allTeams, setAllTeams] = React.useState([]);
    const [allMembers, setAllMembers] = React.useState([]);
    const [tname, setTName] = React.useState('');
    const [description, setDescription] = React.useState('');
    const [projectId, setProjectId] = React.useState('');
    const [selectedOption, setSelectedOption] = React.useState("");
    const [selectedTeams, setSelectedTeams] = React.useState([]);
    const [selectedTeamIds, setSelectedTeamIds] = React.useState([]);
    const [tags, setTags] = React.useState([]);
    const [tagError, setTagError] = React.useState("");

    React.useEffect(() => {
        getAllTeam();
        getAllMemberForProject();
    }, []);

    function getAllTeam() {
        getAllTeams("").then(resp => {

            if (resp.status === 401) {
                logout();
            }
            resp.json().then(data => {

                setAllTeams(data);

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


    const handleProjectNameChange = (e) => {
        setTName(e.target.value);
    };

    const handleProjectDescChange = (e) => {
        setDescription(e.target.value);
    };

    const ITEM_HEIGHT = 48;
    const ITEM_PADDING_TOP = 8;
    const MenuProps = {
        PaperProps: {
            style: {
                maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
                width: 250,
            },
        },
    };

    function addNewProjects() {
        let arr = [];
        selectedTeams.map(r => {
            let d = allTeams.find(e => e.teamName === r);
            arr.push(d.id);
        })

        // let arr = [selectedTeamIds];

        if (tname === "" || tname === undefined || description === "" || description === undefined) {
            toast.error('Fields cannot be blank');
            return false;
        } else if (tags === "" || tags === undefined || tags.length === 0) {
            toast.error('Tags cannot be blank');
            return false;
        } else if (arr === "" || arr === undefined || arr.length === 0) {
            toast.error('At least one team must be selected');
            return false;
        } else {
            addNewProject(tname, description, arr, tags).then(resp => {
                toast.success('Project added successfully');
                addProjectsModal();
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

    const validateTagValue = (e) => {
        if (tags.length < 3) {
            if (e.length > 6) {
                setTagError("Tag length should be less than 6");
                return false;
            } else {
                setTagError("");
                return true;
            }
        } else {
            setTagError("Maximum 3 tags allowed");
            return false;
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
                            <div style={{ textAlign: 'center', display: 'inline-flex' }} ><GrProjects size={"30px"} color='#0B6BCB' /></div>
                            <Typography style={{ textAlign: 'center', fontSize: 14, fontWeight: 'bold', color: '#0B6BCB' }} class="font-bold">{"PROJECT DETAILS"}</Typography>
                        </Paper><br></br>
                        <Divider /><br></br>


                        <FormControl required={true} size="small" fullWidth variant="standard" style={{ textAlign: 'center' }}>
                            <TextField
                                id="standard-adornment-tname"
                                label="Project Name"
                                size="small"
                                type={'text'}
                                value={tname}
                                inputProps={{
                                    style: {
                                        fontSize: '0.9rem'
                                    },
                                    maxlength: 17
                                }}
                                helperText={`${tname.length}/${17}`}
                                onChange={handleProjectNameChange}
                            />
                        </FormControl>
                        <br></br><br></br>
                        <FormControl required={true} size="small" fullWidth variant="standard" style={{ textAlign: 'center' }}>
                            <TextField
                                id="standard-adornment-tname"
                                label="Description"
                                size="small"
                                rows={3}
                                multiline
                                type={'text'}
                                value={description}
                                inputProps={{
                                    style: {
                                        fontSize: '0.9rem'
                                    },
                                    maxlength: 300
                                }}
                                helperText={`${description.length}/${300}`}
                                onChange={handleProjectDescChange}
                            />
                        </FormControl>
                        <br></br><br></br>

                        <Autocomplete
                            multiple
                            id="tags-filled"
                            fullWidth
                            PaperComponent={CustomPaper}
                            options={allTeams.map((option) => option.teamName)}
                            value={selectedTeams}
                            onChange={(event, newValue) => {
                                setSelectedTeams(
                                    newValue);
                            }}
                            // renderTags={(value, getTagProps) =>
                            //     value.map((option, index) => (
                            //         <Chip avatar={<Avatar size="lg" color='danger' {...stringAvatar(option)} />} variant="outlined" label={option} {...getTagProps({ index })} />
                            //     ))
                            // }
                            renderInput={(params) => (
                                <TextField
                                    {...params}
                                    variant="outlined"
                                    size='small'
                                    InputProps={{ ...params.InputProps, style: { fontSize: "0.8rem" } }}
                                    InputLabelProps={{ ...params.InputLabelProps, style: { fontSize: "0.8rem" } }}
                                   
                                    label="Add Project Teams"
                                    placeholder="Add Teams"
                                />
                            )}
                        />
                        <br></br>
                        <TagsInput
                            beforeAddValidate={validateTagValue}
                            value={tags}
                            onChange={setTags}
                            name="Tags"
                            placeHolder="Enter tags / keywords"
                            
                        />
                         <Typography level="body-xs" color='neutral' sx={{opacity:'0.3'}}>Type a tag and press Enter</Typography>
                        <Typography level="body-xs" color='danger'>{tagError}</Typography>
                    </Box>
                </Grid>

            </DialogContent>
            <DialogActions>
                <Button onClick={addNewProjects}>&nbsp;SUBMIT</Button>
            </DialogActions>
        </React.Fragment>
    );
}