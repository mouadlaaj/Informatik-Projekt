import AssignmentIcon from '@mui/icons-material/Assignment';
import GroupAddIcon from '@mui/icons-material/GroupAdd';
import SettingsIcon from '@mui/icons-material/Settings';
import { LuLayoutDashboard } from 'react-icons/lu';
import { IoBarChartSharp } from 'react-icons/io5';
export const navData = [
    {
        id: 2,
        icon: <LuLayoutDashboard  style={{fontSize:'1.4rem'}}/>,
        text: "DASHBOARD",
        link: "/dashboard"
    },
    {
        id: 0,
        icon: <AssignmentIcon style={{fontSize:'1.4rem'}}/>,
        text: "TASK BOARD",
        link: "/board"
    },
    {
        id: 1,
        icon: <GroupAddIcon  style={{fontSize:'1.4rem'}}/>,
        text: "MANAGE",
        link: "/manage"
    },
    
    {
        id: 4,
        icon: <IoBarChartSharp style={{fontSize:'1.4rem'}}/>,
        text: "REPORTS",
        link: "/reports"
    }
]

export const navDataBottom = [
    {
      id: 5,
      icon: <SettingsIcon style={{ fontSize: '1.4rem' }} />,
      text: "SETTINGS",
      link: "/settings",
    },
  ];