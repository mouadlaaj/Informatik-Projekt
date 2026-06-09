import React from 'react';
import { Box } from '@mui/joy';
import Sidenav from './Components/Sidenav';
import HeaderNav from './Components/HeaderNav';

function MainLayout({ children }) {
  return (
    <Box sx={{ display: 'flex' }}>
      <Box sx={{ flexShrink: 0 }}>
        <Sidenav />
      </Box>
      <Box sx={{ flexGrow: 1, width: '100%' }}>
        <HeaderNav />
        <Box sx={{ padding: '10px', width: '100%' }}>
          {children}
        </Box>
      </Box>
    </Box>
  );
}

export default MainLayout;
