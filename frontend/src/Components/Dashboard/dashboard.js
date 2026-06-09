import { Card, CardContent, CircularProgress, Grid, Typography, Tabs, TabList, Tab } from '@mui/joy';
import { easeQuadInOut } from 'd3-ease';
import React from 'react';
import { CircularProgressbar } from 'react-circular-progressbar';
import 'react-circular-progressbar/dist/styles.css';
import CountUp from 'react-countup';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from 'recharts';
import { getDesignationWiseReportStatus, getOverallTaskCount, logout } from '../../service/service-call';
import AnimatedProgressProvider from '../Charts/AnimatedProgressProvider';

export default function Home() {
  const [overAllCount, setOverallCount] = React.useState(null);
  const [chartData, setChartData] = React.useState(null);
  const [activeTab, setActiveTab] = React.useState(0);

  const teams = ['designer', 'developer', 'tester'];
  const currentTeam = teams[activeTab];

  React.useEffect(() => {
    getAllOveralltaskCount();
    getDesignationWiseData(currentTeam);
  }, [activeTab]);

  // Fetch overall task count
  function getAllOveralltaskCount() {
    getOverallTaskCount()
      .then((resp) => {
        if (resp.status === 401) {
          logout();
          return;
        }
        resp.json().then((data) => {
          setOverallCount(data);
        });
      })
      .catch((error) => {
        console.error('Error fetching overall task count:', error);
      });
  }

 const getDesignationWiseData = async (selectedTeam) => {
  const data = await getDesignationWiseReportStatus(selectedTeam);
  setChartData(data)
  console.log("-------------------> ", data.data);
};


  // Convert minutes to appropriate time format
  const convertMinutesToTimeFormat = (minutes) => {
    if (!minutes) return '0m';
    const years = Math.floor(minutes / (365 * 24 * 60));
    const months = Math.floor((minutes % (365 * 24 * 60)) / (30 * 24 * 60));
    const weeks = Math.floor((minutes % (30 * 24 * 60)) / (7 * 24 * 60));
    const days = Math.floor((minutes % (7 * 24 * 60)) / (24 * 60));
    const hours = Math.floor((minutes % (24 * 60)) / 60);
    const mins = minutes % 60;

    if (years > 0) return `${years}y ${months}m`;
    if (months > 0) return `${months}m ${weeks}w`;
    if (weeks > 0) return `${weeks}w ${days}d`;
    if (days > 0) return `${days}d ${hours}h`;
    if (hours > 0) return `${hours}h ${mins}m`;
    return `${mins}m`;
  };

  // Convert minutes to hours for calculation
  const convertMinutesToHours = (minutes) => {
    return minutes ? Math.round((minutes / 60) * 100) / 100 : 0;
  };

  // Transform data for team composition chart
  const getTeamCompositionData = () => {
    if (!chartData) return [];
    return [
      { name: 'Designers', count: chartData.designer?.totalCount || 0, color: '#8884d8' },
      { name: 'Developers', count: chartData.developer?.totalCount || 0, color: '#82ca9d' },
      { name: 'Testers', count: chartData.tester?.totalCount || 0, color: '#ffc658' },
    ];
  };

  // Get team data based on active tab for horizontal bar chart
  const getTeamData = () => {
    if (!chartData || !chartData.members) return [];

    return chartData.members.map((member) => ({
      memberId: member.memberId.length > 15 ? `${member?.memberId.substring(0, 15)}...` : member.memberId,
      actualTimeHours: convertMinutesToHours(member.actualTime),
      estimationTimeHours: convertMinutesToHours(member.estimationTime),
      actualTimeDisplay: convertMinutesToTimeFormat(member.actualTime),
      estimationTimeDisplay: convertMinutesToTimeFormat(member.estimationTime),
      efficiency: parseFloat(member.efficiency.replace('%', '')) || 0,
    }));
  };

  const COLORS = ['#8884d8', '#82ca9d', '#ffc658'];

  const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      return (
        <div
          className="custom-tooltip"
          style={{
            backgroundColor: 'white',
            border: '1px solid #ccc',
            borderRadius: '4px',
            padding: '10px',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
          }}
        >
          <p style={{ margin: 0, fontWeight: 'bold' }}>{`Member: ${label}`}</p>
          {payload.map((entry, index) => (
            <p key={index} style={{ margin: '4px 0', color: entry.color }}>
              {entry.name === 'Actual Time' && (
                <>{`Actual Time: ${entry.payload.actualTimeDisplay} (${entry.value}h)`}</>
              )}
              {entry.name === 'Estimated Time' && (
                <>{`Estimated Time: ${entry.payload.estimationTimeDisplay} (${entry.value}h)`}</>
              )}
            </p>
          ))}
          <p style={{ margin: '4px 0', color: '#ff7300' }}>
            {`Efficiency: ${payload[0]?.payload?.efficiency}%`}
          </p>
        </div>
      );
    }
    return null;
  };

  return (
    <div style={{ height: '84vh', overflow: 'auto' }}>
      {overAllCount && (
        <Typography level="body-sx" fontWeight={600} sx={{ marginBottom: '-15px', opacity: '0.5', fontSize: '0.8rem' }}>
          For Last 10 days
        </Typography>
      )}
      <Grid container spacing={1} direction="row" marginTop="20px">
        {overAllCount ? (
          <Grid container spacing={2} sx={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between' }}>
            {[
              { title: 'TODO', count: overAllCount.tasksInTodo, percent: overAllCount.tasksInTodoPercentage },
              { title: 'DESIGN', count: overAllCount.tasksInDesign, percent: overAllCount.tasksInDesignPercentage },
              { title: 'DEVELOPMENT', count: overAllCount.tasksInDevelopment, percent: overAllCount.tasksInDevelopmentPercentage },
              { title: 'TESTING', count: overAllCount.tasksInTest, percent: overAllCount.tasksInTestPercentage },
              { title: 'DONE', count: overAllCount.tasksInDone, percent: overAllCount.tasksInDonePercentage },
            ].map((stage, index) => (
              <Grid
                key={index}
                item
                xs={12}
                sm={6}
                md={2.4}
                lg={2.4}
                xl={2.4}
                sx={{
                  flexBasis: 'calc(10% - 16px)',
                  maxWidth: 'calc(30% - 16px)',
                  minWidth: '230px',
                }}
              >
                <Card
                  data-aos="flip-left"
                  data-aos-easing="ease-out-cubic"
                  data-aos-duration="2000"
                  sx={{
                    height: 100,
                    width: '100%',
                    backgroundColor: 'white',
                    '&:hover': {
                      color: '#262673',
                      boxShadow: 'rgba(0, 0, 0, 0.25) 0px 25px 50px -12px',
                    },
                  }}
                  variant="soft"
                >
                  <CardContent orientation="horizontal">
                    <AnimatedProgressProvider
                      valueStart={0}
                      valueEnd={parseFloat(stage.percent) || 0}
                      duration={1}
                      easingFunction={easeQuadInOut}
                    >
                      {(value) => {
                        const roundedValue = Math.round(value);
                        return (
                          <div style={{ width: 70 }}>
                            <CircularProgressbar
                              value={value}
                              text={`${roundedValue}%`}
                              strokeWidth={12}
                              styles={{
                                root: { height: '60px' },
                                path: { stroke: '#0B6BCB', strokeLinecap: 'round' },
                                trail: { stroke: '#F0F4F8' },
                              }}
                            />
                          </div>
                        );
                      }}
                    </AnimatedProgressProvider>
                    <CardContent orientation="vertical" sx={{ textAlign: 'center' }}>
                      <Typography level="title-sm" color="primary" fontWeight={700}>
                        {stage.title}
                      </Typography>
                      <Typography level="h1" fontWeight={700}>
                        <CountUp start={1} end={stage.count || 0} duration={1} />
                      </Typography>
                    </CardContent>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        ) : (
          <Grid item md={12}>
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
              <CircularProgress />
            </div>
          </Grid>
        )}
      </Grid>

      {/* Charts Section */}
      {chartData && (
        <Grid container spacing={3} sx={{ marginTop: '10px' }}>
          <Grid item xs={12}>
            <Typography level="h2" fontWeight={600} sx={{ marginBottom: '4px', color: '#262673' }}>
              Team Analytics
            </Typography>
          </Grid>

          {/* Team Composition Chart */}
          <Grid item xs={12} md={6}>
            <Card sx={{ backgroundColor: 'white', padding: '20px', height: '400px' }}>
              <Typography level="title-lg" fontWeight={600} sx={{ marginBottom: '20px', textAlign: 'center' }}>
                Team Composition
              </Typography>
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={getTeamCompositionData()}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, value }) => `${name}: ${value}`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="count"
                  >
                    {getTeamCompositionData().map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </Card>
          </Grid>

          {/* Team Time Tracking & Efficiency - Horizontal Bar Chart */}
          <Grid item xs={12} md={6}>
            <Card sx={{ backgroundColor: 'white', padding: '20px', height: '400px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <Typography level="title-lg" fontWeight={600}>
                  Team Time Tracking & Efficiency
                </Typography>
                <Tabs value={activeTab} onChange={(event, newValue) => setActiveTab(newValue)} size="sm">
                  <TabList>
                    <Tab>Designers</Tab>
                    <Tab>Developers</Tab>
                    <Tab>Testers</Tab>
                  </TabList>
                </Tabs>
              </div>
              <div style={{ width: '100%', height: '85%', overflowX: 'auto' }}>
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart
                    data={getTeamData()}
                    layout="vertical"
                    margin={{ top: 20, right: 30, left: 120, bottom: 5 }}
                    barCategoryGap="20%"
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis
                      type="number"
                      domain={[0, 'dataMax + 2']}
                      label={{ value: 'Hours', position: 'insideBottom', offset: -5 }}
                      tick={{ fontSize: 12 }}
                    />
                    <YAxis
                      type="category"
                      dataKey="memberId"
                      width={120}
                      tick={{ fontSize: 11 }}
                      interval={0}
                    />
                    <Tooltip content={<CustomTooltip />} />
                    <Legend />
                    <Bar
                      dataKey="estimationTimeHours"
                      fill="#82ca9d"
                      name="Estimated Time"
                      radius={[0, 4, 4, 0]}
                      minPointSize={5}
                    />
                    <Bar
                      dataKey="actualTimeHours"
                      fill="#8884d8"
                      name="Actual Time"
                      radius={[0, 4, 4, 0]}
                      minPointSize={5}
                    />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </Card>
          </Grid>
        </Grid>
      )}
    </div>
  );
}