import React, { Fragment, useState } from "react";
import { createRoot } from "react-dom/client";
import { AgChartsReact } from "ag-charts-react";
import { Card } from "@mui/joy";

export default function BarChartStacked ({dbData, title, subTitle, height}){
    function getData() {
        return dbData;
      }
  const [options, setOptions] = useState({
    width : 800,
    height  :height,
        padding: {
            top:20
        },
    title: {
      text: title,
      fontSize: 14,
      fontWeight:'bold',
      color:'#0B6BCB'
    },
    subtitle: {
      text: subTitle,
      fontSize: 12,
    },
    // background: {
    //     fill: 'rgb(63, 127, 255)',
    // },
    scales: {
      xAxes: [{
          barPercentage: 50
      }],
      yAxes: [
        {
          barPercentage: 80
        }
      ]
  },
    data: getData(),
    theme: 'ag-material',
    // theme: {
    //   overrides: {
    //     bar: {
    //       series: {
    //         stroke: "transparent",
    //         cornerRadius: 6,
    //         strokeWidth: 0.9,
    //       },
    //     },
    //   },
    // },
    series: [
      
        {
            type: "bar",
            direction: localStorage.getItem("role")==="EMPLOYEE" ? "vertical": "horizontal",
            xKey: "member",
            yKey: "inprogressEstimatedTime",
            yName: "PRODUCTION ESTIMATED TIME(Hrs)",
            stackGroup: "NOL",
            stacked: true,
            grouped: true,
            fill: "#ffa04a",
          },
          {
            type: "bar",
            direction: localStorage.getItem("role")==="EMPLOYEE" ? "vertical": "horizontal",
            xKey: "member",
            yKey: "actualInprogressTime",
            yName: "PRODUCTION ACTUAL TIME(Hrs)",
            stackGroup: "NOL",
            stacked: true,
            grouped: true,
            fill: "#262674",
          },
          {
            type: "bar",
                direction: localStorage.getItem("role")==="EMPLOYEE" ? "vertical": "horizontal",
                xKey: "member",
                yKey: "inprogressEfficiencyPercentage",
                yName: "PRODUCTION EFFICIENCY(%)",
                stackGroup: "NOL",
                stacked: true,
                grouped: true,
                fill: "green",
          },
      {
        type: "bar",
            direction: localStorage.getItem("role")==="EMPLOYEE" ? "vertical": "horizontal",
            xKey: "member",
            yKey: "qcEstimatedTime",
            yName: "QC ESTIMATED TIME(Hrs)",
            stackGroup: "QC",
            stacked: true,
            grouped: true,
            fill: "#ffa03a",
      },
      {
        type: "bar",
            direction: localStorage.getItem("role")==="EMPLOYEE" ? "vertical": "horizontal",
            xKey: "member",
            yKey: "actualQcTime",
            yName: "QC ACTUAL TIME(Hrs)",
            stackGroup: "QC",
            stacked: true,
            grouped: true,
            fill: "#262673",
      },
      
      {
        type: "bar",
        direction: localStorage.getItem("role")==="EMPLOYEE" ? "vertical": "horizontal",
        xKey: "member",
        yKey: "qcEfficiencyPercentage",
        yName: "QC EFFICIENCY(%)",
        stackGroup: "QC",
        grouped: true,
        stacked: true,
        fill: "lightgreen",
      },

      {
        type: "bar",
        direction: localStorage.getItem("role")==="EMPLOYEE" ? "vertical": "horizontal",
        xKey: "member",
        yKey: "inprogressReworkCount",
        yName: "REWORK",
        stackGroup: "NOL",
        grouped: true,
        stacked: true,
        fill: "red",
      },
    ],

  });

  return (
    <Card style={{minHeight:'330px', maxHeight:'330px', overflowY:"scroll", padding:'10px', marginTop:'10px'}}>
    <AgChartsReact options={options}  />
    </Card>
  );
};
