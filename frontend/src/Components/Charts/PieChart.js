import React, { Fragment, useState } from "react";
import { AgChartsReact } from "ag-charts-react";
import { Card, CircularProgress } from "@mui/joy";

export default function PieChartForAttendance({dbData}){
    function getData() {
        return dbData;
      }

  const [options, setOptions] = useState({
    
    data: getData(),
    theme: 'ag-material',
    title: {
      text: "Today's Attendance"+" ("+dbData[0].date+")",
      fontSize: 14,
      fontWeight:'bold',
      color:'#0B6BCB'
    },
    subtitle: {
        text: "Total: "+dbData[0].all +" members",
        fontSize: 12,
      },
    series: [
      {
        type: "pie",
        calloutLabelKey: "title",
        angleKey: "count",
        sectorLabelKey: "count",
        sectorSpacing:'5px',
        showInLegend:true,
        
        visible: true,
        tooltip: {
            renderer: (e) => {
              return {
                title: e.title,
                content: e.datum.list
              };
            },
          },
      },
      
    ],
  });

  return (
    <Card variant='plain' sx={{minHeight:'330px', maxHeight:'330px', overflowY:"scroll", padding:'10px', marginTop:'10px',"&:hover": { color: '#262673', boxShadow:'rgba(0, 0, 0, 0.25) 0px 25px 50px -12px' } }}>
  <AgChartsReact options={options} />
  </Card> 
  )
};
