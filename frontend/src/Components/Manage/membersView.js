
import CottageIcon from '@mui/icons-material/Cottage';
import EmailRoundedIcon from '@mui/icons-material/EmailRounded';
import PhoneRoundedIcon from '@mui/icons-material/PhoneRounded';
import { Avatar, Badge, badgeClasses } from '@mui/joy';
import AspectRatio from '@mui/joy/AspectRatio';
import Button from '@mui/joy/Button';
import Card from '@mui/joy/Card';
import CardActions from '@mui/joy/CardActions';
import CardContent from '@mui/joy/CardContent'; 
import CardOverflow from '@mui/joy/CardOverflow';
import Typography from '@mui/joy/Typography';
import { DialogContent } from '@mui/material';
import * as React from 'react';
import profile from '../../assets/profile.png';
import profile1 from '../../assets/profile1.png';
import { stringAvatar } from '../../service/service-call';
import SupervisorAccountIcon from '@mui/icons-material/SupervisorAccount';

export default function ViewMembers({ viewMemberModal, member }) {

  return (
    <React.Fragment>
      <DialogContent>
        <Card
          sx={{
            textAlign: 'center',
            alignItems: 'center',
            width: 343,
            resize: 'horizontal',
            '--icon-size': '70px',
          }}
        >
          <CardOverflow variant="soft" color="primary" >
            <AspectRatio
              variant="outlined"
              color="warning"
              ratio="1"
              sx={{
                m: 'auto',
                transform: 'translateY(50%)',
                borderRadius: '50%',
                width: 'var(--icon-size)',
                boxShadow: 'sm',
                bgcolor: 'background.surface',
                position: 'relative',
              }}
            >
              <div>
                <Badge
                  anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                  badgeInset="20%"
                  variant={(member.status === "DROP") ? "outlined" : "solid"}
                  color={(member.status === "ACTIVE") ? "success" : "neutral"}
                  sx={{
                    padding: '5px',
                    [`& .${badgeClasses.badge}`]: {
                      '&::after': {
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        width: '100%',
                        height: '100%',
                        borderRadius: '50%',
                        animation: (member.status === "ACTIVE") ? "ripple 1.2s infinite ease-in-out" : "",
                        border: '2px solid',
                        borderColor: (member.status === "ACTIVE") ? 'success.500' : '',
                        content: '""',
                      },
                    },
                    '@keyframes ripple': {
                      '0%': {
                        transform: 'scale(1)',
                        opacity: 1,
                      },
                      '100%': {
                        transform: 'scale(2)',
                        opacity: 0,
                      },
                    },
                  }}
                >
                  <Avatar src={member.gender === "male" ? profile : profile1} size="lg" {...stringAvatar(member.firstName + " " + member.lastName)}></Avatar>
                </Badge>
              </div>
            </AspectRatio>
          </CardOverflow>
          <Typography level="title-lg" sx={{ mt: 'calc(var(--icon-size) / 2)', color: '#262673', fontSize: '0.9rem', fontWeight: 'bold' }}>
            {member.firstName.toUpperCase() + " " + member.lastName.toUpperCase()}
          </Typography>
          <Typography level="body-sm" sx={{ mt: '-12px', color: '#262673', fontSize: '0.7rem' }}>
            {"(" + member.id + ")"}
          </Typography>
        
          <CardContent sx={{ maxWidth: '40ch', textAlign: 'left', marginLeft: '30px' }}>
            <Typography level="body-xs" sx={{ color: '#262673', fontSize: '0.7rem' }}>
              <CottageIcon style={{ fontSize: '1rem' }} />&nbsp;{member.address}
            </Typography>
            <Typography level="body-xs" style={{ color: '#262673', fontSize: '0.7rem' }}>
              <PhoneRoundedIcon style={{ fontSize: '1rem' }} />+91&nbsp;{member.phoneNumber}
            </Typography >
            <Typography level="body-xs" style={{ color: '#262673', fontSize: '0.7rem' }}>
              <EmailRoundedIcon style={{ fontSize: '1rem' }} />&nbsp;{member.emailId}
            </Typography>
            <Typography level="body-xs" style={{ color: '#262673', fontSize: '0.7rem' }}>
              <SupervisorAccountIcon style={{ fontSize: '1rem' }} />&nbsp;{member.role}
            </Typography>
          </CardContent>
          <CardContent sx={{ maxWidth: '40ch' }}>


          </CardContent>
          <CardActions
            orientation="vertical"
            buttonFlex={1}
            sx={{
              '--Button-radius': '40px',
              width: 'clamp(min(100%, 160px), 50%, min(100%, 200px))',
            }}
          >
            <Button style={{ fontSize: '0.8rem' }} variant="plain" color="warning" onClick={() => viewMemberModal(null)}>
              CLOSE
            </Button>
          </CardActions>
        </Card>




      </DialogContent>
    </React.Fragment>
  );
}