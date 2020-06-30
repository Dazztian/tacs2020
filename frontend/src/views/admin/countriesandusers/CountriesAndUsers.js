import React, {useState, useEffect} from 'react'
import { Grid} from "@material-ui/core";

import Button from '@material-ui/core/Button';

// components
import Widget from "../../../components/Widget";
import MUIDataTable from "mui-datatables";

// styles
import useStyles from "./styles";

//Para el select list
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import { FixedSizeList } from 'react-window';

import Api from "../../../apis/Api"

const api = new Api()

export default function PaisEnComun(props) {
  var classes = useStyles();

  const state = { rowsSelected: [] };
  var listarray = new Array();

  
  const [idLista1, setidLista1]=useState([])
  const [idLista2, setidLista2]=useState([])
  
  const [unArrayConTodosLosUsuarios,setunArrayConTodosLosUsuarios] = useState([])
  const [unArrayListasDePaises,setunArrayListasDePaises] = useState([{}])


  const [ListasUsuarioParticular,setListasUsuarioParticular] = useState({})
  const [ListasUsuarioParticular2,setListasUsuarioParticular2] = useState({})

  const [count,setCount] = useState(0)
  const [PaisesEnComun,setPaisesEnComun]=useState([])

  const obtenerInfoDeUsuarios = async ()=>{
    try{
    const res = await api.getAllReports()
    let elemento = await res.json()

    let resultArray = await Promise.all(elemento)

    setunArrayConTodosLosUsuarios(resultArray)
    }
    catch(err) {
        console.log(err)
        window.alert(err)
    }
  }
  
const obtenerListasDePaisesDelUsuario = async (unIdUsuario)=>{
  try{
      let res = api.getUserListsIDUser()
      let elemento = await res.json()

      let resultArray = await Promise.all(elemento)
      
      setListasUsuarioParticular(resultArray)
      //console.log(resultArray)f

  }
  catch(err) {
      console.log(err)
      window.alert(err)
  }
}

const obtenerListasDePaisesDelUsuario2 = async (unIdUsuario)=>{
  try{
      let res = api.getUserListsIDUser()
      let elemento = await res.json()

      let resultArray = await Promise.all(elemento)
      
      setListasUsuarioParticular2(resultArray)

  }
  catch(err) {
      console.log(err)
      window.alert(err)
  }
}

const obtenerPaisesEnComun = async (idLista1, idLista2)=>{
    try{

        let res = api.compareLists(idLista1,idLista2)
        let elemento = await res.json()
  
        let promArray =  elemento.sharedCountries
  
        let resultArray = await Promise.all(promArray)
        
        setPaisesEnComun(resultArray)
        
        informarResultado(resultArray)
  
    }
    catch(err) {
        console.log(err)
        window.alert(err)
    }
  }

useEffect(() => {
    obtenerInfoDeUsuarios()
}, [count]);    


//Me muestra las listas y al seleccionar me SETEA el id de la lista con el cual LUEGO voy a comparar
 const Column = ({ index, style }) => (
    <ListItem button style={style} key={index}
    onClick={()=> {setidLista1(ListasUsuarioParticular[index].id) }}//Seteo el id de la lista correspondiente
    >
    <ListItemText primary={ListasUsuarioParticular[index].name}/>
    </ListItem>
  );
   
  const SelectListListasDePaisesXUsuario = () => (
    <FixedSizeList   height={125}  itemCount={ListasUsuarioParticular.length}  itemSize={150}  layout="horizontal"  width={350} >
      {Column}
    </FixedSizeList>
  )

//Me muestra las listas y al seleccionar me SETEA el id de la lista con el cual LUEGO voy a comparar
  const Column2 = ({ index, style }) => (
    <ListItem button style={style} key={index}
    onClick={()=>setidLista2(ListasUsuarioParticular2[index].id)}//Seteo el id de la lista correspondiente
    >
    <ListItemText primary={ListasUsuarioParticular2[index].name}/>
    </ListItem>
  );
   
  const SelectListListasDePaisesXUsuario2 = () => (
    <FixedSizeList   height={125}  itemCount={ListasUsuarioParticular2.length}  itemSize={150}  layout="horizontal"  width={450} >
      {Column2}
    </FixedSizeList>
  )



  return (
    <>
    
      <Grid container spacing={4}>
        <Grid container>        
        Elegir lista1
        <SelectListListasDePaisesXUsuario /> 
        Elegir lista2
        <SelectListListasDePaisesXUsuario2 /> 
        <Grid item xs={4}></Grid>  
        <Grid item  xs={4}>  
        <Button  variant="contained" color="primary" 
        onClick={()=> listarray.length >=2 ?
          cargarListas():
          window.alert("Seleccione los 2 usuarios")
          }>
          Cargar listas
        </Button>
        </Grid>
        <Grid item>  
        <Button  variant="contained" color="secondary" 
        //No se me reinicia el contenido de ListasUsuarioParticular 1 y 2. Checkearlo
        onClick={()=> elObjetoEstaVacio(ListasUsuarioParticular) || elObjetoEstaVacio(ListasUsuarioParticular2) ?
          window.alert("Debe elegir las a comparar listas primero"):
          obtenerPaisesEnComun(idLista1,idLista2)}
        >
          Comparar listas
        </Button>
        </Grid>
        </Grid>
        <Grid item xs={12}>
          <Widget title="Escoja los 2 usuarios a comparar"   upperTitle   noBodyPadding    bodyClass={classes.tableWidget} >
            <MUIDataTable
            title="Lista de usuarios"
            data={unArrayConTodosLosUsuarios}
            columns={["id", "name", "email"]}
            options={{
              filter: true,
              selectableRows: 'multiple',
              selectableRowsOnClick: true,
              filterType: 'dropdown',
              responsive: 'stacked',
              rowsPerPage: 10,
              rowsSelected: state.rowsSelected,
            onRowsSelect:  (rowsSelected, allRows) => {
            allRows.map( item =>{ 
              //Genero un array con los ids de usuarios selecciondados
               listarray.push(allRows.map( item => unArrayConTodosLosUsuarios[item.dataIndex].id) ); 
               //ESTO TIENE QUE ESTAR XQ ME SACA REPETIDOS!!
               listarray=Array.from(new Set(listarray[listarray.length - 1]))
            })
          }
          }}
          />
          </Widget>
        </Grid>
      </Grid>
    </>
  )


function cargarListas() {
  const arraysDeIds= listarray
  //Agarro los últimos 2 elementos del último elemento de list array
  const idUsuario1= arraysDeIds[arraysDeIds.length-1]
  const idUsuario2= arraysDeIds[arraysDeIds.length-2]
  obtenerListasDePaisesDelUsuario(idUsuario1)
  obtenerListasDePaisesDelUsuario2(idUsuario2)
}

function elObjetoEstaVacio(ubObjeto){
 return  Object.keys(ubObjeto).length === 0
}

function informarResultado(unArray) {
  unArray.length < 1 ?
    window.alert("No hay paises en comun entre ambas listas") :
    window.alert("Los países en común son: " + unArray);
}


}
