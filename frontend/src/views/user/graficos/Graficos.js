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


// styles
import useStyles from "../dashboard/styles";


const Graficos = ()=>{

    const [unArray,setUnArray] = useState([])
    const [loading,setLoading] = useState(false)
    const [count,setCount] = useState(0)
    //esta funcion podria ir definida en el archivo api.js
    const submitData = async ()=>{
        try{
        let res = await fetch("https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/timeseries?iso2=US&onlyCountries=true",{
            method:"GET",
            headers:{
                'Content-Type': 'application/json'
            }
        })
        let elemento = await res.json()
        
        {/* En este formato me devuelve los datos el fetch
          Timeseries: [
            {
                day: 1,
                date: 2/2/2020,
                confirmed: 2,
                deaths: 34,
                recovered: 5
             }
             ......
         ]*/}

        let promArray = elemento.map( item => {        
            return [ item.day, item.date, item.confirmed, item.deaths, item.recovered]
        })
        
        let resultArray = await Promise.all(promArray)

        setUnArray(resultArray)

        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }
    }

    useEffect(() => {
        setLoading(true)
        submitData()
        setLoading(false)
    }, [count]);    


    var theme = useTheme();
    var classes = useStyles();

    // local
  var [mainChartState, setMainChartState] = useState("monthly");

  

const data = [
	{
		name: 'Page A', uv: 4000, pv: 2400, amt: 2400,
	},
	{
		name: 'Page B', uv: 3000, pv: 1390, amt: 2210,
	},
	{
		name: 'Page C', uv: 2000, pv: 9800, amt: 2290,
	},
	{
		name: 'Page D', uv: 2780, pv: 3980, amt: 2000,
	},
	{
		name: 'Page E', uv: 1890, pv: 4800, amt: 2180,
	},
	{
		name: 'Page F', uv: 2390, pv: 3800, amt: 2500,
	},
	{
		name: 'Page G', uv: 3490, pv: 4300, amt: 2100,
	},
];


const series = [
    {
      name: "series1",
      data: [31, 40, 28, 51, 42, 109, 100],
    },
    {
      name: "series2",
      data: [11, 32, 45, 32, 34, 52, 41],
    },
    {
      name: "series3",
      data: [7, 48, 12, 32, 63, 8, 72],
    },
  ];

//datos del 1er grafico
const mainChartData = getMainChartData();

return(
    <>
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
            COVID-19 CASUALTIES
          </Typography>
          <div className={classes.mainChartHeaderLabels}>
            <div className={classes.mainChartHeaderLabel}>
              <Dot color="warning" />
              <Typography className={classes.mainChartLegentElement}>
                Pais3
              </Typography>
            </div>
            <div className={classes.mainChartHeaderLabel}>
              <Dot color="primary" />
              <Typography className={classes.mainChartLegentElement}>
                Pais2
              </Typography>
            </div>
            <div className={classes.mainChartHeaderLabel}>
              <Dot color="primary" />
              <Typography className={classes.mainChartLegentElement}>
                Pais1
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
            <MenuItem value="Muertos">Muertos</MenuItem>
            <MenuItem value="Confirmados">Confirmados</MenuItem>
            <MenuItem value="Recuperados">Recuperados</MenuItem>
          </Select>
        </div>
      }
    >
      <ResponsiveContainer width="100%" minWidth={500} height={350}>
        <ComposedChart
          margin={{ top: 0, right: -15, left: -15, bottom: 0 }}
          data={mainChartData}
        >
          <YAxis
            ticks={[0, 25, 50, 75, 100]}
            tick={{ fill: theme.palette.text.hint + "80", fontSize: 14 }}
            stroke={theme.palette.text.hint + "80"}
            tickLine={false}
          />
          <XAxis
            tickFormatter={i => i+1 }
            tick={{ fill: theme.palette.text.hint + "80", fontSize: 14 }}
            stroke={theme.palette.text.hint + "80"}
            tickLine={false}
          />
          <Area
            type="natural"
            dataKey="desktop"
            fill={theme.palette.background.light}
            strokeWidth={0}
            activeDot={false}
          />
          <Line
            type="linear"
            dataKey="mobile"
            stroke={theme.palette.primary.main}
            strokeWidth={2}
            dot={false}
            activeDot={false}
          />
          <Line
            type="linear"
            dataKey="tablet"
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
  <Grid item xs={12}>
    <Widget>
            <ApexCharts
            options={themeOptions(theme)}
            series={series}
            type="area"
            height={350}
            />
    </Widget>
  </Grid>
            </>
)

}

export default Graficos


// #######################################################################
function getRandomData(length, min, max, multiplier = 10, maxDiff = 10) {
    var array = new Array(length).fill();
    let lastValue;
  
    return array.map((item, index) => {
      let randomValue = Math.floor(Math.random() * multiplier + 1);
  
      while (
        randomValue <= min ||
        randomValue >= max ||
        (lastValue && randomValue - lastValue > maxDiff)
      ) {
        randomValue = Math.floor(Math.random() * multiplier + 1);
      }
  
      lastValue = randomValue;
  
      return { value: randomValue };
    });
  }
  

function getMainChartData() {
    var resultArray = [];
    var tablet = [{value:48},{value:96},{value:48},{value5:14}]//getRandomData(31, 350, 6500, 7500, 1000);
    var desktop = [{value:14},{value:48},{value:48},{value:79}]//getRandomData(31, 1500, 7500, 7500, 1500);
    var mobile = [{value:14},{value:42},{value:48},{value:48}]//getRandomData(31, 1500, 7500, 7500, 1500);
  
    for (let i = 0; i < tablet.length; i++) {
      resultArray.push({
        tablet: tablet[i].value, //pais1: valor
        desktop: desktop[i].value, //pais2: valor
        mobile: mobile[i].value, //pais3: valor
      });
    }
  
    return resultArray;
  }

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
  