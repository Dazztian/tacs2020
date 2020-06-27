import React, {useState, useEffect} from 'react'
import Button from '@material-ui/core/Button';
import { Grid, TextField,	CircularProgress, Paper } from '@material-ui/core';

import MUIDataTable from "mui-datatables";

import PageTitle from "../../../components/PageTitle/PageTitle";
import Api from '../../../apis/Api';
const api = new Api()

const EditarListas = ()=>{


    const [unArrayTodosLosPaises,setUnArrayTodosLosPaises] = useState([])
    const [unArray,setUnArray] = useState([{name:null,paises:[]}])
    const [selectedList,setSelectedList] = useState([])
    const [selectedRows,setSelectedRows] = useState([])
    const [selectedNewCountries,setSelectedNewCountries] = useState([])
    const [nuevoNombreLista,setNuevoNombreLista] = useState("")
    const [loading,setLoading] = useState(true)

    const [paisElegido,setPaisElegido] = useState(false)

    const obtenerListaDePaises = async ()=>{
      try{

      let res = await api.getCountryList()
      let countryList = await res.json()

      let promArray = countryList.map( country => {        
          return [country.name, country.iso2]
      })
        
      let resultArray = await Promise.all(promArray)

      setUnArrayTodosLosPaises(resultArray)

      }
      catch(err) {
          console.log(err)
          window.alert(err)
      }
  }
    
    const obtenerListaDePaisesXUsuario = async ()=>{
        try{
          //let elemento = await api.getUserLists()
          let res = await api.getUserLists()
          let elemento = await res.json();

          let promArray = elemento.map( item=>{ return [item.name, item.id,
             item.countries.map(pais => [pais.name,pais.iso2])] })

          let resultArray = await Promise.all(promArray)

            setUnArray(resultArray)  
            setLoading(false)

        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }

    }

    async function handleBack(){
      setLoading(true)
      setSelectedList([])
      obtenerListaDePaisesXUsuario()
    }

    async function handleSelectRow(rowsSelected){
      let rowArray = [unArray[rowsSelected.dataIndex][0],unArray[rowsSelected.dataIndex][1],unArray[rowsSelected.dataIndex][2]]
      setSelectedList(rowArray)
    }

    async function handleDeleteCountries(){
      let promArray = selectedRows.map( item => selectedList[2][item.dataIndex])
      let arrToDelete = (await Promise.all(promArray)).flat()
      let newCountries = selectedList[2].filter(value => !arrToDelete.includes(value[1]))
      setSelectedList([selectedList[0],selectedList[1],newCountries])
    }

    async function handleAddCountries(){
      let promArray = selectedNewCountries.map( item => unArrayTodosLosPaises[item.dataIndex])
      let arrNewCountries = await Promise.all(promArray)
      let arrNewCountriesFlat = arrNewCountries.flat()
      let newCountriesToAdd = selectedList[2].filter(value => !arrNewCountriesFlat.includes(value[1]))
      let newCountry = [...newCountriesToAdd,...arrNewCountries]
      setSelectedList([selectedList[0],selectedList[1],newCountry])
    }

    const updateLista = async (nuevoNombre)=>{
        try{
            if(!nuevoNombre.length) nuevoNombre=selectedList[0]

            let isoArray = await Promise.all(selectedList[2].map( arr => arr[1]))

            await api.editCountryList(nuevoNombre,selectedList[1],isoArray)
            window.alert("List: "+nuevoNombre+ " modified")
            setSelectedList([nuevoNombre,selectedList[1],selectedList[2]])
            setNuevoNombreLista("")
        }
        catch(err) {
            console.log(err)
            window.alert(err)
        }
    }

    useEffect(() => {
        obtenerListaDePaises()
        obtenerListaDePaisesXUsuario()
    }, []); 


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
            !selectedList.length ? 
            <div>
            <PageTitle title="Edit lists" /> 
            <Grid container spacing={4}>
                <Grid item lg={12} md={12} sm={12} xs={12}>
                  <MUIDataTable
                  title={""}
                  data={unArray}
                  columns={["List name","Id"]}
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
                      onRowsSelect:  (rowsSelected, allRows) => {
                        handleSelectRow(rowsSelected[0])
                      },
                    }
                  }
                  />
                </Grid>
            </Grid>
            </div>
            : 
            <div>
            <Grid container>
              <Grid item xs={12} align="right">
                <Button  
                  variant="contained" 
                  color="secondary"
                  size="large"
                  onClick={()=> handleBack()}>
                    Back
                </Button>
              </Grid>
            <Grid item xs={12}>
            <PageTitle title={`Editing ${selectedList[0]}`}/>  
            <Grid container spacing={4} justify="space-between">
              <Grid item xs={6} >
              <MUIDataTable
              title={ 
              <div>
              <Grid item lg={12} sm={6} >
              <Grid      
              container
              spacing={2}
              alignItems="center"
              justify="space-between"
            >
                <Grid item xs={4} md={4}>
                <TextField
                  id="filled-number"
                  label={selectedList[0]}
                  type="string"
                  margin='dense'
                  size='small'
                  fullWidth={false}
                  inputProps={
                    {step: 1,}
                  }
                  onChange={e => setNuevoNombreLista(e.target.value)}
                  InputLabelProps={{
                    shrink: true,
                  }}
                />
                </Grid>
                  <Grid item xs={6} md={6}>
                    <Button  
                    xs={4} 
                    md={4} 
                    size='medium'
                    variant="contained" color="primary" 
                    onClick={(e)=> updateLista(nuevoNombreLista)}
                    >    
                    Update list
                    </Button>
                  </Grid>
                </Grid>
              </Grid> 
              </div>
              }
              data={selectedList[2]}
              columns={["Country","Code"]}
              options={{
                fixedHeaderOptions: false,
                fixedSelectColumn: false,
                rowHover: false,
                search: true,
                selectableRowsOnClick: true,
								selectableRowsHeader: false,
                expandableRowsHeader: false,
                expandableRows:false,
                print: false,
								viewColumns: false,
								fixedHeader: false,
								download: false,
								filter: false,
                responsive: 'stacked',
                rowsPerPage: 10,
                rowsPerPageOptions: [10],
                onRowsSelect:  (rowsSelected, allRows) => {
                  setSelectedRows(allRows)
                },
                  onRowsDelete:  () => { handleDeleteCountries() }
              }
              }
              />
              </Grid>
                <Grid item xs={6} >
                <MUIDataTable
                title={
                  <Grid item xs={6} md={6}>
                  <Button  
                    xs={4} 
                    md={4} 
                    disabled={selectedNewCountries.length===0 || selectedNewCountries.length>20 || selectedList[2].length>20 }
                    size='medium'
                    variant="contained" color="primary" 
                    onClick={(e)=> handleAddCountries()}
                  >    
                    Add 
                  </Button>
                </Grid>
                }
                data={unArrayTodosLosPaises}
                columns={["Country", "Code"]}
                options={{
                  fixedHeaderOptions: false,
                  fixedSelectColumn: false,
                  rowHover: false,
                  search: true,
                  selectableRowsOnClick: true,
                  selectableRowsHeader: true,
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
                  onRowsSelect:  (rowsSelected, allRows) => {
                    setSelectedNewCountries(allRows);
                    setPaisElegido(true)    
                  },

                }
                }
                />
                </Grid>
              </Grid>
            </Grid>
            </Grid>
          </div>
          }
            </>
        )
}   


export default EditarListas 


function flatenizarNombrePaises(elemento) {
  return [].concat.apply([], elemento);
}

function quitarElementosDelArray(listaOriginal, elementosABorrar){
  return listaOriginal.filter( ( elemento ) => !elementosABorrar.includes( elemento ) );
}
