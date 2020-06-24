import React, {useState, useEffect} from 'react'
import { TextField,
	Container, 
	TableCell, 
	Grid, 
	Button, 
	CircularProgress,
	TableRow,
	Paper, Table, TableBody, TableContainer, TableHead  } from '@material-ui/core';

import { withStyles, makeStyles } from '@material-ui/core/styles';
import useStyles from "../dashboard/styles";
import MUIDataTable from "mui-datatables"; 

import PageTitle from "../../../components/PageTitle/PageTitle";
import Api from '../../../apis/Api';
const api = new Api()

const CrearListas = ()=>{

    const state = { rowsSelected: [] };
		var classes = useStyles();
    var listarray = new Array();

    const [unArray,setUnArray] = useState([])
    const [loading,setLoading] = useState(true)

    const [nombreLista,setNombreLista] = useState("")

    const [paisElegido,setPaisElegido] = useState(false)

    const [paises, setPaises] = useState([])


    const fetchAllCountries = async ()=>{
        try{
        let res = await api.getCountryList()
        //console.log(res)
        let data = await res.json()

        let promArray = data.map( country => {        
            return [country.name, country.iso2]
        })
        
        let resultArray = await Promise.all(promArray)
        setLoading(false)
        setUnArray(resultArray)

        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }
    }

    const crearListaDePaisesXUsuario = async ()=>{
        try{	
					await api.createCountryList(nombreLista,listarray[listarray.length - 1])
          window.alert("List: "+nombreLista+ " created")
          console.log(listarray)
					listarray = []
					setNombreLista("")
        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }
    }

    useEffect(() => {
        fetchAllCountries()
    },[]);    

return(
    <>
			{loading 
				? 
				<Paper>
					<Grid
						container
						spacing={0}
						direction="column"
						alignItems="center"
						justify="center"
						style={{ minHeight: '80vh' }}
					>
        		<Grid item xs={3}>
							<CircularProgress size={100}/>
						</Grid>   
					</Grid>
				</Paper>
				: 
				<div>
        <PageTitle title="Create a new list"/>
	        <MUIDataTable
            title={<div className={classes.mainChartHeader}>
            <Grid 
              container 
              justify="space-between"
              alignItems="center" 
            >
            <Grid item xs={3} md={3}> 
            <TextField 
              id="filled-namelist"
              label="List name"
              type="string"
              margin='dense'
              size='small'
              value={nombreLista}
              fullWidth={false}
              variant="outlined" 
              onChange={e => setNombreLista(e.target.value)} 
              InputLabelProps={{
                shrink: true,
              }}
            />  
            </Grid>
              <Grid item> 
                <Button 
                  xs={2} 
                  md={2} 
                  variant="contained" 
                  color="primary" 
                  disabled={ !paisElegido || !nombreLista }
                  onClick={() =>{
                      crearListaDePaisesXUsuario()
                    }
                  }
                >
                  Submit
                </Button>
              </Grid>
          </Grid>
            </div>}
            data={unArray}
            columns={["Country", "Code"]}
            options={{
                fixedHeaderOptions: false,
                fixedSelectColumn: false,
                rowHover: false,
                search: true,
                selectableRowsOnClick: true,
								selectableRowsHeader: false,
                expandableRowsHeader: false,
                disableToolbarSelect: true,
                expandableRows:false,
								print: false,
								viewColumns: false,
								fixedHeader: false,
								download: false,
								filter: false,
                responsive: 'stacked',
                rowsPerPage: 10,
                rowsPerPageOptions: [10],
                rowsSelected: state.rowsSelected,
								onRowsSelect:  (rowsSelected, allRows) => {       
              listarray.push(allRows.map( item => unArray[item.dataIndex][1]));
							setPaisElegido(true)
							},
						}
					}
        	/>
				</div>
			}
    </>
    )

}    


export default CrearListas
