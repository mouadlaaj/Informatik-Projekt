import { Grid } from '@mui/material';

import { Button, Stack } from '@mui/joy';
import Typography from '@mui/joy/Typography';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import * as React from 'react';
import { toast } from 'react-toastify';
import { deleteProjectById } from '../../../service/service-call';

export default function ProjectDelete({ deleteProjectsModal, projectId, name }) {

    function deleteProjects() {

        deleteProjectById(projectId).then(resp => {
            toast.success('Project "' + name + '" deleted successfully');
            deleteProjectsModal();
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

    return (
        <React.Fragment>
            <DialogContent>
                <Grid container >
                    <Stack
                        spacing={2}
                        sx={{
                            maxWidth: '60ch',
                        }}
                    >
                        <Typography level="h4" color='warning'>Confirm delete</Typography>

                        <Typography level="body-sm">Are you sure want to delete project "{name}" ?</Typography>




                    </Stack>
                </Grid>

            </DialogContent>
            <DialogActions>
                <Button variant="outlined" color='warning' onClick={deleteProjects}>Yes delete it</Button>
                <Button variant="soft" onClick={deleteProjectsModal}>No keep it</Button>
            </DialogActions>


        </React.Fragment>
    );
}