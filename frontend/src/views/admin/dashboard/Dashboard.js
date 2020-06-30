import React, {useState, useEffect} from 'react'
import { Grid} from "@material-ui/core";

// components
import Widget from "../../../components/Widget/Widget";
//import Table from "../../../components/Table/Table";
import MUIDataTable from "mui-datatables";

import Api from "../../../apis/Api"

// styles 
import useStyles from "./styles";

const api = new Api()

export default function Dashboard(props) {
  var classes = useStyles();

  const state = { rowsSelected: [] };

  const [unArrayTodosLosUsuarios,setunArrayTodosLosUsuarios] = useState([])
  const [unArrayUsuarioParticular,setunArrayUsuarioParticular] = useState({})
  const [count,setCount] = useState(0)

  const obtenerInfoDeUsuarios = async ()=>{
    try{
    
    const res = await api.getAllReports()
    let elemento = await res.json()
    let resultArray = await Promise.all(elemento)

    setunArrayTodosLosUsuarios(resultArray)

    }
    catch(err) {
        console.log(err)
        window.alert(err)
    }
}


const obtenerInfoUsuarioParticular = async (unIdUsuario)=>{
  try{
      let res = await api.getUserReport(unIdUsuario)
      let elemento = await res.json()

      let promArray  =  [{ name:elemento.user.name, listsQuantity:elemento.listsQuantity, countriesTotal:elemento.countriesTotal }]

      let resultArray = await Promise.all(promArray)
      
      setunArrayUsuarioParticular(resultArray)
      
      window.alert("Ha seleccionado el User: "
                 +"\n Nombre: "+resultArray[0].name
                 +"\n Cant de listas: " +resultArray[0].listsQuantity
                 +"\n Cant de total de paises: " +resultArray[0].countriesTotal ) 
  }
  catch(err) {
      console.log(err)
      window.alert(err)
  }
}

useEffect(() => {
  obtenerInfoDeUsuarios()
  //obtenerInfoUsuarioParticular()
}, [count]);    


  return (
    <>
      <Grid container spacing={4}>
        <Grid item xs={12}>        

        </Grid>
        <Grid item xs={12}>
          <Widget title="Usuarios"   upperTitle   noBodyPadding    bodyClass={classes.tableWidget} >
            <MUIDataTable
            title="Lista de usuarios"
            data={unArrayTodosLosUsuarios}
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
            allRows.map( item => obtenerInfoUsuarioParticular( unArrayTodosLosUsuarios[item.dataIndex].id ) )
            }
          }}
          />
          </Widget>
        </Grid>
      </Grid>
    </>
  );
}
  

