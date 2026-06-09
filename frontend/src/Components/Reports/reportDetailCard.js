import React from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Typography,
  Container,
  Box,
} from '@mui/material';

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString("en-GB");
};

export default function ReportCard({data }) {
 if (!Array.isArray(data) || data.length === 0) {
  return <Typography>No data available</Typography>;
}


  const teamNames = [...new Set(data.map((item) => item.teamName))];

  return (
    <Container sx={{ mt: 4 }}>
      {teamNames.map((teamName, i) => (
        <Box key={i} sx={{ mb: 4 }}>
          <Typography
            variant="h6"
            sx={{
              mb: 2,
              color: "#262673",
              fontWeight: "bold",
              textDecoration: "underline",
              textTransform: "uppercase",
            }}
          >
            {teamName}
          </Typography>

          <TableContainer component={Paper} elevation={3}>
            <Table size="small">
              <TableHead>
                <TableRow sx={{ backgroundColor: "#f0f0f0" }}>
                  <TableCell>
                    <strong>Date</strong>
                  </TableCell>
                  <TableCell>
                    <strong>MemberId</strong>
                  </TableCell>
                  <TableCell>
                    <strong>MemberName</strong>
                  </TableCell>
                  <TableCell>
                    <strong>Designation</strong>
                  </TableCell>
                  <TableCell>
                    <strong>TaskId</strong>
                  </TableCell>
                  <TableCell>
                    <strong>TaskTitle</strong>
                  </TableCell>
                  <TableCell>
                    <strong>EstimatedTime</strong>
                  </TableCell>
                  <TableCell>
                    <strong>ActualSpentTime</strong>
                  </TableCell>
                  <TableCell>
                    <strong>Percentage</strong>
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {data
                  .filter((row) => row.teamName === teamName)
                  .map((row, idx) => (
                    <TableRow key={idx}>
                      <TableCell>{formatDate(row.workInfo.date)}</TableCell>
                      <TableCell>{row.workInfo.memberId}</TableCell>
                      <TableCell>{row.workInfo.memberName}</TableCell>
                      <TableCell>{row.workInfo.designation}</TableCell>
                      <TableCell>{row.workInfo.taskId}</TableCell>
                      <TableCell>{row.workInfo.taskTitle}</TableCell>
                      <TableCell>{row.workInfo.estimatedTime}</TableCell>
                      <TableCell>{row.workInfo.actualSpentTime}</TableCell>
                      <TableCell>{row.workInfo.percentage}</TableCell>
                    </TableRow>
                  ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Box>
      ))}
    </Container>
  );
}
