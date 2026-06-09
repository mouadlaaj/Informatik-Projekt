import { styled } from '@mui/material';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import React from 'react';


import { buttonClasses } from '@mui/base/Button';
import { Tab as BaseTab, tabClasses } from '@mui/base/Tab';
import { TabPanel as BaseTabPanel } from '@mui/base/TabPanel';
import { Tabs } from '@mui/base/Tabs';
import { TabsList as BaseTabsList } from '@mui/base/TabsList';
import Projects from '../Components/Manage/Projects/projects';
import Teams from '../Components/Manage/Teams/teams';
import Members from '../Components/Manage/members';

export default function Manage() {
  const [value, setValue] = React.useState(0);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  const blue = {
    50: '#F0F7FF',
    100: '#C2E0FF',
    200: '#80BFFF',
    300: '#66B2FF',
    400: '#3399FF',
    500: '#007FFF',
    600: '#0072E5',
    700: '#0059B2',
    800: '#004C99',
    900: '#003A75',
  };
  
  const grey = {
    50: '#F3F6F9',
    100: '#E5EAF2',
    200: '#DAE2ED',
    300: '#C7D0DD',
    400: '#B0B8C4',
    500: '#9DA8B7',
    600: '#6B7A90',
    700: '#434D5B',
    800: '#303740',
    900: '#1C2025',
  };
  
  const Tab = styled(BaseTab)`
    font-family: 'IBM Plex Sans', sans-serif;
    color: #262673;
    cursor: pointer;
    font-size: 0.8rem;
    font-weight: 600;
    background-color: transparent;
    width: 100%;
    padding: 3px 3px;
    margin: 6px;
    border: none;
    border-radius: 7px;
    display: flex;
    justify-content: center;
  
    &:hover {
      background-color: white;
      color:  #262673;
      transition: transform .5s;
      transform: scale(1.02);
    }
  
    &:focus {
      color: #262673;
      outline: 1px solid orange;
    }
  
    &.${tabClasses.selected} {
      background-color: white;
      color:  orange;
    }
  
    &.${buttonClasses.disabled} {
      opacity: 0.5;
      cursor: not-allowed;
    }
  `;
  
  const TabPanel = styled(BaseTabPanel)(
    ({ theme }) => `
    width: 100%;
    min-height: 75vh;
    max-height: auto;
    font-family: 'IBM Plex Sans', sans-serif;
    font-size: 0.875rem;
    padding: 10px 12px;
    background: ${theme.palette.mode === 'dark' ? grey[900] : '#fff'};
    border: 1px solid ${theme.palette.mode === 'dark' ? grey[700] : grey[200]};
    border-radius: 12px;
    opacity: 1;
    `,
  );
  
  const TabsList = styled(BaseTabsList)(
    ({ theme }) => `
    min-width: 400px;
    background-color: white;
    color: #262673;
    border-radius: 12px;
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    align-content: space-between;
    box-shadow: 0px 4px 30px ${theme.palette.mode === 'dark' ? grey[900] : grey[200]};
    `,
  );
  return (
    <>
       <Tabs defaultValue={1} >
      <TabsList>
        <Tab value={1}>MEMBERS</Tab>
        <Tab value={2}>TEAMS</Tab>
        <Tab value={3}>PROJECTS</Tab>
      </TabsList>
      <TabPanel value={1} ><Members/></TabPanel>
      <TabPanel value={2}><Teams/></TabPanel>
      <TabPanel value={3}><Projects/></TabPanel>
    </Tabs>
      
   </>
  )
}

function CustomTabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{ p: 3 }}>
          <Typography>{children}</Typography>
        </Box>
      )}
    </div>
  );
}

function a11yProps(index) {
  return {
    id: `simple-tab-${index}`,
    'aria-controls': `simple-tabpanel-${index}`,
  };
}
