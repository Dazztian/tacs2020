import React from 'react'
import {
  Grid,
} from "@material-ui/core";

import {
    ResponsiveContainer,
    LineChart,
    Line,
    PieChart,
    Pie,
    Cell,
  } from "recharts";

import { useTheme } from "@material-ui/styles";


//Components
import { Typography } from "../Wrappers";
import Dot from "../Sidebar/components/Dot";
import Widget from "../Widget";

// styles
import useStyles from "../../views/user/dashboard/styles";

const ListTotalStats = ({actualCountryData})=>{
  var theme = useTheme();
  var classes = useStyles();
    /*const [newCases,setNewCases] = useState(actualCountryData.newCases)
    const [newRecovered,setNewRecovered] = useState(actualCountryData.newRecovered)
    const [newDeath,setNewDeath] = useState(actualCountryData.newDeath)
    const [pieChartData,setPieChartData] = useState(
        [
            { name: "", value: actualCountryData.recovered, color: "success" },
            { name: "", value: actualCountryData.confirmed, color: "warning" },
            { name: "", value: actualCountryData.deaths, color: "secondary" }, 
        ])
    const [rateInfected,setRateInfected] = useState((actualCountryData.newCases*100/actualCountryData.confirmed).toFixed(2))
    const [rateRecovered,setRateRecovered] = useState((actualCountryData.newRecovered*100/actualCountryData.recovered).toFixed(2))
    const [rateDeath,setRateDeath] = useState((actualCountryData.newDeath*100/actualCountryData.deaths).toFixed(2))
    */
  let pieChartData = [
    { name: "", value: actualCountryData.recovered, color: "success" },
    { name: "", value: actualCountryData.confirmed, color: "warning" },
    { name: "", value: actualCountryData.deaths, color: "secondary" }, 
  ]

    const newCases = actualCountryData.newCases
    const newRecovered = actualCountryData.newRecovered
    const newDeath = actualCountryData.newDeath
   
    const rateInfected = (actualCountryData.newCases*100/actualCountryData.confirmed).toFixed(2)
    const rateRecovered = (actualCountryData.newRecovered*100/actualCountryData.recovered).toFixed(2)
    const rateDeath = (actualCountryData.newDeath*100/actualCountryData.deaths).toFixed(2)
  
return (
    <div>
        <Grid container>
                    <Grid container spacing={2}>
                        <Grid item lg={6} md={6} sm={6} xs={6}>
                    <Widget
                    upperTitle
                    bodyClass={classes.fullHeightBody}
                    header={
                        <div className={classes.mainChartHeader}>
                        <Typography
                            variant="h5"
                            color="text"
                            colorBrightness="secondary"
                        >
                            New confirmed
                        </Typography>
                        </div>
                    }
                    >
                    <div className={classes.visitsNumberContainer}>
                        <Typography size="xl" weight="medium">
                        {newCases}
                        </Typography>
                        <LineChart
                        width={55}
                        height={30}
                        data={[
                            { value: 10 },
                            { value: 15 },
                            { value: 10 },
                            { value: 17 },
                            { value: 18 },
                        ]}
                        margin={{ left: theme.spacing(2) }}
                        >
                        <Line
                            type="natural"
                            dataKey="value"
                            stroke={theme.palette.warning.main}
                            strokeWidth={2}
                            dot={false}
                        />
                        </LineChart>
                    </div>
                    <Grid
                        container
                        direction="row"
                        justify="space-between"
                        alignItems="center"
                    >
                        <Grid item>
                        <Typography color="text" colorBrightness="secondary">
                            Growth rate
                        </Typography>
                        <Typography size="md">+{rateInfected}%</Typography>
                        </Grid>
                    </Grid>
                    </Widget>
                </Grid>
                <Grid item lg={6} md={6} sm={6} xs={6}>
                <Widget
                    upperTitle
                    bodyClass={classes.fullHeightBody}
                    header={
                        <div className={classes.mainChartHeader}>
                        <Typography
                            variant="h5"
                            color="text"
                            colorBrightness="secondary"
                        >
                            New recovered
                        </Typography>
                        </div>
                    }
                    >
                    <div className={classes.visitsNumberContainer}>
                        <Typography size="xl" weight="medium">
                        {newRecovered}
                        </Typography>
                        <LineChart
                        width={55}
                        height={30}
                        data={[
                            { value: 10 },
                            { value: 15 },
                            { value: 10 },
                            { value: 17 },
                            { value: 18 },
                        ]}
                        margin={{ left: theme.spacing(2) }}
                        >
                        <Line
                            type="natural"
                            dataKey="value"
                            stroke={theme.palette.success.main}
                            strokeWidth={2}
                            dot={false}
                        />
                        </LineChart>
                    </div>
                    <Grid
                        container
                        direction="row"
                        justify="space-between"
                        alignItems="center"
                    >
                        <Grid item>
                        <Typography color="text" colorBrightness="secondary">
                            Growth rate
                        </Typography>
                        <Typography size="md">+{rateRecovered}%</Typography>
                        </Grid>
                    </Grid>
                    </Widget>
                </Grid>
                <Grid  item lg={6} md={6} sm={6} xs={6}> 
                <Widget
                    upperTitle
                    bodyClass={classes.fullHeightBody}
                    header={
                        <div className={classes.mainChartHeader}>
                        <Typography
                            variant="h5"
                            color="text"
                            colorBrightness="secondary"
                        >
                            New deceased
                        </Typography>
                        </div>
                    }
                    >
                    <div className={classes.visitsNumberContainer}>
                        <Typography size="xl" weight="medium">
                        {newDeath}
                        </Typography>
                        <LineChart
                        width={55}
                        height={30}
                        data={[
                            { value: 10 },
                            { value: 15 },
                            { value: 10 },
                            { value: 17 },
                            { value: 18 },
                        ]}
                        margin={{ left: theme.spacing(2) }}
                        >
                        <Line
                            type="natural"
                            dataKey="value"
                            stroke={theme.palette.error.main}
                            strokeWidth={2}
                            dot={false}
                        />
                        </LineChart>
                    </div>
                    <Grid
                        container
                        direction="row"
                        justify="space-between"
                        alignItems="center"
                    >
                        <Grid item>
                        <Typography color="text" colorBrightness="secondary">
                            Growth rate
                        </Typography>
                        <Typography size="md">+{rateDeath}%</Typography>
                        </Grid>
                    </Grid>
                    </Widget>
                </Grid>
                <Grid item lg={6} md={6} sm={6} xs={6}>
                    <Widget
                    upperTitle
                    bodyClass={classes.fullHeightBody}
                    header={
                        <div className={classes.mainChartHeader}>
                        <Typography
                            variant="h5"
                            color="text"
                            colorBrightness="secondary"
                        >
                            Overall total
                        </Typography>
                        </div>
                    }
                    >
                    <Grid container spacing={2}>
                        <Grid item xs={6}>
                        <ResponsiveContainer width="100%" height={82}>
                            <PieChart margin={{ left: theme.spacing(2) }}>
                            <Pie
                                data={pieChartData}
                                innerRadius={0}
                                outerRadius={35}
                                dataKey="value"
                            >
                                {pieChartData.map((entry, index) => (
                                <Cell
                                    key={`cell-${index}`}
                                    fill={theme.palette[entry.color].main}
                                />
                                ))}
                            </Pie>
                            </PieChart>
                        </ResponsiveContainer>
                        </Grid>
                        <Grid item xs={6}>
                        <div className={classes.pieChartLegendWrapper}>
                            {pieChartData.map(({ name, value, color }, index) => (
                            <div key={color} className={classes.legendItemContainer}>
                                <Dot color={color} />
                                <Typography style={{ whiteSpace: "nowrap" }}>
                                &nbsp;{name}&nbsp;
                                </Typography>
                                <Typography color="text" colorBrightness="secondary">
                                &nbsp;{value}
                                </Typography>
                            </div>
                            ))}
                        </div>
                        </Grid>
                    </Grid>
                    </Widget>
                </Grid>
                </Grid>
        </Grid>
    </div>
    );
}

export default  ListTotalStats;