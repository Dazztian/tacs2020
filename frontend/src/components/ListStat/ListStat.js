import React, {useState, useEffect} from 'react'
import {
  Grid,
  LinearProgress,
  CircularProgress,
  Select,
  OutlinedInput,
  MenuItem,
  TextField,
  InputLabel,
  Button
} from "@material-ui/core";
import ApexCharts from "react-apexcharts";
import { useTheme } from "@material-ui/styles";
import Widget from "../Widget";
import { Typography } from "../Wrappers";
import Dot from "../Sidebar/components/Dot";

import TableComponent from "../Table/Table"
import TableComponentb from "../Table/Tablebkp"

// styles
import useStyles from "../../views/user/dashboard/styles";

import Api from "../../apis/Api"
const api = new Api()

const ListStat = ({ data })=>{
  let sec;

  var theme = useTheme();
  var classes = useStyles();

    // local
  const [mainChartState, setMainChartState] = useState("Infected");
  const [isLoading, setIsLoading] = useState(false);


  const series = [
    {
      name: "Argentina",
      data: [1, 1, 2, 8]
    },
    {
      name: "Uruguay",
      data: [4, 6, 8, 29],
    },
    {      
      name: "Brasil",
      data: [1, 1, 2, 3]
  }
  ]

  const data = [ //suponiendo consulta de fromDay=1 toDay=4
      {
        countryname: "Argentina",
        offset:6,
        timeseridate:         [3/3/2020,4/3/2020,5/3/2020,6/3/2020],
        timeseriesinfected:   [1, 1, 2, 8],
        timeseriesreconvered: [0, 0, 0, 0],
        timeseriesdeath:      [0, 0, 0, 0],
        "confirmed": 25987,
        "deaths": 735,
        "recovered": 7991,
      },
      {
        name: "Uruguay",
        offset:14,
        timerseriesdate:      [13/3/2020,14/3/2020,15/3/2020,16/3/2020],
        timeseriesinfected:   [4, 6, 8, 29],
        timeseriesreconvered: [0, 0, 0, 0],
        timeseriesdeath:      [0, 0, 0, 0],
        "confirmed": 25987,
        "deaths": 735,
        "recovered": 7991,
      },
      {
        name: "Brasil",
        offset:1,
        timerseriesdate:      [26/2/2020,27/2/2020,28/2/2020,29/2/2020],
        timeseriesinfected:   [1, 1, 2, 3],
        timeseriesreconvered: [0, 0, 0, 0],
        timeseriesdeath:      [0, 0, 0, 0],
        "confirmed": 25987,
        "deaths": 735,
        "recovered": 7991,
      },
    ];
  
  async function handleFetchOffset(){
    setIsLoading(true);
    const res = await api
      if(true/*res.ok*/) {
        
      } else { //este else va por el res.ok
        //userDispatch({ type: "LOGIN_FAILURE" });
        
      }
      setIsLoading(false);
    } 

    useEffect(() => {
      switch (mainChartState){
        case
      }
    }, [mainChartState]); 

return(
    <>
  <Grid container spacing={1}>
  <Grid item lg={5} md={6} sm={12} xs={12}>
    <Widget 
      upperTitle
      noBodyPadding
      bodyClass={classes.mainChartBody}
      header={
        <div className={classes.mainChartHeader}/> }
    >
        <TableComponent data={data} />
      
    </Widget>
  </Grid>

  <Grid item lg={7} md={6} sm={12} xs={12}>
  <Widget 
      bodyClass={classes.mainChartBody}
      header={
        isLoading ? (
          <Grid 
            container
            spacing={0}
            alignItems="center"
            justify="center">
              <div className={classes.root}>
                <LinearProgress />
              </div>
            </Grid>
        ) : (
          <div className={classes.mainChartHeader}>
            <Grid   
              item lg={8} md={9} sm={10} xs={9}        
              container
              spacing={1}
              alignItems="center"
              
            >
            <Grid item xs={3} md={3} >
              <TextField
                id="filled-number"
                label="Offset start"
                type="number"
                margin='dense'
                size='small'
                fullWidth={false}
                inputProps={
                  {step: 1,}
                }
                onChange={(event) => { 
                  sec=event.target.value
                } }
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>
            <Grid item xs={3} md={3} >
              <TextField
                id="filled-number"
                label="Offset end"
                type="number"
                margin='dense'
                size='small'
                fullWidth={false}
                inputProps={
                  {step: 1,}
                }
                onChange={(event) => { 
                  sec=event.target.value
                } }
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>
          <Grid item>
            <Button xs={2} md={2} variant="outlined" color="primary" onClick={() =>{
              handleFetchOffset()
            }
            }>
              Submit
            </Button>
          </Grid> 
        </Grid>
        <Grid
            item lg={2} md={3} sm={2} xs={3}
            container
            spacing={1}             
            alignItems="center"
            > 
          <Grid > 
          <Select
              value={mainChartState}
              onChange={e => setMainChartState(e.target.value)}
              input={
                <OutlinedInput
                  labelWidth={0}
                  classes={{
                    notchedOutline: classes.mainChartSelectRoot,
                    input: classes.mainChartSelect,
                  }}
                />
              }
              autoWidth
            >
              <MenuItem value="Infected">Infected</MenuItem>
              <MenuItem value="Recovered">Recovered</MenuItem>
              <MenuItem value="Death">Death</MenuItem>
            </Select>
          </Grid>
        </Grid>          
      </div>
        )        
        }
  >
            <ApexCharts
            options={themeOptions(theme)}
            series={series}
            type="area"
            height="auto"
            />
    </Widget>
  </Grid>
  </Grid>
            </>
)
}

export default ListStat

  function themeOptions(theme) {
    return {
      dataLabels: {
        enabled: false,
      },
      stroke: {
        curve: "smooth",
      },
      xaxis: {
        type: "datetime",
        categories: [
          "2018-09-19T00:00:00",
          "2018-09-19T01:30:00",
          "2018-09-19T02:30:00",
          "2018-09-19T03:30:00",
          "2018-09-19T04:30:00",
          "2018-09-19T05:30:00",
          "2018-09-19T06:30:00",
        ],
      },
      tooltip: {
        x: {
          format: "dd/MM/yy HH:mm",
        },
      },
      fill: {
        colors: [theme.palette.primary.light, theme.palette.success.light],
      },
      colors: [theme.palette.primary.main, theme.palette.success.main],
      chart: {
        toolbar: {
          show: false,
        },
      },
      legend: {
        show: false,
      },
    };
  }