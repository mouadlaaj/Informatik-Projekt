// import { Button } from '@mui/joy';
// import { FormControl, Grid, InputLabel, MenuItem, Select, TextField } from '@mui/material';
// import Box from '@mui/material/Box';
// import DialogContent from '@mui/material/DialogContent';
// import Typography from '@mui/material/Typography';
// import * as React from 'react';
// import { changeTaskStatus, changeTaskStatusWithFeedback } from '../../service/service-call';
// import { toast } from 'react-toastify';
// //import { addNewMember } from '../util/apiCalls';

// export default function TaskStatusChangeWithFeedback({ toggleStatusChangeFeedbackModal, taskId, status }) {

//     const [type, setType] = React.useState('');
//     const [message, setMessage] = React.useState('');

//     const handleMessageChange = (e) => {
//         setMessage(e.target.value);
//     };

//     const statusChangeSubmit = () =>{
//         if(message===null || message === undefined || message === ""){
//             toast.error('Message cannot be blank!');
//             return false;
//         }
//         changeTaskStatusWithFeedback(taskId, status, message).then(resp => {
//                 toggleStatusChangeFeedbackModal();
//                 toast.success('Tasks status changed successfully');
//             }).catch(error => {
//                 if(error && error.response && error.response.data && error.response.data.message){
//                     toast.error(error.response.data.message);
//                   } else if(error.response && error.response.data && error.response.data.errors && error.response.data.errors.length > 0){
//                     toast.error(error.response.data.errors[0]);
//                   } else {
//                     toast.error("Internal server error, contact support team");
//                   }
//             })
//     }


//     return (
//         <React.Fragment>
//             <DialogContent>
//                 <Grid container >



//                     <br></br><br></br>

//                     <Box sx={{ flexGrow: 1, padding: '10px'}}>
//                         <Typography>Feedback:</Typography>

//                         <TextField
//                             multiline
//                             rows={3}
//                             type="text"
//                             fullWidth
//                             inputRef={input => input && input.focus()}
//                             variant="outlined"
//                             size='small'
//                             value={message}
                            
//                             inputProps={{
//                                 maxlength: 200
//                             }}
//                             helperText={`${message.length}/${200}`}
//                             onChange={handleMessageChange}
//                         /><br></br><br></br>
//                         <Button variant='soft' onClick={statusChangeSubmit}> SUBMIT</Button>
//                     </Box>





//                 </Grid>

//             </DialogContent>
//         </React.Fragment>
//     );
// }