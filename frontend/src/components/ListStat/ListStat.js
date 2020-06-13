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

import ColapsableTable from "../Table/ColapsableTable"

// styles
import useStyles from "../../views/user/dashboard/styles";

import Api from "../../apis/Api"
const api = new Api()

const ListStat = ({ data })=>{

  var theme = useTheme();
  var classes = useStyles();

    // local
  const [mainChartState, setMainChartState] = useState("Infected");
  const [isLoading, setIsLoading] = useState(false);
  const [series, setSeries] = useState([])
  const [rows,setRows] = useState([])
  const [numDaysArray,setNumDays] = useState([])
  const [dayFinal,setOffDayFinal] = useState(0)
  const [dayInicial,setOffDayInicial] = useState(0)

  async function handleFetchOffset(offinicial,offfinal){
    setIsLoading(true);
    const isoList = []
    
    const res = await api.getCountryDataByDays(isoList,offinicial,offfinal)
    
      if(true/*res.ok*/) {
        //const data = await res.json()
        const data = res
        console.log(data)
        setRows(data)
        setNumDays(await createDays(data))
        setMainChartState("");
        setMainChartState("Infected");
      } else {
        
      }
      setIsLoading(false);
      
    }

    async function createDays(data){
      let dayArray = []
      const final = await data.filter( r => r.offset===1 )[0].timerseriesdate.length
      console.log(final)
      for(let i=1;i<=final;i++){
        dayArray.push(`Day ${i}`)
      }
      return dayArray
    }
    
    async function alterChartData(newChartState){
      let promArr = []
      switch (newChartState){
        case "Infected" : 
              promArr = await rows.map(row => {return {name: row.countryname, data: row.timeseriesinfected}})
              break;
        case "Recovered" :
              promArr = await rows.map(row => {return {name: row.countryname, data: row.timeseriesreconvered}})
              break;
        case "Death" : 
              promArr = await rows.map(row => {return {name: row.countryname, data: row.timeseriesdeath}})
              break;
        default: 
              break; 
      }
      setSeries(await Promise.all(promArr))
    }

    useEffect(() => {
      alterChartData(mainChartState);
    }, [mainChartState]); 

return(
    <>
  <Grid container spacing={1}>
  <Grid item lg={12} md={12} sm={12} xs={12}>
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
                value={dayInicial}
                onChange={(event) => { 
                 setOffDayInicial(event.target.value)
                } }
                InputLabelProps={{
                  shrink: true,
                }}
                variant="outlined"
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
                value={dayFinal}
                onChange={(event) => { 
                  setOffDayFinal(event.target.value)
                } }
                InputLabelProps={{
                  shrink: true,
                }}
                variant="outlined"
              />
            </Grid>
          <Grid item>
            <Button 
              xs={2} 
              md={2} 
              variant="outlined" 
              color="primary" 
              disabled={
                dayFinal === undefined || dayInicial === undefined || dayFinal<=dayInicial
              }
              onClick={() =>{
              handleFetchOffset(dayInicial,dayFinal)
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
            options={themeOptions(numDaysArray,theme)}
            series={series}
            type="area"
            height="auto"
            />
    </Widget>
  </Grid>
  <Grid item lg={12} md={12} sm={12} xs={12}>
    <Widget 
      upperTitle
      noBodyPadding
      bodyClass={classes.mainChartBody}
      header={
        <div className={classes.mainChartHeader}/> }
    >
        <ColapsableTable data={data}/>
    </Widget>
  </Grid>
  </Grid>
            </>
)
}

export default ListStat

  function themeOptions(numDaysArray,theme) {
    return {
      dataLabels: {
        enabled: false,
      },
      stroke: {
        curve: "smooth",
      },
      xaxis: {
        type: "string",
        categories: numDaysArray
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