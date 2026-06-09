// import { Button } from '@mui/joy';
// import { FormControl, Grid, InputLabel, MenuItem, Select, TextField } from '@mui/material';
// import Box from '@mui/material/Box';
// import DialogContent from '@mui/material/DialogContent';
// import Typography from '@mui/material/Typography';
// import * as React from 'react';
// import { changeTaskStatus } from '../../service/service-call';
// import { toast } from 'react-toastify';

// export default function TaskStatusChange({ toggleStatusChangeMessageModal, taskId, status }) {

//     const [message, setMessage] = React.useState('');

   

//     const statusChangeSubmit = () =>{
//         if(message===null || message === undefined || message === ""){
//             toast.error('Message cannot be blank!');
//             return false;
//         }
//         changeTaskStatus(taskId, status, message, type).then(resp => {
//                 toggleStatusChangeMessageModal();
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
            
//         </React.Fragment>
//     );
// }