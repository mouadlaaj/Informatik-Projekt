import styles from "./sidenav.module.css"
import { Link, NavLink } from "react-router-dom";
import KeyboardDoubleArrowRightIcon from '@mui/icons-material/KeyboardDoubleArrowRight';
import KeyboardDoubleArrowLeftIcon from '@mui/icons-material/KeyboardDoubleArrowLeft';
import { navData, navDataBottom } from "../lib/navData";
import { useState } from "react";
import { Box, Divider, ListItemButton } from "@mui/material";
import { alpha } from '@mui/material/styles';
import { useMemo } from 'react';
import { useLocation } from 'react-router-dom';

export default function Sidenav() {
    const [open, setopen] = useState(false)
    const toggleOpen = () => {
        setopen(!open)
    }
    function usePathname() {
        const { pathname } = useLocation();
        
        return useMemo(() => pathname, [pathname]);
      }
   const pathname  = usePathname();
  return (
    
    <div className={open?styles.sidenav:styles.sidenavClosed} onMouseEnter={()=>setopen(true)} onMouseLeave={()=>setopen(false)}>
        
        <button className={styles.menuBtn}>
            {open? <KeyboardDoubleArrowLeftIcon  style={{fontSize:'1.2rem'}}/>: <KeyboardDoubleArrowRightIcon  style={{fontSize:'1.2rem'}}/>}
        </button>
        {navData.map(item =>{
            const active = item.link === pathname;
            return (
       
        <>
        <ListItemButton
      href={item.link}
      className={styles.sideitem}
      sx={{
        minHeight: 48,
        borderRadius: 1,
        typography: 'body2',
        color: "white",
        textTransform: 'capitalize',
        fontSize:'0.8rem',
        fontWeight: '200',
        ...(active && {
          color: 'orange',
          fontWeight: 'fontWeightSemiBold',
          bgcolor: (theme) => alpha(theme.palette.primary.main, 0.08),
          '&:hover': {
            bgcolor: 'orange',
            color:"#262673"
          },
        }),
        ...(!active && {
          '&:hover': {
            bgcolor: 'orange',
            color:"#262673"
          },
        }),
      }}
    >
      <Box component="span" sx={{ width: 24, height: 24, mr: 1 }}>
        {item.icon}
      </Box>

      <Box component="span" className={styles.linkText} 
      style={{
        fontWeight:'500', fontFamily:'sans-serif', 
      }
        
        }>{item.text} </Box>
    </ListItemButton>

    
    </>
            )
            
            
        })}

<Divider sx={{ mt: 20 }} />
<div className={styles.navBottom}>
  {navDataBottom.map((item) => {
    const active = item.link === pathname;
    return (
      <ListItemButton
        key={item.id}
        component={NavLink}
        to={item.link}
        className={styles.sideitem}
        sx={{
          minHeight: 48,
          borderRadius: 1,
          typography: 'body2',
          color: "white",
          textTransform: 'capitalize',
          fontSize: '0.8rem',
          fontWeight: '200',
          ...(active && {
            color: 'orange',
            fontWeight: 'fontWeightSemiBold',
            bgcolor: (theme) => alpha(theme.palette.primary.main, 0.08),
            '&:hover': {
              bgcolor: 'orange',
              color: "#262673"
            },
          }),
          ...(!active && {
            '&:hover': {
              bgcolor: 'orange',
              color: "#262673"
            },
          }),
        }}
      >
        <Box component="span" sx={{ width: 24, height: 24, mr: 1 }}>
          {item.icon}
        </Box>
        <Box
          component="span"
          className={styles.linkText}
          style={{
            fontWeight: '500',
            fontFamily: 'sans-serif',
          }}
        >
          {item.text}
        </Box>
      </ListItemButton>
    )
  }
  )
}
</div>



   </div>
  )
}