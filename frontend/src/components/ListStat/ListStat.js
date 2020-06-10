import React, {useState, useEffect} from 'react'
import {
  Grid,
  LinearProgress,
  Select,
  OutlinedInput,
  MenuItem,
} from "@material-ui/core";
import ApexCharts from "react-apexcharts";
import { useTheme } from "@material-ui/styles";
import Widget from "../Widget";
import { Typography } from "../Wrappers";
import Dot from "../Sidebar/components/Dot";

import TableComponent from "../Table/Table"

// styles
import useStyles from "../../views/user/dashboard/styles";


const ListStat = ({ data })=>{
  
  async function fetchNearData() {
    try {
  
    } catch(error) {
      console.log(error)
    }
    setLoading(false)
  }

    const [unArray,setUnArray] = useState([{}])
    const [loading,setLoading] = useState(false)

    
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
  

return(
    <>
  <Grid container spacing={2}>
  <Grid item lg={12} md={12} sm={12} xs={12}>
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

  <Grid item lg={12} md={12} sm={12} xs={12}>
  <Widget 
      bodyClass={classes.mainChartBody}
      header={
        <div className={classes.mainChartHeader}>   
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
            {/*Acá debería recibir la lista de países*/}
            <MenuItem value="Infected">Infected</MenuItem>
            <MenuItem value="Recovered">Recovered</MenuItem>
            <MenuItem value="Death">Death</MenuItem>
          </Select>         
        </div>
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