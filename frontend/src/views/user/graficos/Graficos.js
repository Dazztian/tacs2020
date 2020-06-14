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
import Widget from "../../../components/Widget";
import { Typography } from "../../../components/Wrappers";
import Dot from "../../../components/Sidebar/components/Dot";
import PageTitle from "../../../components/PageTitle/PageTitle";
import {
  ResponsiveContainer,
  ComposedChart,
  AreaChart,
  LineChart,
  Line,
  Area,
  PieChart,
  Pie,
  Cell,
  YAxis,
  XAxis,
} from "recharts";



import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import { FixedSizeList } from 'react-window';


// styles
import useStyles from "../dashboard/styles";


const Graficos = ()=>{

    const [unArray,setUnArray] = useState([{}])
    const [unArrayListasDePaises,setunArrayListasDePaises] = useState([{}])
    const [unArrayPaisesXLista,setunArrayPaisesXLista] = useState([])
    
    const [loading,setLoading] = useState(false)
    const [count,setCount] = useState(0)
    
    const id_user= "5ee3911bd0a2646d1b8f1279"
    const token ="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImlzcyI6InRhY3MiLCJpZCI6IjVlZTM5MTFiZDBhMjY0NmQxYjhmMTI3OSIsImV4cCI6MTU5MjAwODEyM30.IOk4miqYmBhI6S3fgFSv4fRYvylUgj3Jo9a94s6_Pig"

    const obtenerListasDePaisesXUsuario = async ()=>{
      try{
          let res = await fetch("http://localhost:8080/api/user/"+ id_user + "/lists",{
          method:"get",
          headers:{
              'Accept': 'application/json',
              'Authorization' : 'Bearer '+ token
              }
          })
          let elemento = await res.json();

          let promArray = elemento.map( item=>{ return {name:item.name, id:item.id} })

          let resultArray = await Promise.all(promArray)

          console.log(resultArray)

          setunArrayListasDePaises(resultArray)  
      }
      catch(err) {
          console.log(err)
          window.alert(err)
      }
  }
  const obtenerPaisesXLista = async (lista)=>{
    try{
        let res = await fetch("http://localhost:8080/api/user/"+ id_user + "/lists/"+ lista,{
        method:"get",
        headers:{
            'Accept': 'application/json',
            'Authorization' : 'Bearer '+ token
            }
        })
        let elemento = await res.json();

        //let promArray = elemento.map( item=>  item.countries.map(pais => [pais]) )
        let promArray = elemento.countries

        let resultArray = await Promise.all(promArray)

        setunArrayPaisesXLista(resultArray)  
    }
    catch(err) {
        console.log(err)
        window.alert(err)
    }
}


    const obtenerInfoXPais = async (unPais)=>{
      try{
        //timeseries?countries=AR
      let res = await fetch("http://localhost:8080/api/countries/"+ unPais+ "/timeseries",{
          method:"GET",
          headers:{
            'Accept': 'application/json',
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
          window.alert(err)
      }
  }
  
    useEffect(() => {
        setLoading(true)
        obtenerListasDePaisesXUsuario()
        obtenerInfoXPais('US')
        obtenerInfoXPais('AR')
        obtenerInfoXPais('IT')
        setLoading(false)
    }, [count]);    
    

    var theme = useTheme();
    var classes = useStyles();

    // local
  var [mainChartState, setMainChartState] = useState("monthly");
  
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
  
    /*-----------------ESTE CODIGO ES EL QUE GENERA EL SELECT LIST-----------------*/
    const Column = ({ index, style }) => (
      <ListItem button style={style} key={index} //Debo setear la nueva lista a mostrar window.alert(unArrayListasDePaises[index].name)     
       onClick={()=>obtenerPaisesXLista(unArrayListasDePaises[index].id)}
      >
      <ListItemText primary={unArrayListasDePaises[index].name} 
                    secondary={unArrayListasDePaises[index].id}//Queda pendiente ver como ocultar el id
      /> {/*obtener lista por id*/}
      </ListItem>
    );
     
    const Example = () => (
      <FixedSizeList
        height={125}
        itemCount={unArrayListasDePaises.length}
        itemSize={150}
        layout="horizontal"
        width={450}
      >
        {Column}
      </FixedSizeList>
    )
    /*-----------------ACA TERMINA EL CODIGO  QUE GENERA EL SELECT LIST---------------*/


      /*-----------------------Codigo para mostrar distintos paises-----------------------*/
      const listaDePaises =['US','AR','IT']
      const MuestraPaisesLista = (lista) => (
        unArrayPaisesXLista.map( elemento => { return [ 
          //['US','AR','IT'].map( elemento => { return [ 
        <MenuItem value={elemento} onClick={() =>  obtenerInfoXPais(elemento)}>{elemento}</MenuItem>
        ]
      })
      )
      /*-----------------------ACA TERMINA EL CODIGO para mostrar distintos paises---------*/


return(
    <>
    <PageTitle title="Seleccione la lista a mostrar" />    

    <Grid container spacing={4}>{/*Esto hace el espacio entre los componentes*/}

    <Example />{/*Componente que te permite elegir la lista a mostrar */}

    <Grid item lg={3} md={4} sm={6} xs={12}>

    
          <Widget
            title={unArray.codigo}
            upperTitle
            bodyClass={classes.fullHeightBody}
            className={classes.card}
          >
            <div className={classes.visitsNumberContainer}>
              <Typography size="xl" weight="medium">
               Al dia de la FECHA
              </Typography>
              <LineChart
                width={55}
                height={60}
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
                  Confirmados
                </Typography>
                <Typography size="md">{unArray.confirmed}</Typography>
              </Grid>
              <Grid item>
                <Typography color="text" colorBrightness="secondary">
                  Recuperados
                </Typography>
              <Typography size="md">{unArray.recovered}</Typography>
              </Grid>
              <Grid item>
                <Typography color="text" colorBrightness="secondary">
                  Muertes
                </Typography>
                <Typography size="md">{unArray.deaths}</Typography>
              </Grid>
            </Grid>
          </Widget>
        </Grid>
        
    <Grid item xs={12}>
    <Widget
      bodyClass={classes.mainChartBody}
      header={
        <div className={classes.mainChartHeader}>
          <Typography
            variant="h5"
            color="text"
            colorBrightness="secondary"
          >
            COVID-19  PAIS:{unArray.codigo}
          </Typography>
          <div className={classes.mainChartHeaderLabels}>
            <div className={classes.mainChartHeaderLabel}>
              <Dot color="warning" />
              <Typography className={classes.mainChartLegentElement}>
                Confirmados
              </Typography>
            </div>
            <div className={classes.mainChartHeaderLabel}>
              <Dot color="primary" />
              <Typography className={classes.mainChartLegentElement}>
                Muertes
              </Typography>
            </div>
            <div className={classes.mainChartHeaderLabel}>
              <Dot color="primary" />
              <Typography className={classes.mainChartLegentElement}>
                Recuperados
              </Typography>
            </div>
          </div>
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
            <MuestraPaisesLista />

          </Select>
        </div>
      }
    >
      <ResponsiveContainer width="100%" minWidth={500} height={450}>
        <ComposedChart
          margin={{ top: 0, right: -15, left: -15, bottom: 0 }}
          data={  unArray.timeseries }
            /* [{ deaths: 40,confirmed :14,recovered:59},{ deaths: 40,confirmed :14,recovered:59}]*/
        >
          <YAxis
            ticks={[unArray.confirmed/10,unArray.confirmed/7.5,unArray.confirmed/5,unArray.confirmed/2.5,unArray.confirmed]}
            tick={{ fill: theme.palette.text.hint + "80", fontSize: 14 }}
            stroke={theme.palette.text.hint + "80"}
            tickLine={false}
          />
          <XAxis
            tickFormatter={i => i }
            tick={{ fill: theme.palette.text.hint + "80", fontSize: 14 }}
            stroke={theme.palette.text.hint + "80"}
            tickLine={false}
          />
          <Area
            type="natural"
            dataKey="deaths"
            fill={theme.palette.background.light}
            strokeWidth={0}
            activeDot={false}
          />
          <Line
            type="linear"
            dataKey="recovered"
            stroke={theme.palette.primary.main}
            strokeWidth={2}
            dot={false}
            activeDot={false}
          />
          <Line
            type="linear"
            dataKey="confirmed"
            stroke={theme.palette.warning.main}
            strokeWidth={2}
            dot={{
              stroke: theme.palette.warning.dark,
              strokeWidth: 2,
              fill: theme.palette.warning.main,
            }}
          />
        </ComposedChart>
      </ResponsiveContainer>
    </Widget>
  </Grid>
  <Grid item xs={12}>{/*2do grafico*/}
    <Widget>
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

export default Graficos

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
  