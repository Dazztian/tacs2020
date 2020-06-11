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

// styles
import useStyles from "../../views/user/dashboard/styles";

import Api from "../../apis/Api"
const api = new Api()

const ListStat = ({ data })=>{
  let sec;

    const [unArray,setUnArray] = useState([{}])
    const [loading,setLoading] = useState(false)
    const [isLoading, setIsLoading] = useState(false);
    
    //esta funcion podria ir definida en el archivo api.js
    const submitData = async (unPais)=>{
      try{
      let res = await fetch("http://localhost:8080/api/countries/"+ unPais+ "/timeseries",{
          method:"GET",
          headers:{
              'Content-Type': 'application/json'
          }
      })
      let elemento = await res.json()
      
      let maxCantDias =  Math.max(...elemento.timeseries.map( item => { return [item.number]}))
  
      //ARRAY de elementos ordenanados de menor a mayor POR FECHA
      let timeseriesOrdenado= elemento.timeseries.sort( (a,b) =>{
        return  new Date(a.date) - new Date(b.date);
      });        
             
      let promArray = timeseriesOrdenado.map(({number, date, ...keepAttrs}) => keepAttrs)
  
      let timeSeriesFiltrado = await Promise.all(promArray)
  
      let result = {codigo: elemento.countrycode.iso3,
                       confirmed: elemento.confirmed,
                       deaths: elemento.deaths,
                       recovered: elemento.recovered,
                       diasMaximos: maxCantDias,
                       timeseries: timeSeriesFiltrado}        
  
      setUnArray(result)
      
      }
      catch(err) {
          console.log(err)
          //window.alert(err)
      }
  }
  
    useEffect(() => {

    }, []);    
    

    var theme = useTheme();
    var classes = useStyles();

    // local
  var [mainChartState, setMainChartState] = useState("Infected");
  
  const series = [
      {
        name: "Pais1",
        data: [31, 40, 28, 51, 42, 109, 100],
      },
      {
        name: "Pais2",
        data: [11, 32, 45, 32, 34, 52, 41],
      },
      {
        name: "Pais3",
        data: [7, 48, 12, 32, 63, 8, 72],
      },
    ];
  
  async function handleFetchOffset(){
    setIsLoading(true);
    const res = await api.loginUser()
      if(true/*res.ok*/) {
        
      } else { //este else va por el res.ok
        //userDispatch({ type: "LOGIN_FAILURE" });
        
      }
      setIsLoading(false);
    } 

return(
    <>
  <Grid container spacing={2}>
  <Grid item lg={5} md={6} sm={12} xs={12}>
    <Widget 
      upperTitle
      noBodyPadding
      bodyClass={classes.mainChartBody}
      header={
        <div className={classes.mainChartHeader}/> }
    >
        <TableComponent data={data.near} />
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
              justify="left"
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
            justify="right"> 
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
            height={350}
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